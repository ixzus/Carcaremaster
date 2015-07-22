package com.lazycare.carcaremaster;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.lazycare.carcaremaster.util.Configuration;

/**
 * 用户个人信息
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class UserInfoActivity extends BaseActivity {
	String id = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_userinfo);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setTitle("个人信息");

	}

	@Override
	public void initView() {
		id = getSharePreferences().getString(Configuration.ID, "0");

	}

}
