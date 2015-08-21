package com.lazycare.carcaremaster.adapter;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.util.ImageUtil;

/**
 * 显示选择的所有照片的
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class PictureGridViewAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<String> list;
	private String tempPath;

	public PictureGridViewAdapter(Context context, ArrayList<String> list) {
		super();
		this.context = context;
		this.list = list;

	}

	public PictureGridViewAdapter(Context context) {
		super();
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size() + 1;
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(String newItem) {
		list.add(newItem);
	}

	public void removeAll() {
		list.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView = null;
		String path = "";
		Bitmap bitmap = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.view_one_iv, null);
			imageView = (ImageView) convertView.findViewById(R.id.iv_one);
			convertView.setTag(imageView);
		} else {
			imageView = (ImageView) convertView.getTag();
		}
		// 在最后添加一个加号
		if (position == getCount() - 1) {
			imageView.setImageResource(R.mipmap.common_phone);
		} else {
			path = getItem(position);
			// 缩小图片的品质

			// imageLoader.displayImage(path, imageView, options);
			try {
				bitmap = ImageUtil.getBitmapByPath(path,
						ImageUtil.getOptions(path), 180, 240);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			// bitmap = ImageUtil.getLocalThumbImg(path, 180, 240, "jpg");
			if (bitmap != null)
				imageView.setImageBitmap(bitmap);
		}
		tempPath = path;
		// imageView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent();
		// intent.setClass(context, ImageShowActivity.class);
		// intent.putExtra("path", tempPath);
		// context.startActivity(intent);
		// }
		// });

		return convertView;
	}

}
