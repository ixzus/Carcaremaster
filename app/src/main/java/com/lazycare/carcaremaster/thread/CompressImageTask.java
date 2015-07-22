package com.lazycare.carcaremaster.thread;

import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.lazycare.carcaremaster.util.ImageUtil;

/**
 * 图片压缩线程
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class CompressImageTask implements Runnable {

	/**
	 * 上下文
	 */
	private Context mContext;
	/**
	 * 图片数据集合
	 */
	private Map<String, byte[]> mMap;
	/**
	 * 地址
	 */
	private Uri mUri;
	/**
	 * 地址
	 */
	private String mPath;

	public CompressImageTask(Context context, Map<String, byte[]> map, Uri uri) {
		this.mContext = context;
		this.mMap = map;
		this.mUri = uri;
	}

	public CompressImageTask(Map<String, byte[]> map, String path) {
		this.mMap = map;
		this.mPath = path;
	}

	@Override
	public void run() {
		if (null == mMap || (null == mUri && null == mPath))
			return;
		try {
			if (null != mUri)
				mMap.put(mUri.toString(), ImageUtil
						.compressImage(MediaStore.Images.Media.getBitmap(
								mContext.getContentResolver(), mUri)));
			else
				mMap.put(mUri.toString(), ImageUtil.compressImage(BitmapFactory
						.decodeFile(mPath)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
