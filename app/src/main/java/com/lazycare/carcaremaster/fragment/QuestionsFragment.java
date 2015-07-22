package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.QuestionDetailWithReplyActivity;
import com.lazycare.carcaremaster.QuestionsListActivity2;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.QuestionsAdapter;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;
import com.umeng.analytics.MobclickAgent;

/**
 * 问题列表
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsFragment extends BaseFragment {
    private ListView listView;
    int dataSize = 0;
    String id = "";
    public static String TAG = "QuestionsFragment";
    private Dialog mDialog;
    Handler mHandler;
    QuestionsAdapter adapter = null;
    List<QuestionClass> lstQuestions = new ArrayList<>();
    public static int flag = 0;// 标志位用来判断返回到问题列表用不用刷新数据 0不刷新 1 刷新
    int start = 0;// 页数
    int limit = 10;// 每页多少个
    int type = 1;// 问题类型 0:群发的 1: 1对1的 2:已完成的
    boolean isLoading = false;
    private int pageIndex = 1;
    private QuestionListServices services = QuestionListServices
            .getInstance(getActivity());
    private AudioPlayer player;

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

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    public static QuestionsFragment newInstance(int mtype, String mid) {
        QuestionsFragment f = new QuestionsFragment();
        Bundle b = new Bundle();
        b.putInt("type", mtype);
        b.putString("id", mid);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_question, null);
            id = getArguments().getString("id");
            type = getArguments().getInt("type");
            InitView(view);
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

    private void InitView(View view) {
        mHandler = new LoadQuestionsHandler(this);
        listView = (ListView) view.findViewById(R.id.lv_questions);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                // Toast.makeText(QuestionsListActivity.this, "项目被点击",
                // Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(getActivity(),
                        QuestionDetailWithReplyActivity.class);
                intent.putExtra("question_id",
                        adapter.listQuestion.get(position).getId());
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible || mHasLoadedOnce) {
            return;
        }
        if (type == 0) {// 抢单不存入本地的
            if (NetworkUtil.isNetworkAvailable(getActivity())) {// 加载网络
                mDialog = CustomProgressDialog.showCancelable(getActivity(),
                        "正在加载数据...");
                Map<String, String> map = new HashMap<String, String>();
                start = (pageIndex - 1) * limit;
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(getActivity(),
                        "/Questions/getQuestionsList", mHandler, type, map));
            } else {
                Toast.makeText(getActivity(), "亲,还没网哦", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {// @我 我的回复
            if (NetworkUtil.isNetworkAvailable(getActivity())) {// 加载网络
                mDialog = CustomProgressDialog.showCancelable(getActivity(),
                        "正在加载数据...");
                Map<String, String> map = new HashMap<String, String>();
                start = (pageIndex - 1) * limit;
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(getActivity(),
                        "/Questions/getQuestionsList", mHandler, type, map));
            } else {// 加载本地

                List<QuestionClass> lstQuestion = services.getList(
                        String.valueOf(type), pageIndex - 1, 10, id);
                adapter = new QuestionsAdapter(lstQuestion, getActivity(),
                        player);
                // .setText("@我(" + services.getCount("1") + ")");
                // rb_public.setText("抢单");
                // rb_complete.setText("我的回复(" + services.getCount("2") + ")");
                listView.setAdapter(adapter);
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
    private class LoadQuestionsHandler extends Handler {

        private WeakReference<QuestionsFragment> mWeak;

        public LoadQuestionsHandler(QuestionsFragment activity) {
            mWeak = new WeakReference<QuestionsFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QuestionsFragment activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            DialogUtil.dismiss(activity.mDialog);
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
                    isLoading = false;
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
//                        QuestionsListActivity2.TITLES[0]="@我(" + my + ")";
//                        QuestionsListActivity2.TITLES[1]="抢单(" + pub + ")";
//                        QuestionsListActivity2.TITLES[2]="我的回复(" + complate + ")";
//                        QuestionsListActivity2.adapter.startUpdate();
                        // rb_private.setTex
                        List<QuestionClass> temp = gson.fromJson(list,
                                new TypeToken<List<QuestionClass>>() {
                                }.getType());
                        for (QuestionClass qc : temp) {
                            lstQuestions.add(qc);
                        }
                        if (pageIndex == 1) {
                            adapter = new QuestionsAdapter(lstQuestions,
                                    getActivity(), player);
                            listView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        mHasLoadedOnce = true;
                        // 上拉加载更多
                        listView.setOnScrollListener(new OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(
                                    AbsListView view, int scrollState) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onScroll(AbsListView view,
                                                 int firstVisibleItem,
                                                 int visibleItemCount, int totalItemCount) {
                                // TODO Auto-generated method stub
                                if (totalItemCount == firstVisibleItem
                                        + visibleItemCount) {
                                    if (!isLoading
                                            && adapter.getCount() != 0
                                            && adapter.getCount() < dataSize) {
                                        pageIndex++;
                                        isLoading = true;
                                        lazyLoad();// 再次加载数据
                                    }
                                }
                            }
                        });
                        if (type != 0) {
                            // @wo 我的回复 存进本地数据库
                            services.savaData(lstQuestions, type, id);
                        }

                    } else {
                    }
                    // showToast(msg);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            }
        }
    }
}
