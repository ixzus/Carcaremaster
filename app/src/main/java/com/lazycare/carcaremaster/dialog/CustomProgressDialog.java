package com.lazycare.carcaremaster.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lazycare.carcaremaster.R;

/**
 * 自定义progressdialog
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class CustomProgressDialog extends Dialog {

	private static CustomProgressDialog mCustomProgressDialog = null;

	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * @Title showCancelable
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog showCancelable(Context context) {
		return showCancelable(context, null);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog showCancelable(Context context,
			String message) {
		return show(context, null, message, false);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog showCancelable(Context context,
			int message) {
		return show(context, null, context.getString(message), false);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog show(Context context) {
		return show(context, null);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog show(Context context, String message) {
		return show(context, message, true);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog show(Context context, String message,
			boolean cancelable) {
		return show(context, null, message, cancelable);
	}

	/**
	 * @Title show
	 * @Description 显示进度条
	 * @param context
	 * @return
	 */
	public static CustomProgressDialog show(Context context, String title,
			String message, boolean cancelable) {
		CustomProgressDialog dialog = createDialog(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(cancelable);
		dialog.show();
		return dialog;
	}

	public static CustomProgressDialog createDialog(Context context) {
		mCustomProgressDialog = new CustomProgressDialog(context,
                R.style.ProgressDialog);
		mCustomProgressDialog.setContentView(R.layout.control_dialog_progress);
		mCustomProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return mCustomProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (mCustomProgressDialog == null) {
			return;
		}
		View progressBar = (ProgressBar) mCustomProgressDialog
				.findViewById(R.id.pb_one);
		progressBar.setVisibility(View.VISIBLE);
	}

	/**
	 *
	 * [Summary] setTitile 标题
	 *
	 * @param strTitle
	 * @return
	 *
	 */
	public CustomProgressDialog setTitile(String strTitle) {
		return mCustomProgressDialog;
	}

	/**
	 *
	 * [Summary] setMessage 提示内容
	 *
	 * @param strMessage
	 * @return
	 *
	 */
	public CustomProgressDialog setMessage(String strMessage) {
		if (mCustomProgressDialog == null) {
			return mCustomProgressDialog;
		}
		TextView tvMsg = (TextView) mCustomProgressDialog
				.findViewById(R.id.tv_one);
		if (tvMsg != null) {
			if (null != strMessage) {
				tvMsg.setText(strMessage);
				tvMsg.setVisibility(View.VISIBLE);
			} else
				tvMsg.setVisibility(View.GONE);
		}
		return mCustomProgressDialog;
	}
}