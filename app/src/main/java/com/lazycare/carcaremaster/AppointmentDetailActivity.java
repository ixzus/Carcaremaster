package com.lazycare.carcaremaster;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.fragment.AppointmentFragment;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 预约问题详情
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AppointmentDetailActivity extends BaseActivity implements OnClickListener {
    private LinearLayout llMain;
    Button btn_completeorder, btn_startservice, btn_modifyprice;
    RelativeLayout rl_arrow;
    String order_id = "";
    Handler mHandler = new LoadQuestionDetailHandler(this);
    TextView tv_title, tv_consignee, tv_mobile, tv_car, tv_service_state,
            tv_pack, tv_total, tv_book_time, tv_sn, tv_add_time, tv_remark,
            tv_carnum;
    HashMap<String, String> hmservice_state = new HashMap<>();
    private String list;// 存放左右配件信息(未解析)
    private boolean isServiceStarted = false;
    EditText passwordInput;
    private String pay_state = "0";
    private String standard = "";

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_appointmentdetail);
    }

    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("订单详情");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initView() {
        order_id = getIntent().getStringExtra("order_id");
        standard = getIntent().getStringExtra("standard");
        hmservice_state.put("0", "未服务");
        hmservice_state.put("1", "服务中");
        hmservice_state.put("2", "已完成");
        llMain = (LinearLayout) findViewById(R.id.app_main_layout);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_consignee = (TextView) findViewById(R.id.tv_consignee);
        tv_mobile = (TextView) findViewById(R.id.tv_mobile);
        tv_car = (TextView) findViewById(R.id.tv_car);
        tv_carnum = (TextView) this.findViewById(R.id.tv_car_num);
        tv_service_state = (TextView) findViewById(R.id.tv_service_state);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_book_time = (TextView) findViewById(R.id.tv_book_time);
        tv_sn = (TextView) findViewById(R.id.tv_sn);
        tv_add_time = (TextView) findViewById(R.id.tv_add_time);
        tv_remark = (TextView) findViewById(R.id.tv_remark);
        rl_arrow = (RelativeLayout) findViewById(R.id.detail_arrow);
        btn_completeorder = (Button) findViewById(R.id.btn_completeorder);
        btn_completeorder.setOnClickListener(this);
        btn_startservice = (Button) findViewById(R.id.btn_startservice);
        btn_startservice.setOnClickListener(this);
        btn_modifyprice = (Button) findViewById(R.id.btn_modifyprice);
        btn_modifyprice.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        loadMoreData();
    }

    //设置更新标志位
    private void setFlag() {
        AppointmentFragment.FLAG = 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                AppointmentDetailActivity.this.finish();
                // NavUtils.navigateUpFromSameTask(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadMoreData() {
        mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
        Map<String, String> map = new HashMap<>();
        map.put("artificer_id", id);
        map.put("order_id", order_id);
        TaskExecutor.Execute(new DataRunnable(this, "/Order/get", mHandler, Config.WHAT_ONE, map));
    }

    @Override
    public void onClick(View v) {
        //动态再去获取pay_state
        Map<String, String> map = new HashMap<>();
        map.put("artificer_id", id);
        map.put("order_id", order_id);
        switch (v.getId()) {
            case R.id.btn_completeorder:
                TaskExecutor.Execute(new DataRunnable(this, "/Order/get", mHandler,
                        R.id.btn_completeorder, map));
                break;
            case R.id.btn_startservice:
                TaskExecutor.Execute(new DataRunnable(this, "/Order/get", mHandler,
                        R.id.btn_startservice, map));
                break;
            case R.id.btn_modifyprice:
                TaskExecutor.Execute(new DataRunnable(this, "/Order/get", mHandler,
                        R.id.btn_modifyprice, map));
                break;
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
    private class LoadQuestionDetailHandler extends Handler {

        private WeakReference<AppointmentDetailActivity> mWeak;

        public LoadQuestionDetailHandler(AppointmentDetailActivity activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppointmentDetailActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            DialogUtil.dismiss(activity.mDialog);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(AppointmentDetailActivity activity, int what,
                              String json) {
            switch (what) {
                case Config.WHAT_TWO:
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            // String data = jb.getString("data");
                            // isLoading = false;
                            if (error.equals("0")) {
                                setFlag();
                                isServiceStarted = true;
                                Map<String, String> map = new HashMap<>();
                                map.put("artificer_id", id);
                                map.put("order_id", order_id);
                                TaskExecutor.Execute(new DataRunnable(AppointmentDetailActivity.this, "/Order/get", mHandler, Config.WHAT_ONE, map));
                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }

                    }
                    break;
                case Config.WHAT_THREE:// 修改价格
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            // String data = jb.getString("data");
                            // isLoading = false;
                            if (error.equals("0")) {
                                tv_total.setText(passwordInput.getText() + "元");
                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    mDialog.dismiss();
                    break;
                case Config.WHAT_ONE:// 初始化
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            // Gson gson = new Gson();
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            // isLoading = false;
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                String consignee = "", mobile = "", car = "", car_num = "", service_state = "", total = "", book_time = "", sn = "", add_time = "", remark = "";
                                //备注
                                consignee = jd.getString("consignee");
                                //手机
                                mobile = jd.getString("mobile");
                                //车型
                                car = jd.getString("car");
                                //车牌
                                car_num = jd.getString("license");
                                //服务状态
                                service_state = jd.getString("service_state");
                                //总价
                                total = jd.getString("total") + "元";
                                //预约时间
                                book_time = jd.getString("book_time");
                                //订单号
                                sn = jd.getString("sn");
                                //添加时间
                                add_time = jd.getString("add_time");
                                remark = jd.getString("remark");
                                //支付状态
                                pay_state = jd.getString("pay_state");
                                // 获取服务订单配件详情
                                list = jd.getString("services");

                                tv_carnum.setText(car_num);// 车牌号
                                tv_title.setText("订单号:" + sn);// 标题
                                tv_consignee.setText(consignee);
                                tv_mobile.setText(mobile);// 联系方式
                                tv_car.setText(car);// 车型
                                tv_service_state.setText(hmservice_state.get(service_state));// 服务状态
                                if (service_state.equals("0")) {
                                    isServiceStarted = false;
                                } else {
                                    isServiceStarted = true;
                                }
                                // tv_pack.setText(service);//
                                tv_total.setText(total);// 总价格
                                tv_sn.setText(sn);// 订单编号
                                tv_remark.setText(remark.equals("") ? "" : remark);
                                if (service_state.equals("0")) {
                                    btn_modifyprice.setVisibility(View.VISIBLE);
                                    btn_startservice.setVisibility(View.VISIBLE);
                                } else if (service_state.equals("1")) {
                                    btn_modifyprice.setVisibility(View.INVISIBLE);
                                    btn_startservice.setVisibility(View.INVISIBLE);
                                } else {
                                    btn_modifyprice.setVisibility(View.INVISIBLE);
                                    btn_startservice.setVisibility(View.INVISIBLE);
                                    btn_completeorder.setVisibility(View.INVISIBLE);
                                }
                                //付款之后在服务     如果已经付款，则隐藏修改价格按钮
                                if (pay_state.equals("1"))
                                    btn_modifyprice.setVisibility(View.INVISIBLE);
                                if (!book_time.equals("")) {
                                    String formatDate = "MM-dd hh:mm:ss";
                                    float at = Float.parseFloat(book_time);
                                    String fr = CommonUtil.FormatTime(formatDate, at);
                                    tv_book_time.setText(fr);// 预约时间
                                }
                                if (!add_time.equals("")) {
                                    String formatDate = "MM-dd hh:mm:ss";
                                    float at = Float.parseFloat(add_time);
                                    String fr = CommonUtil.FormatTime(formatDate, at);
                                    tv_add_time.setText(fr);// 下单时间
                                }
                                rl_arrow.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        if (list.equals("")) {
                                            CommonUtil.showSnack(llMain, "没有配件信息");
                                        } else {
                                            Intent intent = new Intent();
                                            intent.setClass(mContext, AppointmentServiceDetailActivity.class);
                                            intent.putExtra("service", list);
                                            startActivity(intent);
                                        }
                                    }
                                });

                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                case R.id.btn_completeorder://点击完成订单
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                pay_state = jd.getString("pay_state");
                                //未付款并且是标准单
                                if (pay_state.equals("0") && standard.equals("1")) {
                                    CommonUtil.showSnack(llMain, "请耐心等待车主付款");
                                } else if (!isServiceStarted) {
                                    CommonUtil.showSnack(llMain, "请先开启服务");
                                } else
                                    // 完成订单
                                    new MaterialDialog.Builder(mContext)
                                            .content(R.string.dialog_appoint_complservice)
                                            .positiveText(R.string.action_yes)
                                            .negativeText(R.string.action_no)
                                            .negativeColorRes(R.color.grey)
                                            .positiveColorRes(R.color.olivedrab)
                                            .callback(new ButtonCallback() {
                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    mDialog = CustomProgressDialog.showCancelable(AppointmentDetailActivity.this, "完成订单中...");
                                                    Map<String, String> map;
                                                    map = new HashMap<>();
                                                    map.put("artificer_id", id);
                                                    map.put("order_id", order_id);
                                                    map.put("service_state", "2");//
                                                    TaskExecutor.Execute(new DataRunnable(AppointmentDetailActivity.this, "/Order/setServiceState", mHandler, Config.WHAT_TWO, map));
                                                }
                                            }).show();
                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                case R.id.btn_startservice://点击开启服务
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                pay_state = jd.getString("pay_state");
                                //为付款并且是标准单
                                if (pay_state.equals("0") && standard.equals("1")) {
                                    CommonUtil.showSnack(llMain, "请耐心等待车主付款");
                                } else
                                    // 开启服务
                                    new MaterialDialog.Builder(mContext)
                                            .content(R.string.dialog_appoint_startservice)
                                            .positiveText(R.string.action_yes)
                                            .negativeText(R.string.action_no)
                                            .callback(new ButtonCallback() {
                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    mDialog = CustomProgressDialog.showCancelable(AppointmentDetailActivity.this, "服务开启中...");
                                                    Map<String, String> map;
                                                    map = new HashMap<>();
                                                    map.put("artificer_id", id);
                                                    map.put("order_id", order_id);
                                                    map.put("service_state", "1");// 进入服务中
                                                    TaskExecutor.Execute(new DataRunnable(AppointmentDetailActivity.this, "/Order/setServiceState", mHandler, Config.WHAT_TWO, map));
                                                }
                                            }).show();
                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    break;
                case R.id.btn_modifyprice://点击修改价格
                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                JSONObject jd = new JSONObject(data);
                                pay_state = jd.getString("pay_state");

                                if (pay_state.equals("1")) {
                                    CommonUtil.showSnack(llMain, "车主已付完款,无法再做更改");
                                } else {
                                    // 弹出一个输入框，来完成修改
                                    MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                                            .title(R.string.dialog_appoint_modifyprice)
                                            .customView(R.layout.dialog_customview_edit, true)
                                            .positiveText(R.string.action_yes)
                                            .negativeText(R.string.action_no)
                                            .negativeColorRes(R.color.grey)
                                            .positiveColorRes(R.color.olivedrab)
                                            .callback(new MaterialDialog.ButtonCallback() {
                                                @Override
                                                public void onPositive(MaterialDialog dialog) {
                                                    String input = passwordInput.getText().toString();
                                                    if (input != null && !input.equals("")) {
                                                        mDialog = CustomProgressDialog.showCancelable(AppointmentDetailActivity.this, "价格修改中...");
                                                        Map<String, String> map;
                                                        map = new HashMap<>();
                                                        map.put("artificer_id", id);
                                                        map.put("order_id", order_id);
                                                        map.put("price", input);// 需要改
                                                        TaskExecutor.Execute(new DataRunnable(AppointmentDetailActivity.this, "/Order/setPrice", mHandler, Config.WHAT_THREE, map));
                                                    }
                                                }
                                            }).build();
                                    passwordInput = (EditText) dialog.getCustomView().findViewById(
                                            R.id.dialog_password);
                                    // 控制小数
                                    passwordInput.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start,
                                                                      int count, int after) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start,
                                                                  int before, int count) {
                                            if (s.toString().contains(".")) {
                                                if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                                                    s = s.toString().subSequence(0,
                                                            s.toString().indexOf(".") + 3);
                                                    passwordInput.setText(s);
                                                    passwordInput.setSelection(s.length());
                                                }
                                            }
                                            if (s.toString().trim().substring(0).equals(".")) {
                                                s = "0" + s;
                                                passwordInput.setText(s);
                                                passwordInput.setSelection(2);
                                            }
                                            if (s.toString().startsWith("0")
                                                    && s.toString().trim().length() > 1) {
                                                if (!s.toString().substring(1, 2).equals(".")) {
                                                    passwordInput.setText(s.subSequence(0, 1));
                                                    passwordInput.setSelection(1);
                                                    return;
                                                }
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {
                                        }
                                    });
                                    dialog.show();
                                }
                            } else
                                CommonUtil.showSnack(llMain, msg);
                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }

                    break;
                default: // 获取数据返回信息

                    break;
            }
        }
    }

}
