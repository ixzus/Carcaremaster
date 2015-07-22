package com.lazycare.carcaremaster.impl;

import android.content.Context;

/**
 * baseactiity的工具类
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public interface IBaseActivity {
	/**
	 * 
	 * 终止服务.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 上午9:05:51
	 */
	public abstract void stopService();

	/**
	 * 
	 * 开启服务.
	 * 
	 * @author shimiso
	 * @update 2012-7-6 上午9:05:44
	 */
	public abstract void startService();

	/**
	 * 
	 * 返回当前Activity上下文.
	 * 
	 * @return
	 * @author shimiso
	 * @update 2012-7-6 上午9:19:54
	 */
	public abstract Context getContext();
}
