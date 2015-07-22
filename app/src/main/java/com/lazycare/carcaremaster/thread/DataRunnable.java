package com.lazycare.carcaremaster.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntity;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
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
 *
 * @author Administrator
 */
public class DataRunnable implements Runnable {

    /**
     * 响应值
     */
    private int mWhat = Config.WHAT_DEFAULT;
    /**
     * 网络连接路径
     */
    private String mUrl;
    /**
     * 数据
     */
    private Map<String, String> mMap;
    /**
     * 图片路径
     */
    private List<String> mList;
    /**
     * 主线程
     */
    private Handler mHandler;
    /**
     * 睡眠时间
     */
    private long mSleep = 0;
    /**
     * 传递数据
     */
    private Message mMessage;
    /**
     * Activity
     */
    private Context mContext;
    /**
     * 前缀地址
     */
    private String mHttpPath;
    private String get = "";


    public DataRunnable(Context context, String url, Handler handler) {
        this.mUrl = url;
        this.mHandler = handler;
        this.mContext = context;
        this.mMessage = mHandler.obtainMessage();
    }

    public DataRunnable(Context context, String url, Handler handler, int what) {
        this(context, url, handler);
        this.mWhat = what;
    }

    public DataRunnable(Context context, String url, Handler handler, Map<String, String> map, List<String> list) {
        this(context, url, handler, map);
        this.mList = list;
    }

    public DataRunnable(Context context, String url, Handler handler, Map<String, String> map) {
        this(context, url, handler);
        this.mMap = map;
    }

    public DataRunnable(Context context, String url, Handler handler, int what, Map<String, String> map) {
        this(context, url, handler, what);
        this.mMap = map;
    }

    public DataRunnable(Context context, String url, Handler handler, int what, Map<String, String> map, List<String> list) {
        this(context, url, handler, what, map);
        this.mList = list;
    }

    public DataRunnable(Context context, String url, Handler handler, int what, Map<String, String> map, long sleep) {
        this(context, url, handler, what, map);
        this.mSleep = sleep;
    }

    public DataRunnable(Context context, String url, Handler handler, long sleep) {
        this(context, url, handler);
        this.mSleep = sleep;
    }

    public DataRunnable(Context context, String s, Handler mHandler, int whatTwo, Map<String, String> map, String get) {
        this(context, s, mHandler, whatTwo, map);
        this.get = get;
    }

    public Runnable setWhat(int what) {
        this.mWhat = what;
        return this;
    }

    public Runnable setSleep(long sleep) {
        this.mSleep = sleep;
        return this;
    }

    public Runnable setMap(Map<String, String> map) {
        this.mMap = map;
        return this;
    }

    public Runnable setList(List<String> list) {
        this.mList = list;
        return this;
    }

    public Runnable setHttpPath(String http) {
        this.mHttpPath = http;
        return this;
    }

    @Override
    public void run() {
        try {
            if (mSleep > 0)
                Thread.sleep(mSleep);
        } catch (Exception e) {
            Log.d("BUG", e.toString());
        }
//		if( null != mContext ) {
//			if( ObjectUtil.isEmpty(mMap) )
//				mMap = new HashMap<String, String>();
//			mMap.put("session_id", InfoUtil.getUser(mContext, "session_id"));
//			mMap.put("au_name", InfoUtil.getUser(mContext, "au_name"));
//		}
        if (ObjectUtil.isEmpty(mMap))
            mMap = new HashMap<String, String>();
        mMessage.what = mWhat;
        MultipartEntity entity = NetworkUtil.create();
        try {
            entity = NetworkUtil.put(entity, mContext, mMap, mList);
            if (!ObjectUtil.isEmpty(mHttpPath))
                mMessage.obj = NetworkUtil.post(entity, mUrl, mHttpPath);
            else {
                if (mUrl.endsWith("/Artificer/enchashment")) {
                    mMessage.obj = NetworkUtil.post2(mMap, mUrl);
                } else {
                    mMessage.obj = NetworkUtil.post(entity, mUrl);
                }

                Log.d("PARAM", "RET:" + mMessage.obj);
            }
        } catch (Exception e) {
            mMessage.obj = ErrorUtil.getMessage(e);
        }
        mHandler.sendMessage(mMessage); // 推送给主UI线程
    }
}
