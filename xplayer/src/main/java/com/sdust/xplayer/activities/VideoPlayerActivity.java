package com.sdust.xplayer.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import com.sdust.xplayer.R;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.fragment.SettingDialogFragment;
import com.sdust.xplayer.utils.BitmapUtils;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.SharedPreferenceUtils;
import com.sdust.xplayer.utils.StringUtils;
import com.sdust.xplayer.utils.SystemUtils;
import com.sdust.xplayer.utils.ToastUtils;

import java.util.List;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnErrorListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.widget.VideoView;


public class VideoPlayerActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, View.OnTouchListener, OnSeekBarChangeListener, SettingDialogFragment.OnSettingCompleteListener {

	/** 更新进度、系统时间 */
	private final int PROGRESS = 0;
	/** 隐藏音量/亮度弹窗 */
	private final int HIDEVOLUME = 1;
	/** 隐藏控制栏 */
	private final int HIDECONTROLLER = 2;

	private MediaPlayer mMediaPlayer;
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
	private ImageView mImgVideoLayout;
	/** 锁定屏幕 */
	private ImageView mImgLockScreen;
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
    /** 锁定屏幕后显示的解锁图标 */
	private ImageView mUnLockScreen;
	/** 设置 */
	private ImageView mImgSetting;
	/** 打开/关闭字幕 */
	private CheckBox  mCbSubtitle;

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
	/** 当前视频的路径-网络路径 */
	private String mPath;
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
    /** 视频当前位置 */
	private long mCurrentPosition;
    /** 设备当前音量 */
    private int  mCurrentVolume;

	// 程序配置项
	/** 画质 */
	private int videoQuality;
	/** 缓冲大小 */
	private int bufferSize;
	/** 是否自动播放 */
	private boolean autoPlay;
	/** 缓存在线视频到sd卡 */
	private boolean bufferVideo;
	/** 显示字幕 */
	private boolean displaySubtitle;
	/** 视频画面比例 */
	private float videoAspectRatio;
	/** 视频播放速度 */
	private float videoSpeed;
	/** 是否循环播放当前视频 */
	private boolean isPlayCycle;

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
        LogUtils.e("onCreate被调用");
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		initView();
		getData();
		setData();
		initSettings();
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
		mImgSetting = (ImageView) findViewById(R.id.img_setting_controller);
		mCbSubtitle = (CheckBox) findViewById(R.id.cb_subtitle_setting);

		mImgVideoLayout = (ImageView) findViewById(R.id.img_video_layout);
        mImgLockScreen = (ImageView) findViewById(R.id.controller_lock_screen);
        mUnLockScreen = (ImageView) findViewById(R.id.img_unlock_screen);
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
		mPath = intent.getStringExtra("path");
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
		} else if (!TextUtils.isEmpty(mPath)) {  // 网络路径
			mVideoView.setVideoPath(mPath);
		}
//		mVideoView.setVideoPath(getIntent().getStringExtra("path"));
//		LogUtils.e(getIntent().getStringExtra("path"));
		initBattery();
		// 最大音量
		mMaxVolume = SystemUtils.getMaxVolume(this);
		mGestDetector = new GestureDetector(this, new SingleGestureListener());
	}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.e("onRestoreInstanceState被调用");
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getLong("position");
            if (mVideoView != null) {
                mVideoView.seekTo(mCurrentPosition);
            }
            LogUtils.e("恢复视频当前位置：" + mCurrentPosition);
        }
    }

    /**
	 * 设置监听器
	 */
	private void setListener() {

		mImgVideoLayout.setOnClickListener(this);
		mCbPlayVideo.setOnCheckedChangeListener(this);
        mImgLockScreen.setOnClickListener(this);
		mImgNextVideo.setOnClickListener(this);
		mImgPreVideo.setOnClickListener(this);
		mImgBack.setOnClickListener(this);
		mImgCut.setOnClickListener(this);
        mUnLockScreen.setOnClickListener(this);
		mImgSetting.setOnClickListener(this);
		mCbSubtitle.setOnCheckedChangeListener(this);
		mSbVideoProgress.setOnSeekBarChangeListener(this);

		// 准备视频监听
		mVideoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				mMediaPlayer = mp;
				mLayoutLoading.setVisibility(View.GONE);
				// 设置播放速度
				mp.setPlaybackSpeed(1.0f);
				LogUtils.e("视频播放速度：" + 1.0f);

				// 设置视频质量
				mp.setVideoQuality(videoQuality);

				// 设置视频缓冲大小（默认1024KB）单位Byte
				mp.setUseCache(true);
				mp.setCacheDirectory("sdcard/xplayer/cache");
				mp.setBufferSize(bufferSize);

				mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, videoAspectRatio);

                mCbPlayVideo.setChecked(false);
				mIsPlaying = true;

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
				if (isPlayCycle) {   // 循环播放
					LogUtils.e("当前视频循环播放");
					mVideoView.seekTo(0);
//					mSbVideoProgress.setProgress(0);
				} else if (autoPlay) {		// 自动播放下一个
					playNextVideo();
				}

				if (mVideoList != null && (mCurVideoPosition == mVideoList.size())) {
					mCurVideoPosition--;	// 若是最后一个视频，则直接关闭当前界面
					finish();
				}
				if (mUri != null) {  //如果是来自第三方软件的视频，那么播放结束时关掉页面
					finish();
				}
				if (!TextUtils.isEmpty(mPath)) {  // 如果播放的是网络视频，那么播放结束直接关掉页面
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
				LogUtils.e("出错了");
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
	}

	/**
	 * 读取程序配置
	 */
	private void initSettings() {
		videoQuality = ApplicationConfig.videoQuality[SharedPreferenceUtils.getIntValues("video_quantity")];
		bufferSize = ApplicationConfig.bufferSize[SharedPreferenceUtils.getIntValues("buffer_size")];
		autoPlay = SharedPreferenceUtils.getBooleanValue("auto_play");
		bufferVideo = SharedPreferenceUtils.getBooleanValue("file_buffer");
		displaySubtitle = SharedPreferenceUtils.getBooleanValue("display_subtitle");
		videoAspectRatio =  ApplicationConfig.videoAspectratio[SharedPreferenceUtils.getIntValues("video_aspectratio")];
		LogUtils.e("画质：" + videoQuality);
		LogUtils.e("缓冲大小：" + bufferSize);
		LogUtils.e("是否自动播放：" + autoPlay);
		LogUtils.e("是否缓存视频到sd卡：" + bufferVideo);
		LogUtils.e("是否自动加载字幕：" + displaySubtitle);
		LogUtils.e("画面比例:" + videoAspectRatio);
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {  //seekbar正在拖动
		if (fromUser) {
			int currentPosition = seekBar.getProgress();
			mTvToast.setText(StringUtils.generateTime(currentPosition));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {  //seekbar开始拖动
		// 显示当前位置
		mTvToast.setVisibility(View.VISIBLE);
		// 移除消息
		handler.removeMessages(HIDECONTROLLER);
		// 记录拖动钱声音
		mCurrentVolume = SystemUtils.getCurVolume(VideoPlayerActivity.this);
		// 拖动时屏蔽声音
		SystemUtils.setCurVolume(VideoPlayerActivity.this, 0);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {  //seekbar停止拖动
		mTvToast.setVisibility(View.GONE);
		// 发送消息
		handler.sendEmptyMessageDelayed(HIDECONTROLLER, 5000);
		mVideoView.seekTo(seekBar.getProgress());
		mCbPlayVideo.setChecked(false);  // true zanting  false bofang
		SystemUtils.setCurVolume(VideoPlayerActivity.this, mCurrentVolume);
	}

	/**
	 * 电量变化的广播接收者
	 */
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
    protected void onResume() {
        super.onResume();
        if (mCurrentPosition != 0) {
            mVideoView.seekTo(mCurrentPosition);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause被调用");
        mCurrentPosition = mVideoView.getCurrentPosition();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
        LogUtils.e("ondestroy调用");
		mIsDestroyed = true;
		// 取消广播接收者
		unregisterReceiver(mBatteryBroadcastReceiver);
		mBatteryBroadcastReceiver = null;
	}

	private int videoAspectratioId;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_back:
			finish();
			break;
		case R.id.img_cut:   // 截屏
			screenShots();
			break;
		case R.id.img_pre:  // 播放上一个
			playPreVideo();
			if (mCurVideoPosition < 0) {  // 如果已经是第一个视频
				ToastUtils.showToast(getString(R.string.first_video));
				mCurVideoPosition++;
			}
			break;
		case R.id.img_next: // 播放下一个
			playNextVideo();
			if (mVideoList != null && (mCurVideoPosition == mVideoList.size())) { // 如果已经是最后一个视频
                ToastUtils.showToast(getString(R.string.last_video));
				mCurVideoPosition--;
			}
			break;
		case R.id.img_video_layout:		// 画面比例
			changeVideoLayout();
			break;
        case R.id.controller_lock_screen:   // 锁频
            lockOrUnlockScreen(true);
            break;
        case R.id.img_unlock_screen:    // 解锁
            lockOrUnlockScreen(false);
            break;
		case R.id.img_setting_controller:  // 设置
			dialogFragment  = new SettingDialogFragment();
			dialogFragment.setOnSettingCompleteListener(this);
			dialogFragment.setCancelable(false);
			dialogFragment.show(getFragmentManager(), "dialog");
			break;
		default:
			break;
		}
	}

	@Override
	public void onConfirm(int videoQuality, float videoSpeed, boolean isCycle) {
		LogUtils.e("画质：" + videoQuality + "播放速度：" + videoSpeed + "是否循环播放：" + isCycle);
		this.videoQuality = videoQuality;  // 视频质量
		this.videoSpeed = videoSpeed;  // 播放速度
		this.isPlayCycle = isCycle;	// 是否循环播放

		mVideoView.setVideoQuality(ApplicationConfig.videoQuality[videoQuality]);  // 画质
		mMediaPlayer.setPlaybackSpeed(videoSpeed);
		dialogFragment.dismiss();
	}

	SettingDialogFragment dialogFragment;

    /**
     * 截取视频当前画面
     * 获取当前时刻视频对应的帧
     */
    private void screenShots() {
        Bitmap bitmap = mVideoView.getCurrentFrame();
        LogUtils.e(bitmap.getWidth() + ": " + bitmap.getHeight());
        String savePath = BitmapUtils.saveBitmapWithUniqueName(this, bitmap, ApplicationConfig.screenShotsDir);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        ToastUtils.showToast("截图成功！保存在:\n" + savePath);
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

    private void changeVideoLayout() {
		if (videoAspectratioId == 0) {	// 全屏
			mImgVideoLayout.setImageResource(R.drawable.full_screen);
			mLayout = VideoView.VIDEO_LAYOUT_SCALE;
			videoAspectratioId = 1;
			ToastUtils.showToast("全屏");
		} else if (videoAspectratioId == 1) {  // 拉伸
			mImgVideoLayout.setImageResource(R.drawable.stretch);
			mLayout = VideoView.VIDEO_LAYOUT_STRETCH;
			videoAspectratioId = 2;
			ToastUtils.showToast("拉伸");
		} else if (videoAspectratioId == 2) {	// 裁剪
			mImgVideoLayout.setImageResource(R.drawable.cut2);
			mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
			videoAspectratioId = 3;
			ToastUtils.showToast("裁剪");
		} else if (videoAspectratioId == 3){
			mImgVideoLayout.setImageResource(R.drawable.scale);
			mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
			videoAspectratioId = 0;
			ToastUtils.showToast("100%");
		}

		mVideoView.setVideoLayout(mLayout, videoAspectRatio);
	}

    /**
     * 屏幕锁定/解锁
     */
    private void lockOrUnlockScreen(boolean lock) {
        if (lock) { // 锁屏
            handler.removeMessages(HIDECONTROLLER);
            hideController();
            mIsLocking = true;
            ToastUtils.showToast(getString(R.string.lock_screen));
            mUnLockScreen.setVisibility(View.VISIBLE);
        } else {  // 解锁
            showController();
            handler.sendEmptyMessageDelayed(HIDECONTROLLER, 5000);
            mIsLocking = false;
            ToastUtils.showToast(getString(R.string.unlock_screen));
            mUnLockScreen.setVisibility(View.GONE);
        }
    }

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_play:
			// 开始/暂停播放
			if (isChecked) {
				mVideoView.pause();
                LogUtils.e("暂停播放");
			} else {
				mVideoView.start();
                LogUtils.e("开始播放");
			}
            mIsPlaying = !mIsPlaying;
			break;
		case R.id.cb_subtitle_setting:
			if (isChecked) {
				ToastUtils.showToast("关闭字幕");
				LogUtils.e("字幕类型：");
				mVideoView.setSubTrack(1);
			} else {
				ToastUtils.showToast("开启字幕");
			}
			break;
		default:
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

    /**
     * 手势识别
     */
	class SingleGestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mIsLocking) {
                if (mUnLockScreen.getVisibility() == View.VISIBLE) {
                    mUnLockScreen.setVisibility(View.GONE);
                } else if (mUnLockScreen.getVisibility() == View.GONE) {
                    mUnLockScreen.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null) {
            mVideoView.setVideoLayout(mLayout, 0);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.e("onSaveInstanceState调用");
        LogUtils.e("seekbar的位置：" + mSbVideoProgress.getProgress());
        long position = mVideoView.getCurrentPosition();
        // 保存视频播放位置
        outState.putLong("position", mSbVideoProgress.getProgress());
        LogUtils.e("保存视频当前位置：" + position);
    }
}
