package com.lazycare.carcaremaster;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.lazycare.carcaremaster.data.Attachments;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.ImageUtil;
import com.squareup.picasso.Picasso;

/**
 * 图片详情
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ImageShowActivity extends BaseActivity implements OnClickListener {

	private ControlView mCv = new ControlView();
	private Intent mIntent;
	private int index;
	private String path = "";
	private String url = "";
	private Bitmap bitmap = null;
	private int width;
	private int height;
	private static final String TAG = "ImageShowActivity";

	private class ControlView {

		// TextView tv_title, tv_action;
		ImageView iv_show;
	}

	@Override
	public void setLayout() {
		setContentView(R.layout.common_activity_image_show);

	}

	@Override
	public void setActionBarOption() {
		ActionBar bar = getSupportActionBar();
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle("图片显示");

	}

	@Override
	public void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;// 宽度
		height = dm.heightPixels;// 高度
		// mCv.tv_title = (TextView)findViewById(R.id.tv_title);
		// mCv.tv_action = (TextView)findViewById(R.id.tv_action);
		mCv.iv_show = (ImageView) findViewById(R.id.iv_one);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mCv.iv_show.setLayoutParams(params);
		mIntent = getIntent();
		Attachments dto = (Attachments) mIntent
				.getParcelableExtra("Attachments");
		index = mIntent.getIntExtra("index", Config.DEFAULT_ERROR);
		path = mIntent.getStringExtra("path");
		url = mIntent.getStringExtra("url");
		// bitmap = ImageUtil.getBitmapByPath(path,
		// ImageUtil.getOptions(path), 180, 240);
		if (!path.equals("")) {
			bitmap = ImageUtil.getLocalThumbImg(path, width / 2, height / 2,
					"jpg");
			mCv.iv_show.setImageBitmap(bitmap);
		}
		if (!url.equals("")) {
			Picasso.with(ImageShowActivity.this).load(url).into(mCv.iv_show);
		}

		// if (ObjectUtil.isEmpty(dto.getFile_path()))
		// return;
		// ImageUtil.ImageLoader(dto.getFile_path(), mCv.iv_show,
		// dto.getFlag());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {
		new MaterialDialog.Builder(this).content(R.string.dialog_findpwd)
				.positiveText(R.string.action_yes)
				.negativeText(R.string.action_no)
				.callback(new ButtonCallback() {
					@Override
					public void onPositive(MaterialDialog dialog) {
						mIntent.putExtra("index", index);
						setResult(RESULT_OK, mIntent);
						finish();
					}
				}).show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			ImageShowActivity.this.finish();
			// NavUtils.navigateUpFromSameTask(this);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
