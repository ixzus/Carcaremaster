package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.ChooseBankClass;

/**
 * bank adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ChooseBankAdapter extends BaseAdapter {

	public List<ChooseBankClass> listBank = new ArrayList<ChooseBankClass>();
	private Activity mContext;

	public ChooseBankAdapter(Activity mContext) {

		this.mContext = mContext;
	}

	public ChooseBankAdapter(List<ChooseBankClass> mList, Activity mContext) {
		super();
		this.listBank = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listBank.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listBank.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(ChooseBankClass newItem) {
		listBank.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_choosebank, null);
			holder.iv_bankicon = (ImageView) convertView
					.findViewById(R.id.iv_bankicon);
			holder.tv_bankname = (TextView) convertView
					.findViewById(R.id.tv_bankname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ChooseBankClass qc = listBank.get(position);
//		Picasso.with(mContext).load(R.drawable.icon_bank).into(holder.iv_bankicon);
		holder.tv_bankname.setText(qc.getName());
		// 依据数据填充内容
		return convertView;
	}

	class ViewHolder {
		ImageView iv_bankicon;
		TextView tv_bankname;
	}
}
