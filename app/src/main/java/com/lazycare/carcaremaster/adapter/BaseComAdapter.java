package com.lazycare.carcaremaster.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 所有adapter的基类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public abstract class BaseComAdapter extends BaseAdapter {

	/**
	 * 得到adapter的数量
	 */
	@Override
	public abstract int getCount();

	/**
	 * 得到对应position的对象
	 */
	@Override
	public abstract Object getItem(int position);

	/**
	 * 得到adapter视图的id
	 */
	@Override
	public abstract long getItemId(int position);

	/**
	 * @param <E>
	 * @Title: add
	 * @Description: 添加对象进入adapter
	 * @param obj
	 *            Aug 23, 2014 4:47:18 PM
	 */
	public abstract void add(Object obj);

	/**
	 * @Title: clear
	 * @Description: adapter清空 Aug 23, 2014 4:47:38 PM
	 */
	public abstract void clear();

	/**
	 * @Title: refresh
	 * @Description: adapter重置
	 * @param list
	 *            Aug 23, 2014 4:47:48 PM
	 */
	public abstract void refresh(List<?> list);

	/**
	 * 得到视图
	 */
	@Override
	public abstract View getView(int position, View convertView,
			ViewGroup parent);
}
