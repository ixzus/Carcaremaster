package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.widget.TextView;
import android.widget.Toast;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.thread.UpdateManager;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 更新界面
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class UpdateFragment extends Fragment {
	private Dialog dialog;
	private Handler mHandler = new UpdateHandler(getActivity());
	public static final String TAG = "UpdateFragment";
	private TextView versioncode;

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
		View view = inflater.inflate(R.layout.fragment_update, null);
		Button update = (Button) view.findViewById(R.id.btn_up);
		versioncode = (TextView) view.findViewById(R.id.versioncode);
		update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadData();
			}
		});
		try {
			versioncode.setText("版本号: v"
					+ getActivity().getPackageManager().getPackageInfo(
							"com.lazycare.carcaremaster", 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

	}

	private void loadData() {
		dialog = CustomProgressDialog.show(getActivity(), "检查更新...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("client", "android");
		TaskExecutor.Execute(new DataRunnable(getActivity(), "/System/update",
				mHandler, map));
	}

	@SuppressLint("HandlerLeak")
	private class UpdateHandler extends Handler {

		private WeakReference<Activity> mWeak;

		public UpdateHandler(Activity activity) {
			mWeak = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Activity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(dialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(Activity activity, int what, String json) {
			switch (what) {
			default: // 返回信息
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						if (error.equals("0")) {
							JSONObject jd = new JSONObject(data);
							String versionCode = jd.getString("copyright");
							String url = jd.getString("download");
							if (versionCode != null && !versionCode.equals("")) {
								UpdateManager manager = new UpdateManager(
										getActivity(), url);
								// 检查软件更新(用户主动检查更新)
								manager.checkUpdate(versionCode, Config.WHAT_ONE);
							}
						} else
							Toast.makeText(getActivity(), msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
		}
	}
}
