package com.lazycare.carcaremaster.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lazycare.carcaremaster.fragment.ModifyPhoneFragment;
import com.lazycare.carcaremaster.fragment.ModifyPhotoFragment;
import com.lazycare.carcaremaster.fragment.ModifyPwdFragment;

/**
 * 预约界面adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AccountInfoPageAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "修改联系方式", "修改登录密码","sdf"};
	private String id;
	private String username;

	public AccountInfoPageAdapter(FragmentManager fm, String id, String username) {
		super(fm);
		this.id = id;
		this.username = username;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return TITLES[position];
	}

	@Override
	public int getCount() {
		return TITLES.length;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment frag = null;
		Bundle bundle = new Bundle();
		switch (position) {
		case 0:
			frag = new ModifyPhoneFragment();
			break;
		case 1:
			frag = new ModifyPwdFragment();
			break;
		case 2:
			frag = new ModifyPhotoFragment();
			break;
		default:
			break;
		}
		bundle.putString("id", id);
		bundle.putString("username", username);
		frag.setArguments(bundle);
		return frag;
	}
}
