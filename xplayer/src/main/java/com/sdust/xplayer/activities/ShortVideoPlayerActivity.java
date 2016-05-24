package com.sdust.xplayer.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.sdust.xplayer.R;
import com.sdust.xplayer.application.XPlayerApplication;
import com.sdust.xplayer.utils.ToastUtils;
import com.sdust.xplayer.widget.SurfaceVideoView;
import com.yixia.camera.util.DeviceUtils;
import com.yixia.camera.util.StringUtils;

/**
 * 通用单独播放界面
 * 
 * @author tangjun
 *
 */
public class ShortVideoPlayerActivity extends BaseActivity implements
		SurfaceVideoView.OnPlayStateListener, OnErrorListener,
		OnPreparedListener, OnClickListener, OnCompletionListener,
		OnInfoListener {

	/** 播放控件 */
	private SurfaceVideoView mVideoView;
	/** 暂停按钮 */
	private View mPlayerStatus;
	private View mLoading;
	/** 保存到本地  */
	private View mSaveToLocal;

	/** 播放路径 */
	private String mPath;
	/** 是否需要回复播放 */
	private boolean mNeedResume;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		XPlayerApplication.getInstance().addActivity(this);
		// 防止锁屏
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		mPath = getIntent().getStringExtra("path");
		if (StringUtils.isEmpty(mPath)) {
			finish();
			return;
		}

		setContentView(R.layout.activity_short_video_player);
		mVideoView = (SurfaceVideoView) findViewById(R.id.videoview);
		mPlayerStatus = findViewById(R.id.play_status);
		mLoading = findViewById(R.id.loading);
		mSaveToLocal = findViewById(R.id.commit);

		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnPlayStateListener(this);
		mVideoView.setOnErrorListener(this);
		mVideoView.setOnClickListener(this);
		mVideoView.setOnInfoListener(this);
		mVideoView.setOnCompletionListener(this);

		mVideoView.getLayoutParams().height = DeviceUtils.getScreenWidth(this); // 设置videoview的高度为屏幕的高度？

		findViewById(R.id.txt_right).setOnClickListener(this);
		findViewById(R.id.commit).setOnClickListener(this);
		String entry = getIntent().getStringExtra("entry");
		if ("shortVideoFragment".equals(entry)) {
			mSaveToLocal.setVisibility(View.GONE);
		}
		mVideoView.setVideoPath(mPath);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mVideoView != null && mNeedResume) {
			mNeedResume = false;
			if (mVideoView.isRelease())
				mVideoView.reOpen();
			else
				mVideoView.start();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mVideoView != null) {
			if (mVideoView.isPlaying()) {
				mNeedResume = true;
				mVideoView.pause();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (mVideoView != null) {
			mVideoView.release();
			mVideoView = null;
		}
		super.onDestroy();
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mVideoView.setVolume(SurfaceVideoView.getSystemVolumn(this));
		mVideoView.start();		// 开始播放
		// new Handler().postDelayed(new Runnable() {
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public void run() {
		// if (DeviceUtils.hasJellyBean()) {
		// mVideoView.setBackground(null);
		// } else {
		// mVideoView.setBackgroundDrawable(null);
		// }
		// }
		// }, 300);
		mLoading.setVisibility(View.GONE);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {// 跟随系统音量走
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			mVideoView.dispatchKeyEvent(this, event);
			break;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onStateChanged(boolean isPlaying) {
		mPlayerStatus.setVisibility(isPlaying ? View.GONE : View.VISIBLE);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (!isFinishing()) {
			// 播放失败
		}
		finish();
		return false;
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.txt_right) {
			Intent intent = new Intent("android.intent.action.newvideo");
			// 发送广播通知fragment更新数据
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
			finish();
			// ToastUtils.showLongToast("视频地址：" + mPath);
		} else if (i == R.id.commit) {
			ToastUtils.showToast("未实现");
		} else if (i == R.id.videoview) {
			if (mVideoView.isPlaying())
				mVideoView.pause();
			else
				mVideoView.start();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!isFinishing())
			mVideoView.reOpen();	// 播放结束后重新播放
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			// 音频和视频数据不正确
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			if (!isFinishing())
				mVideoView.pause();		// 缓冲开始暂停播放
			break;
		case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			if (!isFinishing())
				mVideoView.start();		// 缓冲结束后开始播放
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
			if (DeviceUtils.hasJellyBean()) {
				mVideoView.setBackground(null);
			} else {
				mVideoView.setBackgroundDrawable(null);
			}
			break;
		}
		return false;
	}
}
