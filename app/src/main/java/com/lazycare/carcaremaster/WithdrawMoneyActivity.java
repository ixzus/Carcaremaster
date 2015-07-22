package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycare.carcaremaster.adapter.AppointmentPageAdapter;
import com.lazycare.carcaremaster.adapter.WithdrawPageAdapter;
import com.lazycare.carcaremaster.data.BankClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;
import com.squareup.picasso.Picasso;

/**
 * 提现
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class WithdrawMoneyActivity extends BaseActivity {
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private String money = "";
    String id = "";

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_myappointment);
    }

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("申请提现");
    }

    @Override
    public void initView() {
        id = getSharePreferences().getString(Configuration.ID, "0");
        money = getIntent().getStringExtra("money");
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new WithdrawPageAdapter(
                getSupportFragmentManager(), id, money));
        mViewPager.setOffscreenPageLimit(1);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mPagerSlidingTabStrip
                .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageSelected(int arg0) {
                        // colorChange(arg0);
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
        mPagerSlidingTabStrip.setIndicatorColor(Color.parseColor("#fa5f19"));
        // tab的分割线颜色
        mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
        // tab背景
        mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#ffffff"));
        // tab底线高度
        mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources()
                        .getDisplayMetrics()));
        // 游标高度
        mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources()
                        .getDisplayMetrics()));
        // 选中的文字颜色
        mPagerSlidingTabStrip.setSelectedTextColor(getResources().getColor(R.color.gb_select_textcolor));
        // 正常文字颜色
        mPagerSlidingTabStrip.setTextColor(Color.BLACK);
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
