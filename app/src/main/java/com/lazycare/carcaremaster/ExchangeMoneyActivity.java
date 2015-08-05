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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 我要提现
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ExchangeMoneyActivity extends BaseActivity implements
		OnClickListener {
	RelativeLayout rlBank, rlPay;
	Button btnExchange;
	LoadExchangeMoneyHandler mHandler = new LoadExchangeMoneyHandler(this);
	TextView tv_money_all, tv_money_kjs_all, tv_money_wjs_all, tv_money_wjs_at,
			tv_money_wjs_qd, tv_money_wjs_yw, tv_money_kjs_at, tv_money_kjs_qd,
			tv_money_kjs_yw;
	TextView ex_txt, ex_txt1;
	private String money_able = "";
	public static boolean withdrawDone = false;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_exchangemoney);
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("我要提现");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (withdrawDone) {
			LoadData();
			withdrawDone = false;
		}

	}

	@Override
	public void initView() {
		ex_txt = (TextView) this.findViewById(R.id.ex_txt);
		ex_txt1 = (TextView) this.findViewById(R.id.ex_txt1);

		tv_money_all = (TextView) findViewById(R.id.tv_money_all);
		tv_money_wjs_all = (TextView) findViewById(R.id.tv_money_wjs_all);
		tv_money_wjs_at = (TextView) findViewById(R.id.tv_money_wjs_at);
		tv_money_wjs_qd = (TextView) findViewById(R.id.tv_money_wjs_qd);
		tv_money_wjs_yw = (TextView) findViewById(R.id.tv_money_wjs_yw);

		tv_money_kjs_all = (TextView) findViewById(R.id.tv_money_kjs_all);
		tv_money_kjs_at = (TextView) findViewById(R.id.tv_money_kjs_at);
		tv_money_kjs_qd = (TextView) findViewById(R.id.tv_money_kjs_qd);
		tv_money_kjs_yw = (TextView) findViewById(R.id.tv_money_kjs_yw);

		rlBank = (RelativeLayout) findViewById(R.id.rl_bank);
		rlBank.setOnClickListener(this);
		rlPay = (RelativeLayout) findViewById(R.id.rl_pay);
		rlPay.setOnClickListener(this);
		btnExchange = (Button) findViewById(R.id.btn_exchange);
		btnExchange.setOnClickListener(this);
		ex_txt.setText("@我结算:");
		ex_txt1.setText("@我结算:");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LoadData();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			ExchangeMoneyActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_bank:
			Intent intent = new Intent();
			intent.setClass(ExchangeMoneyActivity.this, BankListActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_pay:
			Intent intent2 = new Intent();
			intent2.setClass(ExchangeMoneyActivity.this,
					PayPwdSettingActivity.class);
			startActivity(intent2);
			break;
		case R.id.btn_exchange:
			Intent intent3 = new Intent();
			intent3.setClass(ExchangeMoneyActivity.this,
					WithdrawMoneyActivity.class);
			intent3.putExtra("money", money_able);
			startActivity(intent3);
			break;
		}
	}

	private void LoadData() {
		mDialog = CustomProgressDialog.showCancelable(this, "加载中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("artificer", id);
		// map.put("App_user", ToObjectUtil.BenToJson(app_user));
		TaskExecutor.Execute(new DataRunnable(this,
				"/ArtificerAccount/getMoney", mHandler, map));
	}

	/**
	 * <p>
	 * 标 题: LoadExchangeMoneyHandler
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
	 * @version
	 */
	@SuppressLint("HandlerLeak")
	private class LoadExchangeMoneyHandler extends Handler {

		private WeakReference<ExchangeMoneyActivity> mWeak;

		public LoadExchangeMoneyHandler(ExchangeMoneyActivity activity) {
			mWeak = new WeakReference<ExchangeMoneyActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ExchangeMoneyActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(ExchangeMoneyActivity activity, int what,
				String json) {
			switch (what) {
			default: // 登录返回信息
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
						String data = jb.getString("data");
						if (error.equals("0")) {
							JSONObject jd = new JSONObject(data);
							String total = jd.getString("total");
							String enableMoney = jd.getString("enableMoney");
							String disableMoney = jd.getString("disableMoney");
							if (!enableMoney.equals("")) {
								JSONObject kjs = new JSONObject(enableMoney);
								String kjs_all = kjs.getString("total");
								String kjs_at = kjs.getString("pri");
								String kjs_qd = kjs.getString("pub");
								String kjs_yw = kjs.getString("bus");
								tv_money_kjs_all.setText(kjs_all + "  元");
								tv_money_kjs_at.setText(kjs_at + "  元");
								tv_money_kjs_qd.setText(kjs_qd + "  元");
								tv_money_kjs_yw.setText(kjs_yw+"  元");
								money_able = kjs_all;
							}
							if (!disableMoney.equals("")) {
								JSONObject wjs = new JSONObject(disableMoney);
								String wjs_all = wjs.getString("total");
								String wjs_at = wjs.getString("pri");
								String wjs_qd = wjs.getString("pub");
								String wjs_yw = wjs.getString("bus");
								tv_money_wjs_all.setText(wjs_all + "  元");
								tv_money_wjs_at.setText(wjs_at + "  元");
								tv_money_wjs_qd.setText(wjs_qd + "  元");
								tv_money_wjs_yw.setText(wjs_yw+"  元");
							}
							tv_money_all.setText(total + "  元");

						} else
							Toast.makeText(ExchangeMoneyActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}
				}
				break;
			}
		}
	}

}
