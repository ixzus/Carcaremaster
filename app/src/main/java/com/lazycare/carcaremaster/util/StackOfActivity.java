package com.lazycare.carcaremaster.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

/**
 * activity后台堆栈
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class StackOfActivity extends Application {
	// 运用list来保存们每一个activity是关键
	private List<Activity> mList = new LinkedList<Activity>();
	// 为了实现每次使用该类时不创建新的对象而创建的静态对象
	private static StackOfActivity instance;

	// 构造方法
	private StackOfActivity() {
	}

	// 实例化一次
	public synchronized static StackOfActivity getInstance() {
		if (null == instance) {
			instance = new StackOfActivity();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		mList.add(activity);
	}

	/**
	 * 获得当前activity
	 * 
	 * @return
	 */
	public Activity currentActivity() {
		boolean bool = mList.isEmpty();
		Activity localActivity = null;
		if (!bool)
			localActivity = (Activity) mList.get(mList.size() - 1);
		return localActivity;
	}

	// 关闭每一个list内的activity
	public void exit() {
		try {
			for (Activity activity : mList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	// 杀进程
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
