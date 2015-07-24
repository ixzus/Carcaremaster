package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.AppointmentDetailActivity;
import com.lazycare.carcaremaster.AppraiseListActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.AppointmentListAdapter;
import com.lazycare.carcaremaster.data.AppointmentClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 问题列表
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    List<AppointmentClass> lstAppointment = new ArrayList<AppointmentClass>();
    private int pageIndex = 1;
    int dataSize = 0;
    AppointmentListAdapter adapter = null;
    boolean isLoading = false;
    private ListView listView;
    LinearLayout ll_appraiseoper;
    Handler mHandler = new LoadMyAppointmentHandler(getActivity());
    String id = "";
    String orderType = "add";// order 排序方式, add ： 按下单时间排序 , book : 按预约时间排序 ,
    // 留空默认按下单时间排
    /**
     * 进度条
     */
    private Dialog mDialog;
    public static int FLAG = 0;// 标志位用来判断返回到列表用不用刷新数据 0不刷新 1 刷新

    // 广播用来接收消息
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.NEW_APPOINTMENT.equals(action)) {
                mHasLoadedOnce = false;
                if (NetworkUtil.isNetworkAvailable(getActivity())) {
                    adapter.removeAll();
                    lazyLoad();// 再次加载数据
                } else {
                    Toast.makeText(getActivity(), "请检查您的网络", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

    };
    private View view;
    private int mCurIndex = -1;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    //评价
    public static String evaluationType = "";// 评价方式, 3:好评 2:中评 1:差评
    //刷新布局
    private SwipeRefreshLayout refreshLayout;

    public static AppointmentFragment newInstance(int position, String evaluationType) {
        AppointmentFragment f = new AppointmentFragment();
        Bundle b = new Bundle();
        b.putInt("POSITION", position);
        b.putString("evaluationType", evaluationType);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FLAG == 1) {
            mHasLoadedOnce = false;
            if (NetworkUtil.isNetworkAvailable(getActivity())) {
                adapter.removeAll();
                lazyLoad();// 再次加载数据
            } else {
                Toast.makeText(getActivity(), "请检查您的网络", Toast.LENGTH_SHORT)
                        .show();
            }
            FLAG = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            mCurIndex = getArguments().getInt("POSITION");
            id = getActivity().getSharedPreferences(Configuration.USERINFO, 0)
                    .getString(Configuration.ID, "0");
            evaluationType = getArguments().getString("evaluationType");
            switch (mCurIndex) {
                case 0:
                    orderType = "add";
                    break;
                case 1:
                    orderType = "book";
                    break;
                case 2:
                    orderType = "evaluation";
                    break;
                default:
                    break;
            }
            view = inflater.inflate(R.layout.fragment_app_tab, null);
            adapter = new AppointmentListAdapter(getActivity());
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
            listView = (ListView) view.findViewById(R.id.lv_appointments);
            listView.setAdapter(adapter);
            isPrepared = true;
            // 刷新时，指示器旋转后变化的颜色
            refreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
            refreshLayout.setOnRefreshListener(this);
            lazyLoad();
            listView.setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view,
                                     int firstVisibleItem, int visibleItemCount,
                                     int totalItemCount) {
                    // TODO Auto-generated method stub
                    if (totalItemCount == firstVisibleItem
                            + visibleItemCount) {
                        if (!isLoading && adapter.getCount() != 0
                                && adapter.getCount() < dataSize) {
                            pageIndex++;
                            isLoading = true;
                            lazyLoad();// 再次加载数据
                        }
                    }
                }
            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("order_id", adapter.listAppointment
                            .get(position).getId());
                    intent.putExtra("standard", adapter.listAppointment
                            .get(position).getStandard());
                    intent.setClass(getActivity(),
                            AppointmentDetailActivity.class);
                    startActivity(intent);
                }
            });
        }
        //注册
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_APPOINTMENT);
        getActivity().registerReceiver(receiver, filter);
        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onRefresh() {
        mHasLoadedOnce = false;
        if (NetworkUtil.isNetworkAvailable(getActivity())) {
            adapter.removeAll();
            if (!isPrepared || !isVisible || mHasLoadedOnce) {
                return;
            }
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", id);
            if (mCurIndex == 2)
                map.put("evaluation", evaluationType);
            map.put("order", orderType);
            TaskExecutor.Execute(new DataRunnable(getActivity(),
                    "/Order/getOrderList", mHandler, map));
        } else {
            Toast.makeText(getActivity(), "请检查您的网络", Toast.LENGTH_SHORT)
                    .show();
            refreshLayout.setRefreshing(false);
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
    private class LoadMyAppointmentHandler extends Handler {

        private WeakReference<Activity> mWeak;

        public LoadMyAppointmentHandler(Activity activity) {
            mWeak = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            mDialog.dismiss();
            refreshLayout.setRefreshing(false);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(Activity activity, int what, String json) {
            switch (what) {
                default: // 获取数据返回信息
                    Log.d("gmy", json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            Gson gson = new Gson();
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                List<AppointmentClass> lstAppointment = gson
                                        .fromJson(
                                                data,
                                                new TypeToken<List<AppointmentClass>>() {
                                                }.getType());
                                for (AppointmentClass ac : lstAppointment) {
                                    adapter.addNewItem(ac);
                                }
                                mHasLoadedOnce = true;
                                adapter.notifyDataSetChanged();
                            } else
                                Toast.makeText(getActivity(), msg,
                                        Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d("gmy", e.getMessage());
                        }

                    }
                    break;
            }
        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        mDialog = CustomProgressDialog.show(getActivity(),
                "加载中...");
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        if (mCurIndex == 2)
            map.put("evaluation", evaluationType);
        map.put("order", orderType);
        TaskExecutor.Execute(new DataRunnable(getActivity(),
                "/Order/getOrderList", mHandler, map));
    }

}
