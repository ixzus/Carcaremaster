package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.MenuClass;

/**
 * 首页菜单适配器
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MenuItemAdapter extends BaseAdapter {
	public List<MenuClass> listMenus = new ArrayList<MenuClass>();
	private Context mContext;
	private List<Integer> res = new ArrayList<Integer>();

	public MenuItemAdapter(Context mContext) {
		this.mContext = mContext;
		res.add(R.drawable.app_artificer_menu_1);
		res.add(R.drawable.app_artificer_menu_2);
		res.add(R.drawable.app_artificer_menu_3);
		res.add(R.drawable.app_artificer_menu_4);
		res.add(R.drawable.app_artificer_menu_5);
		res.add(R.drawable.app_artificer_menu_6);
	}

	public MenuItemAdapter(List<MenuClass> mList, Context mContext) {
		super();
		this.listMenus = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMenus.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listMenus.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(MenuClass newItem) {
		listMenus.add(newItem);
	}

	public void removeAllItem() {
		listMenus.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_meun, null);
			holder.ItemImage = (ImageView) convertView
					.findViewById(R.id.ItemImage);
			holder.ItemText = (TextView) convertView
					.findViewById(R.id.ItemText);
			holder.tip = (TextView) convertView.findViewById(R.id.main_tip);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tip.setVisibility(View.GONE);
		MenuClass mc = listMenus.get(position);
//		if (position == 0 && mc.getUnread() != 0) {
//			holder.tip.setVisibility(View.VISIBLE);
//			holder.tip.setText(String.valueOf(mc.getUnread()));
//		}
		// holder.ItemImage.setImageBitmap(mIl.loadImageSync(NetworkUtil.WSDL_IMG_URL+mc.getIcon()));
		holder.ItemImage.setImageResource(res.get(position));
		holder.ItemText.setText(mc.getName());
		return convertView;
	}

	class ViewHolder {
		ImageView ItemImage;
		TextView ItemText;
		TextView tip;
	}
}
