package com.lazycare.carcaremaster.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.lazycare.carcaremaster.BaseActivity;
import com.lazycare.carcaremaster.EmptyActivity;

/**
 * 监听网络连接
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	public static boolean FLAG = true;// 控制先是网络不可用之后才能在有网络的时候刷新界面（否则直接有网也刷新）
	public static boolean FLAG2 = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			FLAG = false;
//			Toast.makeText(context, "网络不可以用", Toast.LENGTH_LONG).show();
//			context.unbindService(BaseActivity.connection);
//			context.unbindService(BaseActivity.reconnection);
			// 改变背景或者 处理网络的全局变量
		} else {
			// 改变背景或者 处理网络的全局变量
			if (!FLAG) {
				// Toast.makeText(context, "有网了", Toast.LENGTH_LONG).show();
				FLAG = true;
				FLAG2 = false;
				intent.setClass(context, EmptyActivity.class);
				context.startActivity(intent);
			}

		}
	}

}
