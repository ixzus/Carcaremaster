package com.lazycare.carcaremaster.thread;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

/**
 * 按钮计时器
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class CountTime extends CountDownTimer {

	private TextView mView;
	private String mMessage;
	private String mTimeMessage;

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:TODO
	 * </p>
	 * 
	 * @param millisUntilFinished
	 *            时长(毫秒)
	 * @param countDownInterval
	 *            时间间隔(毫秒)
	 * @param view
	 *            需要操纵的控件
	 */
	public CountTime(long millisUntilFinished, long countDownInterval, View view) {
		super(millisUntilFinished, countDownInterval);
		this.mView = (TextView) view;
		mView.setClickable(false);
		this.mMessage = mView.getText().toString();
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:默认倒计时时间为60秒的构造函数
	 * </p>
	 * 
	 * @param countDownInterval
	 *            时间间隔(毫秒)
	 * @param view
	 *            需要操纵的控件
	 */
	public CountTime(long countDownInterval, View view) {
		this(60000, countDownInterval, view);
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:默认倒计时时间为60秒,时间间隔为1秒的构造函数
	 * </p>
	 * 
	 * @param view
	 *            需要操纵的控件
	 */
	public CountTime(View view) {
		this(1000, view);
	}

	public CountTime setMessage(String msg) {
		this.mMessage = msg;
		return this;
	}

	public CountTime setTimeMessage(String msg) {
		this.mTimeMessage = msg;
		return this;
	}

	/**
	 * 每次间隔后执行
	 */
	@Override
	public void onTick(long millisUntilFinished) {
		long time = millisUntilFinished / 1000;
		mView.setText((time < 10 ? "0" + time : time) + "秒"
				+ (mTimeMessage.equals("") ? "" : mTimeMessage));
	}

	/**
	 * 结束时运行
	 */
	@Override
	public void onFinish() {
		if (null != mMessage)
			mView.setText(mMessage);
		mView.setClickable(true);
	}
}