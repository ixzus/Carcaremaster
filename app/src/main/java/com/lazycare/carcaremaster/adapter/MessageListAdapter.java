package com.lazycare.carcaremaster.adapter;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lazycare.carcaremaster.ImagesShowActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.QuestionReplyClass;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.squareup.picasso.Picasso;

/**
 * 车主问题回复adapter
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MessageListAdapter extends BaseAdapter {

	class ViewHolderLeftText {
		TextView timeTV;
		ImageView leftAvatar;
		TextView leftNickname;
		TextView leftText;
	}

	class ViewHolderLeftImage {
		TextView timeTV;
		ImageView leftAvatar;
		TextView leftNickname;
		ImageView leftPhoto;
	}

	class ViewHolderLeftVoice {
		TextView timeTV;
		ImageView leftAvatar;
		TextView leftNickname;
		ImageView leftVoice;
	}

	class ViewHolderRightText {
		TextView timeTV;
		ImageView rightAvatar;
		TextView rightNickname;
		TextView rightText;
		ProgressBar rightProgress;
	}

	class ViewHolderRightImage {
		TextView timeTV;
		ImageView rightAvatar;
		TextView rightNickname;
		ImageView rightPhoto;
		TextView photoProgress;
		ProgressBar rightProgress;
	}

	class ViewHolderRightVoice {
		TextView timeTV;
		ImageView rightAvatar;
		TextView rightNickname;
		ImageView rightVoice;
		ProgressBar rightProgress;
	}

	private List<QuestionReplyClass> items;
	private Context context;
	private LayoutInflater inflater;
	private String artificer_id;

	public MessageListAdapter(Context context, String artificer_id,
			List<QuestionReplyClass> items) {
		Log.i("gmyboy", artificer_id + "");
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.artificer_id = artificer_id;
		this.items = items;
	}

	// public void refreshList(List<IMMessage> items) {
	// this.items = items;
	// this.notifyDataSetChanged();
	// if (this.items.size()>1) {
	// listView.setSelection(items.size()-1);
	// }
	// }

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addNewItem(QuestionReplyClass newItem) {
		items.add(newItem);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderRightText holderRightText = null;
		ViewHolderRightImage holderRightImg = null;
		ViewHolderRightVoice holderRightVoice = null;
		ViewHolderLeftText holderLeftText = null;
		ViewHolderLeftImage holderLeftImg = null;
		ViewHolderLeftVoice holderLeftVoice = null;
		try {
			final QuestionReplyClass message = items.get(position);
			if (convertView == null) {
				// 其他人的回复
				if ("0".equals(message.getBelong())) {
					switch (Integer.parseInt(message.getType())) {
					case Config.REPLY_WENZI:
						holderLeftText = new ViewHolderLeftText();
						convertView = inflater.inflate(R.layout.chat_left_text,
								null);
						holderLeftText.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftText.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftText.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftText.leftText = (TextView) convertView
								.findViewById(R.id.textview_content_l);
						displayLeftText(message, holderLeftText, position);
						convertView.setTag(holderLeftText);
						break;
					case Config.REPLY_TUPIANI:
						holderLeftImg = new ViewHolderLeftImage();
						convertView = inflater.inflate(
								R.layout.chat_left_image, null);
						holderLeftImg.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftImg.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftImg.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftImg.leftPhoto = (ImageView) convertView
								.findViewById(R.id.photo_content_l);
						displayLeftImage(message, holderLeftImg, position);
						convertView.setTag(holderLeftImg);
						break;
					case Config.REPLY_YINPIN:
						holderLeftVoice = new ViewHolderLeftVoice();
						convertView = inflater.inflate(
								R.layout.chat_left_voice, null);
						holderLeftVoice.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftVoice.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftVoice.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftVoice.leftVoice = (ImageView) convertView
								.findViewById(R.id.receiverVoiceNode);
						displayLeftVoice(message, holderLeftVoice, position);
						convertView.setTag(holderLeftVoice);
						break;
					}
				} else {// 自己的回复
					switch (Integer.parseInt(message.getType())) {
					case Config.REPLY_WENZI:
						holderRightText = new ViewHolderRightText();
						convertView = inflater.inflate(
								R.layout.chat_right_text, null);
						holderRightText.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderRightText.rightAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_r);
						holderRightText.rightNickname = (TextView) convertView
								.findViewById(R.id.textview_name_r);
						holderRightText.rightText = (TextView) convertView
								.findViewById(R.id.textview_content_r);
						displayRightText(message, holderRightText, position);
						convertView.setTag(holderRightText);
						break;
					case Config.REPLY_TUPIANI:
						holderRightImg = new ViewHolderRightImage();
						convertView = inflater.inflate(
								R.layout.chat_right_image, null);
						holderRightImg.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderRightImg.rightAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_r);
						holderRightImg.rightNickname = (TextView) convertView
								.findViewById(R.id.textview_name_r);
						holderRightImg.rightPhoto = (ImageView) convertView
								.findViewById(R.id.photo_content_r);
						holderRightImg.photoProgress = (TextView) convertView
								.findViewById(R.id.photo_content_progress);
						holderRightImg.rightProgress = (ProgressBar) convertView
								.findViewById(R.id.view_progress_r);
						displayRightImage(message, holderRightImg, position);
						convertView.setTag(holderRightImg);
						break;

					case Config.REPLY_YINPIN:
						holderRightVoice = new ViewHolderRightVoice();
						convertView = inflater.inflate(
								R.layout.chat_right_voice, null);
						holderRightVoice.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderRightVoice.rightAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_r);
						holderRightVoice.rightNickname = (TextView) convertView
								.findViewById(R.id.textview_name_r);
						holderRightVoice.rightVoice = (ImageView) convertView
								.findViewById(R.id.senderVoiceNode);
						holderRightVoice.rightProgress = (ProgressBar) convertView
								.findViewById(R.id.view_progress_r);
						displayRightVoice(message, holderRightVoice, position);
						convertView.setTag(holderRightVoice);
						break;
					}
				}
			} else {
				// 其他人
				if ("0".equals(message.getBelong())) {
					// switch (Integer.parseInt(message.getType())) {
					// case Config.REPLY_WENZI:
					if (convertView.getTag() instanceof ViewHolderLeftText) {
						holderLeftText = (ViewHolderLeftText) convertView
								.getTag();
						displayLeftText(message, holderLeftText, position);
					} else {
						holderLeftText = new ViewHolderLeftText();
						convertView = inflater.inflate(R.layout.chat_left_text,
								null);
						holderLeftText.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftText.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftText.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftText.leftText = (TextView) convertView
								.findViewById(R.id.textview_content_l);
						displayLeftText(message, holderLeftText, position);
						convertView.setTag(holderLeftText);
					}

					// case Config.REPLY_TUPIANI:
					if (convertView.getTag() instanceof ViewHolderLeftImage) {
						holderLeftImg = (ViewHolderLeftImage) convertView
								.getTag();
						displayLeftImage(message, holderLeftImg, position);
					} else {
						holderLeftImg = new ViewHolderLeftImage();
						convertView = inflater.inflate(
								R.layout.chat_left_image, null);
						holderLeftImg.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftImg.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftImg.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftImg.leftPhoto = (ImageView) convertView
								.findViewById(R.id.photo_content_l);

						displayLeftImage(message, holderLeftImg, position);
						convertView.setTag(holderLeftImg);
					}

					// case Config.REPLY_YINPIN:
					if (convertView.getTag() instanceof ViewHolderLeftVoice) {
						holderLeftVoice = (ViewHolderLeftVoice) convertView
								.getTag();
						displayLeftVoice(message, holderLeftVoice, position);
					} else {
						holderLeftVoice = new ViewHolderLeftVoice();
						convertView = inflater.inflate(
								R.layout.chat_left_voice, null);
						holderLeftVoice.timeTV = (TextView) convertView
								.findViewById(R.id.textview_time);
						holderLeftVoice.leftAvatar = (ImageView) convertView
								.findViewById(R.id.image_portrait_l);
						holderLeftVoice.leftNickname = (TextView) convertView
								.findViewById(R.id.textview_name_l);
						holderLeftVoice.leftVoice = (ImageView) convertView
								.findViewById(R.id.receiverVoiceNode);

						displayLeftVoice(message, holderLeftVoice, position);
						convertView.setTag(holderLeftVoice);
					}

					// break;
					// }
				} else {// 自己恢复
					switch (Integer.parseInt(message.getType())) {
					case Config.REPLY_WENZI:
						if (convertView.getTag() instanceof ViewHolderRightText) {
							holderRightText = (ViewHolderRightText) convertView
									.getTag();
							displayRightText(message, holderRightText, position);
						} else {
							holderRightText = new ViewHolderRightText();
							convertView = inflater.inflate(
									R.layout.chat_right_text, null);
							holderRightText.timeTV = (TextView) convertView
									.findViewById(R.id.textview_time);
							holderRightText.rightAvatar = (ImageView) convertView
									.findViewById(R.id.image_portrait_r);
							holderRightText.rightNickname = (TextView) convertView
									.findViewById(R.id.textview_name_r);
							holderRightText.rightText = (TextView) convertView
									.findViewById(R.id.textview_content_r);
							displayRightText(message, holderRightText, position);
							convertView.setTag(holderRightText);
						}
						break;
					case Config.REPLY_TUPIANI:
						if (convertView.getTag() instanceof ViewHolderRightImage) {
							holderRightImg = (ViewHolderRightImage) convertView
									.getTag();
							displayRightImage(message, holderRightImg, position);
						} else {
							holderRightImg = new ViewHolderRightImage();
							convertView = inflater.inflate(
									R.layout.chat_right_image, null);
							holderRightImg.timeTV = (TextView) convertView
									.findViewById(R.id.textview_time);
							holderRightImg.rightAvatar = (ImageView) convertView
									.findViewById(R.id.image_portrait_r);
							holderRightImg.rightNickname = (TextView) convertView
									.findViewById(R.id.textview_name_r);
							holderRightImg.rightPhoto = (ImageView) convertView
									.findViewById(R.id.photo_content_r);
							holderRightImg.photoProgress = (TextView) convertView
									.findViewById(R.id.photo_content_progress);
							holderRightImg.rightProgress = (ProgressBar) convertView
									.findViewById(R.id.view_progress_r);

							displayRightImage(message, holderRightImg, position);
							convertView.setTag(holderRightImg);
						}

						break;
					case Config.REPLY_YINPIN:
						if (convertView.getTag() instanceof ViewHolderRightVoice) {
							holderRightVoice = (ViewHolderRightVoice) convertView
									.getTag();
							displayRightVoice(message, holderRightVoice,
									position);
						} else {
							holderRightVoice = new ViewHolderRightVoice();
							convertView = inflater.inflate(
									R.layout.chat_right_voice, null);
							holderRightVoice.timeTV = (TextView) convertView
									.findViewById(R.id.textview_time);
							holderRightVoice.rightAvatar = (ImageView) convertView
									.findViewById(R.id.image_portrait_r);
							holderRightVoice.rightNickname = (TextView) convertView
									.findViewById(R.id.textview_name_r);
							holderRightVoice.rightVoice = (ImageView) convertView
									.findViewById(R.id.senderVoiceNode);
							holderRightVoice.rightProgress = (ProgressBar) convertView
									.findViewById(R.id.view_progress_r);

							displayRightVoice(message, holderRightVoice,
									position);

							convertView.setTag(holderRightVoice);
						}

						break;
					}
				}
			}
		} catch (Exception e) {
			Log.i("gmyboy", e + "");
		}
		return convertView;
	}

	private void displayLeftText(QuestionReplyClass msg,
			ViewHolderLeftText viewHolderLeftText, int position) {
		if (msg.getMember_head() != null && !msg.getMember_head().equals("")) {
			Picasso.with(context).load(msg.getMember_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderLeftText.leftAvatar);
		}

		viewHolderLeftText.leftText
				.setText(URLDecoder.decode(msg.getContent()));
		displayName(position, viewHolderLeftText.leftNickname);
		displayTime(position, viewHolderLeftText.timeTV);
	}

	private void displayLeftImage(final QuestionReplyClass msg,
			ViewHolderLeftImage viewHolderLeftImage, int position) {
		if (msg.getMember_head() != null && !msg.getMember_head().equals("")) {
			Picasso.with(context).load(msg.getMember_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderLeftImage.leftAvatar);
		}
		Log.i("gmyboy", msg.getMphotos().size() + "");
		if (msg.getMphotos() != null && !msg.getMphotos().get(0).equals("")) {
			Log.i("gmyboy", msg.getMphotos().get(0));
			Picasso.with(context).load(msg.getMphotos().get(0)).centerCrop()
					.resize(100, 100).into(viewHolderLeftImage.leftPhoto);
		}
		viewHolderLeftImage.leftPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				//
				// intent.setClass(context, ImageShowActivity.class);
				// intent.putExtra("url", );
				// intent.putExtra("path", "");
				// context.startActivity(intent);

				ArrayList<String> list = new ArrayList<String>();
				list.add(NetworkUtil.MAIN_UPLOAD + "/images/" + msg.getPhotos());
				if (DateUtil.isFastDoubleClick())
					return;
				Intent intent = new Intent(context, ImagesShowActivity.class);
				intent.putStringArrayListExtra("mlist",
						(ArrayList<String>) list);
				intent.putExtra("pos", 0 + "");
				intent.putExtra("type", "1");// 网络
				context.startActivity(intent);
			}
		});
		displayName(position, viewHolderLeftImage.leftNickname);
		displayTime(position, viewHolderLeftImage.timeTV);
	}

	private void displayLeftVoice(final QuestionReplyClass msg,
			final ViewHolderLeftVoice viewHolderLeftVoice, int position) {
		if (msg.getMember_head() != null && !msg.getMember_head().equals("")) {
			Picasso.with(context).load(msg.getMember_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderLeftVoice.leftAvatar);
		}
		viewHolderLeftVoice.leftVoice
				.setBackgroundResource(R.drawable.chatfrom_voice_playing_f3);
		viewHolderLeftVoice.leftVoice.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.setClass(context, MediaPlayActivity.class);
				// intent.putExtra("url", NetworkUtil.MAIN_UPLOAD + "/audios/"
				// + msg.getAudio());
				// context.startActivity(intent);

				// AudioPlayer player = new AudioPlayer(context,
				// new OnPlayListener() {
				//
				// @Override
				// public void onStop() {
				//
				// viewHolderLeftVoice.leftVoice
				// .setImageResource(R.drawable.chatfrom_voice_playing);
				// }
				//
				// @Override
				// public void onStart() {
				//
				// }
				// });

				// player.playUrl(NetworkUtil.MAIN_UPLOAD + "/audios/"
				// + msg.getAudio());

			}
		});
		displayName(position, viewHolderLeftVoice.leftNickname);
		displayTime(position, viewHolderLeftVoice.timeTV);
	}

	private void displayRightText(QuestionReplyClass msg,
			ViewHolderRightText viewHolderRightText, int position) {
		if (msg.getArtificer_head() != null
				&& !msg.getArtificer_head().equals("")) {
			Picasso.with(context).load(msg.getArtificer_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderRightText.rightAvatar);
		}
		viewHolderRightText.rightText.setText(URLDecoder.decode(msg
				.getContent()));
		displayName(position, viewHolderRightText.rightNickname);
		displayTime(position, viewHolderRightText.timeTV);
	}

	private void displayRightImage(final QuestionReplyClass msg,
			ViewHolderRightImage viewHolderRightImage, int position) {
		if (msg.getArtificer_head() != null
				&& !msg.getArtificer_head().equals("")) {
			Picasso.with(context).load(msg.getArtificer_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderRightImage.rightAvatar);
		}

		if (msg.getMphotos() != null && !msg.getMphotos().get(0).equals("")) {
			Picasso.with(context).load(msg.getMphotos().get(0)).centerCrop()
					.resize(100, 100).into(viewHolderRightImage.rightPhoto);
		}
		viewHolderRightImage.rightPhoto
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Intent intent = new Intent();
						//
						// intent.setClass(context, ImageShowActivity.class);
						// intent.putExtra("url", NetworkUtil.MAIN_UPLOAD
						// + "/images/" + msg.getPhotos());
						// intent.putExtra("path", "");
						// context.startActivity(intent);

						ArrayList<String> list = new ArrayList<String>();
						list.add(NetworkUtil.MAIN_UPLOAD + "/images/"
								+ msg.getPhotos());
						if (DateUtil.isFastDoubleClick())
							return;
						Intent intent = new Intent(context,
								ImagesShowActivity.class);
						intent.putStringArrayListExtra("mlist",
								(ArrayList<String>) list);
						intent.putExtra("pos", 0 + "");
						intent.putExtra("type", "1");// 网络
						context.startActivity(intent);
					}
				});
		displayName(position, viewHolderRightImage.rightNickname);
		displayTime(position, viewHolderRightImage.timeTV);
	}

	private void displayRightVoice(final QuestionReplyClass msg,
			final ViewHolderRightVoice viewHolderRightVoice, int position) {

		if (msg.getArtificer_head() != null
				&& !msg.getArtificer_head().equals("")) {
			Picasso.with(context).load(msg.getArtificer_head())
					.error(R.drawable.defaulthead)
					.placeholder(R.drawable.defaulthead)
					.into(viewHolderRightVoice.rightAvatar);
		}
		viewHolderRightVoice.rightVoice
				.setBackgroundResource(R.drawable.chatto_voice_playing_f3);
		viewHolderRightVoice.rightVoice
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Log.i("gmyboy", "rightvoice");
						// Intent intent = new Intent();
						// intent.setClass(context, MediaPlayActivity.class);
						// intent.putExtra("url", NetworkUtil.MAIN_UPLOAD
						// + "/audios/" + msg.getAudio());
						// context.startActivity(intent);

						// AudioPlayer player = new AudioPlayer(context,
						// new OnPlayListener() {
						//
						// @Override
						// public void onStop() {
						// viewHolderRightVoice.rightVoice
						// .setImageResource(R.drawable.chatto_voice_playing_f3);
						// }
						//
						// @Override
						// public void onStart() {
						// }
						// });
						// player.playUrl(NetworkUtil.MAIN_UPLOAD + "/audios/"
						// + msg.getAudio());

					}
				});
		displayName(position, viewHolderRightVoice.rightNickname);
		displayTime(position, viewHolderRightVoice.timeTV);

	}

	private void displayName(int position, TextView nickname) {
		nickname.setVisibility(View.VISIBLE);
		nickname.setText(URLDecoder.decode(items.get(position).getName()));
	}

	private void displayTime(int position, TextView timeTV) {
		String currentTime = items.get(position).getAdd_time();
		String previewTime = (position - 1) >= 0 ? items.get(position - 1)
				.getAdd_time() : "0";
		try {
			long time1 = Long.valueOf(currentTime);
			long time2 = Long.valueOf(previewTime);
			if ((time1 - time2) >= 5 * 60) {
				timeTV.setVisibility(View.VISIBLE);
				String formatDate = "MM-dd hh:mm:ss";
				float at = Float.parseFloat(currentTime);
				String fr = CommonUtil.FormatTime(formatDate, at);
				timeTV.setText(fr);
			} else {
				timeTV.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.i("gmyboy", e + "");
		}
	}
}