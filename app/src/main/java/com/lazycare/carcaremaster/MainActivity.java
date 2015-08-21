package com.lazycare.carcaremaster;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.MenuItemAdapter;
import com.lazycare.carcaremaster.data.MenuClass;
import com.lazycare.carcaremaster.service.QuestionListServices;
import com.lazycare.carcaremaster.service.WorkTimeServices;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.thread.UpdateManager;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 首页
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MainActivity extends BaseActivity {
    @Bind(R.id.sdv_headphoto)
    SimpleDraweeView sdvHeadphoto;
    @Bind(R.id.tv_username)
    TextView tvUsername;
    @Bind(R.id.tv_userrank)
    TextView tvUserrank;
    @Bind(R.id.gv_menu)
    GridView gvMenu;
    @Bind(R.id.tv_gotoqipei)
    TextView tvGotoqipei;
    @Bind(R.id.flipper)
    ViewFlipper flipper;
    @Bind(R.id.rl_pannel)
    RelativeLayout rlPannel;

    @OnClick(R.id.sdv_headphoto)
    void goUserCenter() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_gotoqipei)
    void goQiPei() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, QiPeiActivity.class);
        startActivity(intent);
    }

    @OnItemClick(R.id.gv_menu)
    void onItemClick(AdapterView<?> parent, View view,
                     int position, long id) {
        Intent intent = new Intent();
        switch (position) {
            case 0:// 车主问题
//                        intent.setClass(MainActivity.this,
//                                QuestionsListActivity.class);
                intent.setClass(MainActivity.this, QuestionsListActivity.class);
                startActivity(intent);
                break;
            case 1:// 我的预约
                intent.setClass(MainActivity.this, AppointmentListActivity.class);
                startActivity(intent);
                break;
            case 2:// 工时管理
                // intent.setClass(MainActivity.this,
                // MyWorkHoursActivity.class);
                intent.setClass(MainActivity.this, WorkHoursActivity.class);
                startActivity(intent);
                break;
            case 3:// 账号信息
                intent.setClass(MainActivity.this, MyAccountInfoActivity.class);
                startActivity(intent);
                break;
            case 4:// 系统消息
                intent.setClass(MainActivity.this, MessageActivity.class);
                // intent.setClass(MainActivity.this,
                // TestRecordActivity.class);
                startActivity(intent);
                break;
            case 5:// 我要提现
                intent.setClass(MainActivity.this, ExchangeMoneyActivity.class);
                startActivity(intent);
                break;
        }
    }

    private Handler mHandler = new MainLoadHandler(this);
    MenuItemAdapter adapter = null;
    private List<MenuClass> lstMenu;
    private String[] names = {"车主问题", "我的预约", "工时管理", "账号信息", "系统消息", "我要提现"};
    private static long date = 0;
    private QuestionListServices services_question = QuestionListServices
            .getInstance(this);
    private WorkTimeServices services_time = WorkTimeServices.getInstance(this);
    private String remindme = "";

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("首页");
        mToolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(mToolbar);
    }

    @Override
    public void initView() {
        setTwiceExit(true);
        adapter = new MenuItemAdapter(MainActivity.this);
        // 添加Item到网格中
        gvMenu.setAdapter(adapter);
        flipper.setInAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
                R.anim.push_left_out));
        flipper.startFlipping();
//        lstMenu = new ArrayList<>();
//        MenuClass menuClass = null;
//        for (int i = 0; i < 6; i++) {
//            menuClass = new MenuClass();
//            menuClass.setName(names[i]);
//            lstMenu.add(menuClass);
//            menuClass = null;
//        }
//        for (MenuClass mc : lstMenu) {
//            adapter.addNewItem(mc);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getArtifiInfo();
    }

    /**
     * 获取数据信息
     */
    private void getArtifiInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        TaskExecutor.Execute(new DataRunnable(this, "/Index/get", mHandler, Config.WHAT_ONE, map));
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
    private class MainLoadHandler extends Handler {

        private WeakReference<MainActivity> mWeak;

        public MainLoadHandler(MainActivity activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(MainActivity activity, int what, String json) {
            switch (what) {
                case Config.WHAT_ONE:
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            Gson gson = new Gson();
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                // 加载数据
                                JSONObject jd = new JSONObject(data);
                                String artificer = jd.getString("artificer");
                                String menus = jd.getString("menus");
                                JSONObject ju = new JSONObject(artificer);
                                // 师傅信息
                                String head = ju.getString("head");// 需要将图像加载进来
                                String name = ju.getString("name");
                                String order_num = ju.getString("order_num");
                                String ok_num = ju.getString("ok_num");
                                tvUserrank.setText("接单数  " + order_num + "，好评数  "
                                        + ok_num);
                                tvUsername.setText(name);
                                if (!head.equals("") && head != null) {
                                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(head))
                                            .setAutoRotateEnabled(true)//设置图片智能摆正
                                            .setProgressiveRenderingEnabled(true)//设置渐进显示
                                            .build();
                                    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                            .setImageRequest(request)
                                            .setOldController(sdvHeadphoto.getController())
                                            .build();
                                    sdvHeadphoto.setController(controller);
                                    getSharePreferencesEditor().putString(
                                            Config.HEAD, head).commit();// 先将这个头像存储起来
                                }

                                List<MenuClass> lstMenu = gson.fromJson(menus,
                                        new TypeToken<List<MenuClass>>() {
                                        }.getType());
                                for (MenuClass mc : lstMenu) {
                                    adapter.addNewItem(mc);
                                }
                                if (lstMenu.size() > 0) {
                                    gvMenu.setAdapter(adapter);
                                }
                                checkNewVersion();
                            } else
                                Toast.makeText(MainActivity.this, msg,
                                        Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                case Config.WHAT_TWO:
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                String versionCode = jd.getString("copyright");
                                String url = jd.getString("download");
                                remindme = getSharePreferences().getString(Config.REMIND_UPDATE, "1");//默认提醒用户
                                if (versionCode != null && !versionCode.equals("") && remindme.equals("1")) {
                                    UpdateManager manager = new UpdateManager(
                                            mContext, url);
                                    // 检查软件更新(首页自动弹出)
                                    manager.checkUpdate(versionCode, Config.WHAT_TWO);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 检查是否有更新
     */
    private void checkNewVersion() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("client", "android");
        TaskExecutor.Execute(new DataRunnable(mContext, "/System/update",
                mHandler, Config.WHAT_TWO, map));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_close:
//                Intent intent = new Intent();
//                intent.setClass(mContext, LoginActivity.class);
//                startActivity(intent);
//                //清除车主问题、工时、用户配置等缓存
//                services_question.clearData();
//                services_time.clearData();
//                getSharePreferencesEditor()
//                        .putString(Configuration.ID, "")
//                        .putString(Configuration.USERNAME, "")
//                        .putString(Configuration.PWD, "")
//                        .putString(Configuration.TOKEN, "")
//                        .commit();// 先将这个iD存储起来
//                finish();
//
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }


}
