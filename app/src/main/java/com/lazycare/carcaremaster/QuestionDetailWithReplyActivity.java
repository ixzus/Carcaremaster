package com.lazycare.carcaremaster;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.mime.MultipartEntity;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lazycare.carcaremaster.adapter.ImageOneAdapter;
import com.lazycare.carcaremaster.adapter.ReplyListAdapter;
import com.lazycare.carcaremaster.data.Attachments;
import com.lazycare.carcaremaster.data.IMMessage;
import com.lazycare.carcaremaster.data.QuestionReplyClass;
import com.lazycare.carcaremaster.dialog.CustomProgressDialog;
import com.lazycare.carcaremaster.fragment.QuestionsFragment;
import com.lazycare.carcaremaster.impl.RecordStrategy;
import com.lazycare.carcaremaster.thread.DataRunnable;
import com.lazycare.carcaremaster.thread.TaskExecutor;
import com.lazycare.carcaremaster.util.AudioRecorder2Mp3Util;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.Config;
import com.lazycare.carcaremaster.util.Constant;
import com.lazycare.carcaremaster.util.DateUtil;
import com.lazycare.carcaremaster.util.ImageUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;
import com.lazycare.carcaremaster.util.ObjectUtil;
import com.lazycare.carcaremaster.widget.AudioPlayer;
import com.lazycare.carcaremaster.widget.ModelPopup;
import com.lazycare.carcaremaster.widget.RecordButton;
import com.lazycare.carcaremaster.widget.ScrollGridView;

/**
 * 车主问题的详情页
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class QuestionDetailWithReplyActivity extends BaseActivity implements OnClickListener, ModelPopup.OnDialogListener, TextWatcher {
    private LinearLayout layout_more, layout_emo;//更多布局，
    private ViewPager pager_emo;// 表情页面
    private ControlView mCv = new ControlView();
    private ModelPopup popup;//弹出选择框
    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;
    private LinearLayout main_layout;

    private Button voiceOrTextButton;//录音文字切换按钮
    private RecordButton voiceButton;// 回复声音
    private EditText chat_content;//聊天回复输入框
    private Button multiMediaButton;// 添加
    private Button sendButton;//发送按钮

    //头布局
    private class ControlView {
        SimpleDraweeView ciUserPhoto;
        TextView tvPhoneNumber;
        TextView tvCarDescribtion;
        TextView tvContent;
        TextView tvDate;
        TextView tvCount;
        ScrollGridView gvImage;
        ImageView ivvoice;
        TextView tvvoice;
        RelativeLayout rlvoice;
    }

    private List<QuestionReplyClass> lstQuestionReply = new ArrayList<>();
    private int pageIndex = 1;
    private int dataSize = 0;
    private ListView listView;
    private ReplyListAdapter adapter;
    boolean isLoading = false;
    private PopupWindow popReply = null;
    private View layout = null;
    private String question_id = "", member_id = "";
    private Handler mHandler = new LoadQuestionDetailWithReplyHandler(this);
    private String replyContentType = "1";// 1纯文字，2音频 ,3:图片 4:视频
    private int type = 1;
    private int unread = 0;// 列表页传来的未读数目
    private boolean temp = false;
    private List<Attachments> mList;
    private RelativeLayout morebtn1, morebtn2;
    private Uri mOutPutFileUri = null;// 临时存放拍照获取图片
    private File file = null;
    private FloatingActionButton qiang;
    private LinearLayout second_layout;
    //录音相关
    private String audio = "";
    private String audio_time = "0";
    private AudioPlayer player;
    private AnimationDrawable rocketAnimation;
    // IM
    private String to = "";
    private Chat chat = null;
    private XMPPConnection xmppConnection;
    private FileTransferManager fileTransferManager;
    private IncomingFileTransfer infiletransfer;

    private QuestionReplyClass questionReplyClass = null;//用户添加msg时临时存放消息
    // 广播用来接收消息
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.NEW_MESSAGE_ACTION.equals(action)) {
                IMMessage message = intent.getParcelableExtra(IMMessage.IMMESSAGE_KEY);
                questionReplyClass = new QuestionReplyClass();
                String[] lstMsg = message.getContent().split("\\/-");
                if (lstMsg != null && lstMsg.length == 4) {
                    // Log.i("gmyboy", lstMsg[2]+"");
                    /* Jdjjdjd/-imtext/-196/-123 问题id/-消息id */

                    // 判断问题id
                    if (lstMsg[2].equals(question_id)) {

                        if (lstMsg[1].contains(Constant.MY_NEWS_TXT)) {
                            questionReplyClass.setContent(lstMsg[0]);
                        } else if (lstMsg[1].contains(Constant.MY_NEWS_AUDIO)) {
                            questionReplyClass.setAudio(lstMsg[0]);
                        } else if (lstMsg[1].contains(Constant.MY_NEWS_IMG)) {
                            List<String> mphotos = new ArrayList<>();
                            mphotos.add(lstMsg[0]);
                            questionReplyClass.setMphotos(mphotos);
                        } else {

                        }
                        // 设置消息为已读
                        setRead(lstMsg[3]);
                    }
                }

                questionReplyClass.setBelong("0");
                questionReplyClass.setType("1");
                questionReplyClass.setArtificer_name("");
                questionReplyClass.setMember_mobile(to);
                addNewMsg(questionReplyClass);

            }
        }

    };

    @Override
    protected void onDestroy() {
        if (player != null && player.isPlaying()) {
            Log.d("gmyboy", "stop");
            player.stop();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        if (player != null && player.isPlaying()) {
            Log.d("gmyboy", "stop");
            player.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.NEW_MESSAGE_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    public void setLayout() {
        setContentView(R.layout.activity_questiondetailwithreply);
    }

    @Override
    public void setActionBarOption() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("问题详情");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        question_id = intent.getStringExtra("question_id");
        type = intent.getIntExtra("type", 1);
        unread = intent.getIntExtra("unread", 0);
        multiMediaButton = (Button) this.findViewById(R.id.multiMediaButton);
        sendButton = (Button) this.findViewById(R.id.sendButton);
        chat_content = (EditText) this.findViewById(R.id.chat_content);
        voiceOrTextButton = (Button) this.findViewById(R.id.voiceOrTextButton);
        voiceButton = (RecordButton) this.findViewById(R.id.voiceButton);
        popup = new ModelPopup(QuestionDetailWithReplyActivity.this, this);
        main_layout = (LinearLayout) this.findViewById(R.id.main_layout);
        mList = new ArrayList<>();
        morebtn1 = (RelativeLayout) this.findViewById(R.id.more_btn1);
        morebtn2 = (RelativeLayout) this.findViewById(R.id.more_btn2);
        qiang = (FloatingActionButton) this.findViewById(R.id.qiang);
        second_layout = (LinearLayout) this.findViewById(R.id.second_layout);
        View view = getLayoutInflater().inflate(R.layout.view_reply_header, null);
        mCv.ciUserPhoto = (SimpleDraweeView) view.findViewById(R.id.ci_userphoto);
        mCv.tvPhoneNumber = (TextView) view.findViewById(R.id.tv_phonenumber);
        mCv.tvCarDescribtion = (TextView) view.findViewById(R.id.tv_cardescribtion);
        mCv.tvContent = (TextView) view.findViewById(R.id.tv_content);
        mCv.gvImage = (ScrollGridView) view.findViewById(R.id.gv_image);
        mCv.ivvoice = (ImageView) view.findViewById(R.id.view_iv_imageview);
        mCv.rlvoice = (RelativeLayout) view.findViewById(R.id.layout_audio);
        mCv.tvvoice = (TextView) view.findViewById(R.id.view_tv_audio);
        mCv.tvDate = (TextView) view.findViewById(R.id.tv_date);
        mCv.tvCount = (TextView) view.findViewById(R.id.tv_replay1);
        listView = (ListView) findViewById(R.id.lv_replys);
        listView.addHeaderView(view);
        layout_more = (LinearLayout) findViewById(R.id.layout_more);

        if (type == 0) {
        } else {
            qiang.setVisibility(View.GONE);
            second_layout.setVisibility(View.VISIBLE);
        }
        mCv.ivvoice.setBackgroundResource(R.drawable.chatfrom_voice_playing_f3);
        mCv.rlvoice.setVisibility(View.GONE);
        player = new AudioPlayer(mContext, "right");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        multiMediaButton.setOnClickListener(this);
        chat_content.setOnClickListener(this);
        voiceOrTextButton.setOnClickListener(this);
        chat_content.addTextChangedListener(this);
        morebtn1.setOnClickListener(this);
        morebtn2.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        qiang.setOnClickListener(this);
        mCv.rlvoice.setOnClickListener(this);

        // 录音事件监听
        voiceButton.setAudioRecord(new RecordStrategy() {
            private String fileFolder = Config.RECORD_PATH;
            private String fileName;
            private AudioRecorder2Mp3Util audioRecoder;
            private boolean canClean = false;

            /**
             * 释放资源
             */
            @Override
            public void stop() {
                Log.d("gmyboy", "------------stop-------------");

                // Toast.makeText(QuestionDetailWithReplyActivity.this, "正在转换",
                // Toast.LENGTH_SHORT).show();
                audioRecoder.stopRecordingAndConvertFile();

                // Toast.makeText(QuestionDetailWithReplyActivity.this, "ok",
                // Toast.LENGTH_SHORT).show();
                audioRecoder.cleanFile(AudioRecorder2Mp3Util.RAW);
                // 如果要关闭可以
                audioRecoder.close();
                audioRecoder = null;
            }

            /**
             * 开始录音
             */
            @Override
            public void start() {
                Log.d("gmyboy", "------------start-------------");
                if (canClean) {
                    audioRecoder.cleanFile(AudioRecorder2Mp3Util.MP3
                            | AudioRecorder2Mp3Util.RAW);
                }
                audioRecoder.startRecording();
                canClean = true;
            }

            /**
             * 准备工作
             */
            @Override
            public void ready() {
                Log.d("gmyboy", "------------ready-------------");
                File file = new File(fileFolder);
                if (!file.exists()) {
                    file.mkdir();
                }
                fileName = getCurrentDate();
                if (audioRecoder == null) {
                    audioRecoder = new AudioRecorder2Mp3Util(null, getFilePath() + fileName + ".raw", getFilePath() + fileName + ".mp3");
                }

            }

            /**
             * 获取保存路径
             */
            @Override
            public String getFilePath() {
                return fileFolder + "/";
            }

            @Override
            public double getAmplitude() {
                // if (!isRecording) {
                // return 0;
                // }
                // return recorder.getMaxAmplitude();
                return Math.random() * 20000;
            }

            /**
             * 删除本地保存文件
             */
            @Override
            public void deleteOldFile() {
                Log.d("gmyboy", "------------deleteOldFile-------------");
                File file = new File(getFilePath() + fileName + ".mp3");
                if (file.exists())
                    file.delete();

            }

            /**
             * 发送
             */
            @Override
            public void complite(float time) {
                Log.d("gmyboy", "------------complite-------------");
                Attachments att = new Attachments();
                att.setFile_path(getFilePath() + fileName + ".mp3");
                att.setFlag(Config.FLAG_FILE);
                att.setFiletype(Config.FILE_AUDIO_TYPE);
                audio_time = String.valueOf((int) time);
                mList.clear();
                mList.add(att);
                replyContentType = "2";
                postData();
            }
        });
        loadMoreData();
    }

    // 以当前时间作为录音文件名
    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                QuestionDetailWithReplyActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_audio:
                player.setDirect("right");
                player.setImageView(mCv.ivvoice);
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.playUrl(audio);
                }
                break;
            case R.id.qiang:
                qiangSeccuss();
                break;
            case R.id.sendButton:
                String content = chat_content.getText().toString().trim();
                if (content.equals("")) {
                    CommonUtil.showSnack(main_layout, "回复内容不能为空!");
                } else {
                    replyContentType = "1";
                    sendMsg(content);
                }
                break;
            case R.id.more_btn1:
                popup.showAtLocation(main_layout, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.more_btn2:
                break;
            case R.id.multiMediaButton:
                hideSoftInputView();
                if (layout_more.getVisibility() == View.VISIBLE) {
                    layout_more.setVisibility(View.GONE);
                } else if (layout_more.getVisibility() == View.GONE) {
                    layout_more.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.chat_content:
                showSoftInputView();
                break;
            case R.id.voiceOrTextButton:
                if (chat_content.getVisibility() == View.VISIBLE) {
                    hideSoftInputView();
                    chat_content.setVisibility(View.INVISIBLE);
                    voiceButton.setVisibility(View.VISIBLE);
                    voiceOrTextButton.setBackgroundResource(R.drawable.keyborad);
                } else if (chat_content.getVisibility() == View.INVISIBLE) {
                    showSoftInputView();
                    chat_content.setVisibility(View.VISIBLE);
                    voiceButton.setVisibility(View.INVISIBLE);
                    voiceOrTextButton.setBackgroundResource(R.drawable.voice);
                }
                layout_more.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 显示软键盘
     */
    public void showSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            manager.showSoftInput(chat_content, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 加载数据
     */
    private void loadMoreData() {
        mDialog = CustomProgressDialog.showCancelable(mContext, "加载中...");
        Map<String, String> map = new HashMap<>();
        map.put("type", type + "");
        map.put("artificer_id", id);
        map.put("question_id", question_id);
        TaskExecutor.Execute(new DataRunnable(this, "/Questions/get", mHandler, Config.WHAT_ONE, map));
    }

    /**
     * 设置回复内容为已读
     */
    private void setRead(String replayID) {
        Map<String, String> map = new HashMap<>();
        map.put("answer", replayID);
        TaskExecutor.Execute(new DataRunnable(this, "/Answers/setRead", mHandler, Config.WHAT_FOUR, map));
    }

    /**
     * 抢单操作
     */
    private void qiangSeccuss() {
        mDialog = CustomProgressDialog.showCancelable(mContext, "正在拼命的抢...");
        Map<String, String> map = new HashMap<>();
        map.put("artificer", id);
        map.put("from", username);
        map.put("to", to);
        map.put("question", question_id);
        TaskExecutor.Execute(new DataRunnable(this, "/Questions/grab", mHandler, Config.WHAT_THREE, map));
    }

    /**
     * <p>
     * 标 题: LoginHandler
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
     * @author Gmy
     */
    @SuppressLint("HandlerLeak")
    private class LoadQuestionDetailWithReplyHandler extends Handler {

        private WeakReference<QuestionDetailWithReplyActivity> mWeak;

        public LoadQuestionDetailWithReplyHandler(
                QuestionDetailWithReplyActivity activity) {
            mWeak = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QuestionDetailWithReplyActivity activity = mWeak.get();
            doAction(activity, msg.what, (String) msg.obj);
        }

        /**
         * @Title doAction
         * @Description 动作
         */
        private void doAction(QuestionDetailWithReplyActivity activity,
                              int what, String json) {
            mDialog.dismiss();
            switch (what) {
                case Config.WHAT_ONE:
                    Log.d(TAG, json);
                    try {
                        Gson gson = new Gson();
                        JSONObject jb = new JSONObject(json);
                        String error = jb.getString("error");
                        String msg = jb.getString("msg");
                        String data = jb.getString("data");
                        isLoading = false;
                        if (error.equals("0")) {
                            JSONObject jd = new JSONObject(data);
                            String car = jd.getString("car");
                            String mobile = jd.getString("mobile");
                            String content = jd.getString("content");// 内容
                            String head = jd.getString("head");// 头像
                            String mdate = jd.getString("mdate");
                            String mphotos = jd.getString("mphotos");
                            String answers = jd.getString("answers");
                            String question_type = jd.getString("question_type");
                            String _id = jd.getString("artificer_id");
                            String audio_time = jd.getString("audio_time");// 音频时间
                            audio = jd.getString("audio");// 音频
                            member_id = jd.getString("member_id");
                            // 初始化IM
                            // 待解决生产环境有mmobile
                            if (jd.getString("mmobile") != null)
                                to = jd.getString("mmobile");// 得到对方师傅的名字
//                            initIM();
                            if (head != null && !head.equals("")) {
                                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(head))
                                        .setAutoRotateEnabled(true)//设置图片智能摆正
                                        .setProgressiveRenderingEnabled(true)//设置渐进显示
                                        .build();
                                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                                        .setImageRequest(request)
                                        .setOldController(mCv.ciUserPhoto.getController())
                                        .build();
                                mCv.ciUserPhoto.setController(controller);
                            }
                            if (audio != null && !audio.equals("")) {
                                mCv.ivvoice.setVisibility(View.VISIBLE);
                                mCv.rlvoice.setVisibility(View.VISIBLE);
                                mCv.tvvoice.setText(audio_time + "s");
                            }
                            // 判断是不是我自己抢的
                            if (_id.equals(id)) {
                                qiang.setVisibility(View.GONE);
                                second_layout.setVisibility(View.VISIBLE);
                            } else {
                                qiang.setVisibility(View.VISIBLE);// 抢单按钮
                                second_layout.setVisibility(View.GONE);// 底部输入框
                            }
                            mCv.tvPhoneNumber.setText(mobile);
                            mCv.tvCarDescribtion.setText(car);
                            mCv.tvContent.setText(content);
                            mCv.tvDate.setText(mdate);
                            List<String> lstPhoto = gson.fromJson(mphotos, new TypeToken<List<String>>() {
                            }.getType());
                            if (!ObjectUtil.isEmpty(lstPhoto)) {
                                mCv.gvImage.setVisibility(View.VISIBLE);
                                ImageOneAdapter adapter_image;
                                if (1 == lstPhoto.size()) {
                                    mCv.gvImage.setNumColumns(1);
                                    adapter_image = new ImageOneAdapter(QuestionDetailWithReplyActivity.this, lstPhoto, R.layout.view_one_iv_com);
                                } else {
                                    mCv.gvImage.setNumColumns(4);
                                    adapter_image = new ImageOneAdapter(QuestionDetailWithReplyActivity.this, lstPhoto);
                                }
                                mCv.gvImage.setAdapter(adapter_image);
                                mCv.gvImage.setSelector(android.R.color.transparent);
                                mCv.gvImage.setOnItemClickListener(new OnItemListener(lstPhoto));
                                // mCv.gvImage.setOnItemClickListener(new
                                // OnItemListener(lstPhoto));
                            } else
                                mCv.gvImage.setVisibility(View.GONE);

                            List<QuestionReplyClass> lstQuestionReply = gson.fromJson(answers, new TypeToken<List<QuestionReplyClass>>() {
                            }.getType());
                            mCv.tvCount.setText(String.valueOf(lstQuestionReply.size()));
                            adapter = new ReplyListAdapter(QuestionDetailWithReplyActivity.this, lstQuestionReply, player);
                            listView.setAdapter(adapter);
                            listView.setOnScrollListener(new OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(AbsListView view, int scrollState) {
                                }

                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    // TODO Auto-generated method stub
                                    if (totalItemCount == firstVisibleItem + visibleItemCount) {
                                        if (!isLoading && adapter.getCount() != 0 && adapter.getCount() < dataSize) {
                                            pageIndex++;
                                            isLoading = true;
                                            loadMoreData();// 再次加载数据
                                        }
                                    }
                                }
                            });
                            if (unread != 0) {
                                setRead(lstQuestionReply.get(0).getId());
                                setFlag();
                            }
                        } else
                            CommonUtil.showSnack(main_layout, msg);
                    } catch (Exception e) {
                    }
                    break;
                case Config.WHAT_TWO:// 回复
                    Log.d(TAG, json);
                    try {
                        JSONObject jb = new JSONObject(json);
                        String error = jb.getString("error");
                        String msg = jb.getString("msg");
                        if (error.equals("0")) {
                            CommonUtil.showSnack(main_layout, "回复成功");
                            questionReplyClass = new QuestionReplyClass();
                            questionReplyClass.setBelong("1");
                            questionReplyClass.setArtificer_name("");
                            questionReplyClass.setType(replyContentType);
                            if (replyContentType.equals("1")) {
                                questionReplyClass.setContent(chat_content.getText().toString().trim());
                            } else if (replyContentType.equals("2") && mList.size() == 1) {
                                questionReplyClass.setAudio(mList.get(0).getFile_path());
                            } else if (replyContentType.equals("3") && mList.size() == 1) {
                                List<String> mphotos = new ArrayList<>();
                                mphotos.add(mList.get(0).getFile_path());
                                questionReplyClass.setMphotos(mphotos);
                            } else {
                            }
                            addNewMsg(questionReplyClass);
                            setFlag();
                        } else
                        CommonUtil.showSnack(main_layout, "回复失败");
                    } catch (Exception e) {
                    }
                    break;
                case Config.WHAT_THREE:// 抢单
                    Log.d(TAG, json);
                    try {
                        JSONObject jb = new JSONObject(json);
                        String error = jb.getString("error");
                        String msg = jb.getString("msg");
                        CommonUtil.showSnack(main_layout, msg);
                        if (error.equals("0")) {
                            qiang.setVisibility(View.GONE);
                            second_layout.setVisibility(View.VISIBLE);
                            setFlag();
                        } else {
                            // 问题被别人抢走了
                            setFlag();
                            finish();
                        }
                    } catch (Exception e) {
                    }
                    break;
                case Config.WHAT_FOUR:// 回复
                    Log.d(TAG, json);
                    try {
                        JSONObject jb = new JSONObject(json);
                        String error = jb.getString("error");
                        String msg = jb.getString("msg");
                        if (error.equals("0")) {
                            // showToast("设置已读成功");
                        } else {
                        }

                    } catch (Exception e) {
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 添加消息
     *
     * @param
     */
    private void addNewMsg(QuestionReplyClass newreply) {

        lstQuestionReply.add(newreply);
        adapter.addNewItem(newreply);
        adapter.notifyDataSetChanged();
        // 滑到底部
        listView.setSelection(adapter.getCount() - 1);
        if (newreply.getBelong().equals("1")) {
            hideSoftInputView();
            layout_more.setVisibility(View.GONE);
            chat_content.setText("");
        }
    }

    // 设置问题界面的标志位 用来判断是否刷新界面
    private void setFlag() {
        QuestionsFragment.flag = 1;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {

            Attachments att;
            String picPath;
            switch (requestCode) {
                case SELECT_PIC_BY_TACK_PHOTO:
                    // 选择自拍结果
                    // picPath = intent.getStringExtra("path");
                    if (file != null) {
                        att = new Attachments();
                        att.setFile_path(file.getAbsolutePath());
                        att.setFlag(Config.FLAG_FILE);
                        att.setFiletype(Config.FILE_IMG_TYPE);
                        mList.clear();
                        mList.add(att);
                    } else {
                        CommonUtil.showSnack(main_layout, "sdcard不可读");
                    }
                    break;
                case SELECT_PIC_BY_PICK_PHOTO:
                    // 选择图库图片结果
                    picPath = ImageUtil.getPicPathFromUri(intent.getData(),
                            QuestionDetailWithReplyActivity.this);
                    att = new Attachments();
                    att.setFile_path(picPath);
                    att.setFlag(Config.FLAG_FILE);
                    att.setFiletype(Config.FILE_IMG_TYPE);
                    mList.clear();
                    mList.add(att);

                    break;
            }
            replyContentType = "3";
            postData();
        }
    }

    /**
     * IM发送消息
     *
     * @param messageContent
     * @throws Exception
     */
    protected void sendMessage(String messageContent) throws Exception {
        String time = DateUtil.date2Str(Calendar.getInstance(),
                Constant.MS_FORMART);
        org.jivesoftware.smack.packet.Message message = new org.jivesoftware.smack.packet.Message();
        message.setProperty(IMMessage.KEY_TIME, time);
        message.setBody(messageContent);
        chat.sendMessage(message);

        // IMMessage newMessage = new IMMessage();
        // newMessage.setMsgType(1);
        // newMessage.setFromSubJid(chat.getParticipant());
        // newMessage.setContent(messageContent);
        // newMessage.setTime("05-29");
        // message_pool.add(newMessage);


        // 刷新视图
        // loadMoreData();
        // "belong":"1","type":"1","content":"好","audio":"",
        // "audio_time":"0","photos":"","is_read":"0","add_time":"1432788370","member_head":"",
        // "member_mobile":"18918778606","member_nickname":"","artificer_head":"","artificer_name":"朱照亮",
        QuestionReplyClass questionReplyClass = new QuestionReplyClass();
        questionReplyClass.setBelong("1");
        questionReplyClass.setType("1");
        questionReplyClass.setContent(messageContent);
        questionReplyClass.setArtificer_name("");// 我的全在右边"我"
        lstQuestionReply.add(questionReplyClass);
        adapter.addNewItem(questionReplyClass);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);// 显示底部
        layout_more.setVisibility(View.GONE);
        chat_content.setText("");
        hideSoftInputView();
    }


    private void sendMsg(String content) {
        mDialog = CustomProgressDialog.showCancelable(mContext, "发送回复中...");
        Map<String, String> map = new HashMap<>();
        replyContentType = "1";
        map.put("member_id", member_id);
        map.put("from", username);
        map.put("to", to);
        map.put("artificer_id", id);
        map.put("question_id", question_id);
        map.put("type", replyContentType);
        map.put("content", URLEncoder.encode(content));
        TaskExecutor.Execute(new DataRunnable(QuestionDetailWithReplyActivity.this, "/Answers/add", mHandler, Config.WHAT_TWO, map));

    }

    private void postData() { // 开始上传文件
        mDialog = CustomProgressDialog.showCancelable(mContext, "发送回复中...");
        TaskExecutor.Execute(new Runnable() {
            @Override
            public void run() {
                Message msg = mHandler.obtainMessage();
                msg.what = Config.WHAT_TWO;
                HashMap<String, String> map = new HashMap<>();
                map.put("member_id", member_id);// 这个需要修改
                map.put("from", username);
                map.put("to", to);
                map.put("artificer_id", id);
                map.put("question_id", question_id);
                map.put("type", replyContentType);// 这个需要修改
                map.put("audio_time", audio_time);
                MultipartEntity entity = NetworkUtil.create();
                try {
                    entity = NetworkUtil.put(entity, map);// 添加参数内容
                    entity = NetworkUtil.putAttachements(entity, QuestionDetailWithReplyActivity.this, mList);
                    msg.obj = NetworkUtil.post(entity, "/Answers/add");
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
        // Intent intent = new Intent(QuestionDetailWithReplyActivity.this,
        // CameraActivity.class);
        // startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
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

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.toString().equals("")) {
            sendButton.setVisibility(View.GONE);
            multiMediaButton.setVisibility(View.VISIBLE);
        } else {
            sendButton.setVisibility(View.VISIBLE);
            multiMediaButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    /**
     * @author LiuSiQing
     * @Package com.mngwyhouhzmb.activity.neighbour
     * @Title OnItemListener
     * @Description 图片展示监听
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
}
