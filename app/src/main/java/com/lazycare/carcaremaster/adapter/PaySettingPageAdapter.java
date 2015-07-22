package com.lazycare.carcaremaster.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lazycare.carcaremaster.fragment.ForgetPayPwdFragment;
import com.lazycare.carcaremaster.fragment.ModifyPayPwdFragment;

/**
 * 预约界面adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class PaySettingPageAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "修改提现密码", "忘记提现密码" };
	private String id;

	public PaySettingPageAdapter(FragmentManager fm, String id) {
		super(fm);
		this.id = id;
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
			frag = new ModifyPayPwdFragment();
			break;
		case 1:
			frag = new ForgetPayPwdFragment();
			break;

		default:
			break;
		}
		bundle.putString("id", id);
		frag.setArguments(bundle);
		return frag;
	}
}
