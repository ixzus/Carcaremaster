package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.ChooseBankAdapter;
import com.lazycare.carcaremaster.data.ChooseBankClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 选择银行
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ChooseBankActivity extends BaseActivity implements
		OnItemClickListener {
	String TAG = "ChooseBankActivity";
	ListView lv_choosebank;
	private ListView listView;
	private Dialog mDialog;
	private Handler mHandler = new ChooseBankHandler(this);
	ChooseBankAdapter adapter = null;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_choosebank);
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("选择银行卡");

	}

	@Override
	public void initView() {
		adapter = new ChooseBankAdapter(ChooseBankActivity.this);
		listView = (ListView) findViewById(R.id.lv_choosebank);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LoadData();
	}

	private void LoadData() {
		mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
		Map<String, String> map = new HashMap<String, String>();
		// map.put("App_user", ToObjectUtil.BenToJson(app_user));
		TaskExecutor
				.Execute(new DataRunnable(this, "/Banks/get", mHandler, map));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(ChooseBankActivity.this,
					AddBankCardActivity.class);
			intent.putExtra("BANKID", "");
			intent.putExtra("BANKNAME", "");
			setResult(RESULT_OK, intent);
			ChooseBankActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		String bankid = adapter.listBank.get(position).getId();
		String bankname = adapter.listBank.get(position).getName();
		Intent intent = new Intent(ChooseBankActivity.this,
				AddBankCardActivity.class);
		intent.putExtra("BANKID", bankid);
		intent.putExtra("BANKNAME", bankname);
		setResult(RESULT_OK, intent);
		ChooseBankActivity.this.finish();
	}

	/**
	 * <p>
	 * 标 题: ChooseBankHandler
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
	private class ChooseBankHandler extends Handler {

		private WeakReference<ChooseBankActivity> mWeak;

		public ChooseBankHandler(ChooseBankActivity activity) {
			mWeak = new WeakReference<ChooseBankActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ChooseBankActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(ChooseBankActivity activity, int what, String json) {
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
							List<ChooseBankClass> lstChooseBank = gson
									.fromJson(
											data,
											new TypeToken<List<ChooseBankClass>>() {
											}.getType());
							for (ChooseBankClass cbc : lstChooseBank) {
								adapter.addNewItem(cbc);
							}
							listView.setAdapter(adapter);
						} else
							Toast.makeText(ChooseBankActivity.this, msg,
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
