package com.lazycare.carcaremaster.adapter;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.QuestionReplyClass;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.squareup.picasso.Picasso;

/**
 * 车主问题回复adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionReplyAdapter extends BaseAdapter {

	public List<QuestionReplyClass> listQuestionReply = new ArrayList<QuestionReplyClass>();
	private Context mContext;

	public QuestionReplyAdapter(Context mContext) {

		this.mContext = mContext;
	}

	public QuestionReplyAdapter(List<QuestionReplyClass> mList, Context mContext) {
		super();
		this.listQuestionReply = mList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listQuestionReply.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listQuestionReply.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(QuestionReplyClass newItem) {
		listQuestionReply.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_questionsreply, null);
			holder.ci_replyuserphoto = (SimpleDraweeView) convertView
					.findViewById(R.id.ci_replyuserphoto);
			holder.tv_replyusername = (TextView) convertView
					.findViewById(R.id.tv_replyusername);
			holder.tv_replytime = (TextView) convertView
					.findViewById(R.id.tv_replytime);
			holder.tv_replycontent = (TextView) convertView
					.findViewById(R.id.tv_replycontent);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.reply_have);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final QuestionReplyClass qrc = listQuestionReply.get(position);
		// holder.ci_replyuserphoto.setText(qc.getPhoneNumber());
		// if (!qrc.getHead().equals("") && qrc != null) {
		// Picasso.with(mContext).load(qrc.getHead())
		// .placeholder(R.drawable.defaulthead)
		// .error(R.drawable.defaulthead)
		// .into(holder.ci_replyuserphoto);
		// }

		holder.tv_replyusername.setText(qrc.getName());
		String addtime = qrc.getAdd_time();
		if (!addtime.equals("")) {
			String formatDate = "MM-dd hh:mm:ss";
			float at = Float.parseFloat(addtime);
			String fr = CommonUtil.FormatTime(formatDate, at);
			holder.tv_replytime.setText(fr);
		}
		holder.tv_replycontent.setText(URLDecoder.decode(qrc.getContent()));
		if (qrc.getPhotos() != null && !qrc.getPhotos().equals("")) {
			holder.imageView.setVisibility(View.VISIBLE);
			holder.tv_replycontent.setVisibility(View.GONE);
			Picasso.with(mContext)
					.load(NetworkUtil.MAIN_UPLOAD + "/images/"
							+ qrc.getPhotos()).into(holder.imageView);
			holder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 单独显示图片的界面

				}
			});
		}
		if (qrc.getAudio() != null && !qrc.getAudio().equals("")) {
			holder.imageView.setVisibility(View.VISIBLE);
			holder.tv_replycontent.setVisibility(View.GONE);
			Picasso.with(mContext)
					.load(R.drawable.compose_photo_video_highlighted)
					.centerCrop().resize(30, 30).into(holder.imageView);
			// mIl.displayImage(qrc.getPhotos(), holder.imageView);
			holder.imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 单独显示图片的界面

				}
			});
		}
		return convertView;
	}

	class ViewHolder {
		SimpleDraweeView ci_replyuserphoto;
		TextView tv_replyusername;
		TextView tv_replytime;
		TextView tv_replycontent;
		ImageView imageView;
	}
}
