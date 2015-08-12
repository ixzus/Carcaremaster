package com.lazycare.carcaremaster.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.QuestionDetailWithReplyActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.QuestionsAdapter;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;
import com.lazycare.carcaremaster.widget.PagerSlidingTabStrip;
import com.lazycare.carcaremaster.widget.RefreshLayout;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问题列表
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsFragment extends BaseFragment implements RefreshLayout.OnRefreshListener, RefreshLayout.OnLoadListener {

    private ListView lvQuestions;
    private RefreshLayout swiperefresh;
    private String id = "";
    public static String TAG = "QuestionsFragment";
    //每一栏对应的数据总数
    private int dataSize = 0;
    private Handler mHandler;

    private QuestionsAdapter adapter = null;
    public static int flag = 0;// 标志位用来判断返回到问题列表用不用刷新数据 0不刷新 1 刷新
    private int start = 0;// 页数
    private int limit = 10;// 每页多少个
    private int pageIndex = 1;//页码
    private int type = 1;// 问题类型 0:群发的 1: 1对1的 2:已完成的
    private QuestionListServices services = QuestionListServices.getInstance(getActivity());
    private AudioPlayer player;

    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    private boolean mHasLoadedOnce;
    private static PagerSlidingTabStrip mPagerSlidingTabStrip;
    private View view;

    @Override
    public void onPause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
        if (flag == 1) {
            autoRefresh();
            flag = 0;
        }
    }

    @Override
    public void onDestroy() {
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        super.onDestroy();
    }

    public static QuestionsFragment newInstance(int mtype, String mid, PagerSlidingTabStrip pagerSlidingTabStrip) {
        QuestionsFragment f = new QuestionsFragment();
        mPagerSlidingTabStrip = pagerSlidingTabStrip;
        Bundle b = new Bundle();
        b.putInt("type", mtype);
        b.putString("id", mid);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_question, null);
            id = getArguments().getString("id");
            type = getArguments().getInt("type");
            initView(view);
            //view 已经准备好去加载数据
            isPrepared = true;
            lazyLoad();
        }
        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    private void initView(View view) {
        mHandler = new LoadQuestionsHandler(this);
        player = new AudioPlayer(getActivity(), "right");
        swiperefresh = (RefreshLayout) view.findViewById(R.id.swiperefresh);
        lvQuestions = (ListView) view.findViewById(R.id.lv_questions);
        // 刷新时，指示器旋转后变化的颜色
        swiperefresh.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        swiperefresh.setFooterView(getActivity(), lvQuestions, R.layout.view_refresh_footer);
        swiperefresh.setOnRefreshListener(this);
        swiperefresh.setOnLoadListener(this);
        adapter = new QuestionsAdapter(getActivity(), player);
        lvQuestions.setAdapter(adapter);
        lvQuestions.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), QuestionDetailWithReplyActivity.class);
                intent.putExtra("question_id", adapter.listQuestion.get(position).getId());
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }

    /**
     * 刷新列表页
     *
     * @param
     */
    private void autoRefresh() {
        // 刷新数据
        adapter.removeAll();
        pageIndex = 1;
        isPrepared = true;
        mHasLoadedOnce = false;
        isVisible = true;
        Log.e("gmyboy", isPrepared + "  " + isVisible + "   " + mHasLoadedOnce + "  " + type);
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        if (type == 0) {// 抢单不存入本地的
            if (NetworkUtil.isNetworkAvailable(getActivity())) {// 加载网络
                swiperefresh.setRefreshing(true);
                Map<String, String> map = new HashMap<>();
                start = (pageIndex - 1) * limit;
                Log.e("gmyboy", start + "  " + pageIndex + "   " + "  " + dataSize);
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(getActivity(), "/Questions/getQuestionsList", mHandler, type, map));
            } else {
                CommonUtil.showSnack(swiperefresh, "亲,还没网哦");
            }
        } else {// @我 我的回复
            if (NetworkUtil.isNetworkAvailable(getActivity())) {// 加载网络
                swiperefresh.setRefreshing(true);
                Map<String, String> map = new HashMap<>();
                start = (pageIndex - 1) * limit;
                Log.e("gmyboy", start + "  " + pageIndex + "   " + "  " + dataSize);
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(getActivity(), "/Questions/getQuestionsList", mHandler, type, map));
            } else {
                // 加载本地
                List<QuestionClass> lstQuestion = services.getList(String.valueOf(type), pageIndex - 1, 10, id);
                adapter = new QuestionsAdapter(getActivity(), player, lstQuestion);
                lvQuestions.setAdapter(adapter);
            }
        }
    }

    @Override
    public void onRefresh() {
        autoRefresh();
    }

    @Override
    public void onLoad() {
        if (adapter.getCount() != 0 && adapter.getCount() < dataSize) {
            pageIndex++;
            isPrepared = true;
            mHasLoadedOnce = false;
            lazyLoad();// 再次加载数据
        } else {

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
    private class LoadQuestionsHandler extends Handler {

        private WeakReference<QuestionsFragment> mWeak;

        public LoadQuestionsHandler(QuestionsFragment activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QuestionsFragment activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            swiperefresh.setRefreshing(false);
            swiperefresh.setLoading(false);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(QuestionsFragment activity, int what, String json) {

            // 获取第一次数据返回信息
            Log.d(TAG, json);
            if (!CommonUtil.isEmpty(json)) {
                try {
                    Gson gson = new Gson();
                    JSONObject jb = new JSONObject(json);
                    String error = jb.getString("error");
                    String msg = jb.getString("msg");
                    String data = jb.getString("data");
                    if (error.equals("0")) {
                        JSONObject jd = new JSONObject(data);
                        String count = jd.getString("count");
                        String list = jd.getString("list");
                        JSONObject jcount = new JSONObject(count);

                        String my = jcount.getString("my");
                        String complate = jcount.getString("complate");
                        String pub = jcount.getString("pub");

                        if (type == 0) {
                            dataSize = Integer.parseInt(pub);
                        } else if (type == 1) {
                            dataSize = Integer.parseInt(my);
                        } else if (type == 2) {
                            dataSize = Integer.parseInt(complate);
                        }
                        mPagerSlidingTabStrip.setTabText(0, "@我 (" + my + ")");
                        mPagerSlidingTabStrip.setTabText(1, "抢单 (" + pub + ")");
                        mPagerSlidingTabStrip.setTabText(2, "我的回复 (" + complate + ")");
                        //获取到所有数据集
                        List<QuestionClass> temp = gson.fromJson(list, new TypeToken<List<QuestionClass>>() {
                        }.getType());
                        //没有数据时显示提示
                        if (temp.size() == 0) {
                            CommonUtil.showSnack(swiperefresh, "空空如也");
                        } else {
                            for (QuestionClass qc : temp) {
                                adapter.addNewItem(qc);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        mHasLoadedOnce = true;
//                        if (type != 0) {
//                            // @wo 我的回复 存进本地数据库
//                            services.savaData(lstQuestions, type, id);
//                        }
                    } else {
                        CommonUtil.showSnack(swiperefresh, msg);
                    }
                } catch (Exception e) {

                }
            }
        }
    }
}
