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
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DialogUtil;

/**
 * 添加银行卡
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AddBankCardActivity extends BaseActivity {
	String TAG = "AddBankCardActivity";
	EditText txt_name, txt_account, txt_open_bank;
	TextView tv_bankname;
	Button btn_ok;
	String bank_id = "", bank_name = "";
	String id = "";
	private Handler mHandler = new AddBankCardHandler(this);
	/** 进度条 */
	private Dialog mDialog;
	RelativeLayout rl_bankname;
	static int GETBANKNAME = 1;

	@Override
	public void setLayout() {
		setContentView(R.layout.activity_addbank);
	}

	@Override
	public void initView() {
		id = getSharePreferences().getString(Configuration.ID, "0");
		rl_bankname = (RelativeLayout) findViewById(R.id.rl_bankname);
		rl_bankname.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AddBankCardActivity.this,
						ChooseBankActivity.class);
				startActivityForResult(intent, GETBANKNAME);
			}
		});
		txt_name = (EditText) findViewById(R.id.txt_name);
		txt_account = (EditText) findViewById(R.id.txt_account);
		tv_bankname = (TextView) findViewById(R.id.tv_bankname);
		txt_open_bank = (EditText) findViewById(R.id.txt_open_bank);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		txt_account.addTextChangedListener(new CardNumWatcher());
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SaveData();
			}
		});
	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("添加银行卡");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			AddBankCardActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == GETBANKNAME) {
			Bundle b = data.getExtras(); // data为B中回传的Intent
			String bankid = b.getString("BANKID");
			String bankname = b.getString("BANKNAME");
			if (bankname.length() > 0) {
				bank_id = bankid;
				bank_name = bankname;
				tv_bankname.setText(bank_name);
			}
		}
	}

	private void SaveData()// 还需要加入一些逻辑判断
	{
		String name = txt_name.getText().toString().trim();
		String account = txt_account.getText().toString().trim();
		String open_bank = txt_open_bank.getText().toString().trim();
		mDialog = CustomProgressDialog.showCancelable(this, "添加中...");
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("name", name);
		map.put("account", account);
		map.put("bank", bank_id);
		map.put("open_bank", open_bank);
		TaskExecutor.Execute(new DataRunnable(this, "/ArtificerPayee/add",
				mHandler, map));
	}

	/**
	 * <p>
	 * 标 题: AddBankCardHandler
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
	private class AddBankCardHandler extends Handler {

		private WeakReference<AddBankCardActivity> mWeak;

		public AddBankCardHandler(AddBankCardActivity activity) {
			mWeak = new WeakReference<AddBankCardActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AddBankCardActivity activity = mWeak.get();
			doAction(activity, msg.what, (String) msg.obj);
			DialogUtil.dismiss(activity.mDialog);
		}

		/**
		 * @Title doAction
		 * @Description 动作
		 */
		private void doAction(AddBankCardActivity activity, int what,
				String json) {
			switch (what) {
			default: // 登录返回信息
				Log.d(TAG, json);
				if (!CommonUtil.isEmpty(json)) {
					try {
						JSONObject jb = new JSONObject(json);
						String error = jb.getString("error");
						String msg = jb.getString("msg");
//						String data = jb.getString("data");
						if (error.equals("0")) {
							Toast.makeText(AddBankCardActivity.this, "添加成功!",
									Toast.LENGTH_SHORT).show();
							AddBankCardActivity.this.finish();
						} else
							Toast.makeText(AddBankCardActivity.this, msg,
									Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.d(TAG, e.getMessage());
					}

				}
				break;
			}
		}
	}
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
