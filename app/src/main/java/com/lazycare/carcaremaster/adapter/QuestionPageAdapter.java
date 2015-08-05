package com.lazycare.carcaremaster.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.lazycare.carcaremaster.fragment.AppointmentFragment;
import com.lazycare.carcaremaster.fragment.QuestionsFragment;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;

/**
 * Created by Administrator on 2015/7/13.
 */
public class QuestionPageAdapter extends FragmentPagerAdapter {
    public String[] titles = {"@我", "抢单", "我的回复"};
    private static String id = "";
    private FragmentManager fm;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private boolean[] fragmentsUpdateFlag = {false, false, false};
    private int type = 1;

    public QuestionPageAdapter(FragmentManager fm, String id, PagerSlidingTabStrip mPagerSlidingTabStrip) {
        super(fm);
        this.id = id;
        this.fm = fm;
        this.mPagerSlidingTabStrip = mPagerSlidingTabStrip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean[] getFragmentsUpdateFlag() {
        return fragmentsUpdateFlag;
    }

    public void setFragmentsUpdateFlag(boolean[] fragmentsUpdateFlag) {
        this.fragmentsUpdateFlag = fragmentsUpdateFlag;
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
    public Object instantiateItem(ViewGroup container, int position) {
        //得到缓存的fragment
        Fragment fragment = (Fragment) super.instantiateItem(container,
                position);
        //得到tag
        String fragmentTag = fragment.getTag();

        if (fragmentsUpdateFlag[position % fragmentsUpdateFlag.length]) {
            //如果这个fragment需要更新
            FragmentTransaction ft = fm.beginTransaction();
            //移除旧的fragment
            ft.remove(fragment);
            //换成新的fragment
            fragment = QuestionsFragment.newInstance(type, id, mPagerSlidingTabStrip);
            //添加新fragment时必须用前面获得的tag
            ft.add(container.getId(), fragment, fragmentTag);
            ft.attach(fragment);
            ft.commit();
            //复位更新标志
            fragmentsUpdateFlag[position % fragmentsUpdateFlag.length] = false;
        }
        return fragment;
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
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
        return QuestionsFragment.newInstance(type, id, mPagerSlidingTabStrip);
    }
}
