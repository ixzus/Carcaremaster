package com.lazycare.carcaremaster.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.ErrorUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.util.ObjectUtil;

/**
 * <p>标 题: NetWorkRunnable</p>
 * <p>描 述: 网络连接线程</p>
 * <p>版 权: Copyright (c) Administrator</p>
 * <p>创建时间: Aug 30, 2014 10:37:03 AM</p>
 * @author Administrator
 * @version
 */
public class DataOtherRunnable implements Runnable{

	/** 响应值 */
	private int mWhat = Config.WHAT_DEFAULT;
	/** 网络连接路径 */
	private String mUrl;
	/** 数据 */
	private Map<String, String> mMap;
	/** 集合类数据 */
	private Map<String, List<String>> mMapList;
	/** 主线程 */
	private Handler mHandler;
	/** 睡眠时间 */
	private long mSleep = 0;
	/** 传递数据 */
	private Message mMessage;
	/** Activity */
	private Context mContext;
	/** 前缀地址 */
	private String mHttpPath;

	public DataOtherRunnable(Context context, String url, Handler handler) {
		this.mUrl = url;
		this.mHandler = handler;
		this.mContext = context;
		this.mMessage = mHandler.obtainMessage();
	}

	public DataOtherRunnable(Context context, String url, Handler handler, int what) {
		this(context, url, handler);
		this.mWhat = what;
	}

	public DataOtherRunnable(Context context, String url, Handler handler, Map<String, String> map) {
		this(context, url, handler);
		this.mMap = map;
	}

	public DataOtherRunnable(Context context, String url, Handler handler, int what, Map<String, String> map) {
		this(context, url, handler, what);
		this.mMap = map;
	}

	public DataOtherRunnable(Context context, String url, Handler handler, int what, Map<String, String> map, long sleep) {
		this(context, url, handler, what, map);
		this.mSleep = sleep;
	}

	public DataOtherRunnable(Context context, String url, Handler handler, long sleep) {
		this(context, url, handler);
		this.mSleep = sleep;
	}

	public DataOtherRunnable setWhat(int what) {
		this.mWhat = what;
		return this;
	}

	public DataOtherRunnable setSleep(long sleep) {
		this.mSleep = sleep;
		return this;
	}

	public DataOtherRunnable setMap(Map<String, String> map) {
		this.mMap = map;
		return this;
	}

	public DataOtherRunnable setHttpPath(String http) {
		this.mHttpPath = http;
		return this;
	}

	public DataOtherRunnable setMapList(Map<String, List<String>> map) {
		this.mMapList = map;
		return this;
	}

	@Override
	public void run() {
		try {
			if( mSleep > 0 )
				Thread.sleep(mSleep);
		} catch(Exception e) {
			Log.d("BUG", e.toString());
		}
//		if( null != mContext ) {
//			if( ObjectUtil.isEmpty(mMap) )
//				mMap = new HashMap<String, String>();
//			mMap.put("session_id", InfoUtil.getUser(mContext, "session_id"));
//			mMap.put("au_name", InfoUtil.getUser(mContext, "au_name"));
//		}
		if( ObjectUtil.isEmpty(mMap) )
			mMap = new HashMap<String, String>();
		mMessage.what = mWhat;
		List<NameValuePair> list = NetworkUtil.interfaceCreateHttp();
		try {
			if( !ObjectUtil.isEmpty(mMap) )
				list = NetworkUtil.interfacePut(list, mMap);
			if( !ObjectUtil.isEmpty(mMapList) )
				list = NetworkUtil.interfacePutList(list, mMapList);
			if( !ObjectUtil.isEmpty(mHttpPath) )
				mMessage.obj = NetworkUtil.interfacePost(list, mUrl, mHttpPath);
			else
				mMessage.obj = NetworkUtil.interfacePost(list, mUrl);
			Log.d("PARAM", "RET:"+mMessage.obj );
		} catch(Exception e) {
			mMessage.obj = ErrorUtil.getMessage(e);
		}
		mHandler.sendMessage(mMessage); // 推送给主UI线程
	}
}
