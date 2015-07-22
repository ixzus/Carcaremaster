package com.lazycare.carcaremaster.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lazycare.carcaremaster.fragment.MessageFragment;

/**
 * 预约界面adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MessagePageAdapter extends FragmentPagerAdapter {

	private final String[] TITLES = { "未读消息", "已读消息", "在线更新" };
	private String id;

	public MessagePageAdapter(FragmentManager fm, String id) {
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
		return MessageFragment.newInstance(position, id);
	}
}
