package com.lazycare.carcaremaster;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.QuestionPageAdapter;
import com.lazycare.carcaremaster.adapter.QuestionsAdapter;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.fragment.BaseFragment;
import com.lazycare.carcaremaster.fragment.QuestionsFragment;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车主问题列表页 （fragment+radiogroup）
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsListActivity2 extends BaseActivity {
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private static String id = "";
    public QuestionPageAdapter pageadapter;
    public static int flag = 0;// 标志位用来判断返回到问题列表用不用刷新数据 0不刷新 1 刷新
    private AudioPlayer player;
    private int NOTITYPE = -1;// 通知跳转标志，位默认是@我
    private ContacterReceiver receiver = null;

    @Override
    protected void onDestroy() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // 卸载广播接收器
        unregisterReceiver(receiver);
        if (player != null && player.isPlaying()) {
            player.pause();
        }
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
    protected void onRestart() {
        super.onRestart();
        if (player == null) {
            Log.d("gmyboy", "初始化audio");
            player = new AudioPlayer(mContext, "right");
        }
        if (flag == 1) {
//            adapter.removeAll();
//            refreshList();
            flag = 0;
        }
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
        NOTITYPE = getIntent().getIntExtra("type", -1);
        id = getSharePreferences().getString(Configuration.ID, "0");
        player = new AudioPlayer(mContext, "right");
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        pageadapter = new QuestionPageAdapter(getSupportFragmentManager(), id);
        mViewPager.setAdapter(pageadapter);
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
        Log.d("gmyboy", "" + NOTITYPE);
        if (NOTITYPE == 0) {
//            rb_oper.check(R.id.rb_public);
//            type = 0;
//            refreshList();
            mViewPager.setCurrentItem(1);
        } else if (NOTITYPE == 2) {
//            rb_oper.check(R.id.rb_complete);
//            type = 2;
//            refreshList();
            mViewPager.setCurrentItem(2);
        } else if (NOTITYPE == 1) {
//            rb_oper.check(R.id.rb_private);
//            type = 1;
//            refreshList();
            mViewPager.setCurrentItem(0);
        } else {

        }
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
            // 收到新回复并且当前在我的回复tab 刷新
            if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
//                mDialog = CustomProgressDialog.showCancelable(mContext,
//                        "加载中...");
//                // 刷新数据
//
//                lstQuestions.clear();
//                // 滑动时adapter.getcount会报错
//                if (adapter != null) {
//                    adapter.notifyDataSetChanged();
//                }
//                refreshList();
//                flag = 0;
//                mDialog.dismiss();
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
                // boolean isSuccess = intent.getBooleanExtra(
                // Constant.RECONNECT_STATE, false);
                // showToast("重连");
            } else if (Constant.NEW_QUESTION_ACTION.equals(action)) {
//                mDialog = CustomProgressDialog.showCancelable(mContext,
//                        "加载中...");
//                // 刷新数据
//                lstQuestions.clear();
//                // 滑动时adapter.getcount会报错
//                if (adapter != null) {
//                    adapter.notifyDataSetChanged();
//                }
//                refreshList();
//                flag = 0;
//                mDialog.dismiss();
            }
        }
    }
}
