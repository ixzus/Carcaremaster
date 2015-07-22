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
import com.lazycare.carcaremaster.data.BankClass;
import com.squareup.picasso.Picasso;

/**
 * bank adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class BankAdapter extends BaseAdapter {

	public List<BankClass> listBank = new ArrayList<BankClass>();
	private Activity mContext;

	public BankAdapter(Activity mContext) {

		this.mContext = mContext;
	}

	public BankAdapter(List<BankClass> mList, Activity mContext) {
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

	public void addNewItem(BankClass newItem) {
		listBank.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.banklist_item, null);
			holder.iv_bankicon = (ImageView) convertView
					.findViewById(R.id.iv_bankicon);
			holder.tv_bankname = (TextView) convertView
					.findViewById(R.id.tv_bankname);
			holder.tv_bankshortnumber = (TextView) convertView
					.findViewById(R.id.tv_bankshortnumber);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BankClass qc = listBank.get(position);
		Picasso.with(mContext).load(qc.getIcon()).into(holder.iv_bankicon);
		holder.tv_bankname.setText(qc.getBank());
		holder.tv_bankshortnumber.setText(qc.getTail());
		// 依据数据填充内容
		return convertView;
	}

	class ViewHolder {
		ImageView iv_bankicon;
		TextView tv_bankname;
		TextView tv_bankshortnumber;
	}
}
