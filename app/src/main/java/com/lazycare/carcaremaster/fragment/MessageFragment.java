package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.AppointmentDetailActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.AppointmentListAdapter;
import com.lazycare.carcaremaster.adapter.SysMessageAdapter;
import com.lazycare.carcaremaster.data.SysMessage;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.thread.UpdateManager;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;

/**
 * 问题列表
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MessageFragment extends BaseFragment {
	SysMessageAdapter adapter = null;
	private ListView listView;
	/** 进度条 */
	private Dialog mDialog;
	Handler mHandler = new DataLoadHandler(getActivity());
	private View view;
	private int mCurIndex = -1;
	boolean isLoading = false;
	static String id = "";
	/** 标志位，标志已经初始化完成 */
	private boolean isPrepared;
	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean mHasLoadedOnce;
	int dataSize = 0;

	public static MessageFragment newInstance(int position, String sid) {
		MessageFragment f = new MessageFragment();
		Bundle b = new Bundle();
		b.putInt("POSITION", position);
		f.setArguments(b);
		id=sid;
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			mCurIndex = getArguments().getInt("POSITION");

			switch (mCurIndex) {
			case 0:
				view = inflater.inflate(R.layout.fragment_messagesread, null);

				adapter = new SysMessageAdapter(getActivity());
				listView = (ListView) view.findViewById(R.id.lv_questions);
				listView.setAdapter(adapter);
				isPrepared = true;
				lazyLoad();
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						final int posi = ((SysMessage) adapter
								.getItem(position)).getId();
						new MaterialDialog.Builder(getActivity())
								.content(R.string.dialog_deletemessage)
								.positiveText(R.string.action_yes)
								.negativeText(R.string.action_no)
								.callback(new ButtonCallback() {
									@Override
									public void onPositive(MaterialDialog dialog) {
										mDialog = CustomProgressDialog
												.showCancelable(getActivity(),
														"删除消息中...");
										Map<String, String> map;
										map = new HashMap<String, String>();
										map.put("id", posi + "");
										Message message = new Message();
										message.what = 1;
										mHandler.sendMessage(message);
										TaskExecutor.Execute(new DataRunnable(
												getActivity(),
												"/PushGet/delete", mHandler,
												map));
									}
								}).show();

						return false;
					}
				});
				break;
			case 1:
				view = inflater.inflate(R.layout.fragment_messagesread, null);

				adapter = new SysMessageAdapter(getActivity());
				listView = (ListView) view.findViewById(R.id.lv_questions);
				listView.setAdapter(adapter);
				isPrepared = true;
				lazyLoad();
				listView.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						final int posi = ((SysMessage) adapter
								.getItem(position)).getId();
						new MaterialDialog.Builder(getActivity())
								.content(R.string.dialog_deletemessage)
								.positiveText(R.string.action_yes)
								.negativeText(R.string.action_no)
								.callback(new ButtonCallback() {
									@Override
									public void onPositive(MaterialDialog dialog) {
										mDialog = CustomProgressDialog
												.showCancelable(getActivity(),
														"删除消息中...");
										Map<String, String> map;
										map = new HashMap<String, String>();
										map.put("id", posi + "");
										TaskExecutor.Execute(new DataRunnable(
												getActivity(),
												"/PushGet/delete", mHandler,
												Config.WHAT_TWO, map));
									}
								}).show();

						return false;
					}
				});
				break;
			case 2:
				view = inflater.inflate(R.layout.fragment_update, null);
				Button update = (Button) view.findViewById(R.id.btn_up);
				TextView versioncode = (TextView) view
						.findViewById(R.id.versioncode);
				update.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						checkUpdate();
					}

				});
				try {
					versioncode
							.setText("版本号: v"
									+ getActivity()
											.getPackageManager()
											.getPackageInfo(
													"com.lazycare.carcaremaster",
													0).versionName);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				break;
			}

		}

		// 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void checkUpdate() {
		mDialog = CustomProgressDialog.show(getActivity(), "检查更新...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("client", "android");
		TaskExecutor.Execute(new DataRunnable(getActivity(), "/System/update",
				mHandler, Config.WHAT_THREE, map));

	}

	private class DataLoadHandler extends Handler {

		private WeakReference<Activity> mWeak;

		public DataLoadHandler(Activity activity) {
			mWeak = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Activity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			mDialog.dismiss();
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(Activity activity, int what, String json) {
			switch (what) {
			case Config.WHAT_TWO: {// 删除消息
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						// String data = jb.getString("data");

						if (error.equals("0")) {
							Map<String, String> map = new HashMap<String, String>();
							map.put("artificer_id", id);
							Message msgg = new Message();
							msgg.what = 0;
							mHandler.sendMessage(msgg);
							TaskExecutor.Execute(new DataRunnable(
									getActivity(), "/PushGet/getList",
									mHandler, map));

						} else
							Toast.makeText(getActivity(), msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}

				}
				break;
			}
			case Config.WHAT_ONE: {// 获取消息
				if (!CommonUtil.isEmpty(json)) {
					try {
						Gson gson = new Gson();
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");

						if (error.equals("0")) {
							// JSONObject jd = new JSONObject(data);
							List<SysMessage> lstQuestion = gson.fromJson(data,
									new TypeToken<List<SysMessage>>() {
									}.getType());
							for (SysMessage qc : lstQuestion) {
								adapter.addNewItem(qc);
							}
							mHasLoadedOnce = true;
							adapter.notifyDataSetChanged();

						} else
							Toast.makeText(getActivity(), msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
			case Config.WHAT_THREE:
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
								// 检查软件更新
								manager.checkUpdate(versionCode,Config.WHAT_ONE);
							}
						} else
							Toast.makeText(getActivity(), msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}
				}
				break;
			}

		}
	}

	@Override
	protected void lazyLoad() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		mDialog = CustomProgressDialog.show(getActivity(), "加载中...");

		Map<String, String> map = new HashMap<String, String>();
		map.put("artificer_id", id);
		Message message = new Message();
		message.what = 0;
		mHandler.sendMessage(message);
		TaskExecutor.Execute(new DataRunnable(getActivity(),
				"/PushGet/getList", mHandler, Config.WHAT_ONE, map));
	}

}
