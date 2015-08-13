package com.lazycare.carcaremaster;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

/**
 * 授予应用boot权限
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //接收广播：系统启动完成后运行程序
//        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
//            Intent newIntent = new Intent(context, WatchInstall.class);
//            newIntent.setAction("android.intent.action.MAIN");
//            newIntent.addCategory("android.intent.category.LAUNCHER");
//            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(newIntent);
//        }
        //接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
        if (action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_REPLACED)) {
//            Toast.makeText(context, action, Toast.LENGTH_LONG).show();
            String packageName = intent.getData().getSchemeSpecificPart();
//            Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (packageName.equals("com.lazycare.carcaremaster")) {
                Intent newIntent;
                PackageManager packageManager = context.getPackageManager();
                newIntent = packageManager.getLaunchIntentForPackage(packageName);
//            newIntent.setComponent(new ComponentName(packageName, "SplashActivity"));
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(newIntent);
            }
        }
        //接收广播：设备上删除了一个应用程序包。
        if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
//            Toast.makeText(context, "ACTION_PACKAGE_REMOVED", Toast.LENGTH_LONG).show();
//            Uri uri = Uri.parse("http://192.168.0.121:8088/Blog/index.jsp");
//            Intent newIntent = new Intent(Intent.ACTION_VIEW, uri);
//            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(newIntent);
        }
    }
}
