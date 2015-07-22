package com.lazycare.carcaremaster;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.AppointmentServiceAdapter;
import com.lazycare.carcaremaster.data.Service;

/**
 * 预约服务详情
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentServiceDetailActivity extends BaseActivity {
	private AppointmentServiceAdapter adapter;
	private List<Service> services;
	private ListView listview;
	private TextView nodata;
	@Override
	public void setLayout() {
		setContentView(R.layout.activity_appointmentservicedetail);
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("服务详情");
	}

	@Override
	public void initView() {
		listview = (ListView) findViewById(R.id.list_appointservice);
		nodata = (TextView) findViewById(R.id.nodata);
		Gson gson = new Gson();
		services = gson.fromJson(getIntent().getStringExtra("service"),
				new TypeToken<List<Service>>() {
				}.getType());
		if (services.size()==0) {
			nodata.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
		}else {
			adapter = new AppointmentServiceAdapter(services, mContext);
			listview.setAdapter(adapter);
		}
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(mContext,
						AppointmentServiceDeviceActivity.class);
				intent.putExtra("serviceid",
						String.valueOf(services.get(position).getId()));
				startActivity(intent);
			}
		});
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
			AppointmentServiceDetailActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
