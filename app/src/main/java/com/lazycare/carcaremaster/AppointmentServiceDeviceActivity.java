package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.AppointmentServiceDeviceAdapter;
import com.lazycare.carcaremaster.data.Goods;
import com.lazycare.carcaremaster.data.Service;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 预约服务配件详情
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentServiceDeviceActivity extends BaseActivity {
	private AppointmentServiceDeviceAdapter adapter;
	private List<Goods> goods;
	private ListView listview;
	private String serviceID = "";
	Handler mHandler = new LoadDeviceHandler(this);
	private TextView nodata;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_appointmentservicedevice);
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("配件详情");
	}

	@Override
	public void initView() {
		serviceID = getIntent().getStringExtra("serviceid");
		listview = (ListView) findViewById(R.id.list_appointservicedevice);
		nodata = (TextView) findViewById(R.id.nodatadevice);
		mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("service", serviceID);
		TaskExecutor.Execute(new DataRunnable(this, "/OrderGoods/getByService",
				mHandler, map));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			AppointmentServiceDeviceActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@SuppressLint("HandlerLeak")
	private class LoadDeviceHandler extends Handler {

		private WeakReference<AppointmentServiceDeviceActivity> mWeak;

		public LoadDeviceHandler(AppointmentServiceDeviceActivity activity) {
			mWeak = new WeakReference<AppointmentServiceDeviceActivity>(
					activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AppointmentServiceDeviceActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(AppointmentServiceDeviceActivity activity,
				int what, String json) {

			// 获取第一次数据返回信息
			Log.d(TAG, json);
			if (!CommonUtil.isEmpty(json)) {
				try {
					Gson gson = new Gson();
					JSONObject jb = new JSONObject(json);
					String error = jb.getString("error");
					String msg = jb.getString("msg");
					String data = jb.getString("data");

					if (error.equals("0")) {
						goods = gson.fromJson(data,
								new TypeToken<List<Goods>>() {
								}.getType());
						if (goods.size() == 0) {
							nodata.setVisibility(View.VISIBLE);
							listview.setVisibility(View.GONE);
						} else {
							adapter = new AppointmentServiceDeviceAdapter(
									goods, mContext);
							listview.setAdapter(adapter);
						}

					} else
						showToast(msg);
				} catch (Exception e) {
					Log.d(TAG, e.getMessage());
				}

			}
		}
	}
}
