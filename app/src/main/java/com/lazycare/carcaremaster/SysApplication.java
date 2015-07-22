package com.lazycare.carcaremaster;

import java.io.File;

import android.app.Application;

import com.lazycare.carcaremaster.handler.ErrorHandler;
import com.lazycare.carcaremaster.util.Config;

/**
 * app
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class SysApplication extends Application {

	private File dir;
	public String CurDate = "2015-05-13";

	@Override
	public void onCreate() {
		super.onCreate();
//		ErrorHandler crashHandler = ErrorHandler.getInstance();
//		crashHandler.init(getApplicationContext());
		dir = new File(Config.RECORD_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		dir = new File(Config.IMG_PATH);
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}

	public String getCurDate() {
		return CurDate;
	}

	public void setCurDate(String curDate) {
		CurDate = curDate;
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}