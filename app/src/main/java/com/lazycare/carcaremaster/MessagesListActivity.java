package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.SysMessageAdapter;
import com.lazycare.carcaremaster.data.SysMessage;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 系统消息列表页
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MessagesListActivity extends BaseActivity {
	private ListView listView;
	List<SysMessage> lstQuestions = new ArrayList<SysMessage>();
	int dataSize = 0;
	SysMessageAdapter adapter = null;
	boolean isLoading = false;
	String id = "";

	Handler mHandler = new DataLoadHandler(this);
	private Dialog mDialog;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_messageslist);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("系统消息");

	}

	@Override
	public void initView() {
		id = getSharePreferences().getString(Configuration.ID, "0");
		adapter = new SysMessageAdapter(MessagesListActivity.this);
		listView = (ListView) findViewById(R.id.lv_questions);
		listView.setAdapter(adapter);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				DialogListener l = new DialogListener(((SysMessage) adapter
						.getItem(position)).getId());
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MessagesListActivity.this);
				builder.setTitle("提醒");
				builder.setMessage("确定要删除该条消息吗？删除之后将不能恢复！！！");
				builder.setPositiveButton("确定", l);
				builder.setNeutralButton("取消", l);
				AlertDialog ad = builder.create();
				ad.show();
				return false;
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		loadMoreData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			MessagesListActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DialogListener implements DialogInterface.OnClickListener {
		private int position;

		public DialogListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				Map<String, String> map;

				mDialog = CustomProgressDialog.showCancelable(
						MessagesListActivity.this, "正在删除消息...");
				map = new HashMap<String, String>();
				map.put("id", position + "");
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
				TaskExecutor.Execute(new DataRunnable(
						MessagesListActivity.this, "/PushGet/delete", mHandler,
						map));

			} else if (which == DialogInterface.BUTTON_NEGATIVE) {

			}
		}

	}

	private void loadMoreData() {
		mDialog = CustomProgressDialog.showCancelable(this, "正在加载数据...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("artificer_id", id);
		Message message = new Message();
		message.what = 0;
		mHandler.sendMessage(message);
		TaskExecutor.Execute(new DataRunnable(this, "/PushGet/getList",
				mHandler, map));
	}

	private class DataLoadHandler extends Handler {

		private WeakReference<MessagesListActivity> mWeak;

		public DataLoadHandler(MessagesListActivity activity) {
			mWeak = new WeakReference<MessagesListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			MessagesListActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(MessagesListActivity activity, int what,
				String json) {
			switch (what) {
			case 1: {// 删除消息
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
									MessagesListActivity.this,
									"/PushGet/getList", mHandler, map));

						} else
							Toast.makeText(MessagesListActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
					}

				}
				break;
			}
			case 0: {// 获取消息
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
							listView.setAdapter(adapter);

						} else
							Toast.makeText(MessagesListActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
			}

		}
	}

}
