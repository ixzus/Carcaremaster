package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.BankAdapter;
import com.lazycare.carcaremaster.data.BankClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 我的银行卡列表
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class BankListActivity extends BaseActivity {
	private ListView listView;
	BankAdapter adapter = null;
	RelativeLayout rlAddBank;
	Handler mHandler = new BankListHandler(this);
	private Intent intent;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_banklist);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("我的银行卡");

	}

	@Override
	public void initView() {
		intent = getIntent();
		rlAddBank = (RelativeLayout) findViewById(R.id.rl_addbank);
		rlAddBank.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(BankListActivity.this,
						AddBankCardActivity.class);
				startActivity(intent);
			}
		});
		listView = (ListView) findViewById(R.id.lv_banks);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent();
				// intent.setClass(BankListActivity.this,
				// BankCardDetailActivity.class);
				// intent.putExtra("id",
				// adapter.listBank.get(position).getId());
				// startActivity(intent);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("bank",
						(BankClass) adapter.getItem(position));
				intent.putExtras(mBundle);
				setResult(Activity.RESULT_OK, intent);
				finish();

			}
		});
		// listView.setTextFilterEnabled(true);
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
			BankListActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadMoreData() {
		mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		TaskExecutor.Execute(new DataRunnable(this, "/ArtificerPayee/getList",
				mHandler, map));
	}

	/**
	 * <p>
	 * 标 题: AddBankCardHandler
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
	private class BankListHandler extends Handler {

		private WeakReference<BankListActivity> mWeak;

		public BankListHandler(BankListActivity activity) {
			mWeak = new WeakReference<BankListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			BankListActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(BankListActivity activity, int what, String json) {
			switch (what) {
			default: // 登录返回信息
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						Gson gson = new Gson();
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						if (error.equals("0")) {
							List<BankClass> lstBank = gson.fromJson(data,
									new TypeToken<List<BankClass>>() {
									}.getType());
							for (BankClass bc : lstBank) {
								adapter.addNewItem(bc);
							}
							listView.setAdapter(adapter);
						} else
							Toast.makeText(BankListActivity.this, msg,
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
