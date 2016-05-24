package com.sdust.xplayer.config;

import android.os.Environment;

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
}
