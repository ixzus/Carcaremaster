package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 技师银行卡详情,暂时不需要这个页面
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class BankCardDetailActivity extends BaseActivity {
	String TAG = "BankCardDetailActivity";
	/** 进度条 */
	private Dialog mDialog;
	String id = "";
	Handler mHandler = new LoadBankCardDetailHandler(this);

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_appointmentdetail);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("银行卡详情");

	}

	@Override
	public void initView() {
		id = getIntent().getStringExtra("id");

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
			BankCardDetailActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void loadMoreData() {
		mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		TaskExecutor.Execute(new DataRunnable(this, "/ArtificerPayee/get",
				mHandler, Config.WHAT_ONE, map));
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
	private class LoadBankCardDetailHandler extends Handler {

		private WeakReference<BankCardDetailActivity> mWeak;

		public LoadBankCardDetailHandler(BankCardDetailActivity activity) {
			mWeak = new WeakReference<BankCardDetailActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			BankCardDetailActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(BankCardDetailActivity activity, int what,
				String json) {
			switch (what) {
			case Config.WHAT_TWO:
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						// isLoading = false;
						if (error.equals("0")) {

						} else
							Toast.makeText(BankCardDetailActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}

				}
				break;
			case Config.WHAT_ONE:
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						// isLoading = false;
						if (error.equals("0")) {
						} else
							Toast.makeText(BankCardDetailActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}

				}
				break;
			default: // 获取数据返回信息

				break;
			}
		}
	}

}
