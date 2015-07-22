package com.lazycare.carcaremaster.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.lazycare.carcaremaster.R;

/**
 * 选择照片弹出提示
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ModelPopup extends PopupWindow implements OnClickListener {

	private OnDialogListener listener;
	private View mPopView;

	public ModelPopup(Context context, OnDialogListener listener) {
		super(context);
		this.listener = listener;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPopView = inflater.inflate(R.layout.dialog_choose, null);
		this.setContentView(mPopView);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		Button btn_choose_photo = (Button) mPopView
				.findViewById(R.id.btn_choose_photo);
		Button btn_take_photo = (Button) mPopView
				.findViewById(R.id.btn_take_photo);

		Button btn_cancel = (Button) mPopView.findViewById(R.id.btn_cancel);

		btn_choose_photo.setOnClickListener(this);
		btn_take_photo.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 点击外面的控件也可以使得PopUpWindow dimiss
		this.setOutsideTouchable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.PopupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);// 0xb0000000
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);// 半透明颜色
	}

	/**
	 * Dialog按钮回调接口
	 * 
	 */
	public interface OnDialogListener {

		public void onChoosePhoto();// 选择本地照片

		public void onTakePhoto();// 照相

		public void onCancel();// 取消

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_choose_photo:
			listener.onChoosePhoto();
			break;
		case R.id.btn_take_photo:
			listener.onTakePhoto();
			break;
		case R.id.btn_cancel:
			listener.onCancel();
			break;
		}
		dismiss();
	}
}
