package com.lazycare.carcaremaster.fragment;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 找回支付密码
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ModifyPayPwdFragment extends Fragment implements OnClickListener {
    public static final String TAG = "ModifyPayPwdFragment";
    private LinearLayout llMain;
    private EditText et1, et2, et3;
    private Button btn_certain;
    private Dialog mDialog;
    private String id = "";
    Handler mHandler = new PayPwdHandler(getActivity());

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("id");
        View view = inflater.inflate(R.layout.fragment_modifypwd, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        llMain = (LinearLayout) view.findViewById(R.id.ll_modify_pwd);
        et1 = (EditText) view.findViewById(R.id.et_pay_oldpwd);// 旧密码
        et2 = (EditText) view.findViewById(R.id.et_pay_newpwd);// 新密码
        et3 = (EditText) view.findViewById(R.id.et_pay_newpwd_certain);// 确认新密码
        btn_certain = (Button) view.findViewById(R.id.btn_pwd_certain);// 确定
        btn_certain.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String old = et1.getText().toString().trim(), newp = et2.getText()
                .toString().trim(), newp2 = et3.getText().toString().trim();
        if (et1.getText() == null || et2.getText() == null || et3.getText() == null || old.equals("") || newp.equals("") || newp2.equals("")) {
            CommonUtil.showSnack(llMain, "输入不能有空");
        } else if (old.equals(newp)) {
            CommonUtil.showSnack(llMain, "新密码不能和旧密码相同");
        } else if (!newp.equals(newp2)) {
            CommonUtil.showSnack(llMain, "两次输入新密码不一致");
        } else {
            postData(old, newp, newp2);
        }
    }

    private void postData(String oldPwd, String newPwd, String newPwd2) {
        mDialog = CustomProgressDialog.showCancelable(getActivity(), "密码修改中...");
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("safe_pass", oldPwd);
        map.put("new_safe_pass", newPwd);
        map.put("confirm_new_safe_pass", newPwd2);
        TaskExecutor.Execute(new DataRunnable(getActivity(), "/Artificer/updateSafePass", mHandler, map));
    }

    /**
     * 线程处理返回值
     *
     * @author gmy
     */
    @SuppressLint("HandlerLeak")
    private class PayPwdHandler extends Handler {

        private WeakReference<Activity> mWeak;

        public PayPwdHandler(Activity activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            DialogUtil.dismiss(mDialog);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(Activity activity, int what, String json) {

            Log.d(TAG, json);
            if (!CommonUtil.isEmpty(json)) {
                try {
                    JSONObject jb = new JSONObject(json);
                    String error = jb.getString("error");
                    String msg = jb.getString("msg");
                    String data = jb.getString("data");
                    if (error.equals("0"))
                        CommonUtil.showSnack(llMain, "密码修改成功，请牢记哒");
                    else
                        CommonUtil.showSnack(llMain, msg);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                } finally {
                    et1.setText("");
                    et2.setText("");
                    et3.setText("");
                }
            }
        }
    }
}
