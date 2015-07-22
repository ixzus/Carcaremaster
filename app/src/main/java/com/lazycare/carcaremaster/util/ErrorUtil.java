package com.lazycare.carcaremaster.util;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;

/**
 * <p>标 题: ErrorInfo</p>
 * <p>描 述: 错误信息</p>
 * <p>版 权: Copyright (c) Administrator</p>
 * <p>创建时间: Aug 27, 20143:12:46 PM</p>
 * @author Administrator
 * @version
 */
public class ErrorUtil {

	/** 维修资金成功 */
	public final static String SUCCESS = "操作成功";
	/** 维修资金成功 */
	public final static String SUCCESS_WEIXIUZIJIN = "SUCCESS";
	/** NICK_EXIST */
	public final static String NICK_EXIST = "昵称已存在";
	/** USER_NOT_FOUND */
	public final static String USER_NOT_FOUND = "没有找到用户信息";
	/** 您的网络不好，请稍后重试 */
	public static final String NETWORK_UNCONNECT = "您的网络不好，请稍后重试！";
	/** 网络异常 */
	public static final String NETWORK_ERROR = "网络异常";
	/** 网络不可用，请使用WiFi/3G/4G网络 */
	public static final String NETWORK_OFF = "网络不可用，请使用WiFi/3G/4G网络";
	/** 系统正在建设中 */
	public static final String SYSTEM_CONSTRUCTION = "系统正在建设中......";
	/** 错误信息前缀 */
	public static final String ERROR = "error^";

	/**
	 * @Title: getMessage
	 * @Description: 拼接异常信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:35:08
	 */
	public static String getMessage(Exception e) {
		return "error^" + e.getMessage();
	}

	public static String getMessage(String e) {
		return "error^" + e;
	}

	/**
	 * @Title: isDataError
	 * @Description: 是否是错误信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:34:57
	 */
	public static String isDataError(String data) {
		if( !ObjectUtil.isEmpty(data) ) {
			int index = data.indexOf(ERROR);
			if( index != -1 )
				return data.substring(ERROR.length(), data.length());
		}
		return "";
	}

	/**
	 * @Title isErrorJson 
	 * @Description 是否是错误信息
	 * @param json
	 * @return
	 */
	/**
	 * @Title: isErrorJson
	 * @Description: 是否是错误信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:34:43
	 */
	public static boolean isErrorJson(String json) {
		return isError(ErrorUtil.isDataError(json));
	}

	/**
	 * @Title: isErrorStr
	 * @Description: 是否是错误信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:34:24
	 */
	public static boolean isError(String error) {
		if( !ObjectUtil.isEmpty(error) )
			return true;
		return false;
	}

	/*
	 * TODO 错误提示
	 */
	/**
	 * @Title: showError
	 * @Description: 显示错误
	 * @param
	 * 	context 上下文
	 * 	error	错误信息		
	 * 	close	是否关闭
	 * 	show	是否显示
	 * 	ocl		提示框点击监听
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:20:25
	 */
	public static boolean showError(Context context, String error, boolean close, boolean show, OnClickListener ocl) {
		if( isError(error) ) {
			if( !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT, error) && show ) {
//				if( close )
//					CustomDialog.showBuilderCancelableOne(context, "提示", error, "关闭", new OnClickFinishDialog(context));
//				else
//					CustomDialog.showBuilderOne(context, error);
			}
			return true;
		}
		return false;
	}

	/**
	 * @Title: showErrorJson
	 * @Description: 显示错误
	 * @param
	 * 	context 上下文
	 * 	json	信息		
	 * 	close	是否关闭
	 * 	show	是否显示
	 * 	ocl		提示框点击监听
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:20:25
	 */
	public static boolean showErrorJson(Context context, String json, boolean close, boolean show, OnClickListener ocl) {
		String error = isDataError(json);
		if( isError(error) ) {
			if( !StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT, error) && show ) {
//				if( close )
//					CustomDialog.showBuilderCancelableOne(context, "提示", error, "关闭", new OnClickFinishDialog(context));
//				else
//					CustomDialog.showBuilderOne(context, error);
			}
			return true;
		}
		return false;
	}

	/**
	 * @Title: showError
	 * @Description: 显示错误
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:20:25
	 */
	public static boolean showError(Context context, String error, boolean close, boolean show) {
//		return showError(context, error, close, show, new OnClickFinishDialog(context));
		return true;
	}

	/**
	 * @Title: showErrorJson
	 * @Description: 显示错误
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:30:13
	 */
	public static boolean showErrorJson(Context context, String json, boolean close, boolean show) {
//		return showErrorJson(context, json, close, show, new OnClickFinishDialog(context));
		return true;
	}

	/**
	 * @Title: showErrorAll
	 * @Description: 显示所有错误信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:28:13
	 */
	public static boolean showErrorAll(Context context, String json, boolean close) {
		return showErrorJson(context, json, close, true);
	}

	/**
	 * @Title: showErrorAllFinish
	 * @Description: 显示所有错误信息
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:28:13
	 */
	public static boolean showErrorAll(Context context, String json) {
		return showErrorAll(context, json, false);
	}

	/**
	 * @Title: showErrorAllFinish
	 * @Description: 显示所有错误信息，并关闭页面
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:28:13
	 */
	public static boolean showErrorAllFinish(Context context, String json) {
		return showErrorAll(context, json, true);
	}

	/**
	 * @Title: showError
	 * @Description: 显示错误
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:28:13
	 */
	public static boolean showError(Context context, String json, boolean close) {
		return showErrorJson(context, json, close, false);
	}

	/**
	 * @Title: showErrorFinish
	 * @Description: 显示错误，并关闭页面
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:27:32
	 */
	public static boolean showErrorFinish(Context context, String json) {
		return showError(context, json, true);
	}

	/**
	 * @Title: showError
	 * @Description: 显示错误提示，如果有错返回 true
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:27:23
	 */
	public static boolean showError(Context context, String json) {
		return showError(context, json, false);
	}

	/*
	 * TODO 网络错误
	 */
	/**
	 * @Title: isNetworkErrorJson
	 * @Description: 是否是网络错误
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:33:14
	 */
	public static boolean isNetworkErrorJson(String json) {
		return isNetworkError(ErrorUtil.isDataError(json));
	}

	/**
	 * @Title: isNetworkError
	 * @Description: 是否是网络错误
	 * @author: LiuSiQing
	 * @date: 2015-3-5 下午5:33:09
	 */
	public static boolean isNetworkError(String error) {
		if( StringUtil.equals(ErrorUtil.NETWORK_UNCONNECT, error) )
			return true;
		return false;
	}
}
