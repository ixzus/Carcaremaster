package com.lazycare.carcaremaster;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lazycare.carcaremaster.handler.ErrorHandler;
import com.lazycare.carcaremaster.util.Config;

/**
 * app
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class SysApplication extends Application {

    private File dir;
    public String CurDate = "2015-05-13";
    //堆栈，用来管理activity
    private static Stack<Activity> mStack;
    private static SysApplication instance;

    public synchronized static SysApplication getInstance() {
        if (null == instance) {
            instance = new SysApplication();
        }
        return instance;
    }

    /**
     * 添加activity到后台堆栈中
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mStack == null) {
            mStack = new Stack<>();
        }
        mStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = mStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = mStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            mStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : mStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void exit() {
        try {
            for (int i = 0, size = mStack.size(); i < size; i++) {
                if (null != mStack.get(i)) {
                    mStack.get(i).finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            restoreWifiDormancy();
            mStack.clear();
            System.exit(0);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Fresco.initialize(this);
//		ErrorHandler crashHandler = ErrorHandler.getInstance();
//		crashHandler.init(getApplicationContext());
        dir = new File(Config.RECORD_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(Config.IMG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        wifiNeverDormancy(this);
    }

    public String getCurDate() {
        return CurDate;
    }

    public void setCurDate(String curDate) {
        CurDate = curDate;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    /**
     * 设置手机休眠状态下不会断开网络连接
     *
     * @param mContext
     */
    public void wifiNeverDormancy(Context mContext) {
        ContentResolver resolver = mContext.getContentResolver();

        int value = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("wifi", value);
        editor.commit();
        if (Settings.System.WIFI_SLEEP_POLICY_NEVER != value) {
            Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);
        }
        System.out.println("wifi value:" + value);
    }

    /**
     * 还原手机休眠状态下不会断开网络连接的设置
     */
    private void restoreWifiDormancy() {
        final SharedPreferences prefs = getSharedPreferences("wifi", Context.MODE_PRIVATE);
        int defaultPolicy = prefs.getInt("wifi", Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY, defaultPolicy);
    }
}