package com.lazycare.carcaremaster;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.QuestionsAdapter;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 车主问题列表页
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsListActivity extends BaseActivity implements
        SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView listView;
    List<QuestionClass> lstQuestions = new ArrayList<QuestionClass>();
    private int pageIndex = 1;
    int dataSize = 0;
    QuestionsAdapter adapter = null;
    boolean isLoading = false;
    SearchView searchView;
    String id = "";
    Handler mHandler = new LoadQuestionsHandler(this);
    int start = 0;// 页数
    int limit = 10;// 每页多少个
    int type = 1;// 问题类型 0:群发的 1: 1对1的 2:已完成的
    RadioGroup rb_oper;
    RadioButton rb_private, rb_public, rb_complete;
    /**
     * 进度条
     */
    private Dialog mDialog;
    private QuestionListServices services = QuestionListServices
            .getInstance(this);
    public static int flag = 0;// 标志位用来判断返回到问题列表用不用刷新数据 0不刷新 1 刷新
    private AudioPlayer player;
    private ContacterReceiver receiver = null;
    private int NOTITYPE = -1;// 默认是@我
    private SwipeRefreshLayout refreshLayout;

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
            adapter.removeAll();
            refreshList();
            flag = 0;
        }
    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_questionslist);
    }

    @Override
    public void setActionBarOption() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("问题列表");
    }

    /**
     * 刷新列表页
     *
     * @param
     */
    private void refreshList() {

        pageIndex = 1;
        if (NetworkUtil.isNetworkAvailable(this)) {
            Map<String, String> map = new HashMap<String, String>();
            start = (pageIndex - 1) * limit;
            map.put("id", id);
            map.put("start", String.valueOf(start));
            map.put("limit", String.valueOf(limit));
            map.put("type", String.valueOf(type));
            TaskExecutor.Execute(new DataRunnable(this,
                    "/Questions/getQuestionsList", mHandler, map));
        } else {
            // List<QuestionClass> lstQuestion = services.getList(
            // String.valueOf(type), 0, 3);
            // for (QuestionClass qc : lstQuestion) {
            // adapter.addNewItem(qc);
            // }
            showToast("请检查您的网络");
        }
    }

    @Override
    public void initView() {
        NOTITYPE = getIntent().getIntExtra("type", -1);
        // pullView = (PullToRefreshListView) findViewById(R.id.first_pullview);
        // pullView.setPullLoadEnabled(false);
        // pullView.setScrollLoadEnabled(true);
        //
        // listView = pullView.getRefreshableView();
        // listView.setDividerHeight(20);
        // listView.setSelector(R.color.transparent);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        rb_private = (RadioButton) findViewById(R.id.rb_private);
        rb_public = (RadioButton) findViewById(R.id.rb_public);
        rb_complete = (RadioButton) findViewById(R.id.rb_complete);
        listView = (ListView) findViewById(R.id.lv_questions);
        player = new AudioPlayer(mContext, "right");
        rb_private.setText("@我");
        rb_public.setText("抢单");
        rb_complete.setText("我的回复");
        rb_oper = (RadioGroup) findViewById(R.id.rb_oper);
        Log.d("gmyboy", "" + NOTITYPE);
        if (NOTITYPE == 0) {
            rb_oper.check(R.id.rb_public);
            type = 0;
            refreshList();
        } else if (NOTITYPE == 2) {
            rb_oper.check(R.id.rb_complete);
            type = 2;
            refreshList();
        } else if (NOTITYPE == 1) {
            rb_oper.check(R.id.rb_private);
            type = 1;
            refreshList();
        } else {

        }
        rb_oper.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.rb_private:
                        type = 1;
                        break;
                    case R.id.rb_public:
                        type = 0;
                        break;
                    case R.id.rb_complete:
                        type = 2;
                        break;
                }
                pageIndex = 1;
                lstQuestions.clear();
                // 滑动时adapter.getcount会报错
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }

                // adapter = new QuestionListAdapter(type,
                // QuestionsListActivity.this);
                // pageIndex = 1;
                // listView.setAdapter(adapter);
                loadMoreData();
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Toast.makeText(QuestionsListActivity.this, "项目被点击",
                // Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(QuestionsListActivity.this,
                        QuestionDetailWithReplyActivity.class);
                intent.putExtra("question_id",
                        adapter.listQuestion.get(position).getId());
                intent.putExtra("type", type);
                // 传入未读数目
                intent.putExtra("unread", adapter.listQuestion.get(position)
                        .getUnread());
                startActivity(intent);
            }
        });

        listView.setTextFilterEnabled(true);
        // pullView.setOnRefreshListener(new OnRefreshListener<ListView>() {
        //
        // @Override
        // public void onPullDownToRefresh(
        // PullToRefreshBase<ListView> refreshView) {
        // loadMoreData();
        // }
        //
        // @Override
        // public void onPullUpToRefresh(
        // PullToRefreshBase<ListView> refreshView) {
        // pageIndex++;
        // Map<String, String> map = new HashMap<String, String>();
        // start = (pageIndex - 1) * limit;
        // map.put("id", id);
        // map.put("start", String.valueOf(start));
        // map.put("limit", String.valueOf(limit));
        // map.put("type", String.valueOf(type));
        //
        // TaskExecutor.Execute(new DataRunnable(
        // QuestionsListActivity.this,
        // "/Questions/getQuestionsList", mHandler,Config.WHAT_TWO, map));
        //
        // }
        // });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        id = getSharedPreferences(Configuration.USERINFO, 0).getString(
                Configuration.ID, "1");

        // searchView.setOnQueryTextListener(this);
        // searchView.setSubmitButtonEnabled(false);
        // 初始化广播
        receiver = new ContacterReceiver();
        // 刷新时，指示器旋转后变化的颜色
        refreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        refreshLayout.setOnRefreshListener(this);
        loadMoreData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                QuestionsListActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMoreData() {

        if (type == 0) {// 抢单不存入本地的
            if (NetworkUtil.isNetworkAvailable(this)) {// 加载网络
                mDialog = CustomProgressDialog
                        .showCancelable(this, "加载中...");
                Map<String, String> map = new HashMap<String, String>();
                start = (pageIndex - 1) * limit;
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(this,
                        "/Questions/getQuestionsList", mHandler, map));
            } else {
                showToast("亲,还没网哦");
            }
        } else {// @我 我的回复
            if (NetworkUtil.isNetworkAvailable(this)) {// 加载网络
                mDialog = CustomProgressDialog
                        .showCancelable(this, "加载中...");
                Map<String, String> map = new HashMap<String, String>();
                start = (pageIndex - 1) * limit;
                map.put("id", id);
                map.put("start", String.valueOf(start));
                map.put("limit", String.valueOf(limit));
                map.put("type", String.valueOf(type));
                TaskExecutor.Execute(new DataRunnable(this,
                        "/Questions/getQuestionsList", mHandler, map));
            } else {// 加载本地
                mDialog = CustomProgressDialog
                        .showCancelable(this, "加载中...");
                List<QuestionClass> lstQuestion = services.getList(
                        String.valueOf(type), pageIndex - 1, 10, id);
                adapter = new QuestionsAdapter(lstQuestion,
                        QuestionsListActivity.this, player);
                rb_private.setText("@我(" + services.getCount("1", id) + ")");
                rb_public.setText("抢单");
                rb_complete.setText("我的回复(" + services.getCount("2", id) + ")");
                listView.setAdapter(adapter);
                mDialog.dismiss();
            }
        }

    }

    @Override
    public void onRefresh() {
        // 刷新数据
        lstQuestions.clear();
        // 滑动时adapter.getcount会报错
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        refreshList();
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

        private WeakReference<QuestionsListActivity> mWeak;

        public LoadQuestionsHandler(QuestionsListActivity activity) {
            mWeak = new WeakReference<QuestionsListActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QuestionsListActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            DialogUtil.dismiss(activity.mDialog);
            refreshLayout.setRefreshing(false);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(QuestionsListActivity activity, int what,
                              String json) {

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
                        rb_private.setText("@我(" + my + ")");
                        rb_public.setText("抢单(" + pub + ")");
                        rb_complete.setText("我的回复(" + complate + ")");
                        List<QuestionClass> temp = gson.fromJson(list,
                                new TypeToken<List<QuestionClass>>() {
                                }.getType());
                        if (pageIndex == 1) {
                            for (QuestionClass qc : temp) {
                                lstQuestions.add(qc);
                            }
                            adapter = new QuestionsAdapter(lstQuestions,
                                    QuestionsListActivity.this, player);
                            listView.setAdapter(adapter);
                        } else {
                            for (QuestionClass qc : temp) {
                                lstQuestions.add(qc);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        // 上拉加载更多
                        listView.setOnScrollListener(new OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view,
                                                             int scrollState) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onScroll(AbsListView view,
                                                 int firstVisibleItem, int visibleItemCount,
                                                 int totalItemCount) {

                                if (totalItemCount == firstVisibleItem
                                        + visibleItemCount) {
                                    if (!isLoading && adapter.getCount() != 0
                                            && adapter.getCount() < dataSize) {
                                        pageIndex++;
                                        isLoading = true;
                                        loadMoreData();// 再次加载数据
                                    }
                                }
                            }
                        });
                        // if (type != 0) {
                        // // @wo 我的回复 存进本地数据库
                        // services.savaData(lstQuestions, type, id);
                        // }

                    } else
                        showToast(msg);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

            }
        }
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO Auto-generated method stub
        if (TextUtils.isEmpty(newText)) {
            // Clear the text filter.
            listView.clearTextFilter();
        } else {
            // Sets the initial value for the text filter.
            Toast.makeText(QuestionsListActivity.this, "搜索输入!",
                    Toast.LENGTH_SHORT).show();
            listView.setFilterText(newText.toString());
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        // TODO Auto-generated method stub
        return false;
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
            if (Constant.NEW_MESSAGE_ACTION.equals(action) && type == 2) {
                mDialog = CustomProgressDialog.showCancelable(mContext,
                        "加载中...");
                // 刷新数据
                lstQuestions.clear();
                // 滑动时adapter.getcount会报错
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                refreshList();
                flag = 0;
                mDialog.dismiss();
            } else if (Constant.ACTION_RECONNECT_STATE.equals(action)) {
                // boolean isSuccess = intent.getBooleanExtra(
                // Constant.RECONNECT_STATE, false);
                // showToast("重连");
            } else if (Constant.NEW_QUESTION_ACTION.equals(action) && type != 2) {
                mDialog = CustomProgressDialog.showCancelable(mContext,
                        "加载中...");
                // 刷新数据
                lstQuestions.clear();
                // 滑动时adapter.getcount会报错
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                refreshList();
                flag = 0;
                mDialog.dismiss();
            }
        }
    }
}
