package com.lazycare.carcaremaster.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.MaterialDialog.ButtonCallback;
import com.lazycare.carcaremaster.ImagesShowActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.adapter.PictureGridViewAdapter;
import com.lazycare.carcaremaster.data.Attachments;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Configuration;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.DialogUtil;
import com.lazycare.carcaremaster.util.ImageUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.util.ObjectUtil;
import com.lazycare.carcaremaster.widget.ModelPopup;
import com.lazycare.carcaremaster.widget.ModelPopup.OnDialogListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 师傅上传图像
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ModifyPhotoFragment extends Fragment implements OnClickListener,
		OnDialogListener, OnItemClickListener {
	private static String TAG = "ModifyPhotoFragment";
	/** 从本地加载标示 */
	public static final int FLAG_FILE = 3;
	/** 图片文件标识 */
	public static final String FILE_IMG_TYPE = "img";
	/***
	 * 使用照相机拍照获取图片
	 */
	public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
	/***
	 * 使用相册中的图片
	 */
	public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
	private Button btn_certain;
	private ModelPopup mPopup;
	private LinearLayout layout_root;
	private GridView gridView;
	public static ArrayList<String> paths;// 用于存放选取（拍照）图片的路径
	public static PictureGridViewAdapter adapter;
	private ImageView imageView;
	/** 图片信息 */
	public List<Attachments> mList = new ArrayList<Attachments>();
	/**
	 * 主线程处理
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String json = (String) msg.obj;
			Log.d(TAG, json);
			if (!ObjectUtil.isEmpty(json)) {

				String error = "";
				String msge = "";
				String data = "";
				try {
					JSONObject jb = new JSONObject(json);
					error = jb.getString("error");
					msge = jb.getString("msg");
					data = jb.getString("data");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (error.equals("0")) {
					DialogUtil.dismiss(mDialog);
					new MaterialDialog.Builder(getActivity())
							.content(R.string.dialog_updatephotodone)
							.negativeText(R.string.action_know)
							.callback(new ButtonCallback() {
								public void onNegative(MaterialDialog dialog) {
									adapter.removeAll();
									paths.clear();
									mList.clear();
									adapter.notifyDataSetChanged();
								};
							}).show();
				} else {
					Toast.makeText(getActivity(), "上传图片出错", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(getActivity(), "上传图片出错", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	private String id;
	private Dialog mDialog;
	private Uri mOutPutFileUri = null;// 临时存放拍照获取图片
	private File file = null;

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
		View view = inflater.inflate(R.layout.fragment_accountinfo_picture,
				null);
		initView(view);
		return view;
	}

	private void initView(View view) {
		id = getActivity().getSharedPreferences(Configuration.USERINFO, 0)
				.getString(Configuration.ID, "0");
		btn_certain = (Button) view.findViewById(R.id.btn_picture_certain);// 确定
		layout_root = (LinearLayout) view
				.findViewById(R.id.responsetouchContainer);
		gridView = (GridView) view.findViewById(R.id.gv_picture);
		imageView = (ImageView) view.findViewById(R.id.iv_picture);
		imageView.setOnClickListener(this);
		paths = new ArrayList<String>();
		mPopup = new ModelPopup(getActivity(), this);
		gridView.setOnItemClickListener(this);
		btn_certain.setOnClickListener(this);
		adapter = new PictureGridViewAdapter(getActivity(), paths);
		gridView.setAdapter(adapter);
		// gridView.setOnDragListener(new OnDragListener() {
		//
		// @Override
		// public boolean onDrag(View v, DragEvent event) {
		// Log.i("outer", "dsfsdfsdf");
		// return false;
		// }
		// });
		gridView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (position == paths.size()) {
					mPopup.showAtLocation(layout_root, Gravity.BOTTOM, 0, 0);
				} else {
					new MaterialDialog.Builder(getActivity())
							.content(R.string.dialog_deleteselectedphoto)
							.positiveText(R.string.action_yes)
							.negativeText(R.string.action_no)
							.callback(new ButtonCallback() {
								@Override
								public void onPositive(MaterialDialog dialog) {
									paths.remove(position);
									mList.remove(position);
									adapter.notifyDataSetChanged();
								}
							}).show();
				}
				return true;
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_picture:
			mPopup.showAtLocation(layout_root, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.btn_picture_certain:
			PostData();
			break;
		default:
			break;
		}
	}

	private void PostData() {
		mDialog = CustomProgressDialog.showCancelable(getActivity(),
				"图片上传中...");
		TaskExecutor.Execute(new Runnable() {
			@Override
			public void run() {
				Message msg = mHandler.obtainMessage();
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("id", id);
				MultipartEntity entity = NetworkUtil.create();
				try {
					entity = NetworkUtil.put(entity, map);// 添加文字内容
					entity = NetworkUtil.putAttachements(entity, getActivity(),
							mList);
					msg.obj = NetworkUtil.post(entity, "/ArtificerAlbum/add");
				} catch (Exception e) {
					msg.obj = e.getMessage();
				}
				mHandler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onChoosePhoto() {
		// 从相册中取图片
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(choosePictureIntent, SELECT_PIC_BY_PICK_PHOTO);

	}

	@Override
	public void onTakePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String path = Config.IMG_PATH;
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		file = new File(dir, System.currentTimeMillis() + ".jpg");
		mOutPutFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutPutFileUri);
		startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub

	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == getActivity().RESULT_OK) {
			imageView.setVisibility(View.GONE);
			gridView.setVisibility(View.VISIBLE);
			Attachments att;
			String picPath;
			switch (requestCode) {
			case SELECT_PIC_BY_TACK_PHOTO:
				// 选择自拍结果
				// picPath = intent.getStringExtra("path");
				// adapter.addNewItem(picPath);
				//
				// att = new Attachments();
				// att.setFile_path(picPath);
				// att.setFlag(Config.FLAG_FILE);
				// att.setFiletype(Config.FILE_IMG_TYPE);
				// mList.add(att);
				//
				// adapter.notifyDataSetChanged();

				if (file != null) {
					att = new Attachments();
					att.setFile_path(file.getAbsolutePath());
					att.setFlag(Config.FLAG_FILE);
					att.setFiletype(Config.FILE_IMG_TYPE);
					paths.add(att.getFile_path());
					mList.add(att);
					adapter.notifyDataSetChanged();
				} else {
					Toast.makeText(getActivity(), "sdcard不可读",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case SELECT_PIC_BY_PICK_PHOTO:
				// 选择图库图片结果
				// picPath = ImageUtil.getPicPathFromUri(intent.getData(),
				// getActivity());
				// adapter.addNewItem(picPath);
				//
				// att = new Attachments();
				// att.setFile_path(picPath);
				// att.setFlag(Config.FLAG_FILE);
				// att.setFiletype(Config.FILE_IMG_TYPE);
				// mList.add(att);
				//
				// adapter.notifyDataSetChanged();

				picPath = ImageUtil.getPicPathFromUri(intent.getData(),
						getActivity());

				att = new Attachments();
				att.setFile_path(picPath);
				att.setFlag(Config.FLAG_FILE);
				att.setFiletype(Config.FILE_IMG_TYPE);
				paths.add(att.getFile_path());
				mList.add(att);
				adapter.notifyDataSetChanged();
				break;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (position == paths.size()) {
			mPopup.showAtLocation(layout_root, Gravity.BOTTOM, 0, 0);
		} else {
			// 单独显示图片的界面
			// Intent intent = new Intent();
			//
			// intent.setClass(getActivity(), ImageShowActivity.class);
			// intent.putExtra("path", mList.get(position).getFile_path());
			// intent.putExtra("url", "");
			// startActivity(intent);

			if (DateUtil.isFastDoubleClick())
				return;
			Intent intent = new Intent(getActivity(), ImagesShowActivity.class);
			intent.putStringArrayListExtra("mlist", paths);
			intent.putExtra("pos", position + "");
			getActivity().startActivity(intent);
		}

	}

}
