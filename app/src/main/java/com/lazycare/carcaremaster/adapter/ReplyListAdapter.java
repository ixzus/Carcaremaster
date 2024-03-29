package com.lazycare.carcaremaster.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lazycare.carcaremaster.ImagesShowActivity;
import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.data.QuestionReplyClass;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.StringUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;

/**
 * 车主问题回复adapter 带聊天界面
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class ReplyListAdapter extends BaseAdapter {

    class ViewHolderLeft {
        TextView timeTV;// 左边 时间
        SimpleDraweeView leftAvatar;// 头像
        TextView leftNickname;// 昵称
        TextView leftText;// 文字
        SimpleDraweeView leftPhoto;// 图片
        ImageView leftVoice;// 音频
    }

    class ViewHolderRight {
        TextView timeTV;
        SimpleDraweeView rightAvatar;
        TextView rightNickname;
        TextView rightText;
        SimpleDraweeView rightPhoto;
        ImageView rightVoice;
    }

    private List<QuestionReplyClass> items;
    private Context context;
    private LayoutInflater inflater;
    private AudioPlayer player;
    AnimationDrawable rocketAnimation;

    public ReplyListAdapter(Context context, List<QuestionReplyClass> items,
                            AudioPlayer player) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.player = player;
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
        ViewHolderRight holderRight = null;
        ViewHolderLeft holderLeft = null;
        try {
            QuestionReplyClass message = items.get(position);
            if (convertView == null) {
                // 其他人的回复
                if ("0".equals(message.getBelong())) {

                    holderLeft = new ViewHolderLeft();
                    convertView = inflater.inflate(R.layout.reply_left, null);
                    holderLeft.timeTV = (TextView) convertView
                            .findViewById(R.id.textview_time);
                    holderLeft.leftAvatar = (SimpleDraweeView) convertView
                            .findViewById(R.id.image_portrait_l);
                    holderLeft.leftNickname = (TextView) convertView
                            .findViewById(R.id.textview_name_l);
                    holderLeft.leftText = (TextView) convertView
                            .findViewById(R.id.textview_content_l);
                    holderLeft.leftPhoto = (SimpleDraweeView) convertView
                            .findViewById(R.id.photo_content_l);
                    holderLeft.leftVoice = (ImageView) convertView
                            .findViewById(R.id.receiverVoiceNode);
                    displayLeft(message, holderLeft, position);
                    convertView.setTag(holderLeft);

                } else {// 自己的回复

                    holderRight = new ViewHolderRight();
                    convertView = inflater.inflate(R.layout.reply_right, null);
                    holderRight.timeTV = (TextView) convertView
                            .findViewById(R.id.textview_time);
                    holderRight.rightAvatar = (SimpleDraweeView) convertView
                            .findViewById(R.id.image_portrait_r);
                    holderRight.rightNickname = (TextView) convertView
                            .findViewById(R.id.textview_name_r);
                    holderRight.rightText = (TextView) convertView
                            .findViewById(R.id.textview_content_r);
                    holderRight.rightPhoto = (SimpleDraweeView) convertView
                            .findViewById(R.id.photo_content_r);
                    holderRight.rightVoice = (ImageView) convertView
                            .findViewById(R.id.senderVoiceNode);
                    displayRight(message, holderRight, position);
                    convertView.setTag(holderRight);
                }
            } else {
                // 其他人
                if ("0".equals(message.getBelong())) {
                    if (convertView.getTag() instanceof ViewHolderLeft) {
                        holderLeft = (ViewHolderLeft) convertView.getTag();
                        displayLeft(message, holderLeft, position);
                    } else {
                        holderLeft = new ViewHolderLeft();
                        convertView = inflater.inflate(R.layout.reply_left,
                                null);
                        holderLeft.timeTV = (TextView) convertView
                                .findViewById(R.id.textview_time);
                        holderLeft.leftAvatar = (SimpleDraweeView) convertView
                                .findViewById(R.id.image_portrait_l);
                        holderLeft.leftNickname = (TextView) convertView
                                .findViewById(R.id.textview_name_l);
                        holderLeft.leftText = (TextView) convertView
                                .findViewById(R.id.textview_content_l);
                        holderLeft.leftPhoto = (SimpleDraweeView) convertView
                                .findViewById(R.id.photo_content_l);
                        holderLeft.leftVoice = (ImageView) convertView
                                .findViewById(R.id.receiverVoiceNode);
                        displayLeft(message, holderLeft, position);
                        convertView.setTag(holderLeft);
                    }

                } else {// 自己恢复
                    if (convertView.getTag() instanceof ViewHolderRight) {
                        holderRight = (ViewHolderRight) convertView.getTag();
                        displayRight(message, holderRight, position);
                    } else {
                        holderRight = new ViewHolderRight();
                        convertView = inflater.inflate(R.layout.reply_right,
                                null);
                        holderRight.timeTV = (TextView) convertView
                                .findViewById(R.id.textview_time);
                        holderRight.rightAvatar = (SimpleDraweeView) convertView
                                .findViewById(R.id.image_portrait_r);
                        holderRight.rightNickname = (TextView) convertView
                                .findViewById(R.id.textview_name_r);
                        holderRight.rightText = (TextView) convertView
                                .findViewById(R.id.textview_content_r);
                        holderRight.rightPhoto = (SimpleDraweeView) convertView
                                .findViewById(R.id.photo_content_r);
                        holderRight.rightVoice = (ImageView) convertView
                                .findViewById(R.id.senderVoiceNode);
                        displayRight(message, holderRight, position);
                        convertView.setTag(holderRight);
                    }

                }
            }
        } catch (Exception e) {
            Log.i("gmyboy", e + "");
        }
        return convertView;
    }

    private void displayLeft(QuestionReplyClass msg,
                             final ViewHolderLeft viewHolderLeft, int position) {
        // 避免加载错位
        viewHolderLeft.leftText.setVisibility(View.GONE);
        viewHolderLeft.leftPhoto.setVisibility(View.GONE);
        viewHolderLeft.leftVoice.setVisibility(View.GONE);
        // 显示头像
        if (msg.getMember_head() != null && !msg.getMember_head().equals("")) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(msg.getMember_head()))
                    .setAutoRotateEnabled(true)//设置图片智能摆正
                    .setProgressiveRenderingEnabled(true)//设置渐进显示
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolderLeft.leftAvatar.getController())
                    .build();
            viewHolderLeft.leftAvatar.setController(controller);
        }
        // 显示内容
        if (!msg.getContent().equals("")) {
            viewHolderLeft.leftText.setVisibility(View.VISIBLE);
            viewHolderLeft.leftText
                    .setText(URLDecoder.decode(msg.getContent()));
        }
        // 显示图片
        if (msg.getMphotos() != null && msg.getMphotos().size() != 0) {
            viewHolderLeft.leftPhoto.setVisibility(View.VISIBLE);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(msg.getMphotos().get(0)))
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .setAutoRotateEnabled(true)//设置图片智能摆正
                    .setProgressiveRenderingEnabled(true)//设置渐进显示
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolderLeft.leftPhoto.getController())
                    .build();
            viewHolderLeft.leftPhoto.setController(controller);
            // 图片点击动作
            viewHolderLeft.leftPhoto.setTag(msg.getMphotos().get(0));

            viewHolderLeft.leftPhoto.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    ArrayList<String> list = new ArrayList<>();
                    list.add((String) v.getTag());
                    if (DateUtil.isFastDoubleClick())
                        return;
                    Intent intent = new Intent(context,
                            ImagesShowActivity.class);
                    intent.putStringArrayListExtra("mlist", list);
                    intent.putExtra("pos", 0 + "");
                    intent.putExtra("type", "1");// 网络
                    context.startActivity(intent);
                }
            });
        }

        // 显示音频
        viewHolderLeft.leftVoice.setTag(msg.getAudio());
        if (!msg.getAudio().equals("")) {
            viewHolderLeft.leftVoice.setVisibility(View.VISIBLE);
            viewHolderLeft.leftVoice
                    .setBackgroundResource(R.mipmap.chatfrom_voice_playing);

            viewHolderLeft.leftVoice.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    player.setDirect("right");
                    player.setImageView((ImageView) v);
                    v.setFocusable(true);
                    v.requestFocus();
                    if (player.isPlaying()) {
                        player.pause();
                    } else {
                        player.playUrl(String.valueOf(v.getTag()));
                    }
                }
            });
        }
        displayName(position, viewHolderLeft.leftNickname);
        displayTime(position, viewHolderLeft.timeTV);
    }

    private void displayRight(QuestionReplyClass msg,
                              final ViewHolderRight viewHolderRight, int position) {
        // 避免加载错位
        viewHolderRight.rightText.setVisibility(View.GONE);
        viewHolderRight.rightPhoto.setVisibility(View.GONE);
        viewHolderRight.rightVoice.setVisibility(View.GONE);
        if (msg.getArtificer_head() != null
                && !msg.getArtificer_head().equals("")) {
            String head = context.getSharedPreferences(Config.USERINFO,
                    0).getString(Config.HEAD, "");
            if (!head.equals("")) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(head))
                        .setAutoRotateEnabled(true)//设置图片智能摆正
                        .setProgressiveRenderingEnabled(true)//设置渐进显示
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setOldController(viewHolderRight.rightAvatar.getController())
                        .build();
                viewHolderRight.rightAvatar.setController(controller);
            }
        }
        if (msg.getMphotos() != null && msg.getMphotos().size() != 0) {
            viewHolderRight.rightPhoto.setVisibility(View.VISIBLE);
            String urlOrPath = msg.getMphotos().get(0);
            urlOrPath = StringUtil.isHttp(urlOrPath) ? urlOrPath : "file://" + urlOrPath;
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(urlOrPath))
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .setAutoRotateEnabled(true)//设置图片智能摆正
                    .setProgressiveRenderingEnabled(true)//设置渐进显示
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(viewHolderRight.rightPhoto.getController())
                    .build();
            viewHolderRight.rightPhoto.setController(controller);

            viewHolderRight.rightPhoto.setTag(urlOrPath);
            viewHolderRight.rightPhoto
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            ArrayList<String> list = new ArrayList<>();
                            list.add((String) v.getTag());
                            if (DateUtil.isFastDoubleClick())
                                return;
                            Intent intent = new Intent(context, ImagesShowActivity.class);
                            intent.putStringArrayListExtra("mlist", list);
                            intent.putExtra("pos", 0 + "");//当前点击的位置
                            intent.putExtra("type", "1");// 网络
                            context.startActivity(intent);
                        }
                    });
        }

        if (!msg.getAudio().equals("")) {
            viewHolderRight.rightVoice.setTag(msg.getAudio());
            viewHolderRight.rightVoice.setVisibility(View.VISIBLE);
            viewHolderRight.rightVoice.setBackgroundResource(R.mipmap.chatto_voice_playing_f3);
            viewHolderRight.rightVoice
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            player.setDirect("left");
                            player.setImageView((ImageView) v);
                            v.setFocusable(true);
                            v.requestFocus();
                            if (player.isPlaying()) {
                                player.pause();
                            } else {
                                player.playUrl(String.valueOf(v.getTag()));
                            }
                        }
                    });
        }
        if (!msg.getContent().equals("")) {
            viewHolderRight.rightText.setVisibility(View.VISIBLE);
            viewHolderRight.rightText.setText(msg.getContent());
        }

        viewHolderRight.rightNickname.setVisibility(View.VISIBLE);
        viewHolderRight.rightNickname.setText("我");
        // 右边的name，直接显示我
        displayTime(position, viewHolderRight.timeTV);
    }

    private void displayName(int position, TextView nickname) {
        nickname.setVisibility(View.VISIBLE);
        nickname.setText(URLDecoder.decode(items.get(position)
                .getMember_mobile()));
    }

    private void displayTime(int position, TextView timeTV) {
        String currentTime = items.get(position).getAdd_time();
        String previewTime = (position - 1) >= 0 ? items.get(position - 1)
                .getAdd_time() : "0";
        try {
            long time1 = Long.valueOf(currentTime);
            long time2 = Long.valueOf(previewTime);
            if ((time1 - time2) >= 5 * 60) {// 时间超过5分钟就会再次显示时间
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