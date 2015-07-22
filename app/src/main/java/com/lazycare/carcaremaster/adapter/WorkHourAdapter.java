package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;

/**
 * 工时管理adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class WorkHourAdapter extends BaseAdapter {
	private Context context;
	private List<Boolean> list;

	public WorkHourAdapter(Context context, List<Boolean> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public static List mapTransitionList(Map map) {
		List list = new ArrayList();
		Iterator iter = map.entrySet().iterator(); // 获得map的Iterator
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();
			list.add(entry.getValue());
		}
		return list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.view_hour, null);
			holder.txt1 = (TextView) convertView.findViewById(R.id.hour_txt1);
			holder.txt2 = (TextView) convertView.findViewById(R.id.hour_txt2);
			holder.txt3 = (TextView) convertView.findViewById(R.id.hour_txt3);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txt1.setText((9 + position) + ":00");
		if (list.get(position)) {
			holder.txt2.setText("已安排");
			holder.txt2.setTextColor(Color.WHITE);
			holder.txt1.setTextColor(Color.WHITE);
			convertView.setBackgroundResource(R.color.light_blue);
		} else {
			holder.txt2.setText("");
			holder.txt2.setTextColor(Color.BLACK);
			holder.txt1.setTextColor(Color.BLACK);
			convertView.setBackgroundResource(R.color.white);
		}

		return convertView;
	}

	class ViewHolder {
		TextView txt1;
		TextView txt2;
		TextView txt3;
	}
}
