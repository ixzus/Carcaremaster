package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.Attachments;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.ObjectUtil;
import com.squareup.picasso.Picasso;

/**
 * 一张图片时显示
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ImageOneAdapter extends BaseComAdapter {

	/** activity */
	private Context mActivity;
	/** 地址集合 */
	private List<String> mList;
	/** xml视图配置文件 */
	private int mRes = Config.DEFAULT_ERROR;
	/** 显示数量 */
	private int mMax = Config.DEFAULT_ERROR;

	private class ControlView {

		ImageView iv_one;
	}

	public ImageOneAdapter(Context activity, List<String> list) {
		this.mActivity = activity;
		this.mList = list;
	}

	public ImageOneAdapter(List<Attachments> list, Activity activity) {
		List<String> temp = new ArrayList<String>();
		for (Attachments att : list)
			temp.add(att.getFile_path());
		this.mActivity = activity;
		this.mList = temp;
	}

	public ImageOneAdapter(Context mContext, List<String> list, int res) {
		this(mContext, list);
		this.mRes = res;
	}

	public ImageOneAdapter(Activity activity, List<Attachments> list, int res,
			int max) {
		this(list, activity);
		this.mRes = res;
		this.mMax = max;
	}

	@Override
	public int getCount() {
		if (Config.DEFAULT_ERROR == mMax)
			return ObjectUtil.getSize(mList);
		else if (ObjectUtil.getSize(mList) < mMax)
			return ObjectUtil.getSize(mList);
		else
			return mMax;
	}

	@Override
	public String getItem(int position) {
		return (String) ObjectUtil.getObject(mList, position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void add(Object obj) {
		if (null != obj && obj instanceof String)
			mList.add((String) obj);
	}

	@Override
	public void clear() {
		if (!ObjectUtil.isEmpty(mList))
			mList.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(List<?> list) {
		if (null != list)
			mList = (List<String>) list;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (ObjectUtil.isEmpty(mList))
			return view;
		if (Config.DEFAULT_ERROR != mMax && mList.size() >= mMax)
			return view;
		ControlView cv;
		if (null == view) {
			cv = new ControlView();
			if (Config.DEFAULT_ERROR == mRes)
				view = LayoutInflater.from(mActivity).inflate(
						R.layout.view_one_iv, null);
			else
				view = LayoutInflater.from(mActivity).inflate(mRes, null);
			cv.iv_one = (ImageView) view.findViewById(R.id.iv_one);
			view.setTag(cv);
		} else
			cv = (ControlView) view.getTag();
		if (!mList.get(position).equals("")) {
			Log.d("gmyboy", mList.get(position));
			Picasso.with(mActivity).load(mList.get(position)).centerCrop()
					.resize(100, 100).into(cv.iv_one);
		}
		return view;
	}
}
