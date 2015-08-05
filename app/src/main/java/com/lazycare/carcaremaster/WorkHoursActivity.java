package com.lazycare.carcaremaster;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.data.ArtificerFilterTimeClass;
import com.lazycare.carcaremaster.data.Temp;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.fragment.WorkFragment;
import com.lazycare.carcaremaster.service.WorkTimeServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 工时管理父界面
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
@SuppressLint("NewApi")
public class WorkHoursActivity extends BaseActivity {
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private List<String> lstDate;
    HashMap<Integer, Integer> hmWorkHours = new HashMap<>();// 11\7个数据
    public static HashMap<Integer, Boolean> hmWorkStates = new HashMap<>();// 11\7个数据
    public static List<Boolean> list = new ArrayList<Boolean>(); // true为已预约
    LoadMyWorkHoursHandler mHandler = new LoadMyWorkHoursHandler(WorkHoursActivity.this);
    private Button btn_save;
    private SysApplication app;
    private String curTime = "";
    private WorkTimeServices service;

    @Override
    public void onPause() {
        super.onPause();
        hmWorkStates.clear();
        list.clear();
    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_workhours);

    }

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("工时管理");

    }

    @Override
    public void initView() {
        app = (SysApplication) getApplication();
        curTime = getSharePreferences().getString(Config.TIME,
                "2015-05-18");
        service = WorkTimeServices.getInstance(mContext);
        id = getSharePreferences().getString(Config.ID, "0");
        lstDate = CommonUtil.getDateList(curTime);
        btn_save = (Button) this.findViewById(R.id.btn_save);
        mViewPager = (ViewPager) findViewById(R.id.pager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hmWorkStates.clear();
        list.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hmWorkStates.clear();
        list.clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoadData();
        btn_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (NetworkUtil.isNetworkAvailable(mContext)) {
                    String selectTime = CommonUtil.getInBusyTime(curTime, list);
                    mDialog = CustomProgressDialog.showCancelable(mContext,
                            "保存中...");
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("id", id);
                    map.put("start_time", selectTime);
                    Log.d(TAG, "selectTime:" + selectTime);
                    TaskExecutor.Execute(new DataRunnable(
                            WorkHoursActivity.this, "/ArtificerFilterTime/add",
                            mHandler, Config.WHAT_TWO, map));
                } else {
                    showToast("您还没联网哦,亲");
                }

            }
        });
    }

    public static void setState(int position, boolean state) {
        list.set(position, state);
    }

    /**
     * 获取数据
     */
    private void LoadData() {

        // 然后根据States来获取实际的选择情况
        if (list.size() != 0) {
            list.clear();
        }
        if (NetworkUtil.isNetworkAvailable(mContext)) {// 有网
            if (service.hasData()) {// 本地有数据
                // if (getSharePreferences().getBoolean(Configuration.TIME_UPDATE,//
                // 时间超过一天
                // false)) {
                // // 获取网络数据
                // mDialog = CustomProgressDialog.showCancelable(mContext,
                // "正在加载...");
                // Map<String, String> map = new HashMap<String, String>();
                // map.put("artificer", id);
                // TaskExecutor.Execute(new DataRunnable(
                // WorkHoursActivity.this, "/ArtificerCalendar/get",
                // mHandler, Config.WHAT_ONE, map));
                // } else {
                // 数据库抓数据
                list = service.getList();
                mSectionsPagerAdapter = new SectionsPagerAdapter(
                        getSupportFragmentManager(), list);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                // }
            } else {
                // 获取网络数据
                mDialog = CustomProgressDialog.showCancelable(mContext,
                        "加载中...");
                Map<String, String> map = new HashMap<String, String>();
                map.put("artificer", id);
                TaskExecutor.Execute(new DataRunnable(WorkHoursActivity.this,
                        "/ArtificerCalendar/get", mHandler, Config.WHAT_ONE,
                        map));
            }
        } else {
            if (service.hasData()) {
                // 数据库抓数据
                list = service.getList();
                mSectionsPagerAdapter = new SectionsPagerAdapter(
                        getSupportFragmentManager(), list);
                mViewPager.setAdapter(mSectionsPagerAdapter);
            } else {
                showToast("您还没联网哦,亲");
            }
        }
    }

    /**
     * <p>
     * 标 题: LoginHandler
     * </p>
     * <p>
     * 描 述: 主线程处理
     * </p>
     * <p>
     * 版 权: Copyright (c) Administrator
     * </p>
     * <p>
     * 创建时间: Sep 25, 2014 3:06:35 PM
     * </p>
     *
     * @author Administrator
     */
    @SuppressLint("HandlerLeak")
    private class LoadMyWorkHoursHandler extends Handler {

        private WeakReference<Activity> mWeak;
        private String TAG;

        public LoadMyWorkHoursHandler(Activity activity) {
            mWeak = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);

        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(Activity activity, int what, String json) {
            switch (what) {
                case Config.WHAT_ONE:

                    Log.d(TAG, json);
                    try {
                        Gson gson = new Gson();
                        if (json != null) {

                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");

                            if (error.equals("0")) {
                                List<ArtificerFilterTimeClass> lstAFTC = new ArrayList<>();
                                lstAFTC = gson
                                        .fromJson(
                                                data,
                                                new TypeToken<List<ArtificerFilterTimeClass>>() {
                                                }.getType());
                                Log.i("gmyboy", "lstAFTC" + lstAFTC.size());
                                // 取出所有true/false 放入list
                                for (ArtificerFilterTimeClass aftc : lstAFTC) {
                                    List<Temp> temp = aftc.getTime();
                                    Log.i("gmyboy", "temp" + temp.size());
                                    for (Temp t : temp) {

                                        if (t.getStatus().equals("1")) {
                                            list.add(false);
                                        } else {
                                            list.add(true);
                                        }

                                    }
                                }
                                // 存进数据库
                                saveData2Local("load");
                                // mSectionsPagerAdapter.notifyDataSetChanged();


                            } else {
                                // 初始化一个空的
                                Toast.makeText(WorkHoursActivity.this, msg,
                                        Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < Config.TIME_HOURS
                                        * Config.TIME_DAY; i++) {
                                    list.set(i, false);
                                }
                                mSectionsPagerAdapter = new SectionsPagerAdapter(
                                        getSupportFragmentManager(), list);
                                mViewPager.setAdapter(mSectionsPagerAdapter);
                                mDialog.dismiss();
                            }

                        } else
                            mDialog.dismiss();
                    } catch (Exception e) {
                        mDialog.dismiss();
                    }
                    break;
                case Config.WHAT_TWO:
                    Log.d(TAG, json);
                    try {
                        JSONObject jb = new JSONObject(json);
                        String error = jb.getString("error");
                        String msg = jb.getString("msg");
                        if (error.equals("0")) {
                            // 存进数据库
                            saveData2Local("save");

                        } else {// 保存出错
                            mDialog.dismiss();
                            LoadData(); // 重新加载ui
                            showToast(msg);
                        }
                    } catch (Exception e) {
                        showToast("保存出错");
                        mDialog.dismiss();
                    }
                    break;
                case Config.WHAT_THREE://存入数据库延时操作
                    if (json.equals("save")) {
                        showToast("保存成功");
                    } else {
                        mSectionsPagerAdapter = new SectionsPagerAdapter(
                                getSupportFragmentManager(), list);

                        mViewPager.setAdapter(mSectionsPagerAdapter);
                    }
                    mDialog.dismiss();
                    break;
                default: // 获取数据返回信息
                    break;
            }
        }
    }

    private void saveData2Local(final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                service.savaData(list);
                Message message = new Message();
                message.what = Config.WHAT_THREE;
                message.obj = str;
                mHandler.sendMessage(message);
            }
        }).start();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<Boolean> list;

        public SectionsPagerAdapter(FragmentManager fm, List<Boolean> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            List<Boolean> list_temp = null;

            fragment = new WorkFragment();

            list_temp = new ArrayList<Boolean>();
            for (int i = Config.TIME_HOURS * position; i < Config.TIME_HOURS
                    * (position + 1); i++) {
                list_temp.add(list.get(i));
            }
            bundle.putSerializable("data", (Serializable) list_temp);
            bundle.putInt("key", position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return Config.TIME_DAY;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            return lstDate.get(position).toUpperCase(l);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                WorkHoursActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
