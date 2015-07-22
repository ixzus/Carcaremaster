package com.lazycare.carcaremaster.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.lazycare.carcaremaster.fragment.AppointmentFragment;

public class AppointmentPageAdapter extends FragmentPagerAdapter {
    private final String[] TITLES = {"下单时间", "预约时间", "服务评价"};
    private FragmentManager fm;
    private boolean[] fragmentsUpdateFlag = {false, false, false};
    public String evaluationType = "";// 评价方式, 1:好评 2:中评 3:差评

    public AppointmentPageAdapter(FragmentManager fm, String evaluationType) {
        super(fm);
        this.fm = fm;
        this.evaluationType = evaluationType;
    }

    public boolean[] getFragmentsUpdateFlag() {
        return fragmentsUpdateFlag;
    }

    public void setFragmentsUpdateFlag(boolean[] fragmentsUpdateFlag) {
        this.fragmentsUpdateFlag = fragmentsUpdateFlag;
    }

    public String getEvaluationType() {
        return evaluationType;
    }

    public void setEvaluationType(String evaluationType) {
        this.evaluationType = evaluationType;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
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
            fragment = AppointmentFragment.newInstance(position, evaluationType);
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
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return AppointmentFragment.newInstance(position, evaluationType);
    }
}
