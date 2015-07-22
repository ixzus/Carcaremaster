package com.lazycare.carcaremaster.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

/**
 * View工具类
 * @author Administrator
 *
 */
public class ViewUtil {

	/**
	 * @Title: setEnabled 
	 * @Description: 控件无法获取焦点
	 * @param set
	 */
	public static void setEnabled(Set<View> set) {
		Iterator<View> iterator = set.iterator();
		while( iterator.hasNext() ) {
			iterator.next().setEnabled(false);
		}
	}

	/**
	 * @Title: setVisibility 
	 * @Description: 控件无法显示
	 * @param set
	 */
	public static void setVisibility(Set<View> set) {
		Iterator<View> iterator = set.iterator();
		while( iterator.hasNext() ) {
			iterator.next().setVisibility(View.GONE);
		}
	}

	/**
	 * @Title: requestFocus 
	 * @Description: 获得焦点
	 * @param v
	 * @return
	 */
	public static boolean requestFocus(View v) {
		if( null != v && !"".equals(v) ) {
			v.setFocusable(true);
			v.setFocusableInTouchMode(true);
			return v.requestFocus();
		}
		return false;
	}

	/**
	 * @Title: InitSlidingMenu 
	 * @Description: 初始化侧滑菜单
	 * @param activity
	 * @return
	 * Jun 30, 2014 7:59:20 PM
	 */
	// public static SlidingMenu InitSlidingMenu(BaseSlidingFragmentActivity mActivity, boolean mClose) {
	// DisplayMetrics dm = new DisplayMetrics();
	// mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int mScreenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
	// // TODO Auto-generated method stub
	// mActivity.setBehindContentView(R.layout.fragment_left_menu_layout);// 设置左菜单
	// FragmentTransaction mFragementTransaction = mActivity.getSupportFragmentManager().beginTransaction();
	// LeftMenuFragment mFrag = new LeftMenuFragment();
	// mFrag.setValue(mActivity, mClose);
	// mFragementTransaction.replace(R.id.fragment_left_menu, mFrag);
	// mFragementTransaction.commit();
	// // customize the SlidingMenu
	// SlidingMenu mSlidingMenu = mActivity.getSlidingMenu();
	// mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置是左滑还是右滑，还是左右都可以滑，我这里左右都可以滑
	// mSlidingMenu.setShadowWidth(mScreenWidth / 40);// 设置阴影宽度
	// mSlidingMenu.setBehindOffset((mScreenWidth / 4) * 3);// 设置菜单宽度
	// mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
	// mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
	// mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);// 设置左菜单阴影图片
	// // mSlidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);// 设置右菜单阴影图片
	// mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
	// mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
	// return mSlidingMenu;
	// }
	/**
	 * @Title: setCloseDialog 
	 * @Description: 设置是否关闭弹窗
	 * @param mDialog 弹窗
	 * @param mClose true 关闭(默认)/false 不关闭
	 * Jul 1, 2014 11:46:20 AM
	 */
	public static void setCloseDialog(DialogInterface mDialog, boolean mClose) {
		try {
			Field mField = mDialog.getClass().getSuperclass().getDeclaredField("mShowing");
			mField.setAccessible(true);
			mField.set(mDialog, mClose);
			if( null != mDialog )
				mDialog.dismiss();
		} catch(Exception e) {
			Log.d("Exception", e.toString());
		}
	}

	/**
	 * @Title: setListViewHeightBasedOnChildren 
	 * @Description: 
	 * 	处理在scrollview中使用listview兼容问题
	 * 	该工具类有小BUG,进入activity中会将焦点定位于使用了此类的ListView的第一条数据,而不是屏幕顶端
	 * @param listView
	 * Jul 2, 2014 6:15:57 PM
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if( listAdapter == null ) {
			// pre-condition
			return;
		}
		int totalHeight = 0;
		for(int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		params.height += 5;
		listView.setLayoutParams(params);
	}

	/**
	 * 解决scrollview与listview共存,将页面显示跳回scrollview(0,0)
	 * @param sv
	 */
	public static void setScrollViewReturn(final ScrollView sv) {
		sv.post(new Runnable() {

			@Override
			public void run() {
				sv.scrollTo(0, 0);
			}
		});
	}

	/**
	 * @Title: getDrawable 
	 * @Description: 得到Drawable
	 * @param mActivity
	 * @param mBoolean
	 * @return
	 * Jul 7, 2014 2:30:03 PM
	 */
	// public static Drawable getDrawable(Activity mActivity, boolean mBoolean) {
	// if( mBoolean )
	// return mActivity.getResources().getDrawable(R.drawable.common_all_right);
	// else
	// return mActivity.getResources().getDrawable(R.drawable.common_no);
	// }
	/**
	 * @Title: getView 
	 * @Description: 通过上下文和视图得到一个view
	 * @param context 上下文
	 * @param res 页面配置文件
	 * @return
	 * Aug 25, 2014 4:26:20 PM
	 */
	public static View getView(Context context, int res) {
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(res, null);
	}

	/**
	 * @Title: conLength 
	 * @Description: 限制输入长度
	 * @param editText
	 * @param maxLength
	 * Sep 5, 2014 1:53:43 PM
	 */
	public static void conLength(EditText editText, int maxLength) {
		Editable editable = editText.getText();
		int len = editable.length();
		if( len <= maxLength )
			return;
		int endIndex = Selection.getSelectionEnd(editable);
		editText.setText(editable.toString().substring(0, maxLength)); // 截取新字符串
		editable = editText.getText();
		if( endIndex > editable.length() ) // 新字符串的长度
			endIndex = editable.length(); // 旧光标位置超过字符串长度
		Selection.setSelection(editable, endIndex); // 设置新光标所在的位置
	}

	/**
	 * @Title: conLength 
	 * @Description: 限制输入长度20
	 * @param editText
	 * Sep 5, 2014 1:57:10 PM
	 */
	public static void conLength(EditText editText) {
		conLength(editText, 20);
	}

	/**
	 * @Title: isXh 
	 * @Description: 判断是否是xh分辨率
	 * @param activity
	 * @return
	 * Sep 5, 2014 8:09:09 PM
	 */
	public static boolean isXh(Activity activity) {
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		if( dm.widthPixels > 480 )
			return true;
		return false;
	}

	/**
	 * @Title: isXhGrid 
	 * @Description: 如果分辨率为xh返回4,否则返回3
	 * @param activity
	 * @return
	 * Sep 5, 2014 8:10:40 PM
	 */
	public static int isXhGrid(Activity activity) {
		if( isXh(activity) )
			return 4;
		return 3;
	}

	/**
	 * @Title: getImageWidth 
	 * @Description: 得到图片宽度
	 * @param activity
	 * @param size
	 * @return
	 * Sep 9, 2014 4:42:04 PM
	 */
	public static int getImageWidth(Activity activity, int size) {
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		return dm.widthPixels / size;
	}

	/**
	 * @Title: setFocusable 
	 * @Description: 控件焦点控制
	 * @param view
	 * @param focusable
	 * Sep 10, 2014 5:35:55 PM
	 */
	public static void setFocusable(View view, boolean focusable) {
		view.setFocusable(focusable);
		view.setFocusableInTouchMode(focusable);
		if( focusable ) {
			view.requestFocus();
			view.requestFocusFromTouch();
		}
	}

	/**
	 * @Title: setFocusable 
	 * @Description: 控件焦点控制
	 * @param view
	 * @param focusable
	 * Sep 10, 2014 5:35:55 PM
	 */
	public static void setFocusable(View view) {
		setFocusable(view, true);
	}
}
