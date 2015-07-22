package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 找回密码
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class FindPasswordActivity extends BaseActivity implements
		OnClickListener {
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	private EditText et1, et2, et3;
	private Button btn1, btn_certain;
	private Dialog mDialog;
	private String id = "";
	private String username;
	Handler mHandler = new ForgetPwdHandler(this);
	public static String TAG = "FindPasswordActivity";
	private String newp, newp2, captche;
	private String captche_temp = "";// 临时记录验证码
	private int recLen = 60;// 验证码 60s 之内有效
	private SmsObserver smsObserver;// 接收短信的contentprovider
	private String resultData = "";
	private Timer timer;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				btn1.setText("获取验证码(" + recLen + ")");
			} else if (msg.what == 2) {
				btn1.setText("获取验证码");
				btn1.setClickable(true);
				Toast.makeText(FindPasswordActivity.this, "验证码失效",
						Toast.LENGTH_LONG).show();
			} else if (msg.what == 3) {
				if ("000000".equals(resultData)) {
					// 防止重复点击
					btn1.setClickable(false);
					// 定义定时器
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							recLen--;
							Message message = new Message();
							message.obj = recLen;
							message.what = 1;
							handler.sendMessage(message);

							if (recLen < 0) {
								timer.cancel();
								handler.sendEmptyMessage(2);
								recLen = 60;
							}
						}
					}, 1000, 1000);
				} else {
					Toast.makeText(FindPasswordActivity.this, "请求失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
	};

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_findpassword);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("找回密码");

	}

	@Override
	public void initView() {
		id = getSharePreferences().getString(Configuration.ID, "0");
		username = getSharePreferences().getString(Configuration.USERNAME, "1");
		smsObserver = new SmsObserver(this, handler);
		this.getContentResolver().registerContentObserver(SMS_INBOX, true,
				smsObserver);
		et1 = (EditText) this.findViewById(R.id.tv_find_newpwd);// 新密码
		et2 = (EditText) this.findViewById(R.id.tv_find_newpwd_certain);// 确认新密码
		et3 = (EditText) this.findViewById(R.id.tv_find_captche);// 验证码输入框
		btn1 = (Button) this.findViewById(R.id.btn_captche);// 获取验证码按钮
		btn_certain = (Button) this.findViewById(R.id.btn_contact_certain);// 确定
		btn_certain.setOnClickListener(this);
		btn1.setOnClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			FindPasswordActivity.this.finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_contact_certain:
			newp = et1.getText().toString().trim();
			newp2 = et2.getText().toString().trim();
			captche = et3.getText().toString().trim();
			if (et1.getText() == null || et2.getText() == null
					|| et3.getText() == null || newp.equals("")
					|| newp2.equals("") || captche.equals("")) {
				Toast.makeText(FindPasswordActivity.this, "输入项不能有空",
						Toast.LENGTH_SHORT).show();
			} else if (!newp.equals(newp2)) {
				Toast.makeText(FindPasswordActivity.this, "两次输入新密码不一致",
						Toast.LENGTH_SHORT).show();
			} 
//			else if (!captche.equals(captche_temp)) {
//				Toast.makeText(FindPasswordActivity.this, "验证码不正确",
//						Toast.LENGTH_SHORT).show();
//			} 
			else {
				postData(newp, captche);
			}
			break;
		case R.id.btn_captche:
			et3.setText("");
			new MaterialDialog.Builder(this).content(R.string.dialog_findpwd)
					.positiveText(R.string.action_yes)
					.negativeText(R.string.action_no)
					.negativeColorRes(R.color.grey)
					.positiveColorRes(R.color.olivedrab)
					.callback(new ButtonCallback() {
						@Override
						public void onPositive(MaterialDialog dialog) {
							Map<String, String> map;

							if (username != null && !username.equals("")) {
								mDialog = CustomProgressDialog.showCancelable(
										FindPasswordActivity.this, "请求中...");
								captche_temp = generateRandomNum();
								new Thread() {
									public void run() {
										sendSMS(username, captche_temp);
										handler.sendEmptyMessage(3);
									}

									;
								}.start();
							} else {
								Toast.makeText(FindPasswordActivity.this,
										"系统没有您的注册信息", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}).show();

			break;
		default:
			break;
		}
	}

	/**
	 * 形成六位验证码（需要加一天之内不重复功能）
	 * 
	 * @return
	 */
	private String generateRandomNum() {
		Random random = new Random();
		StringBuilder sb = new StringBuilder(6);
		for (int i = 0; i < 6; i++)
			sb.append((char) ('0' + random.nextInt(10)));
		return sb.toString();
	}

	/**
	 * 发送验证码请求
	 * 
	 * @param num
	 * @param content
	 */
	private void sendSMS(String num, String content) {
		HashMap<String, Object> result = null;
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("sandboxapp.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount(NetworkUtil.accountSid, NetworkUtil.authToken);// 初始化主帐号名称和主帐号令牌
		restAPI.setAppId(NetworkUtil.appid);// 初始化应用ID
		result = restAPI.sendTemplateSMS(num, NetworkUtil.templateid,
				new String[] { captche_temp, "1" });
		System.out.println("SDKTestGetSubAccounts result=" + result);
		mDialog.dismiss();
		resultData = (String) result.get("statusCode");
	}

	private void postData(String num, String captche) {
		mDialog = CustomProgressDialog.showCancelable(
				FindPasswordActivity.this, "密码修改中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("safe_pass", num);
		map.put("confirm_ safe_pass", num);
		TaskExecutor.Execute(new DataRunnable(FindPasswordActivity.this,
				"/Artificer/setSafePass", mHandler, map));
	}

	@SuppressLint("HandlerLeak")
	private class ForgetPwdHandler extends Handler {

		private WeakReference<Activity> mWeak;

		public ForgetPwdHandler(Activity activity) {
			mWeak = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Activity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(Activity activity, int what, String json) {

			Log.d(TAG, json);
			if (!CommonUtil.isEmpty(json)) {
				try {
					JSONObject jb = new JSONObject(json);
					String error = jb.getString("error");
					String msg = jb.getString("msg");
					String data = jb.getString("data");
					if (error.equals("0")) {
						Toast.makeText(FindPasswordActivity.this,
								"密码修改成功，请记好哦", Toast.LENGTH_SHORT).show();
					} else
						Toast.makeText(FindPasswordActivity.this, msg,
								Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Log.d(TAG, e.getMessage());
				} finally {
					et1.setText("");
					et2.setText("");
					et3.setText("");
					btn1.setClickable(true);
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		FindPasswordActivity.this.getContentResolver()
				.unregisterContentObserver(smsObserver);
	}

	class SmsObserver extends ContentObserver {

		public SmsObserver(Context context, Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// 每当有新短信到来时，使用我们获取短消息的方法
			getSmsFromPhone();
		}

	}

	public void getSmsFromPhone() {
		ContentResolver cr = FindPasswordActivity.this.getContentResolver();
		String[] projection = new String[] { "body", "address", "person" };// "_id",
																			// "address",
		// "person",, "date",
		// "type
		String where = " 1=1";
		// " AND date >  "
		// + (System.currentTimeMillis() - 10 * 60 * 1000);
		Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
		if (null == cur)
			return;
		if (cur.moveToNext()) {
			String number = cur.getString(cur.getColumnIndex("address"));// 手机号
			String name = cur.getString(cur.getColumnIndex("person"));// 联系人姓名列表
			String body = cur.getString(cur.getColumnIndex("body"));
			if (body.contains("【触动养车】")) {
				// 这里我是要获取自己短信服务号码中的验证码~~
				Log.i("otu", body);
				Pattern pattern = Pattern.compile("[0-9]{6}");
				Matcher matcher = pattern.matcher(body);
				if (matcher.find()) {
					String res = matcher.group();
					et3.setText(res);
					timer.cancel();
					btn1.setText("获取验证码");
				}
			}

		}
	}

}
