package com.lazycare.carcaremaster.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lazycare.carcaremaster.fragment.AppointmentFragment;
import com.lazycare.carcaremaster.fragment.WithdrawFragment;

public class WithdrawPageAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = {"支付宝", "银行卡"};
    private String id;
    private String money;

    public WithdrawPageAdapter(FragmentManager fm, String id, String money) {
        super(fm);
        this.id = id;
        this.money = money;
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
        return WithdrawFragment.newInstance(position,id,money);
    }
}
