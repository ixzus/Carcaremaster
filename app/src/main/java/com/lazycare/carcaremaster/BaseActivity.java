package com.lazycare.carcaremaster;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.lazycare.carcaremaster.service.CoreService;
import com.lazycare.carcaremaster.service.ReConnectService;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

/**
 * 所有activity的基类
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public abstract class BaseActivity extends ActionBarActivity {
    //用户的相关配置信息
    public String id = "";
    public String username = "";
    public String password = "";

    private long exitTime = 0;
    private boolean isTwiceExit = false;
    public String TAG = this.getClass().getSimpleName();
    public Dialog mDialog;
    public Context mContext = this;
    private String IMEI = "";
    protected SharedPreferences preferences;
    private static CoreService mService = new CoreService();
    private static ReConnectService mReConnectService = new ReConnectService();
    public Toolbar mToolbar;
    private ReconnectReceiver receiver;
    private boolean isFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //避免点击打开，返回，点击图标后
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        // bug分析
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(this);
        SysApplication.getInstance().addActivity(this);

        id = getSharedPreferences(Config.USERINFO, 0).getString(Config.ID, "0");
        username = getSharedPreferences(Config.USERINFO, 0).getString(Config.USERNAME, "");
        password = getSharedPreferences(Config.USERINFO, 0).getString(Config.PWD, "111111");
        preferences = getSharedPreferences(Constant.LOGIN_SET, 0);
        setLayout();
        setActionBarOption();
        ButterKnife.bind(this);
        //initView之前
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        if (isFullScreen)
            tintManager.setStatusBarTintResource(R.color.transparent);
        else
            tintManager.setStatusBarTintResource(R.color.statusbar_bg);
        initView();
        receiver = new ReconnectReceiver();

        //获取imei码
//        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//        IMEI = tm.getDeviceId();
    }

    public void setIsFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        MobclickAgent.onResume(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.ACTION_RECONNECT_STATE);
        registerReceiver(receiver, filter);
    }

    public SharedPreferences getSharePreferences() {
        return getSharedPreferences(Config.USERINFO, 0);
    }

    public SharedPreferences.Editor getSharePreferencesEditor() {
        return getSharedPreferences(Config.USERINFO, 0).edit();
    }

    /**
     * 销毁核心服务.
     */
    public void stopService() {
        // 聊天服务
        Intent chatServer = new Intent(mContext, CoreService.class);
        mContext.stopService(chatServer);
    }

    /**
     * 开启核心服务
     */
    public void startService() {
        // 聊天服务
        Intent chatServer = new Intent(mContext, CoreService.class);
        mContext.startService(chatServer);
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
            showToast("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            exit();
        }
    }

    /**
     * 退出应用
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

    /**
     * 定义接收消息的广播
     *
     * @author GMY
     * @mail 2275964276@qq.com
     * @date 2015年6月4日
     */
    private class ReconnectReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 收到重连成功的消息
            if (Config.ACTION_RECONNECT_STATE.equals(action)) {
                showToast("重连成功了");
                startService();
            }
        }
    }
}
