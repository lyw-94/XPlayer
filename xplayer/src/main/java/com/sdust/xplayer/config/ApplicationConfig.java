package com.sdust.xplayer.config;

import android.os.Environment;

import io.vov.vitamio.MediaPlayer;

/**
 * 应用的一些配置信息
 * Created by Liu Yongwei on 2016/5/8.
 * <p/>
 * version : 1.0
 */
public class ApplicationConfig {

    /** 视频缩略图宽度 */
    public static final int VIDEO_THUBM_WIDTH = 150;

    /** 视频缩略图高度 */
    public static final int VIDEO_THUBM_HEIGHT = 110;

    /** 过滤视频大小(低于该大小不显示) */
    public static final int FILTER_VIDEO_SIZE = 3 * 1024 * 1024;

    /** 应用主目录 */
    public static final String appDir = Environment.getExternalStorageDirectory() + "/xplayer/";

    /** 拍摄的短视频存放路径 */
    public static final String videoDir = appDir + "/video/";

    /** 应用缓存目录  */
    public static final String cacheDir = appDir + "/cache/";

    public static final String screenShotsDir = appDir + "/screenShots/";

    public static final int[] bufferSize = {0, 512 * 1024, 1 * 1024 * 1024, 4 * 1024 * 1024, 8 * 1024 * 1024}; // 单位 kb

    public static final int[] videoQuality = {MediaPlayer.VIDEOQUALITY_LOW, MediaPlayer.VIDEOQUALITY_MEDIUM, MediaPlayer.VIDEOQUALITY_HIGH };

    public static final float[] videoAspectratio = {0, 1, 4/3.0f, 3/2.0f, 14/9.0f, 16/9.0f};
}
