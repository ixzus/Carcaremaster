package com.lazycare.carcaremaster;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import com.lazycare.carcaremaster.widget.Player;

/**
 * 媒体播放界面
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public class MediaPlayActivity extends Activity {
	private Player player;
	private SeekBar skbProgress;
	private Button btn;
	private String url = "";
	private boolean isPlaying = false;
	private int t = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movieplayer);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		init();
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!url.equals("")) {
					if (t == 0) {
						if (!isPlaying) {
							player.playUrl(url);
							isPlaying = true;
							btn.setBackgroundResource(R.drawable.videoplayer_icon_stop);
						} else {
							player.pause();
							isPlaying = false;
							btn.setBackgroundResource(R.drawable.videoplayer_icon_play);
						}
						t++;
					} else {
						if (!isPlaying) {
							player.play();
							isPlaying = true;
							btn.setBackgroundResource(R.drawable.videoplayer_icon_stop);
						} else {
							player.pause();
							isPlaying = false;
							btn.setBackgroundResource(R.drawable.videoplayer_icon_play);
						}
					}

				}
			}
		});
	}

	private void init() {
		url = getIntent().getStringExtra("url");
		Log.i("gmyboy", "  " + url);
		skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
		btn = (Button) this.findViewById(R.id.btnPlay);
		player = new Player(skbProgress);
	}
}
