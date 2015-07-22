package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.widget.Toast;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改登录密码
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ModifyPwdFragment extends Fragment implements OnClickListener {
	public static final String TAG = "ModifyPwdFragment";
	private EditText et1, et2, et3;
	private Button btn_certain;
	private Dialog mDialog;
	private String id = "";
	Handler mHandler = new PwdHandler(getActivity());

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
		View view = inflater.inflate(R.layout.fragment_accountinfo_password,
				null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		et1 = (EditText) view.findViewById(R.id.et_account_oldpwd);// 旧密码
		et2 = (EditText) view.findViewById(R.id.et_account_newpwd);// 新密码
		et3 = (EditText) view.findViewById(R.id.et_account_certain);// 确认新密码
		btn_certain = (Button) view.findViewById(R.id.btn_pwd_certain);// 确定
		btn_certain.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		String old = et1.getText().toString().trim(), newp = et2.getText()
				.toString().trim(), newp2 = et3.getText().toString().trim();
		if (et1.getText() == null || et2.getText() == null
				|| et3.getText() == null || old.equals("") || newp.equals("")
				|| newp2.equals("")) {
			Toast.makeText(getActivity(), "输入项不能有空", Toast.LENGTH_SHORT).show();
		} else if (old.equals(newp)) {
			Toast.makeText(getActivity(), "新密码不能和旧密码相同", Toast.LENGTH_SHORT)
					.show();
		} else if (!newp.equals(newp2)) {
			Toast.makeText(getActivity(), "两次输入新密码不一致", Toast.LENGTH_SHORT)
					.show();
		} else {
			postData(old, newp);
		}
	}

	private void postData(String oldPwd, String newPwd) {
		mDialog = CustomProgressDialog.showCancelable(getActivity(), "密码修改中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("oldPassword", oldPwd);
		map.put("newPassword", newPwd);
		TaskExecutor.Execute(new DataRunnable(getActivity(),
				"/Artificer/updatePass", mHandler, map));
	}

	/**
	 * 线程处理返回值
	 * 
	 * @author gmy
	 * 
	 */
	@SuppressLint("HandlerLeak")
	private class PwdHandler extends Handler {

		private WeakReference<Activity> mWeak;

		public PwdHandler(Activity activity) {
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
						Toast.makeText(getActivity(), "密码修改成功，请记好哦",
								Toast.LENGTH_SHORT).show();
						// 保存最新的密码
						getActivity()
								.getSharedPreferences(Configuration.USERINFO, 0)
								.edit()
								.putString(Configuration.PWD,
										et3.getText().toString().trim())
								.commit();
					} else
						Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT)
								.show();
				} catch (Exception e) {
					Log.d(TAG, e.getMessage());
				} finally {
					et1.setText("");
					et2.setText("");
					et3.setText("");
				}
			}
		}
	}
}
