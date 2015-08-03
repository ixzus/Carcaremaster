package com.lazycare.carcaremaster;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lazycare.carcaremaster.data.LoginConfig;
import com.lazycare.carcaremaster.impl.IBaseActivity;
import com.lazycare.carcaremaster.service.IMChatService;
import com.lazycare.carcaremaster.service.LoginService;
import com.lazycare.carcaremaster.service.ReConnectService;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.umeng.analytics.MobclickAgent;

/**
 * 所有activity的基类
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public abstract class BaseActivity extends ActionBarActivity implements
        IBaseActivity {

    private long exitTime = 0;
    private boolean isTwiceExit = false;
    public String TAG = this.getClass().getSimpleName();
    public Dialog dialog;
    public Context mContext = this;
    private String IMEI = "";
    protected SharedPreferences preferences;
    private static IMChatService mService = new IMChatService();
    private static ReConnectService mReConnectService = new ReConnectService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // bug分析
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        SysApplication.getInstance().addActivity(this);
        preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
        setActionBarOption();
        setLayout();
        //initView之前
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//			setTranslucentStatus(true);
//		}
//		SystemBarTintManager tintManager = new SystemBarTintManager(this);
//		tintManager.setStatusBarTintEnabled(true);
//		tintManager.setStatusBarTintResource(android.R.color.background_dark);
        initView();
//        if (!NetworkUtil.isNetworkAvailable(mContext)) {
//            this.getLayoutInflater().inflate(R.layout.view_nonet,null);
//        }
        // loginService();
        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        // startService();
    }

    public LoginConfig getLoginConfig(String name, String password) {
        LoginConfig loginConfig = new LoginConfig();
        String a = preferences.getString(Constant.XMPP_HOST, null);
        String b = getResources().getString(R.string.xmpp_host);
        loginConfig.setXmppHost(preferences.getString(Constant.XMPP_HOST,
                getResources().getString(R.string.xmpp_host)));
        loginConfig.setXmppPort(preferences.getInt(Constant.XMPP_PORT,
                getResources().getInteger(R.integer.xmpp_port)));
        //
        loginConfig.setUsername(name);
        loginConfig.setPassword(password);
        loginConfig.setXmppServiceName(preferences.getString(
                Constant.XMPP_SEIVICE_NAME,
                getResources().getString(R.string.xmpp_service_name)));
        return loginConfig;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void startService() {
        // 聊天服务
        Intent chatServer = new Intent(mContext, IMChatService.class);
        mContext.startService(chatServer);
        // bindService(chatServer, connection, Context.BIND_AUTO_CREATE);
        // 自动恢复连接服务
        Intent reConnectService = new Intent(mContext, ReConnectService.class);
        mContext.startService(reConnectService);
        // bindService(reConnectService, reconnection,
        // Context.BIND_AUTO_CREATE);

    }

    /**
     * 销毁服务.
     *
     * @author shimiso
     * @update 2012-5-16 下午12:16:08
     */
    @Override
    public void stopService() {
        // 好友联系人服务
        // Intent server = new Intent(mContext, IMContactService.class);
        // mContext.stopService(server);
        // 聊天服务
        Intent chatServer = new Intent(mContext, IMChatService.class);
        mContext.stopService(chatServer);
        // unbindService(connection);
        // 自动恢复连接服务
        Intent reConnectService = new Intent(mContext, ReConnectService.class);
        mContext.stopService(reConnectService);
        // unbindService(reconnection);

    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 加载布局
     */
    public abstract void setLayout();

    /**
     * 设置actionbar
     */
    public abstract void setActionBarOption();

    /**
     * find the widget
     */
    public abstract void initView();

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("gmyboy", TAG + "---------onRestart-----------");
        startService();
    }

    @Override
    protected void onDestroy() {
        stopService();
        Log.d("gmyboy", TAG + "---------onDestroy-----------");
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        stopService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);

    }

    public SharedPreferences getSharePreferences() {
        return getSharedPreferences(Configuration.USERINFO, 0);
    }

    public SharedPreferences.Editor getSharePreferencesEditor() {
        return getSharePreferences().edit();
    }

    public void checkIMEI(String newIMEI) {
        if (IMEI.equals(newIMEI)) {
            Intent intent = new Intent();
            intent.setClass(mContext, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void loginService() {
        Intent intent = new Intent(this, LoginService.class);
        // 启动服务
        startService(intent);
    }

    /**
     * 设置是否可以按两次退出
     *
     * @param isTwiceExit
     */
    public void setTwiceExit(boolean isTwiceExit) {
        this.isTwiceExit = isTwiceExit;
    }

    /**
     * 实现联系按两次推出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isTwiceExit) {
            twiceexit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void twiceexit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            exit();
        }
    }

    /**
     * 退出所有activity
     */
    public void exit() {
        stopService();
        SysApplication.getInstance().exit();
    }

    /**
     * 显示toast
     *
     * @param massage
     */
    public void showToast(String massage) {
        Toast.makeText(this, massage, Toast.LENGTH_SHORT).show();
    }


}
