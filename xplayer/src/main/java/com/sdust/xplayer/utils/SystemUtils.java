package com.sdust.xplayer.utils;

import android.content.Context;
import android.media.AudioManager;

public class SystemUtils {

	/**
	 * 获取最大音量
	 * 
	 * @param context
	 * @return
	 */
	public static int getMaxVolume(Context context) {
		return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 获取当前音量
	 * 
	 * @param context
	 * @return
	 */
	public static int getCurVolume(Context context) {
		return ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
				.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	/**
	 * 设置当前音量
	 * 
	 * @param context
	 * @param index
	 */
	public static void setCurVolume(Context context, int index) {
		((AudioManager) context.getSystemService(Context.AUDIO_SERVICE))
				.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
	}

	/**
	 * 获取屏幕像素
	 * 
	 * @param context
	 * @return
	 */
//	public static ScreenBean getScreenPix(Context context) {
//		Display dplay = ((PlayAcy) context).getWindowManager()
//				.getDefaultDisplay();
//		return new ScreenBean(dplay.getWidth(),dplay.getHeight());
//	}
}
