package com.lazycare.carcaremaster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

import com.lazycare.carcaremaster.adapter.AccountInfoPageAdapter;
import com.lazycare.carcaremaster.fragment.ModifyPhotoFragment;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;

/**
 * 账号信息
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MyAccountInfoActivity extends BaseActivity {


    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    MenuItem item;

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("账号信息");
    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_myappointment);
    }


    @Override
    public void initView() {
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new AccountInfoPageAdapter(
                getSupportFragmentManager(), id, username));
        mViewPager.setOffscreenPageLimit(1);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        switch (arg0) {
                            case 0:
                                item.setVisible(false);
                                break;
                            case 1:
                                item.setVisible(false);
                                break;
//                                case 2:
//                                    item.setVisible(false);
//                                    break;
                        }
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

    private void initTabsValue() {
        // 底部游标颜色
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#fa5f19"));
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#ffffff"));
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.gb_select_textcolor));
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(Color.BLACK);
        mPagerSlidingTabStrip.setTextSize(getResources().getDimensionPixelOffset(R.dimen.username_fontsize));
        mPagerSlidingTabStrip.setShouldExpand(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.accountinfo, menu);
        item = menu.findItem(R.id.action_delete);

        item.setVisible(false);
        item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ModifyPhotoFragment.paths.clear();
                ModifyPhotoFragment.adapter.notifyDataSetChanged();
                // adapter.notifyDataSetChanged();
                // vp_pager.setCurrentItem(2);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                MyAccountInfoActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
