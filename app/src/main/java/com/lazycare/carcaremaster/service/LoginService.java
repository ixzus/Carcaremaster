package com.lazycare.carcaremaster.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lazycare.carcaremaster.EmptyActivity;

/**
 * 登录服务
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class LoginService extends Service {

	// private MyThread myThread;
	private NotificationManager manager;
	private Notification notification;
	private PendingIntent pi;
	// private AsyncHttpClient client;
	private boolean flag = true;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println("oncreate()");
		// this.client = new AsyncHttpClient();
		// this.myThread = new MyThread();
		// this.myThread.start();
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		this.flag = false;
		super.onDestroy();
	}

	private void notification(String content, String number, String date) {
		// 获取系统的通知管理器
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// notification = new Notification(R.drawable.ic_menu_compose, content,
		// System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL; // 使用默认设置，比如铃声、震动、闪灯
		notification.flags = Notification.FLAG_AUTO_CANCEL; // 但用户点击消息后，消息自动在通知栏自动消失
		notification.flags |= Notification.FLAG_NO_CLEAR;// 点击通知栏的删除，消息不会依然不会被删除

		Intent intent = new Intent(getApplicationContext(), EmptyActivity.class);
		intent.putExtra("content", content);
		intent.putExtra("number", number);
		intent.putExtra("date", date);

		pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

		notification.setLatestEventInfo(getApplicationContext(), number
				+ "发来短信", content, pi);

		// 将消息推送到状态栏
		manager.notify(0, notification);

	}

	// private class MyThread extends Thread {
	// @Override
	// public void run() {
	// String url = "http://110.65.99.66:8080/jerry/PushSmsServlet";
	// while (flag) {
	// System.out.println("发送请求");
	// try {
	// // 每个10秒向服务器发送一次请求
	// Thread.sleep(10000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// // 采用get方式向服务器发送请求
	// client.get(url, new AsyncHttpResponseHandler() {
	// @Override
	// public void onSuccess(int statusCode, Header[] headers,
	// byte[] responseBody) {
	// try {
	// JSONObject result = new JSONObject(new String(
	// responseBody, "utf-8"));
	// int state = result.getInt("state");
	// // 假设偶数为未读消息
	// if (state % 2 == 0) {
	// String content = result.getString("content");
	// String date = result.getString("date");
	// String number = result.getString("number");
	// notification(content, number, date);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Override
	// public void onFailure(int statusCode, Header[] headers,
	// byte[] responseBody, Throwable error) {
	// Toast.makeText(getApplicationContext(), "数据请求失败", 0)
	// .show();
	// }
	// });
	//
	// }
	// }
	// }
}
