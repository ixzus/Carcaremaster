package com.lazycare.carcaremaster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.TypedValue;
import android.view.MenuItem;

import com.lazycare.carcaremaster.adapter.QuestionPageAdapter;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;

import butterknife.Bind;

/**
 * 车主问题列表页 （fragment+radiogroup）
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsListActivity extends BaseActivity {
    @Bind(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @Bind(R.id.pager)
    ViewPager pager;

    public QuestionPageAdapter pageadapter;
    private int NOTITYPE = -1;// 通知跳转标志，位默认是@我
    private ContacterReceiver receiver = null;


    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        filter.addAction(Constant.NEW_QUESTION_ACTION);
        filter.addAction(Constant.ACTION_RECONNECT_STATE);
        registerReceiver(receiver, filter);
        super.onResume();
    }


    @Override
    public void setLayout() {
        setContentView(R.layout.activity_myappointment);
    }

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("问题列表");
    }

    @Override
    public void initView() {
        // 初始化广播
        receiver = new ContacterReceiver();
        NOTITYPE = getIntent().getIntExtra("type", -1);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager = (ViewPager) findViewById(R.id.pager);
        pageadapter = new QuestionPageAdapter(getSupportFragmentManager(), id, tabs);
        pager.setAdapter(pageadapter);
        pager.setOffscreenPageLimit(1);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        if (NOTITYPE == 0) {
            pager.setCurrentItem(1);
        } else if (NOTITYPE == 2) {
            pager.setCurrentItem(2);
        } else {
            pager.setCurrentItem(0);
        }
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        tabs.setIndicatorColor(Color.parseColor("#fa5f19"));
        // tab的分割线颜色
        tabs.setDividerColor(Color.TRANSPARENT);
        // tab背景
        tabs.setBackgroundColor(Color.parseColor("#ffffff"));
        // tab底线高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        // 游标高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        tabs.setSelectedTextColor(getResources().getColor(R.color.gb_select_textcolor));
        // 正常文字颜色
        tabs.setTextColor(Color.BLACK);
        tabs.setTextSize(getResources().getDimensionPixelOffset(R.dimen.username_fontsize));
        tabs.setShouldExpand(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 定义接收消息的广播
     *
     * @author GMY
     * @mail 2275964276@qq.com
     * @date 2015年6月4日
     */
    private class ContacterReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // 收到新回复并且当前在我的回复tab
            if (Constant.NEW_MESSAGE_ACTION.equals(action) && pager.getCurrentItem() == 2) {
                //选择性的更新第三个tab
                pageadapter.setFragmentsUpdateFlag(new boolean[]{false, false, true});
                pageadapter.setType(2);
                pageadapter.notifyDataSetChanged();
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {

            } else if (Constant.NEW_QUESTION_ACTION.equals(action)) {
                if (pager.getCurrentItem() == 0) {
                    pageadapter.setFragmentsUpdateFlag(new boolean[]{true, false, false});
                    pageadapter.setType(1);
                    pageadapter.notifyDataSetChanged();
                } else if (pager.getCurrentItem() == 1) {
                    pageadapter.setFragmentsUpdateFlag(new boolean[]{false, true, false});
                    pageadapter.setType(0);
                    pageadapter.notifyDataSetChanged();
                }

            }
        }
    }
}
