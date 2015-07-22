package com.lazycare.carcaremaster.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lazycare.carcaremaster.fragment.QuestionsFragment;

/**
 * Created by Administrator on 2015/7/13.
 */
public class QuestionPageAdapter extends FragmentPagerAdapter {
    public String[] titles = {"@我", "抢单", "我的回复"};
    private static String id = "";

    public QuestionPageAdapter(FragmentManager fm, String id) {
        super(fm);
        this.id = id;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Fragment getItem(int position) {
        int type = 1;
        switch (position) {
            case 0:
                type = 1;
                break;
            case 1:
                type = 0;
                break;
            case 2:
                type = 2;
                break;
        }
        return QuestionsFragment.newInstance(type, id);
    }
}
