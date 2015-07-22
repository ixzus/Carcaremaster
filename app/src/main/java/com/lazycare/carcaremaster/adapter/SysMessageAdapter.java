package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.SysMessage;

/**
 * 系统消息adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class SysMessageAdapter extends BaseAdapter {

	public List<SysMessage> listMessage = new ArrayList<SysMessage>();
	private Activity mContext;

	public SysMessageAdapter(Activity mContext) {

		this.mContext = mContext;
	}

	public SysMessageAdapter(List<SysMessage> mList, Activity mContext) {
		super();
		this.listMessage = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listMessage.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listMessage.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(SysMessage newItem) {
		listMessage.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.messagelist_item, null);
			holder.tv_description = (TextView) convertView
					.findViewById(R.id.tv_ml_description);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_ml_content);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_ml_title);
			holder.tv_addtime = (TextView) convertView
					.findViewById(R.id.tv_ml_add_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SysMessage qc = listMessage.get(position);
		holder.tv_content.setText(qc.getContent());
		holder.tv_title.setText(qc.getTitle());
		holder.tv_description.setText(qc.getDescription());
		holder.tv_addtime.setText(qc.getAdd_time());
		return convertView;
	}

	/**
	 * @Package com.mngwyhouhzmb.activity.neighbour
	 * @Title OnItemListener
	 * @Description 图片展示监听
	 * @author LiuSiQing
	 * @Time 2014年11月12日上午11:31:00
	 */
	private class OnItemListener implements OnItemClickListener {

		private List<String> list;

		private OnItemListener(List<String> list) {
			this.list = list;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) { // 需要将图片展示出来
			// Toast.makeText(mContext, "onItemClick",
			// Toast.LENGTH_SHORT).show();
			// if( DateUtil.isFastDoubleClick() ) return;
			// Intent intent = new Intent(mContext, ImageSwitcherView.class);
			// intent.putStringArrayListExtra("mlist", (ArrayList<String>)list);
			// intent.putExtra("pos", position);
			// mContext.startActivity(intent);
		}
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_description;
		TextView tv_content;
		TextView tv_addtime;
	}

}
