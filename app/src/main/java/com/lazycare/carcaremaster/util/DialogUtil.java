package com.lazycare.carcaremaster.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * <p>
 * 标 题: ProgressDialogUtil
 * </p>
 * <p>
 * 描 述: 进度条工具类
 * </p>
 * <p>
 * 版 权: Copyright (c) Administrator
 * </p>
 * <p>
 * 创建时间: Aug 25, 2014 5:47:58 PM
 * </p>
 * 
 * @author Administrator
 * @version
 */
@SuppressLint("InflateParams")
public class DialogUtil {

	/**
	 * @Title: getProgressDialog
	 * @Description: 得到一个进度条
	 * @param context
	 * @return Aug 25, 2014 5:49:31 PM
	 */
	public static ProgressDialog getProgressDialog(Context context) {
		return new ProgressDialog(context);
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 显示一个进度条
	 * @param context
	 *            上下文
	 * @param title
	 *            标题
	 * @param msg
	 *            提示信息
	 * @param indeterminate
	 *            是否显示详细进度条 Aug 25, 2014 5:51:57 PM
	 */
	public static ProgressDialog showProgressDialog(Context context,
			String title, String msg, boolean flag, boolean indeterminate) {
		ProgressDialog dialog = getProgressDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(msg);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(flag);
		dialog.show();
		return dialog;
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 显示一个进度条
	 * @param context
	 * @param title
	 * @param msg
	 * @return Aug 30, 2014 11:52:04 AM
	 */
	public static ProgressDialog showProgressDialog(Context context,
			String title, String msg) {
		return showProgressDialog(context, title, msg, true, false);
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 显示一个进度条
	 * @param context
	 * @param msg
	 * @return Aug 30, 2014 11:53:00 AM
	 */
	public static ProgressDialog showProgressDialogCancelable(Context context,
			String title, String msg) {
		return showProgressDialog(context, title, msg, false, false);
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 显示一个进度条
	 * @param context
	 * @param msg
	 * @return Aug 30, 2014 11:53:00 AM
	 */
	public static ProgressDialog showProgressDialogCancelable(Context context,
			String msg) {
		return showProgressDialogCancelable(context, "请等候", msg);
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 标题为请等待,内容为正在加载数据，请稍候...,进度条
	 * @param context
	 * @return Aug 30, 2014 10:58:39 AM
	 */
	public static ProgressDialog showProgressDialogCancelable(Context context) {
		return showProgressDialog(context, "请等待", "正在加载数据，请稍候...", false, false);
	}

	/**
	 * @Title: showProgressDialogNoTitle
	 * @Description: 显示一个进度条
	 * @param context
	 *            上下文
	 * @param indeterminate
	 *            是否显示详细进度条
	 * @return Aug 25, 2014 5:54:14 PM
	 */
	public static ProgressDialog showProgressDialogNoTitle(Context context,
			boolean indeterminate, boolean flag) {
		ProgressDialog dialog = getProgressDialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(flag);
		dialog.show();
		return dialog;
	}

	/**
	 * @Title: showProgressDialogNoTitle
	 * @Description: 显示一个进度条
	 * @param context
	 *            上下文
	 * @return Aug 25, 2014 5:55:19 PM
	 */
	public static ProgressDialog showProgressDialogNoTitle(Context context) {
		return showProgressDialogNoTitle(context, false, false);
	}

	/**
	 * @Title: showProgressDialog
	 * @Description: 显示进度条
	 * @param context
	 * @param dialog
	 * @return Aug 26, 2014 5:44:25 PM
	 */
	public static ProgressDialog showProgressDialog(Context context,
			ProgressDialog dialog) {
		if (null == dialog) {
			dialog = getProgressDialog(context);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setIndeterminate(false);
			TextView mMessageView = (TextView) dialog
					.findViewById(android.R.id.message);
			if (null != mMessageView)
				mMessageView.setVisibility(View.GONE);
			dialog.show();
		} else {
			if (!dialog.isShowing()) {
				dialog.show();
			}
		}
		return dialog;
	}

	/**
	 * @Title: dismiss
	 * @Description: 将dialog销毁
	 * @param dialog
	 *            Aug 28, 2014 11:12:14 AM
	 */
	public static void dismiss(Dialog dialog) {
		if (null != dialog)
			dialog.dismiss();
	}

	/**
	 * @Title: dismiss
	 * @Description: 将dialog销毁
	 * @param dialog
	 *            Aug 28, 2014 11:12:14 AM
	 */
	public static void dismiss(DialogInterface dialog) {
		if (null != dialog)
			dialog.dismiss();
	}

	/**
	 * @Title: hide
	 * @Description: 将dialog隐藏
	 * @param dialog
	 *            Sep 1, 2014 12:19:24 PM
	 */
	public static void hide(Dialog dialog) {
		if (null != dialog && dialog.isShowing())
			dialog.hide();
	}

	/**
	 * @Title: show
	 * @Description: 将dialog展示
	 * @param dialog
	 *            Sep 1, 2014 12:19:10 PM
	 */
	public static void show(Dialog dialog) {
		if (null != dialog && !dialog.isShowing())
			dialog.show();
	}

}