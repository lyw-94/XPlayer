package com.sdust.xplayer.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sdust.xplayer.R;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.utils.StringUtils;
import com.sdust.xplayer.utils.SystemUtils;

import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;


public class VideoPlayerActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	/** 更新进度、系统时间 */
	private final int PROGRESS = 0;
	/** 隐藏音量/亮度弹窗 */
	private final int HIDEVOLUME = 1;
	/** 隐藏控制栏 */
	private final int HIDECONTROLLER = 2;

	private VideoView mVideoView;
	/** 视频名称 */
	private TextView mTvVideoName;
	/** 系统时间 */
	private TextView mTvSystemTime;
	/** 电量 */
	private ImageView mImgBattery;
	/** 视频播放当前时间 */
	private TextView mTvCurrentPlayTime;
	/** 视频播放进度条 */
	private SeekBar mSbVideoProgress;
	/** 视频时长 */
	private TextView mTvVideoDuration;
	/** 视频当前播放位置 */
	private TextView mTvToast;
	/** 上一个视频 */
	private ImageView mImgPreVideo;
	/** 下一个视频 */
	private ImageView mImgNextVideo;
	/** 播放/暂停 */
	private CheckBox mCbPlayVideo;
	/** 全屏/退出全屏 */
	private CheckBox mCbFullScreen;
	/** 锁定屏幕 */
	private CheckBox mCbLockScreen;
	/** 返回 */
	private ImageView mImgBack;
	/** 截屏  */
	private ImageView mImgCut;
	/** 控制面板 */
	private RelativeLayout mLayoutController;
	/** 视频加载中 */
	private RelativeLayout mLayoutLoading;
	/** 缓冲加载中 */
	private RelativeLayout mLayoutBuffing;
	
	/** 音量/亮度调节框 */
	private View mVolumeBrightnessLayout;
	/** 音量/亮度 */
	private ImageView mOperationBg;
	/** 音量值/亮度 当前值 */
	private ImageView mOperationPercent;
	/** 音量/亮度 总值 */
	private ImageView mOperationFull;
	/** 手势识别 */
	private GestureDetector mGestDetector;
	/** 当前缩放模式 */
	private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;

	/** 当前播放的视频对象 */
	private Video mVideo;
	/** 当前视频的uri */
	private Uri mUri;
	/** 视频列表 */
	private List<Video> mVideoList;
	/** 当前视频在视频列表中的位置 */
	private int mCurVideoPosition;
	/** 当前声音 */
	private int mVolume = -1;
	/** 最大音量 */
	private int mMaxVolume;
	/** 当前亮度 */
	private float mBrightness = -1f;

	/** 监听电量改变的广播接收者 */
	private BatteryBroadcastReceiver mBatteryBroadcastReceiver;
	/** 当前电量值 */
	private int mLevel;
	/** 当前界面是否销毁 */
	private boolean mIsDestroyed;
	/** 是否正在播放视频 */
	private boolean mIsPlaying;
	/** 是否显示控制面板 */
	private boolean mIsShowController;
	/** 是否是网络视频 */
	private boolean mIsWebVideo;
	/** 是否处于锁屏状态 */
	private boolean mIsLocking;
	
	/**
	 * true:视频卡了但是没有把卡的效果(进度条)消除
	 * false:卡了但是卡效果已消除   
	 * 主要用于解决卡的时候显示了进度条但是没有执行到卡结束取消进度条那个方法
	 */
	private boolean mIsBuffering;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PROGRESS:
				// 得到视频播放当前进度
				long currentPosition = mVideoView.getCurrentPosition();
				mTvCurrentPlayTime.setText(StringUtils
						.generateTime(currentPosition));
				// 系统时间
				mTvSystemTime.setText(StringUtils.getSystemTime());
				// 设置当前进度条位置
				mSbVideoProgress.setProgress((int) currentPosition);
				// 电量
				setBattery();
				// 网络视频设置缓冲进度
				if (mIsWebVideo) {
					// 缓冲值 0-100
					int percentage = mVideoView.getBufferPercentage();
					int bufferProgress = percentage * mSbVideoProgress.getMax() / 100;
					mSbVideoProgress.setSecondaryProgress(bufferProgress);
				}
				
				// 消息死循环
				if (!mIsDestroyed) {
					handler.removeMessages(PROGRESS);
					handler.sendEmptyMessageDelayed(PROGRESS, 1000);
				}
				break;
			case HIDEVOLUME:
				mVolumeBrightnessLayout.setVisibility(View.GONE);
				break;
			case HIDECONTROLLER:
				mLayoutController.setVisibility(View.GONE);
				mIsShowController = false;
			default:
				break;
			}
		}
	};

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		initView();
		getData();
		setData();
		setListener();
	};

	private void initView() {
		setContentView(R.layout.activity_video_player);
		// 防止锁屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 绑定控件
		mVideoView = (VideoView) findViewById(R.id.videoview_vitamio);
		mTvVideoName = (TextView) findViewById(R.id.tv_video_title);
		mTvSystemTime = (TextView) findViewById(R.id.tv_system_time);
		mTvToast = (TextView) findViewById(R.id.tv_toast);
		mImgBattery = (ImageView) findViewById(R.id.iv_battery);
		mTvCurrentPlayTime = (TextView) findViewById(R.id.tv_crrent_time);
		mSbVideoProgress = (SeekBar) findViewById(R.id.sb_video);
		mTvVideoDuration = (TextView) findViewById(R.id.tv_duration);
		mImgPreVideo = (ImageView) findViewById(R.id.img_pre);
		mImgNextVideo = (ImageView) findViewById(R.id.img_next);
		mCbPlayVideo = (CheckBox) findViewById(R.id.cb_play);
		mImgBack = (ImageView) findViewById(R.id.img_back);
		mImgCut = (ImageView) findViewById(R.id.img_cut);
		mCbFullScreen = (CheckBox) findViewById(R.id.cb_fullscreen);
		mCbLockScreen = (CheckBox) findViewById(R.id.cb_lock_screen);
		mLayoutController = (RelativeLayout) findViewById(R.id.layout_controller);
		mLayoutLoading = (RelativeLayout) findViewById(R.id.layout_loading);
		mLayoutBuffing = (RelativeLayout) findViewById(R.id.layout_buffing);
		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
		mOperationFull = (ImageView) findViewById(R.id.operation_full);
	}

	/**
	 * 得到页面传递过来的数据
	 */
	@SuppressWarnings("unchecked")
	private void getData() {
		Intent intent = getIntent();
		// 视频在列表中的位置
		mCurVideoPosition = intent.getIntExtra("position", 0);
		// 视频列表
		mVideoList = (List<Video>) intent.getSerializableExtra("videolist");
		if (mVideoList != null) {
			mVideo = mVideoList.get(mCurVideoPosition);
		}
		// 得到视频地址---用于播放来自第三方软件的视频 如文件管理器、浏览器等
		mUri = intent.getData();
	}

	/**
	 * 设置数据
	 */
	private void setData() {

		if (mVideoList != null && mVideoList.size() > 0) { // 来自播放列表
			mVideoView.setVideoPath(mVideo.url);
			mTvVideoDuration.setText(StringUtils.generateTime(mVideo.duration));
			mTvVideoName.setText(mVideo.name);
		} else if (mUri != null) { // 来自第三方软件
			mVideoView.setVideoURI(mUri);
			mTvVideoName.setText(mUri.toString());
		}
		initBattery();
		// 最大音量
		mMaxVolume = SystemUtils.getMaxVolume(this);
		mGestDetector = new GestureDetector(this, new SingleGestureListener());
	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		// 准备视频监听
		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// 开始播放
				//mVideoView.start();
				mLayoutLoading.setVisibility(View.GONE);
				// 设置播放速度
				mp.setPlaybackSpeed(1.0f);
				mCbPlayVideo.setChecked(false);
//				mIsPlaying = true;
				// 视频时长与进度条关联
				mSbVideoProgress.setMax((int) mVideoView.getDuration());
				// 默认隐藏控制栏
				hideController();
				
				handler.sendEmptyMessage(PROGRESS);
			}
		});
		// 视频播放完成监听
		mVideoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				playNextVideo();
				if (mVideoList != null && (mCurVideoPosition == mVideoList.size())) {
					// 若是最后一个视频，则直接关闭当前界面
					mCurVideoPosition--;
					finish();
				}
				if (mUri != null) {  //如果是来自第三方软件的视频，那么播放结束时关掉页面
					finish();
				}
			}
		}); 
		// 播放出错监听
		mVideoView.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				new AlertDialog.Builder(getApplicationContext())
					.setTitle("提示")
					.setMessage(getString(R.string.canot_play))
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create().show();
					
				return true;
			}
		});
		// 播放卡顿监听
		// TODO:  加上下面的代码导致视频第一次播放时不显示画面
//		mVideoView.setOnInfoListener(new OnInfoListener() {
//			
//			@Override
//			public boolean onInfo(MediaPlayer mp, int what, int extra) {
//				switch (what) {
//				// 卡了
//				case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//					//mLayoutBuffing.setVisibility(View.VISIBLE);
//					mIsBuffering = true;
//					break;
//				// 卡结束
//				case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//					//mLayoutBuffing.setVisibility(View.GONE);
//					mIsBuffering = false;
//					break;
//				default:
//					break;
//				}
//				return true;
//			}
//		});
//		mVideoView.setOnSeekCompleteListener(new OnSeekCompleteListener() {
//			
//			@Override
//			public void onSeekComplete(MediaPlayer mp) {
//				if (mIsBuffering) {
//					mLayoutBuffing.setVisibility(View.GONE);
//				}
//			}
//		});
		mSbVideoProgress
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						mTvToast.setVisibility(View.GONE);
						// 发送消息
						handler.sendEmptyMessageDelayed(HIDECONTROLLER, 5000);
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// 显示当前位置
						mTvToast.setVisibility(View.VISIBLE);
						// 移除消息
						handler.removeMessages(HIDECONTROLLER);
					}
					
					/**
					 * SeekBar状态改变时调用这个方法 progress和视频长度一一对应
					 */
					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser) {
							int currentPosition = seekBar.getProgress();
							mTvToast.setText(StringUtils.generateTime(currentPosition));
							mVideoView.seekTo(progress);
						}
					}
				});
		mCbFullScreen.setOnCheckedChangeListener(this);
		mCbPlayVideo.setOnCheckedChangeListener(this);
		mCbLockScreen.setOnCheckedChangeListener(this);
		mImgNextVideo.setOnClickListener(this);
		mImgPreVideo.setOnClickListener(this);
		mImgBack.setOnClickListener(this);
		mImgCut.setOnClickListener(this);
	}

	/**
	 * 初始化广播接收者
	 */
	private void initBattery() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mBatteryBroadcastReceiver = new BatteryBroadcastReceiver();
		registerReceiver(mBatteryBroadcastReceiver, filter);
	}

	private class BatteryBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 当前电量
			mLevel = intent.getIntExtra("level", 0);
		}
	}

	/**
	 * 根据电量等级设置相应图片
	 */
	private void setBattery() {
		if (mLevel <= 0) {
			mImgBattery.setImageResource(R.drawable.ic_battery_0);
		} else if (mLevel <= 10) {
			mImgBattery.setImageResource(R.drawable.ic_battery_10);
		} else if (mLevel <= 20) {
			mImgBattery.setImageResource(R.drawable.ic_battery_20);
		} else if (mLevel <= 40) {
			mImgBattery.setImageResource(R.drawable.ic_battery_40);
		} else if (mLevel <= 60) {
			mImgBattery.setImageResource(R.drawable.ic_battery_60);
		} else if (mLevel <= 80) {
			mImgBattery.setImageResource(R.drawable.ic_battery_80);
		} else if (mLevel <= 100) {
			mImgBattery.setImageResource(R.drawable.ic_battery_100);
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mIsDestroyed = true;
		// 取消广播接收者
		unregisterReceiver(mBatteryBroadcastReceiver);
		mBatteryBroadcastReceiver = null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.img_cut:
			// 截屏
			Toast.makeText(this, "裁剪", Toast.LENGTH_SHORT).show();
			break;
		case R.id.img_pre:
			// 播放上一个
			playPreVideo();
			if (mCurVideoPosition < 0) {  // 如果已经是第一个视频
				Toast.makeText(VideoPlayerActivity.this,
						getString(R.string.first_video), Toast.LENGTH_SHORT)
						.show();
				mCurVideoPosition++;
			}
			break;
		case R.id.img_next:
			// 播放下一个
			playNextVideo();
			if (mVideoList != null && (mCurVideoPosition == mVideoList.size())) { // 如果已经是最后一个视频
				Toast.makeText(VideoPlayerActivity.this,
						getString(R.string.last_video), Toast.LENGTH_SHORT)
						.show();
				mCurVideoPosition--;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 截取视频当前画面
	 * 获取当前时刻视频对应的帧
	 */
	private void screenShots() {
        MediaPlayer mp = new MediaPlayer(this);
        mp.getCurrentFrame();
        mVideoView.getVi
	}

	/**
	 * 播放下一个视频
	 */
	public void playNextVideo() {
		if (mVideoList != null && mVideoList.size() > 0) {
			mCurVideoPosition++;
			if (mCurVideoPosition < mVideoList.size()) {
				// 播放下一个视频
				mVideo = mVideoList.get(mCurVideoPosition);
				mVideoView.setVideoPath(mVideo.url);
				mTvVideoName.setText(mVideo.name);
				mTvVideoDuration.setText(StringUtils
						.generateTime(mVideo.duration));
				handler.removeMessages(HIDECONTROLLER);
			}
		}
	}

	/**
	 * 播放上一个视频
	 */
	public void playPreVideo() {
		if (mVideoList != null && mVideoList.size() > 0) {
			mCurVideoPosition--;
			if (mCurVideoPosition >= 0) {
				// 播放下一个视频
				mVideo = mVideoList.get(mCurVideoPosition);
				mVideoView.setVideoPath(mVideo.url);
				mTvVideoName.setText(mVideo.name);
				handler.removeMessages(HIDECONTROLLER);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_play:
			// 开始/暂停播放
			if (isChecked) {
				mVideoView.pause();
			} else {
				mVideoView.start();
			}
			// mIsPlaying = !mIsPlaying;
			break;
		case R.id.cb_lock_screen:
			// 锁屏
			lockOrUnlockScreen(isChecked);
			break;
		case R.id.cb_fullscreen:
			// 全屏
			if (mVideoView != null) {
				if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM) {
					mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
				} else {
					mLayout++;
				}
				mVideoView.setVideoLayout(mLayout, 0);
				showScaleToast(mLayout);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 屏幕锁定/解锁
	 */
	private void lockOrUnlockScreen(boolean isChecked) {
		if (isChecked) { // 锁屏
			handler.removeMessages(HIDECONTROLLER);
			hideController();
			mIsLocking = true;
			Toast.makeText(this, getString(R.string.lock_screen), Toast.LENGTH_SHORT).show();
		} else {  // 解锁
			showController();
			handler.sendEmptyMessageDelayed(HIDECONTROLLER, 5000);
			mIsLocking = false;
			Toast.makeText(this, getString(R.string.unlock_screen), Toast.LENGTH_SHORT).show();
		}
	}
	
	public void showScaleToast(int id) {
		switch (id) {
		case 0:
			Toast.makeText(VideoPlayerActivity.this, "拉伸", Toast.LENGTH_SHORT)
					.show();
			break;
		case 1:
			Toast.makeText(VideoPlayerActivity.this, "裁剪", Toast.LENGTH_SHORT)
					.show();
			break;
		case 2:
			Toast.makeText(VideoPlayerActivity.this, "100%", Toast.LENGTH_SHORT)
					.show();
			break;
		case 3:
			Toast.makeText(VideoPlayerActivity.this, "全屏", Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			endGesture();
			break;
		default:
			break;
		}
		return true;
	}

	class SingleGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if (mIsLocking) {
				return true;
			}
			if (mIsShowController) {
				hideController();
				handler.removeMessages(HIDECONTROLLER);
			} else {
				showController();
				// 5s后隐藏控制栏
				handler.sendEmptyMessageDelayed(HIDECONTROLLER, 5000);
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if (mIsLocking) {
				return true;
			}
			// 双击暂停/播放
			if (mCbPlayVideo.isChecked()) {
				mVideoView.pause();
			} else {
				mVideoView.start();
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
			if (mIsLocking) {
				return ;
			}
			if (mCbPlayVideo.isChecked()) {
				mVideoView.start();
				mCbPlayVideo.setChecked(false);
			} else {
				mVideoView.pause();
				mCbPlayVideo.setChecked(true);
			}
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (mIsLocking) {
				return true;
			}
			Display disp = getWindowManager().getDefaultDisplay();
			int windowWidth = disp.getWidth();
			int windowHeight = disp.getHeight();

			float mOldX = e1.getX();
			float mOldY = e1.getY();
			float y = e2.getY();
			if (Math.abs(mOldY - y) > 50) { // 当移动距离大于50px的时候才认为用户想要改变音量或亮度
				if (mOldX > windowWidth / 2.0) // 右边滑动
					changeBrightness((mOldY - y) / windowHeight);
				else if (mOldX < windowWidth / 2.0) // 左边滑动
					changeVolume((mOldY - y) / windowHeight);
			}
			return super.onScroll(e1, e2, distanceX, distanceY);
		}
	}

	/**
	 * 改变声音大小
	 * 
	 * @param percent
	 *            滑动的距离/屏幕
	 */
	private void changeVolume(float percent) {
		if (mVolume == -1) {
			mVolume = SystemUtils.getCurVolume(this);
			if (mVolume < 0)
				mVolume = 0;
			// 显示
			mOperationBg.setImageResource(R.drawable.video_volumn_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}
		int index = (int) (percent * mMaxVolume) + mVolume;
		if (index > mMaxVolume)
			index = mMaxVolume;
		else if (index < 0)
			index = 0;

		// 变更声音
		SystemUtils.setCurVolume(this, index);

		// 变更进度条
		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = mOperationFull.getLayoutParams().width * index / mMaxVolume;
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 改变亮度
	 * 
	 * @param percent
	 */
	private void changeBrightness(float percent) {
		WindowManager.LayoutParams lpa = getWindow().getAttributes();
		if (mBrightness < 0) {
			mBrightness = lpa.screenBrightness;
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			// 显示
			mOperationBg.setImageResource(R.drawable.video_brightness_bg);
			mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
		}

		lpa.screenBrightness = mBrightness + percent;
		if (lpa.screenBrightness > 1.0f)
			lpa.screenBrightness = 1.0f;
		else if (lpa.screenBrightness < 0.01f)
			lpa.screenBrightness = 0.01f;
		// 设置当前亮度
		getWindow().setAttributes(lpa);

		ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
		lp.width = (int) (mOperationFull.getLayoutParams().width * lpa.screenBrightness);
		mOperationPercent.setLayoutParams(lp);
	}

	/**
	 * 隐藏控制面板
	 */
	private void hideController() {
		mLayoutController.setVisibility(View.GONE);
		mIsShowController = false;
	}

	/**
	 * 显示控制面板
	 */
	private void showController() {
		mLayoutController.setVisibility(View.VISIBLE);
		mIsShowController = true;
	}

	/**
	 * 手势结束
	 */
	private void endGesture() {
		mVolume = -1;
		mBrightness = -1f;

		// 隐藏
		handler.removeMessages(HIDEVOLUME);
		handler.sendEmptyMessageDelayed(HIDEVOLUME, 500);
	}
}
