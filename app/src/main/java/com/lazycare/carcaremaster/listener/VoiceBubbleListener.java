package com.lazycare.carcaremaster.listener;

import android.view.View;

/**
 * 音频播放策略
 * 
 * @author GMY
 * @mail 2275964276@qq.com
 * @date 2015年6月2日
 */
public interface VoiceBubbleListener {

	public void playFail(View messageBubble);

	public void playStoped(View messageBubble);

	public void playStart(View messageBubble);

	public void playDownload(View messageBubble);

	public void playCompletion(View messageBubble);

}
