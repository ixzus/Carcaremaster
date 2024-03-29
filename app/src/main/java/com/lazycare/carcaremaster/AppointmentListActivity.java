package com.lazycare.carcaremaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import com.lazycare.carcaremaster.adapter.AppointmentPageAdapter;
import com.lazycare.carcaremaster.fragment.AppointmentFragment;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;

/**
 * 我的预约
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentListActivity extends BaseActivity {
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private AppointmentPageAdapter pageAdapter;
    public static String evaluationType = "3";// 评价方式, 1:好评 2:中评 3:差评
    // 广播用来接收消息
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.NEW_APPOINTMENT.equals(action)) {
                switch (mViewPager.getCurrentItem()) {
                    case 0:
                        pageAdapter.setFragmentsUpdateFlag(new boolean[]{true, false, false});
                        break;
                    case 1:
                        pageAdapter.setFragmentsUpdateFlag(new boolean[]{false, true, false});
                        break;
                    case 2:
                        pageAdapter.setFragmentsUpdateFlag(new boolean[]{false, false, true});
                        break;
                }


                pageAdapter.notifyDataSetChanged();
            }
        }

    };

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_APPOINTMENT);
        registerReceiver(receiver, filter);
    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_myappointment);
    }

    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("我的预约");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initView() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        pageAdapter = new AppointmentPageAdapter(
                getSupportFragmentManager(), evaluationType);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                    }

                    @Override
                    public void onPageScrolled(int arg0, float arg1, int arg2) {
                    }

                    @Override
                    public void onPageScrollStateChanged(int arg0) {

                    }
                });
        initTabsValue();
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        mPagerSlidingTabStrip.setExpandTab(2);
        mPagerSlidingTabStrip.setExpandMenuId(R.menu.view_appraise_pop);
        mPagerSlidingTabStrip.setMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.good:
                        evaluationType = "3";
                        break;
                    case R.id.mid:
                        evaluationType = "2";
                        break;
                    case R.id.bad:
                        evaluationType = "1";
                        break;
                }
                //选择性的更新第三个tab
                pageAdapter.setFragmentsUpdateFlag(new boolean[]{false, false, true});
                //设置评价排序规则
                pageAdapter.setEvaluationType(evaluationType);
                pageAdapter.notifyDataSetChanged();
                return false;
            }
        });
        // 底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(getResources().getColor(R.color.tab_indicator));
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(getResources().getColor(R.color.statusbar_bg));
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.tab_txt_selected));
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(getResources().getColor(R.color.tab_txt_normal));
        mPagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelOffset(R.dimen.username_fontsize));
        mPagerSlidingTabStrip.setShouldExpand(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
