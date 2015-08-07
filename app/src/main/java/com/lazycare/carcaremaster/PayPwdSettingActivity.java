package com.lazycare.carcaremaster;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;

import com.lazycare.carcaremaster.adapter.PaySettingPageAdapter;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;

/**
 * 支付密码设置
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
@SuppressLint("NewApi")
public class PayPwdSettingActivity extends BaseActivity {


    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_myappointment);
    }

    @Override
    public void initView() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new PaySettingPageAdapter(
                getSupportFragmentManager(), id));
        mViewPager.setOffscreenPageLimit(1);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new OnPageChangeListener() {

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
                PayPwdSettingActivity.this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("提现密码设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
