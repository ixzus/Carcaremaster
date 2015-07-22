package com.lazycare.carcaremaster;

import android.app.Activity;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

/**
 * 工具界面 用来在后来打开网络的时候重新加载界面主数据
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class EmptyActivity extends Activity {
	private String TAG = "emptyactivity";

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(TAG);
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MobclickAgent.setDebugMode(true);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
		EmptyActivity.this.finish();
	}
}
