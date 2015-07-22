package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.AppointmentListAdapter;
import com.lazycare.carcaremaster.data.AppointmentClass;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;

/**
 * 评价的任务
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppraiseListActivity extends BaseActivity implements
		View.OnClickListener {
	String TAG = "AppraiseListActivity";
	private int pageIndex = 1;
	int dataSize = 0;
	AppointmentListAdapter adapter = null;
	boolean isLoading = false;
	ListView listView;
	// TextView tv_appraisegood,tv_appraisemiddle,tv_appraiselow;
	Handler mHandler = new LoadAppraiseListHandler(this);
	String id = "";
	String orderType = "book";// order 排序方式, add ： 按下单时间排序 , book : 按预约时间排序 ,
								// 留空默认按下单时间排
	String evaluationType = "1";// 评价方式, 1:好评 2:中评 3:差评
	RadioGroup rg_oper;
	RadioButton rb_ordertimerange, rb_appointtimerange, rb_servicerange;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_appraiselist);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("服务评价");

	}

	@Override
	public void initView() {
		id = getSharePreferences().getString(Configuration.ID, "0");
		listView = (ListView) findViewById(R.id.lv_appraise);
		adapter = new AppointmentListAdapter(AppraiseListActivity.this);
		listView.setAdapter(adapter);
		rg_oper = (RadioGroup) findViewById(R.id.rg_oper);
		rg_oper.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_ordertimerange:
					listView.setVisibility(View.VISIBLE);
					orderType = "add";
					evaluationType = "1";
					adapter.listAppointment.clear();
					listView.setAdapter(adapter);
					loadMoreData();
					break;
				case R.id.rb_appointtimerange:
					listView.setVisibility(View.VISIBLE);
					orderType = "add";
					evaluationType = "2";
					adapter.listAppointment.clear();
					listView.setAdapter(adapter);
					loadMoreData();
					break;
				case R.id.rb_servicerange:
					listView.setVisibility(View.VISIBLE);
					orderType = "add";
					evaluationType = "1";
					adapter.listAppointment.clear();
					listView.setAdapter(adapter);
					loadMoreData();
					break;
				}
			}
		});
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if (totalItemCount == firstVisibleItem + visibleItemCount) {
					if (!isLoading && adapter.getCount() != 0
							&& adapter.getCount() < dataSize) {
						pageIndex++;
						isLoading = true;
						loadMoreData();// 再次加载数据
					}
				}
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(AppraiseListActivity.this,
						AppointmentDetailActivity.class);
				startActivity(intent);
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
			AppraiseListActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 析构数据
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 加载数据
	 */
	private void loadMoreData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
//		map.put("order", orderType);
		map.put("evaluation", evaluationType);
		Log.d(TAG, "id:" + id);
		Log.d(TAG, "orderType:" + orderType);
		Log.d(TAG, "evaluationType:" + evaluationType);
		TaskExecutor.Execute(new DataRunnable(this, "/Order/getOrderList",
				mHandler, map));
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
	private class LoadAppraiseListHandler extends Handler {

		private WeakReference<AppraiseListActivity> mWeak;

		public LoadAppraiseListHandler(AppraiseListActivity activity) {
			mWeak = new WeakReference<AppraiseListActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AppraiseListActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			// DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(AppraiseListActivity activity, int what,
				String json) {
			switch (what) {
			default: // 获取数据返回信息
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						Gson gson = new Gson();
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						isLoading = false;
						if (error.equals("0")) {
							List<AppointmentClass> lstAppointment = gson
									.fromJson(
											data,
											new TypeToken<List<AppointmentClass>>() {
											}.getType());
							for (AppointmentClass ac : lstAppointment) {
								adapter.addNewItem(ac);
							}
							listView.setAdapter(adapter);
						} else
							Toast.makeText(AppraiseListActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_appraisegood:
			break;
		case R.id.tv_appraisemiddle:
			break;
		case R.id.tv_appraiselow:
			break;
		}
	}

}
