package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.Service;

/**
 * 预约界面adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentServiceAdapter extends BaseAdapter {

	public List<Service> list = new ArrayList<Service>();
	private Context mContext;

	public AppointmentServiceAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public AppointmentServiceAdapter(List<Service> mList, Context mContext) {
		super();
		this.list = mList;
		this.mContext = mContext;
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

	public void addNewItem(Service newItem) {
		list.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_appointmentservice, null);
			holder.tv_name = (TextView) convertView
					.findViewById(R.id.app_txt_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Service item = list.get(position);
		holder.tv_name.setText(item.getService() + "(" + item.getPack() + ")");
		return convertView;
	}

	class ViewHolder {
		TextView tv_name;
	}
}
