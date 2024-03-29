package com.lazycare.carcaremaster.fragment;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.cloopen.rest.sdk.CCPRestSDK;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 找回支付密码
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ForgetPayPwdFragment extends Fragment implements OnClickListener {
	private LinearLayout llMain;
	private Uri SMS_INBOX = Uri.parse("content://sms/");
	private EditText et1, et2, et3;
	private Button btn1, btn_certain;
	private Dialog mDialog;
	private String id = "";
	Handler mHandler = new ForgetPwdHandler(getActivity());
	public static String TAG = "ModifyPhoneFragment";
	private String newp, newp2, captche;
	private String captche_temp = "";
	private int recLen = 60;
	private SmsObserver smsObserver;
	private String resultData = "";
	private Timer timer;
	private String username;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				btn1.setText("获取验证码(" + recLen + ")");
			} else if (msg.what == 2) {
				btn1.setText("获取验证码");
				btn1.setClickable(true);
				CommonUtil.showSnack(llMain, "验证码失效");
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
					CommonUtil.showSnack(llMain,"请求失败");
					// 测试发送短信
					// SmsManager smsManager = SmsManager.getDefault();
					// smsManager.sendTextMessage("5556", null,
					// "【云通讯】您使用的是云通讯短信模板，您的验证码是694213，请于1分钟内正确输入", null,
					// null);
				}
			}
		};
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(TAG);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		id = getArguments().getString("id");
		View view = inflater.inflate(R.layout.fragment_forgetpaypwd, null);
		initView(view);
		smsObserver = new SmsObserver(getActivity(), handler);
		username = getActivity()
				.getSharedPreferences(Config.USERINFO, 0).getString(
						Config.USERNAME, "");
		getActivity().getContentResolver().registerContentObserver(SMS_INBOX,
				true, smsObserver);
		return view;
	}

	private void initView(View view) {
		llMain=(LinearLayout)view.findViewById(R.id.ll_forgetpwd);
		et1 = (EditText) view.findViewById(R.id.tv_forget_newpwd);// 新密码
		et2 = (EditText) view.findViewById(R.id.tv_forget_newpwd_certain);// 确认新密码
		et3 = (EditText) view.findViewById(R.id.tv_forget_captche);// 验证码输入框
		btn1 = (Button) view.findViewById(R.id.btn_captche);// 获取验证码按钮
		btn_certain = (Button) view.findViewById(R.id.btn_contact_certain);// 确定
		btn_certain.setOnClickListener(this);
		btn1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_contact_certain:
			newp = et1.getText().toString().trim();
			newp2 = et2.getText().toString().trim();
			captche = et3.getText().toString().trim();
			if (et1.getText() == null || et2.getText() == null || et3.getText() == null || newp.equals("") || newp2.equals("") || captche.equals("")) {
				CommonUtil.showSnack(llMain,"输入不能有空");
			} else if (!newp.equals(newp2)) {
				CommonUtil.showSnack(llMain, "两次输入新号码不一致");
			} else if (!captche.equals(captche_temp)) {
				CommonUtil.showSnack(llMain, "验证码不正确");
			} else {
				postData(newp, captche);
			}
			break;
		case R.id.btn_captche:
			et3.setText("");
			new MaterialDialog.Builder(getActivity())
					.content(R.string.dialog_findpwd)
					.positiveText(R.string.action_yes)
					.negativeText(R.string.action_no)
					.negativeColorRes(R.color.grey)
					.positiveColorRes(R.color.olivedrab)
					.callback(new ButtonCallback() {
						@Override
						public void onPositive(MaterialDialog dialog) {
							if (username != null && !username.equals("")) {
								mDialog = CustomProgressDialog.showCancelable(getActivity(), "请求中...");
								captche_temp = generateRandomNum();
								new Thread() {
									public void run() {
										sendSMS(username, captche_temp);
										handler.sendEmptyMessage(3);
									}

									;
								}.start();
							} else {
								CommonUtil.showSnack(llMain, "请输入手机号");
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
		mDialog = CustomProgressDialog.showCancelable(getActivity(), "密码修改中...");
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("safe_pass", num);
		map.put("confirm_ safe_pass", num);
		TaskExecutor.Execute(new DataRunnable(getActivity(), "/Artificer/setSafePass", mHandler, map));
	}

	@SuppressLint("HandlerLeak")
	private class ForgetPwdHandler extends Handler {

		private WeakReference<Activity> mWeak;

		public ForgetPwdHandler(Activity activity) {
			mWeak = new WeakReference<>(activity);
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
						CommonUtil.showSnack(llMain, "支付密码修改成功，请牢记哒");
					} else
						CommonUtil.showSnack(llMain, msg);
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
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().getContentResolver().unregisterContentObserver(
				smsObserver);
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
		ContentResolver cr = getActivity().getContentResolver();
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
