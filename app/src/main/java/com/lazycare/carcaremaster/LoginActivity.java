package com.lazycare.carcaremaster;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.data.ArtificerFilterTimeClass;
import com.lazycare.carcaremaster.data.LoginConfig;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.service.IMChatService;
import com.lazycare.carcaremaster.service.ReConnectService;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.util.XmppConnectionManager;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登陆界面
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class LoginActivity extends ActionBarActivity {
    @Bind(R.id.txt_username)
    EditText txtUsername;
    @Bind(R.id.txt_password)
    EditText txtPassword;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_forgetpwd)
    TextView tvForgetpwd;

    @OnClick(R.id.btn_login)
    void goLogin() {
        String strUserName = txtUsername.getText().toString().trim();
        String strPwd = txtPassword.getText().toString().trim();
        if (strUserName.equals("")) {
            CommonUtil.showToast(mContext, "用户名不能为空");
        } else if (strPwd.equals("")) {
            CommonUtil.showToast(mContext, "密码不能为空");
        } else {
            if (NetworkUtil.isNetworkAvailable(LoginActivity.this)) {
                mDialog = CustomProgressDialog.showCancelable(this, "登陆中...");
                Map<String, String> map = new HashMap<>();
                map.put("mobile", strUserName);
                map.put("password", strPwd);
                TaskExecutor.Execute(new DataRunnable(this, "/Artificer/signIn", mHandler, Config.WHAT_ONE, map));
            } else {
                CommonUtil.showToast(mContext, "您还没联网哦,亲");
            }
        }
    }

    @OnClick(R.id.tv_forgetpwd)
    void forgetPwd() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, FindPasswordActivity.class);
        startActivity(intent);
    }

    private Handler mHandler = new LoginHandler(this);
    private LoginConfig loginConfig;
    protected SharedPreferences preferences;

    private Dialog mDialog;
    public String TAG = "LoginActivity";
    public Context mContext = this;
    //用户相关信息
    private String username = "";
    private String id = "";
    private String pwd = "";

    public void initView() {
        preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
        // 从share中拿到id
        id = getSharedPreferences(Config.USERINFO, 0).getString(Config.ID, "0");
        username = getSharedPreferences(Config.USERINFO, 0).getString(Config.USERNAME, "");
        pwd = getSharedPreferences(Config.USERINFO, 0).getString(Config.PWD, "111111");

        txtUsername.setText(username);
        txtPassword.setText(pwd);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("登录");
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * <p>
     * 标 题: LoginHandler
     * </p>
     * <p>
     * 描 述: 主线程处理
     * </p>
     * <p>
     * 版 权: Copyright (c) Administrator
     * </p>
     * <p>
     * 创建时间: Sep 25, 2014 3:06:35 PM
     * </p>
     *
     * @author Administrator
     */
    @SuppressLint("HandlerLeak")
    private class LoginHandler extends Handler {

        private WeakReference<LoginActivity> mWeak;

        public LoginHandler(LoginActivity activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(LoginActivity activity, int what, String json) {
            switch (what) {
                case Config.WHAT_ONE: // 验证用户名
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                String tid = jd.getString("id");
                                String token = jd.getString("token");
                                // 保存用户信息
                                getSharedPreferences(Config.USERINFO, 0)
                                        .edit()
                                        .putString(Config.ID, tid)
                                        .putString(Config.USERNAME, txtUsername.getText().toString().trim())
                                        .putString(Config.PWD, txtPassword.getText().toString().trim())
                                        .putString(Config.TOKEN, token)
                                        .commit();
                                getServerDate(id);// 获取服务器上的时间
                                //登陆openfire
                                new LoginTask(txtUsername.getText().toString().trim(), "111111").execute();
                            } else {
                                CommonUtil.showToast(mContext, msg);
                                mDialog.dismiss();
                            }

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                            mDialog.dismiss();
                        }

                    }
                    // if( activity.showError(json) )
                    // break;
                    // Object[] objs = ToObjectUtil.JsonToObj(json, User.class);
                    // if( ObjectUtil.isEmpty(objs) )
                    // break;
                    // User user = (User)objs[0];
                    // InfoUtil.setUser(activity, user);
                    // InfoUtil.setSect(activity, user.getSect_id(),
                    // user.getSect_name(), user.getSect_addr());
                    // SysApplication.getInstance().finish();
                    // IntentUtil.startActivityFinish(activity, MainActivity.class);
                    break;
                case Config.WHAT_TWO:
                    Log.d(TAG, json);
                    try {
                        Gson gson = new Gson();
                        if (json != null) {

                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");

                            if (error.equals("0")) {
                                List<ArtificerFilterTimeClass> lstAFTC = new ArrayList<>();
                                lstAFTC = gson.fromJson(data, new TypeToken<List<ArtificerFilterTimeClass>>() {
                                }.getType());
                                int period = DateUtil.getDatePeriod(getSharedPreferences(Config.USERINFO, 0).getString(Config.TIME, "1990-01-01"), lstAFTC.get(0).getRealDay());
                                if (period >= 1) {// 相隔1天
                                    getSharedPreferences(Config.USERINFO, 0).edit().putBoolean(// 需要更新工时管理
                                            Config.TIME_UPDATE, true).commit();
                                }
                                getSharedPreferences(Config.USERINFO, 0).edit().putString(Config.TIME, lstAFTC.get(0).getRealDay()).commit();
                            } else {

                            }
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        mDialog.dismiss();
                    }
                    // case Config.WHAT_ONE: // 验证用户名
                    // if (!StringUtil.equals(ErrorUtil.SUCCESS, json)
                    // && !StringUtil.equals(ErrorUtil.isDataError(json),
                    // ErrorUtil.NETWORK_UNCONNECT)
                    // && !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT,
                    // ErrorUtil.isDataError(json)))
                    // activity.mCv.et_name.setError("用户已存在！");
                    // break;
                    // case Config.WHAT_TWO: // 验证昵称
                    // if (!StringUtil.equals(ErrorUtil.SUCCESS, json)
                    // && !StringUtil.equals(ErrorUtil.isDataError(json),
                    // ErrorUtil.NETWORK_UNCONNECT)
                    // && !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT,
                    // ErrorUtil.isDataError(json)))
                    // activity.mCv.et_nick.setError("昵称已存在！");
                    // break;
                    // case Config.WHAT_FOUR: // 校验用户信息
                    // if (activity.showError(json))
                    // break;
                    // Intent intent = new Intent(activity, HouAddActivity.class);
                    // intent.putExtra("App_user", activity.mAppUser);
                    // activity.startActivity(intent);
                    // break;
                    // case Config.WHAT_THREE: // 校验用户信息
                    // activity.showError(json);
                    // break;

            }
        }
    }

    class LoginTask extends AsyncTask<String, Integer, Integer> {


        public LoginTask(String name, String pwd) {
            super();

            loginConfig = getLoginConfig(name, pwd);
            XmppConnectionManager.getInstance().init(loginConfig);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            return login();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Integer result) {
            mDialog.dismiss();
            if (result == Constant.LOGIN_SECCESS) {
                CommonUtil.showToast(mContext, "登陆成功");
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                startService(); // 初始化各项服务
            } else {
                CommonUtil.showToast(mContext, "登陆失败");
            }
            super.onPostExecute(result);
        }
    }

    // 登录
    private Integer login() {
        String username = loginConfig.getUsername();
        String password = loginConfig.getPassword();
        try {
            XMPPConnection connection = XmppConnectionManager.getInstance().getConnection();
            connection.connect();
            connection.login(username, password); // 登录
            connection.sendPacket(new Presence(Presence.Type.available));
            if (loginConfig.isNovisible()) {// 隐身登录
                Presence presence = new Presence(Presence.Type.unavailable);
                Collection<RosterEntry> rosters = connection.getRoster().getEntries();
                for (RosterEntry rosterEntry : rosters) {
                    presence.setTo(rosterEntry.getUser());
                    connection.sendPacket(presence);
                }
            }
            loginConfig.setUsername(username);
            if (loginConfig.isRemember()) {// 保存密码
                loginConfig.setPassword(password);
            } else {
                loginConfig.setPassword("");
            }
            loginConfig.setOnline(true);
            return Constant.LOGIN_SECCESS;
        } catch (Exception xee) {
            if (xee instanceof XMPPException) {
                XMPPException xe = (XMPPException) xee;
                final XMPPError error = xe.getXMPPError();
                int errorCode = 0;
                if (error != null) {
                    errorCode = error.getCode();
                }
                if (errorCode == 401) {
                    return Constant.LOGIN_ERROR_ACCOUNT_PASS;
                } else if (errorCode == 403) {
                    return Constant.LOGIN_ERROR_ACCOUNT_PASS;
                } else {
                    return Constant.SERVER_UNAVAILABLE;
                }
            } else {
                return Constant.LOGIN_ERROR;
            }
        }
    }

    private void getServerDate(String id) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("artificer", id);
        TaskExecutor.Execute(new DataRunnable(mContext, "/ArtificerCalendar/get", mHandler, Config.WHAT_TWO, map));
    }

    public LoginConfig getLoginConfig(String name, String password) {
        LoginConfig loginConfig = new LoginConfig();
        String a = preferences.getString(Constant.XMPP_HOST, null);
        String b = getResources().getString(R.string.xmpp_host);
        loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST, getResources().getString(R.string.xmpp_host)));
        loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT, getResources().getInteger(R.integer.xmpp_port)));
        //
        loginConfig.setUsername(name);
        loginConfig.setPassword(password);
        loginConfig.setXmppServiceName(preferences.getString(Constant.XMPP_SEIVICE_NAME, getResources().getString(R.string.xmpp_service_name)));
        return loginConfig;
    }

    public void startService() {
        // 聊天服务
        Intent chatServer = new Intent(mContext, IMChatService.class);
        mContext.startService(chatServer);
        // 自动恢复连接服务
        Intent reConnectService = new Intent(mContext, ReConnectService.class);
        mContext.startService(reConnectService);
    }


    /**
     * 销毁服务.
     */
    public void stopService() {

        // 聊天服务
        Intent chatServer = new Intent(mContext, IMChatService.class);
        mContext.stopService(chatServer);
        // 自动恢复连接服务
        Intent reConnectService = new Intent(mContext, ReConnectService.class);
        mContext.stopService(reConnectService);

    }
}
