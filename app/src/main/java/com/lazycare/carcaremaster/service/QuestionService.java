package com.lazycare.carcaremaster.service;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.QuestionDetailWithReplyActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 后台轮询new question 的service
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionService extends Service implements Runnable {

	private Thread mThread;
	public int count = 0;
	private boolean isTip = true;
	// private static String mRestMsg;
	private static String KEY_REST_MSG = "KEY_REST_MSG";
	Handler mHandler = new LoadQuestionsHandler(this);
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	private static int ids_at;// 用于存放所有新@我问题的id;
	private static int ids_pub;

	@Override
	public void run() {
		while (true) {
			try {
				if (count > 1) {
					count = 1;
					// 应用终止时就停止service
					if (isTip) {
						// 判断应用是否在运行
						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
						List<RunningTaskInfo> list = am.getRunningTasks(100);
						for (RunningTaskInfo info : list) {
							if (info.topActivity.getPackageName().equals(
									"com.lazycare.carcaremaster.MainActivity")) {
								// 通知应用，显示提示“连接不到服务器”
								Intent intent = new Intent(
										"com.lazycare.carcaremaster.MainActivity");
								sendBroadcast(intent);
								break;
							}
						}
						isTip = false;
					}
				}
				// if (mRestMsg != "" && mRestMsg != null) {
				// 向服务器发送心跳包
				// sendHeartbeatPackage(mRestMsg);
				// loadMoreData();
				// loadMoreData2();
				count += 1;
				// }
				// 10s每次
				Thread.sleep(1000 * 10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendHeartbeatPackage(String msg) {
		HttpGet httpGet = new HttpGet(msg);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// 发送请求
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (httpResponse == null) {
			return;
		}

		// 处理返回结果
		final int responseCode = httpResponse.getStatusLine().getStatusCode();
		if (responseCode == HttpStatus.SC_OK) {
			// 只要服务器有回应就OK
			count = 0;
			isTip = true;
		} else {
			Log.i("@qi", "responseCode " + responseCode);
		}

	}

	private void loadMoreData() {
		if (NetworkUtil.isNetworkAvailable(this)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", this.getSharedPreferences(Configuration.USERINFO, 0)
					.getString(Configuration.ID, "0"));
			map.put("start", "0");
			map.put("limit", "10");
			map.put("type", "1");
			TaskExecutor.Execute(new DataRunnable(this,
					"/Questions/getQuestionsList", mHandler, Config.WHAT_ONE,
					map));
		}

	}

	private void loadMoreData2() {
		if (NetworkUtil.isNetworkAvailable(this)) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", this.getSharedPreferences(Configuration.USERINFO, 0)
					.getString(Configuration.ID, "0"));
			map.put("start", "0");
			map.put("limit", "10");
			map.put("type", "0");
			TaskExecutor.Execute(new DataRunnable(this,
					"/Questions/getQuestionsList", mHandler, Config.WHAT_TWO,
					map));
		}

	}

	// private void showUpNotification(int count, String content) {
	// CharSequence tickerText = "新问题";
	// mNotification = new Notification(R.drawable.ic_launcher, tickerText,
	// System.currentTimeMillis());
	//
	// // 放置在"正在运行"栏目中
	// mNotification.flags = Notification.FLAG_ONGOING_EVENT;
	// RemoteViews contentView = new RemoteViews(this.getPackageName(),
	// R.layout.notification_download);
	// contentView.setTextViewText(R.id.name, "有" + count + "条新的问题来啦...");
	// contentView.setTextViewText(R.id.tv_progress, content);
	// // 指定个性化视图
	// mNotification.contentView = contentView;
	//
	// Intent intent = new Intent(this, QuestionsListActivity.class);
	// // 下面两句是 在按home后，点击通知栏，返回之前activity 状态;
	// // 有下面两句的话，假如service还在后台下载， 在点击程序图片重新进入程序时，直接到下载界面，相当于把程序MAIN 入口改了 - -
	// // 是这么理解么。。。
	// // intent.setAction(Intent.ACTION_MAIN);
	// // intent.addCategory(Intent.CATEGORY_LAUNCHER);
	// PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	// intent, PendingIntent.FLAG_UPDATE_CURRENT);
	//
	// // 指定内容意图
	// mNotification.contentIntent = contentIntent;
	// mNotificationManager.notify(0, mNotification);
	// }

	void showAppNotification(int count, String strfrom, String strmessage,
			String id, String type) {
		// look up the notification manager service
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// The details of our fake message
		CharSequence from;
		if (type.equals("1")) {
			from = strfrom + "   (" + count + ")  条新@我问题";// 车主姓名
		} else {
			from = strfrom + "   (" + count + ")  条新抢单问题";// 车主姓名
		}

		CharSequence message = strmessage;// 问题内容

		Intent intent = new Intent(this, QuestionDetailWithReplyActivity.class);
		intent.putExtra("question_id", id);
		intent.putExtra("type", type);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);
		String tickerText = getString(R.string.imcoming_message_ticker_text,
				message);

		mNotification = new Notification(R.drawable.ic_launcher, tickerText,
				System.currentTimeMillis());

		mNotification.setLatestEventInfo(this, from, message, contentIntent);

		mNotification.defaults = Notification.DEFAULT_ALL;
		mNotification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotificationManager.notify(R.string.imcoming_message_ticker_text,
				mNotification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void onStart(Intent intent, int startId) {
		// 从本地读取服务器的URL，如果没有就用传进来的URL
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mThread = new Thread(this);
		mThread.start();
		count = 0;

		super.onStart(intent, startId);
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
	 * @version
	 */
	@SuppressLint("HandlerLeak")
	private class LoadQuestionsHandler extends Handler {

		private WeakReference<QuestionService> mWeak;

		public LoadQuestionsHandler(QuestionService activity) {
			mWeak = new WeakReference<QuestionService>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			QuestionService activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);

		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(QuestionService activity, int what, String json) {
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
							count = 0;
							isTip = true;
							JSONObject jd = new JSONObject(data);
							String count = jd.getString("count");
							String list = jd.getString("list");
							JSONObject jcount = new JSONObject(count);
							String my = jcount.getString("my");
							String complate = jcount.getString("complate");
							String pub = jcount.getString("pub");
							int t = 0;
							if (!my.equals("0")) {
								// showUpNotification(Integer.parseInt(my),
								// "发动机异响");
								List<QuestionClass> lstQuestions = gson
										.fromJson(
												list,
												new TypeToken<List<QuestionClass>>() {
												}.getType());
								// 第一次进入
								if (ids_at == 0) {
									showAppNotification(Integer.parseInt(my),
											lstQuestions.get(0).getMobile(),
											lstQuestions.get(0).getCar(),
											lstQuestions.get(0).getId(), "1");
								} else {
									Log.d("gmyboy", "ids" + ids_at + "\n"
											+ Integer.parseInt(my));
									if (Integer.parseInt(my) > ids_at) {
										showAppNotification(
												Integer.parseInt(my) - ids_at,
												lstQuestions.get(0).getMobile(),
												lstQuestions.get(0).getCar(),
												lstQuestions.get(0).getId(),
												"1");
									}
								}
								ids_at = Integer.parseInt(my);
							}
						} else {
						}
					} catch (Exception e) {
					}

				}
				break;
			case Config.WHAT_TWO:
				if (!CommonUtil.isEmpty(json)) {
					try {
						Gson gson = new Gson();
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						if (error.equals("0")) {
							count = 0;
							isTip = true;
							JSONObject jd = new JSONObject(data);
							String count = jd.getString("count");
							String list = jd.getString("list");
							JSONObject jcount = new JSONObject(count);
							String my = jcount.getString("my");
							String complate = jcount.getString("complate");
							String pub = jcount.getString("pub");

							if (!pub.equals("0")) {
								// showUpNotification(Integer.parseInt(my),
								// "发动机异响");
								List<QuestionClass> lstQuestions = gson
										.fromJson(
												list,
												new TypeToken<List<QuestionClass>>() {
												}.getType());
								// 第一次进入
								if (ids_pub == 0) {
									showAppNotification(Integer.parseInt(pub),
											lstQuestions.get(0).getMobile(),
											lstQuestions.get(0).getCar(),
											lstQuestions.get(0).getId(), "0");
								} else {
									Log.d("gmyboy", "ids_pub" + ids_pub + "\n"
											+ Integer.parseInt(pub));
									if (Integer.parseInt(pub) > ids_pub) {
										showAppNotification(
												Integer.parseInt(pub) - ids_pub,
												lstQuestions.get(0).getMobile(),
												lstQuestions.get(0).getCar(),
												lstQuestions.get(0).getId(),
												"0");
									}
								}
								ids_pub = Integer.parseInt(pub);

							}
						} else {
						}
					} catch (Exception e) {
					}

				}
				break;
			default: // 获取数据返回信息

				break;
			}
		}
	}

}
