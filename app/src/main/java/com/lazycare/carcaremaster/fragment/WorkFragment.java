package com.lazycare.carcaremaster.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.WorkHoursActivity;
import com.lazycare.carcaremaster.adapter.WorkHourAdapter;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 工时管理界面
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class WorkFragment extends Fragment {
	private int key = 0;
	private GridView gridView;
	public static final String TAG = "WorkFragment";
	List<Map<Integer, Boolean>> list;
	String id = "0";
	WorkHourAdapter adapter;
	List<Boolean> list_temp = new ArrayList<Boolean>();
	boolean t = true;// 默认全选
	private CheckBox checkBox;

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
		View view = inflater.inflate(R.layout.fragment_workhours, null);
		checkBox = (CheckBox) view.findViewById(R.id.btn_all);
		id = getActivity().getSharedPreferences(Config.USERINFO, 0)
				.getString(Config.ID, "1");
		key = getArguments().getInt("key");
		list_temp = (List<Boolean>) getArguments().getSerializable("data");
		// 判断刚开始是否已经全选
		for (int i = 0; i < list_temp.size(); i++) {
			if (list_temp.get(i) == false) {
				t = false;
				break;
			}
		}
		if (t) {
			checkBox.setChecked(true);
		}
		gridView = (GridView) view.findViewById(R.id.hour_grid);
		adapter = new WorkHourAdapter(getActivity(), list_temp);
		gridView.setAdapter(adapter);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (NetworkUtil.isNetworkAvailable(getActivity())) {
					// 如果是全选 ，则全不选
					if (t) {
						for (int i = 0; i < list_temp.size(); i++) {
							list_temp.set(i, false);
							WorkHoursActivity.setState(i + Config.TIME_HOURS
									* key, false);
						}
						t = false;
						adapter.notifyDataSetChanged();
					} else {// 如果不是全选，则全选
						for (int i = 0; i < list_temp.size(); i++) {
							list_temp.set(i, true);
							WorkHoursActivity.setState(i + Config.TIME_HOURS
									* key, true);
						}

						t = true;
						adapter.notifyDataSetChanged();
					}
				} else {
					CommonUtil.showToast(getActivity(),"您还没联网哦,亲");
				}
			}
		});
		// btn_all.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// }
		// });
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (NetworkUtil.isNetworkAvailable(getActivity())) {
					TextView txt = (TextView) view.findViewById(R.id.hour_txt2);
					TextView txt1 = (TextView) view
							.findViewById(R.id.hour_txt1);
					TextView txt3 = (TextView) view
							.findViewById(R.id.hour_txt3);

					if (list_temp.get(position)) {
						view.setBackgroundResource(R.color.white);
						txt.setText("");
						txt.setTextColor(Color.BLACK);
						txt1.setTextColor(Color.BLACK);
						list_temp.set(position, false);
						WorkHoursActivity.setState(position + Config.TIME_HOURS * key, false);
					} else {
						view.setBackgroundResource(R.color.light_blue);
						txt.setText("已安排");
						txt.setTextColor(Color.WHITE);
						txt1.setTextColor(Color.WHITE);
						list_temp.set(position, true);
						WorkHoursActivity.setState(position + Config.TIME_HOURS * key, true);
					}
				} else {
					CommonUtil.showToast(getActivity(), "您还没联网哦,亲");
				}

			}
		});
		return view;
	}

}
