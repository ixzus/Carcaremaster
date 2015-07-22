package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.XMPPError;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lazycare.carcaremaster.adapter.MenuItemAdapter;
import com.lazycare.carcaremaster.data.LoginConfig;
import com.lazycare.carcaremaster.data.MenuClass;
import com.lazycare.carcaremaster.fragment.ModifyPhotoFragment;
import com.lazycare.carcaremaster.impl.IBaseActivity;
import com.lazycare.carcaremaster.receiver.ConnectionChangeReceiver;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.service.QuestionService;
import com.lazycare.carcaremaster.service.WorkTimeServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.thread.UpdateManager;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.XmppConnectionManager;
import com.lazycare.carcaremaster.widget.CircularImage;
import com.squareup.picasso.Picasso;

/**
 * 首页
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MainActivity extends BaseActivity {
    // 用户信息相关
    CircularImage ci_headphoto;
    TextView tv_username, tv_userrank;
    String id = "";
    private Handler mHandler = new MainLoadHandler(this);
    MenuItemAdapter adapter = null;
    GridView gridview;
    //    Intent serviceIntent;
    private ConnectionChangeReceiver myReceiver;
    private List<MenuClass> lstMenu;
    private String[] names = {"车主问题", "我的预约", "工时管理", "账号信息", "系统消息", "我要提现"};
    private static long date = 0;
    private QuestionListServices services_question = QuestionListServices
            .getInstance(this);
    private WorkTimeServices services_time = WorkTimeServices.getInstance(this);
    private String username = "";
    private String remindme = "";
    private LoginConfig loginConfig;

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setTitle("首页");

    }

    @Override
    public void initView() {
        setTwiceExit(true);
        id = getSharePreferences().getString(Configuration.ID, "0");
        username = getSharePreferences().getString(Configuration.USERNAME, "");
        adapter = new MenuItemAdapter(MainActivity.this);
        ci_headphoto = (CircularImage) findViewById(R.id.ci_headphoto);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_userrank = (TextView) findViewById(R.id.tv_userrank);
        gridview = (GridView) findViewById(R.id.GridView);
        // 添加Item到网格中
        gridview.setAdapter(adapter);
        lstMenu = new ArrayList<MenuClass>();
        MenuClass menuClass = null;
        for (int i = 0; i < 6; i++) {
            menuClass = new MenuClass();
            menuClass.setName(names[i]);
            lstMenu.add(menuClass);
            menuClass = null;
        }
        for (MenuClass mc : lstMenu) {
            adapter.addNewItem(mc);
        }
        // 跳转源泰搜汽配
        ci_headphoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // intent.setClass(MainActivity.this, UserInfoActivity.class);
                intent.setClass(MainActivity.this, QiPeiActivity.class);
                startActivity(intent);
            }
        });
        gridview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                switch (position) {
                    case 0:// 车主问题
                        intent.setClass(MainActivity.this,
                                QuestionsListActivity.class);
//					 intent.setClass(MainActivity.this,
//					 QuestionsListActivity2.class);
                        startActivity(intent);
                        break;
                    case 1:// 我的预约
                        intent.setClass(MainActivity.this,
                                AppointmentListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:// 工时管理
                        // intent.setClass(MainActivity.this,
                        // MyWorkHoursActivity.class);
                        intent.setClass(MainActivity.this, WorkHoursActivity.class);
                        startActivity(intent);
                        break;
                    case 3:// 账号信息
                        intent.setClass(MainActivity.this,
                                MyAccountInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 4:// 系统消息
                        intent.setClass(MainActivity.this, MessageActivity.class);
                        // intent.setClass(MainActivity.this,
                        // TestRecordActivity.class);
                        startActivity(intent);
                        break;
                    case 5:// 我要提现
                        intent.setClass(MainActivity.this,
                                ExchangeMoneyActivity.class);
                        startActivity(intent);
                        break;
                }
                // System.out.println("click index:"+id);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArtifiInfo();
        registerReceiver();
//        serviceIntent = new Intent(mContext, QuestionService.class);
        //登陆openfire
        new LoginTask(MainActivity.this, username, "111111").execute();
    }

    /**
     * 注册网络监听器
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);
    }

    private void unregisterReceiver() {
        this.unregisterReceiver(myReceiver);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("gmyboy", TAG + "---------onRestart-----------");
        startService();
        if (!ConnectionChangeReceiver.FLAG2) {
            getArtifiInfo();
        }
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(myReceiver);
        stopService();
        Log.d("gmyboy", TAG + "---------onDestroy-----------");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopService();
    }

    /**
     * 获取数据信息
     */
    private void getArtifiInfo() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        TaskExecutor
                .Execute(new DataRunnable(this, "/Index/get", mHandler, Config.WHAT_ONE, map));
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
    private class MainLoadHandler extends Handler {

        private WeakReference<MainActivity> mWeak;

        public MainLoadHandler(MainActivity activity) {
            mWeak = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            // DialogUtil.dismiss(activity.mDialog);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(MainActivity activity, int what, String json) {
            switch (what) {
                case Config.WHAT_ONE:
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            Gson gson = new Gson();
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                // 加载数据
                                JSONObject jd = new JSONObject(data);
                                String artificer = jd.getString("artificer");
                                String menus = jd.getString("menus");
                                JSONObject ju = new JSONObject(artificer);
                                // 师傅信息
                                String head = ju.getString("head");// 需要将图像加载进来
                                String name = ju.getString("name");
                                // String rank = ju.getString("rank");
                                String order_num = ju.getString("order_num");
                                String ok_num = ju.getString("ok_num");
                                // String rank_name = ju.getString("rank_name");
                                tv_userrank.setText("接单数  " + order_num + "，好评数  "
                                        + ok_num);
                                tv_username.setText(name);
                                // tv_userrank.setText(rank_name);
                                if (!head.equals("") && head != null) {
                                    Picasso.with(MainActivity.this).load(head)
                                            .placeholder(R.drawable.defaulthead)
                                            .into(ci_headphoto);
                                    getSharePreferencesEditor().putString(
                                            Configuration.HEAD, head).commit();// 先将这个头像存储起来
                                }

                                // List<MenuClass> lstMenu = gson.fromJson(menus,
                                // new TypeToken<List<MenuClass>>() {
                                // }.getType());
                                // // 避免重复加载
                                // if (!ConnectionChangeReceiver.FLAG2) {
                                // adapter.removeAllItem();
                                // ConnectionChangeReceiver.FLAG2 = true;
                                // }
                                // for (MenuClass mc : lstMenu) {
                                // adapter.addNewItem(mc);
                                // }
                                // if (lstMenu.size() > 0) {
                                // gridview.setAdapter(adapter);
                                // }
                                checkNewVersion();
//                                startService(serviceIntent);
                            } else
                                Toast.makeText(MainActivity.this, msg,
                                        Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                case Config.WHAT_TWO:
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                String versionCode = jd.getString("copyright");
                                String url = jd.getString("download");
                                remindme = getSharePreferences().getString(Configuration.REMIND_UPDATE, "1");//默认提醒用户
                                if (versionCode != null && !versionCode.equals("") && remindme.equals("1")) {
                                    UpdateManager manager = new UpdateManager(
                                            mContext, url);
                                    // 检查软件更新(首页自动弹出)
                                    manager.checkUpdate(versionCode, Config.WHAT_TWO);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 检查是否有更新
     */
    private void checkNewVersion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("client", "android");
        TaskExecutor.Execute(new DataRunnable(mContext, "/System/update",
                mHandler, Config.WHAT_TWO, map));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_close:
//                Intent intent = new Intent();
//                intent.setClass(mContext, LoginActivity.class);
//                startActivity(intent);
//                //清除车主问题、工时、用户配置等缓存
//                services_question.clearData();
//                services_time.clearData();
//                getSharePreferencesEditor()
//                        .putString(Configuration.ID, "")
//                        .putString(Configuration.USERNAME, "")
//                        .putString(Configuration.PWD, "")
//                        .putString(Configuration.TOKEN, "")
//                        .commit();// 先将这个iD存储起来
//                finish();
//
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    class LoginTask extends AsyncTask<String, Integer, Integer> {
        private IBaseActivity baseactivity;

        public LoginTask(IBaseActivity baseactivity, String name, String pwd) {
            super();
            this.baseactivity = baseactivity;
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
            super.onPostExecute(result);
        }
    }

    // 登录
    private Integer login() {
        String username = loginConfig.getUsername();
        String password = loginConfig.getPassword();
        try {
            XMPPConnection connection = XmppConnectionManager.getInstance()
                    .getConnection();
            connection.connect();
            connection.login(username, password); // 登录
            // OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
            connection.sendPacket(new Presence(Presence.Type.available));
            if (loginConfig.isNovisible()) {// 隐身登录
                Presence presence = new Presence(Presence.Type.unavailable);
                Collection<RosterEntry> rosters = connection.getRoster()
                        .getEntries();
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
}
