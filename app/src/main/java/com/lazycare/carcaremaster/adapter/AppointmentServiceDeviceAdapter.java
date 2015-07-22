package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.Goods;
import com.lazycare.carcaremaster.util.Rotate3dAnimation;

/**
 * 预约-服务-配件 adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentServiceDeviceAdapter extends BaseAdapter {

	public List<Goods> list = new ArrayList<Goods>();
	private Context mContext;

	public AppointmentServiceDeviceAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public AppointmentServiceDeviceAdapter(List<Goods> mList, Context mContext) {
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

	public void addNewItem(Goods newItem) {
		list.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_appointmentservice_device, null);
			holder.app_txt_title = (TextView) convertView
					.findViewById(R.id.app_txt_title);
			holder.app_txt_name = (TextView) convertView
					.findViewById(R.id.app_txt_name);
			holder.app_txt_sn = (TextView) convertView
					.findViewById(R.id.app_txt_sn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Goods item = list.get(position);
		holder.app_txt_title.setText(item.getGoods_title());
		holder.app_txt_name.setText("型号：" + item.getGoods_name());
		holder.app_txt_sn.setText("货号:" + item.getGoods_sn());
		// applyRotation(holder.app_txt_title, 0, 90);
		// applyRotation(holder.app_txt_name, 0, 90);
		// applyRotation(holder.app_txt_sn, 0, 90);
		return convertView;
	}
	private void applyRotation(View view, float start, float end) {
		// Find the center of the container
		final float centerX = view.getWidth() / 2.0f;
		final float centerY = view.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		view.startAnimation(rotation);
	}

	class ViewHolder {
		TextView app_txt_title;
		TextView app_txt_name;
		TextView app_txt_sn;
	}
}
