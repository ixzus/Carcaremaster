package com.lazycare.carcaremaster.widget;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.lazycare.carcaremaster.R;
import com.lazycare.carcaremaster.util.CommonUtil;
import com.lazycare.carcaremaster.util.NetworkUtil;

/**
 * 播放音频
 *
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class AudioPlayer implements OnBufferingUpdateListener,
        OnCompletionListener, MediaPlayer.OnPreparedListener,
        OnAudioFocusChangeListener, MediaPlayer.OnErrorListener {
    public MediaPlayer mediaPlayer;
    private Timer mTimer = new Timer();
    private AudioManager audioManager;
    private Context context;
    private ImageView imageView;// 播放动画 imageview
    private String direct;// 麦克风的朝向

    private AnimationDrawable rocketAnimation;

    public AudioPlayer(Context context, String direct) {

        this.context = context;
        this.direct = direct;
        mTimer.schedule(mTimerTask, 0, 1000);
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);

        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying()) {
                handleProgress.sendEmptyMessage(0);
            }
        }
    };

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }


    public void setDirect(String direct) {
        this.direct = direct;
    }

    Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {

            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

        }

        ;
    };

    // *****************************************************

    public void play() {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        } else {
            return false;
        }

    }

    public void playUrl(String videoUrl) {
        if (requestAudioFocus()&& NetworkUtil.isNetworkAvailable(context)) {

            try {
                // 开始动画
                startAnim();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(videoUrl);
                mediaPlayer.prepare();// prepare之后自动播放
                // mediaPlayer.start();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else
            CommonUtil.showToast(context,"请检查您的网络");
    }

    public void pause() {
        stopAnim();
        mediaPlayer.pause();
    }

    private void stopAnim() {
        rocketAnimation.stop();
        if (direct.equals("right")) {
            imageView.setBackgroundResource(R.mipmap.chatfrom_voice_playing);
        } else {
            imageView.setBackgroundResource(R.mipmap.chatto_voice_playing_f3);
        }
    }

    private void startAnim() {
        // 初始化图片资源
        if (direct.equals("right")) {
            imageView.setBackgroundResource(R.drawable.voice_ing);
        } else {
            imageView.setBackgroundResource(R.drawable.voice_ing_right);
        }
        rocketAnimation = (AnimationDrawable) imageView.getBackground();
        rocketAnimation.start();
    }

    public void stop() {
        if (mediaPlayer != null) {
            stopAnim();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    /**
     * 通过onPrepared播放
     */
    public void onPrepared(MediaPlayer arg0) {
        arg0.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        Log.e("mediaPlayer", "onCompletion");
//        if (!arg0.isPlaying()) {
            Log.e("mediaPlayer", "onstop");
            stopAnim();
//        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
        Log.e("mediaPlayer", "bufferingProgress= " + bufferingProgress);

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            // 失去焦点之后的操作
            abandonAudioFocus();
//             stopAnim();
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            // 获得焦点之后的操作
            requestAudioFocus();
//             startAnim();
        }
    }

    private boolean requestAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return false;
        }
        if (audioManager == null)
            audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            // DLog.i(TAG, "Request audio focus");
            int ret = audioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return true;
            } else {
                // DLog.i(TAG, "request audio focus fail. " + ret);
                return false;
            }
        } else {
            return false;
        }
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1) {
            return;
        }
        if (audioManager != null) {
            audioManager.abandonAudioFocus(this);
            audioManager = null;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stopAnim();
        return false;
    }
}
