package com.lazycare.carcaremaster.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.AppointmentDetailActivity;
import com.lazycare.carcaremaster.BankListActivity;
import com.lazycare.carcaremaster.ChooseBankActivity;
import com.lazycare.carcaremaster.ExchangeMoneyActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.AppointmentListAdapter;
import com.lazycare.carcaremaster.adapter.ChooseBankAdapter;
import com.lazycare.carcaremaster.data.AppointmentClass;
import com.lazycare.carcaremaster.data.BankClass;
import com.lazycare.carcaremaster.data.ChooseBankClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 问题列表
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class WithdrawFragment extends BaseFragment {
    private String id = "", money = "", safePass = "", name = "", bank = "支付宝", account = "", open_bank = "";

    /**
     * 进度条
     */
    private Dialog mDialog;
    private View view;
    private int mCurIndex = -1;
    /**
     * 标志位，标志已经初始化完成
     */
    private boolean isPrepared;
    /**
     * 是否已被加载过一次，第二次就不再去请求数据了
     */
    // private boolean mHasLoadedOnce;
    EditText txt_money, txt_safePass, txt_name;
    Button btn_submit;
    private Handler mHandler = new WithdrawMoneyHandler(this);
    //card
    public static final int REQUESTCODE = 1;
    private RelativeLayout rl_bank, rl_where;
    private ImageView bank_icon;
    private TextView tv_bankname;
    private EditText txt_account;//开户地，银行卡号
    private TextView txt_moneywhere;
    //alipay
    private EditText txt_alipaynum;
    private ChooseBankAdapter adapter;

    public static WithdrawFragment newInstance(int position, String id, String money) {
        WithdrawFragment f = new WithdrawFragment();
        Bundle b = new Bundle();
        b.putInt("POSITION", position);
        b.putString("ID", id);
        b.putString("MONEY", money);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            mCurIndex = getArguments().getInt("POSITION");
            id = getArguments().getString("ID");
            money = getArguments().getString("MONEY");
            switch (mCurIndex) {
                //支付宝
                case 0:
                    view = inflater.inflate(R.layout.fragment_withdrawbyalipay, null);
                    txt_money = (EditText) view.findViewById(R.id.txt_money);
                    btn_submit = (Button) view.findViewById(R.id.btn_ok);
                    txt_safePass = (EditText) view.findViewById(R.id.txt_safePass);
                    txt_name = (EditText) view.findViewById(R.id.txt_moneyname);

                    txt_alipaynum = (EditText) view.findViewById(R.id.txt_card);//支付宝账号
                    txt_money.setEnabled(false);
                    if (!money.equals("")) {
                        btn_submit.setClickable(true);
                        txt_money.setText(money + "元");
                    }
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit(mCurIndex);
                        }
                    });
                    isPrepared = true;
                    break;
                //银行卡
                case 1:
                    view = inflater.inflate(R.layout.fragment_withdrawbycard, null);
                    txt_money = (EditText) view.findViewById(R.id.txt_money);
                    txt_name = (EditText) view.findViewById(R.id.txt_moneyname);
                    txt_safePass = (EditText) view.findViewById(R.id.txt_safePass);
                    btn_submit = (Button) view.findViewById(R.id.btn_ok);//提交

                    txt_account = (EditText) view.findViewById(R.id.txt_yhcard);//提现账户
                    rl_bank = (RelativeLayout) view.findViewById(R.id.rl_bank);//选择银行
                    rl_where = (RelativeLayout) view.findViewById(R.id.rl_where);//选择开户地
                    txt_moneywhere = (TextView) view.findViewById(R.id.tv_bankwhere);//开户行所在地
                    bank_icon = (ImageView) view.findViewById(R.id.iv_bankicon);
                    tv_bankname = (TextView) view.findViewById(R.id.tv_bankname);
                    //跳转选择银行卡列表
                    rl_bank.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent();
//                            intent.setClass(getActivity(),
//                                    ChooseBankActivity.class);
//                            startActivityForResult(intent, REQUESTCODE);

//                            adapter = new ChooseBankAdapter(getActivity());
//                            mDialog = CustomProgressDialog.showCancelable(getActivity(), "加载中...");
//                            Map<String, String> map = new HashMap<String, String>();
//                            // map.put("App_user", ToObjectUtil.BenToJson(app_user));
//                            TaskExecutor
//                                    .Execute(new DataRunnable(getActivity(), "/Banks/get", mHandler, Config.WHAT_ONE, map));

                            new MaterialDialog.Builder(getActivity())
                                    .title(R.string.dialog_selectbank)
                                    .items(R.array.bank)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            tv_bankname.setText(text);
                                        }
                                    })
                                    .positiveText(android.R.string.cancel)
                                    .show();

                        }
                    });
                    rl_where.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MaterialDialog.Builder(getActivity())
                                    .title(R.string.dialog_selectprovice)
                                    .items(R.array.province)
                                    .itemsCallback(new MaterialDialog.ListCallback() {
                                        @Override
                                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            txt_moneywhere.setText(text);
                                        }
                                    })
                                    .positiveText(android.R.string.cancel)
                                    .show();
                        }
                    });

                    txt_money.setEnabled(false);
                    if (!money.equals("")) {
                        btn_submit.setClickable(true);
                        txt_money.setText(money + "元");
                    }
                    txt_account.addTextChangedListener(new CardNumWatcher());
                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            submit(mCurIndex);
                        }
                    });
                    isPrepared = true;
                    break;
                default:
                    break;
            }

        }
        // 因为共用一个Fragment视图，所以当前这个视图已被加载到Activity中，必须先清除后再加入Activity
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == getActivity().RESULT_OK) {

            switch (requestCode) {
                case REQUESTCODE:

                    Bundle b = intent.getExtras(); // data为B中回传的Intent
                    String bankname = b.getString("BANKNAME");
                    if (bankname.length() > 0) {
                        bank = bankname;
                        tv_bankname.setText(bank);
                    }


//                    BankClass bank = (BankClass) intent.getExtras()
//                            .getSerializable("bank");
//                    if (bank != null) {
//
//                        if (bank.getBank() != null
//                                && !bank.getBank().trim().equals("")) {
//                            tv_bankname.setText(bank.getBank());
//                        }
//                        if (bank.getTail() != null
//                                && !bank.getTail().trim().equals("")) {
//                            txt_account.setText(bank.getTail());
//                        }
//                        Picasso.with(getActivity())
//                                .load(bank.getIcon()).into(bank_icon);
//                    }
                    break;
                case 2:
                    break;
            }

        }
    }

    @Override
    protected void lazyLoad() {

    }

    private void submit(int mCurIndex) {
        safePass = txt_safePass.getText().toString().trim();

        name = txt_name.getText().toString().trim();
        if (mCurIndex == 1) {
            account = txt_account.getText().toString().trim();
            bank = tv_bankname.getText().toString().trim();
            open_bank = txt_moneywhere.getText().toString().trim();

            Pattern pattern = Pattern.compile("^\\d{16}|\\d{19}$", Pattern.LITERAL);
            Matcher matcher = pattern.matcher(account);
            if (name.equals("") || name == null) {
                Toast.makeText(getActivity(), "收款人不能为空!!", Toast.LENGTH_SHORT).show();
            } else if (account == null || account.equals("")) {
                Toast.makeText(getActivity(), "银行卡号不能为空!!", Toast.LENGTH_SHORT).show();
            }
//        else if (!matcher.matches()) {
//            Toast.makeText(getActivity(), "银行卡号格式不对!!", Toast.LENGTH_SHORT).show();
//        }
            else if (bank == null || bank.equals("")) {
                Toast.makeText(getActivity(), "银行不能为空!!", Toast.LENGTH_SHORT).show();
            } else if (open_bank == null || open_bank.equals("")) {
                Toast.makeText(getActivity(), "银行开户地不能为空!!", Toast.LENGTH_SHORT).show();
            } else if (safePass == null || safePass.equals("")) {
                Toast.makeText(getActivity(), "请填写提现密码!!", Toast.LENGTH_SHORT).show();
            } else {

                mDialog = CustomProgressDialog.showCancelable(getActivity(), "正在进行取现操作...");
                Map<String, String> map = new HashMap<String, String>();
                map.put("artificer_id", id);
                map.put("safePass", safePass);
                map.put("account", account);
                map.put("name", name);
                map.put("open_bank", open_bank);
                map.put("bank", bank);
                TaskExecutor.Execute(new DataRunnable(getActivity(),
                        "/Artificer/enchashment", mHandler, Config.WHAT_TWO, map));
            }
        } else {
            account = txt_alipaynum.getText().toString().trim();

            if (name.equals("") || name == null) {
                Toast.makeText(getActivity(), "收款人不能为空!!", Toast.LENGTH_SHORT).show();
            } else if (account == null || account.equals("")) {
                Toast.makeText(getActivity(), "银行卡号不能为空!!", Toast.LENGTH_SHORT).show();
            } else if (safePass == null || safePass.equals("")) {
                Toast.makeText(getActivity(), "请填写提现密码!!", Toast.LENGTH_SHORT).show();
            } else {

                mDialog = CustomProgressDialog.showCancelable(getActivity(), "正在进行取现操作...");
                Map<String, String> map = new HashMap<String, String>();
                map.put("artificer_id", id);
                map.put("safePass", safePass);
                map.put("account", account);
                map.put("name", name);
                map.put("bank", bank);
                TaskExecutor.Execute(new DataRunnable(getActivity(),
                        "/Artificer/enchashment", mHandler, Config.WHAT_TWO, map));
            }


        }
    }

    /**
     * <p>
     * 标 题: WithdrawMoneyHandler
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
    private class WithdrawMoneyHandler extends Handler {

        private WeakReference<WithdrawFragment> mWeak;

        public WithdrawMoneyHandler(WithdrawFragment activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            WithdrawFragment activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
            DialogUtil.dismiss(activity.mDialog);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(WithdrawFragment activity, final int what,
                              String json) {
            switch (what) {
                case Config.WHAT_TWO:
//                    Log.d(TAG, json);
                    if (!CommonUtil.isEmpty(json)) {
                        try {
                            JSONObject jb = new JSONObject(json);
                            String error = jb.getString("error");
                            String msg = jb.getString("msg");
                            String data = jb.getString("data");
                            if (error.equals("0")) {
                                //返回并刷新
                                Toast.makeText(getActivity(),
                                        "您的取现申请已提交到客服!", Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                ExchangeMoneyActivity.withdrawDone = true;
                            } else
                                Toast.makeText(getActivity(), msg,
                                        Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
//                            Log.d(TAG, e.getMessage());
                        }

                    }
                    break;
//                case Config.WHAT_ONE:
//                    if (!CommonUtil.isEmpty(json)) {
//                        try {
//                            Gson gson = new Gson();
//                            JSONObject jb = new JSONObject(json);
//                            String error = jb.getString("error");
//                            String msg = jb.getString("msg");
//                            String data = jb.getString("data");
//                            if (error.equals("0")) {
//                                List<ChooseBankClass> lstChooseBank = gson
//                                        .fromJson(
//                                                data,
//                                                new TypeToken<List<ChooseBankClass>>() {
//                                                }.getType());
//                                for (ChooseBankClass cbc : lstChooseBank) {
//                                    adapter.addNewItem(cbc);
//                                }
//                                new MaterialDialog.Builder(getActivity())
//                                        .title(R.string.dialog_selectbank)
//                                        .adapter(adapter, new MaterialDialog.ListCallback() {
//                                            @Override
//                                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
//                                                tv_bankname.setText(text);
//                                            }
//                                        })
//                                        .positiveText(android.R.string.cancel)
//                                        .show();
//                            } else
//                                Toast.makeText(getActivity(), msg,
//                                        Toast.LENGTH_SHORT).show();
//                        } catch (Exception e) {
//                        }
//
//                    }
//                    break;
            }
        }
    }

    /**
     * 银行卡输入字符控制
     */
    class CardNumWatcher implements TextWatcher {
        int beforeTextLength = 0;
        int onTextLength = 0;
        boolean isChanged = false;

        int location = 0;// 记录光标的位置
        private char[] tempChar;
        private StringBuffer buffer = new StringBuffer();
        int konggeNumberB = 0;

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            onTextLength = s.length();
            buffer.append(s.toString());
            if (onTextLength == beforeTextLength || onTextLength <= 3
                    || isChanged) {
                isChanged = false;
                return;
            }
            isChanged = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
            beforeTextLength = s.length();
            if (buffer.length() > 0) {
                buffer.delete(0, buffer.length());
            }
            konggeNumberB = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ' ') {
                    konggeNumberB++;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            if (isChanged) {
                location = txt_account.getSelectionEnd();
                int index = 0;
                while (index < buffer.length()) {
                    if (buffer.charAt(index) == ' ') {
                        buffer.deleteCharAt(index);
                    } else {
                        index++;
                    }
                }

                index = 0;
                int konggeNumberC = 0;
                while (index < buffer.length()) {
                    if ((index == 4 || index == 9 || index == 14 || index == 19)) {
                        buffer.insert(index, ' ');
                        konggeNumberC++;
                    }
                    index++;
                }

                if (konggeNumberC > konggeNumberB) {
                    location += (konggeNumberC - konggeNumberB);
                }

                tempChar = new char[buffer.length()];
                buffer.getChars(0, buffer.length(), tempChar, 0);
                String str = buffer.toString();
                if (location > str.length()) {
                    location = str.length();
                } else if (location < 0) {
                    location = 0;
                }

                txt_account.setText(str);
                Editable etable = txt_account.getEditableText();
                Selection.setSelection(etable, location);
                isChanged = false;
            }
        }

    }
}
