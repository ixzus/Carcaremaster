package com.lazycare.carcaremaster.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lazycare.carcaremaster.ImagesShowActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.QuestionClass;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.ObjectUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;
import com.lazycare.carcaremaster.widget.CircularImage;
import com.lazycare.carcaremaster.widget.ScrollGridView;
import com.squareup.picasso.Picasso;

/**
 * 车主问题列表页adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionsAdapter extends BaseAdapter {

	public List<QuestionClass> listQuestion;
	private Context mContext;
	private AudioPlayer player;

	public QuestionsAdapter(List<QuestionClass> mList, Context mContext,
			AudioPlayer player) {
		super();
		this.listQuestion = mList;
		this.mContext = mContext;
		this.player = player;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listQuestion.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listQuestion.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addNewItem(QuestionClass newItem) {
		listQuestion.add(newItem);
		notifyDataSetChanged();
	}

	public void removeAll() {
		listQuestion.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		List<String> lstPhoto;// 所有图片的url
		final QuestionClass qc;

		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_questionslist, null);
			holder.ci_userphoto = (CircularImage) convertView
					.findViewById(R.id.ci_userphoto);
			holder.tv_phonenumber = (TextView) convertView
					.findViewById(R.id.tv_phonenumber);
			holder.tv_cardescribtion = (TextView) convertView
					.findViewById(R.id.tv_cardescribtion);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.tv_replaytime = (TextView) convertView
					.findViewById(R.id.tv_replaytime);
			holder.tv_replaycount = (TextView) convertView
					.findViewById(R.id.tv_replaycount);
			holder.gv_image = (ScrollGridView) convertView
					.findViewById(R.id.gv_image_qa);
			holder.iv_imageview = (ImageView) convertView
					.findViewById(R.id.iv_imageview);
			holder.rl_voice = (RelativeLayout) convertView
					.findViewById(R.id.layout_audio);
			holder.tv_voice = (TextView) convertView
					.findViewById(R.id.view_tv_audio);
			holder.unread = (Button) convertView.findViewById(R.id.item_tip);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();
		qc = listQuestion.get(position);
		// 避免重复
		holder.unread.setVisibility(View.GONE);
		holder.tv_content.setVisibility(View.GONE);
		holder.gv_image.setVisibility(View.GONE);
		holder.iv_imageview.setVisibility(View.GONE);
		holder.rl_voice.setVisibility(View.GONE);
		// 设置头像
		if (qc.getHead() != null && !qc.getHead().equals("")) {
			Picasso.with(mContext).load(qc.getHead())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(holder.ci_userphoto);
		}
		// 手机号码
		holder.tv_phonenumber.setText(qc.getMobile());
		// 车系
		holder.tv_cardescribtion.setText(qc.getCar());
		// 内容描述
		if (!qc.getContent().equals("")) {
			holder.tv_content.setVisibility(View.VISIBLE);
			holder.tv_content.setText(qc.getContent());
		}
		if (qc.getUnread() != 0) {
			holder.unread.setVisibility(View.VISIBLE);
			holder.unread.setText(String.valueOf(qc.getUnread()));
		}
		// 回复时间
		holder.tv_replaytime.setText(qc.getMdate());
		// 回复的数量
		holder.tv_replaycount.setText(qc.getAnswers_count());
		// 获得所有的图片url
		lstPhoto = new ArrayList<String>();
		lstPhoto = qc.getMphotos();
		if (!ObjectUtil.isEmpty(lstPhoto)) {
			holder.gv_image.setVisibility(View.VISIBLE);
			ImageOneAdapter adapter_image;
			if (1 == lstPhoto.size()) {
				holder.gv_image.setNumColumns(1);
				adapter_image = new ImageOneAdapter(mContext, lstPhoto,
						R.layout.view_one_iv_com);
			} else {
				holder.gv_image.setNumColumns(4);
				adapter_image = new ImageOneAdapter(mContext, lstPhoto);
			}
			holder.gv_image.setAdapter(adapter_image);
			holder.gv_image.setSelector(android.R.color.transparent);
			holder.gv_image
					.setOnItemClickListener(new OnItemListener(lstPhoto));
		} else
			holder.gv_image.setVisibility(View.GONE);

		if (qc.getAudio() != null && !qc.getAudio().equals("")) {
			holder.tv_voice.setText(qc.getAudio_time().equals("") ? "" : qc
					.getAudio_time() + "s");
			holder.iv_imageview.setVisibility(View.VISIBLE);
			holder.rl_voice.setVisibility(View.VISIBLE);
			holder.iv_imageview
					.setBackgroundResource(R.drawable.chatfrom_voice_playing);
			// player = new AudioPlayer(mContext, holder.iv_imageview, "right");
			// 点击播放音频
			holder.rl_voice.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					player.setImageView((ImageView)v.findViewById(R.id.iv_imageview));
					if (player.isPlaying()) {
						player.pause();
					} else {
						player.playUrl(qc.getAudio());
					}
				}
			});

		}
		return convertView;
	}

	/**
	 * @Package com.mngwyhouhzmb.activity.neighbour
	 * @Title OnItemListener
	 * @Description 图片展示监听
	 * @author LiuSiQing
	 * @Time 2014年11月12日上午11:31:00
	 */
	private class OnItemListener implements OnItemClickListener {

		private List<String> list;

		private OnItemListener(List<String> list) {
			this.list = list;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) { // 需要将图片展示出来
			if (DateUtil.isFastDoubleClick())
				return;
			Intent intent = new Intent(mContext, ImagesShowActivity.class);
			intent.putStringArrayListExtra("mlist", (ArrayList<String>) list);
			intent.putExtra("pos", position + "");
			intent.putExtra("type", "1");// 网络
			mContext.startActivity(intent);
		}
	}

	class ViewHolder {
		CircularImage ci_userphoto;
		TextView tv_phonenumber;
		TextView tv_cardescribtion;
		TextView tv_content;
		TextView tv_replaytime;
		TextView tv_replaycount;
		ScrollGridView gv_image;
		LinearLayout ll_speechs;
		ImageView iv_imageview;
		RelativeLayout rl_voice;
		TextView tv_voice;
		Button unread;
	}
}
