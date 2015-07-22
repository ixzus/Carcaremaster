package com.lazycare.carcaremaster.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.Attachments;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.ObjectUtil;
import com.squareup.picasso.Picasso;

/**
 * 一个附件时
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ImageOneAttachmentsAdapter extends BaseComAdapter {

	/** activity */
	private Activity mActivity;
	/** 数据集合 */
	private List<Attachments> mList;
	/** xml配置文件 */
	private int mRes = Config.DEFAULT_ERROR;
	/** 最大显示 */
	private int mMax = Config.DEFAULT_ERROR;

	public ImageOneAttachmentsAdapter(Activity activity, List<Attachments> list) {
		this.mActivity = activity;
		this.mList = list;
	}

	public ImageOneAttachmentsAdapter(Activity activity,
			List<Attachments> list, int res) {
		this(activity, list);
		this.mRes = res;
	}

	/**
	 * @Title setMax
	 * @Description 设置最大值
	 * @param @param max
	 */
	public void setMax(int max) {
		this.mMax = max;
	}

	@Override
	public int getCount() {
		if (ObjectUtil.isEmpty(mList))
			return 1;
		if (Config.DEFAULT_ERROR == mMax)
			return mList.size() + 1;
		if (mList.size() + 1 < mMax)
			return mList.size() + 1;
		else
			return mMax;
	}

	@Override
	public Attachments getItem(int position) {
		return ObjectUtil.isEmpty(mList) ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void add(Object obj) {
		if (null != obj)
			mList.add((Attachments) obj);
	}

	@Override
	public void clear() {
		mList.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(List<?> list) {
		if (null != list)
			mList = (List<Attachments>) list;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (mMax != Config.DEFAULT_ERROR ? position < mMax
				: true && position < getCount()) {
			ImageView iv_image;
			if (null == view) {
				if (Config.DEFAULT_ERROR == mRes)
					mRes = R.layout.view_one_iv;
				view = mActivity.getLayoutInflater().inflate(mRes, null);
				iv_image = (ImageView) view.findViewById(R.id.iv_one);
				view.setTag(iv_image);
			} else
				iv_image = (ImageView) view.getTag();
			if (null == mList || mList.size() == position) {
				iv_image.setTag(null);
				Picasso.with(mActivity)
						.load(String.valueOf(R.drawable.common_phone))
						.into(iv_image);
			} else {
				String url = mList.get(position).getFile_path();
				iv_image.setTag(url);
				Picasso.with(mActivity).load(url).into(iv_image);
			}
		}
		return view;
	}
}
