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
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.data.ArtificerFilterTimeClass;
import com.lazycare.carcaremaster.data.LoginConfig;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.impl.IBaseActivity;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.service.WorkTimeServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.util.XmppConnectionManager;

/**
 * 登陆界面
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class LoginActivity extends BaseActivity implements OnClickListener {
	/** 控件 */
	private ControlView mCv = new ControlView();
	private Handler mHandler = new LoginHandler(this);
	private LoginConfig loginConfig;

	private class ControlView {
		EditText txtUserName, txtPassword;
		TextView tvForgetPwd;
		Button btnLogin;
	}

	/** 进度条 */
	private Dialog mDialog;
	private String username = "";
	private String id = "";
	private String pwd = "";
	private SysApplication app;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_login);
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle("登录");

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	@Override
	public void initView() {

		app = (SysApplication) getApplication();
		// 从share中拿到id
		id = getSharePreferences().getString(Configuration.ID, "0");
		username = getSharePreferences().getString(Configuration.USERNAME, "");
		pwd = getSharePreferences().getString(Configuration.PWD, "111111");
		mCv.tvForgetPwd = (TextView) findViewById(R.id.tv_forgetpwd);
		mCv.txtUserName = (EditText) findViewById(R.id.txt_username);
		mCv.txtPassword = (EditText) findViewById(R.id.txt_password);
		mCv.btnLogin = (Button) findViewById(R.id.btn_login);
		mCv.txtUserName.setText(username);
		mCv.txtPassword.setText(pwd);
		mCv.btnLogin.setOnClickListener(this);
		mCv.tvForgetPwd.setOnClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_forgetpwd:
			intent.setClass(LoginActivity.this, FindPasswordActivity.class);// 跳转到找回密码中
			startActivity(intent);
			break;
		case R.id.btn_login:
			String strUserName = mCv.txtUserName.getText().toString().trim();
			String strPwd = mCv.txtPassword.getText().toString().trim();

			if (strUserName.equals("")) {
				showToast("用户名不能为空");
			} else if (strPwd.equals("")) {
				showToast("密码不能为空");
			} else {
				if (NetworkUtil.isNetworkAvailable(mContext)) {
					mDialog = CustomProgressDialog.showCancelable(this,
							"登陆中...");
					Map<String, String> map = new HashMap<String, String>();
					map.put("mobile", strUserName);
					map.put("password", strPwd);
					TaskExecutor
							.Execute(new DataRunnable(this,
									"/Artificer/signIn", mHandler,
									Config.WHAT_ONE, map));
				} else {
					showToast("您还没联网哦,亲");
				}
			}

			break;
		}
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
	private class LoginHandler extends Handler {

		private WeakReference<LoginActivity> mWeak;

		public LoginHandler(LoginActivity activity) {
			mWeak = new WeakReference<LoginActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LoginActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(LoginActivity activity, int what, String json) {
			switch (what) {
			case Config.WHAT_ONE: // 验证用户名
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						if (error.equals("0")) {
							JSONObject jd = new JSONObject(data);
							String tid = jd.getString("id");
							String token = jd.getString("token");
							getSharePreferencesEditor()
									.putString(Configuration.ID, tid)
									.putString(
											Configuration.USERNAME,
											mCv.txtUserName.getText()
													.toString().trim())
									.putString(
											Configuration.PWD,
											mCv.txtPassword.getText()
													.toString().trim())
									.putString(Configuration.TOKEN, token)
									.commit();// 保存用户信息
							getServerDate(id);// 获取服务器上的时间
							// showToast("登陆成功");
							// Intent intent = new Intent();
							// intent.setClass(LoginActivity.this,
							// MainActivity.class);
							// startActivity(intent);
							// LoginActivity.this.finish();
							// 登陆IM
							new LoginTask(LoginActivity.this, mCv.txtUserName
									.getText().toString().trim(),
									"111111")
									.execute();
						} else {
							showToast(msg);
							mDialog.dismiss();
						}

					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
						mDialog.dismiss();
					}

				}
				// if( activity.showError(json) )
				// break;
				// Object[] objs = ToObjectUtil.JsonToObj(json, User.class);
				// if( ObjectUtil.isEmpty(objs) )
				// break;
				// User user = (User)objs[0];
				// InfoUtil.setUser(activity, user);
				// InfoUtil.setSect(activity, user.getSect_id(),
				// user.getSect_name(), user.getSect_addr());
				// SysApplication.getInstance().finish();
				// IntentUtil.startActivityFinish(activity, MainActivity.class);
				break;
			case Config.WHAT_TWO:
				Log.d(TAG, json);
				try {
					Gson gson = new Gson();
					if (json != null) {

						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");

						if (error.equals("0")) {
							List<ArtificerFilterTimeClass> lstAFTC = new ArrayList<>();
							lstAFTC = gson
									.fromJson(
											data,
											new TypeToken<List<ArtificerFilterTimeClass>>() {
											}.getType());
							int period=DateUtil.getDatePeriod(
									getSharePreferences().getString(
											Configuration.TIME, "1990-01-01"),
									lstAFTC.get(0).getRealDay());
							if ( period>= 1) {// 相隔1天
								getSharePreferencesEditor().putBoolean(// 需要更新工时管理
										Configuration.TIME_UPDATE, true)
										.commit();
							}
							getSharePreferencesEditor().putString(
									Configuration.TIME,
									lstAFTC.get(0).getRealDay()).commit();
						} else {

						}
					}
				} catch (Exception e) {
					Log.d(TAG, e.getMessage());
					mDialog.dismiss();
				}
				// case Config.WHAT_ONE: // 验证用户名
				// if (!StringUtil.equals(ErrorUtil.SUCCESS, json)
				// && !StringUtil.equals(ErrorUtil.isDataError(json),
				// ErrorUtil.NETWORK_UNCONNECT)
				// && !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT,
				// ErrorUtil.isDataError(json)))
				// activity.mCv.et_name.setError("用户已存在！");
				// break;
				// case Config.WHAT_TWO: // 验证昵称
				// if (!StringUtil.equals(ErrorUtil.SUCCESS, json)
				// && !StringUtil.equals(ErrorUtil.isDataError(json),
				// ErrorUtil.NETWORK_UNCONNECT)
				// && !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT,
				// ErrorUtil.isDataError(json)))
				// activity.mCv.et_nick.setError("昵称已存在！");
				// break;
				// case Config.WHAT_FOUR: // 校验用户信息
				// if (activity.showError(json))
				// break;
				// Intent intent = new Intent(activity, HouAddActivity.class);
				// intent.putExtra("App_user", activity.mAppUser);
				// activity.startActivity(intent);
				// break;
				// case Config.WHAT_THREE: // 校验用户信息
				// activity.showError(json);
				// break;

			}
		}
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
			mDialog.dismiss();
			if (result == Constant.LOGIN_SECCESS) {
				showToast("登陆成功");
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				LoginActivity.this.finish();
				baseactivity.startService(); // 初始化各项服务
			} else {
				showToast("登陆失败");
			}
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

	private void getServerDate(String id) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("artificer", id);
		TaskExecutor.Execute(new DataRunnable(mContext,
				"/ArtificerCalendar/get", mHandler, Config.WHAT_TWO, map));
	}
}
