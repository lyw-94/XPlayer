package com.sdust.xplayer.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.service.AssertService;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.SharedPreferenceUtils;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.DeviceUtils;

import org.xutils.x;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liu Yongwei on 2016/5/6.
 * <p>
 * version : 1.0
 */
public class XPlayerApplication extends Application{

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        mContext = getApplicationContext();

        // 设置拍摄视频缓存路径
//        File dcim = Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//        if (DeviceUtils.isZte()) {
//            if (dcim.exists()) {
//                VCamera.setVideoCachePath(dcim + "/xplayer/");
//            } else {
//                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
//                        "/sdcard-ext/")
//                        + "/xplayer/");
//            }
//        } else {
//            VCamera.setVideoCachePath(dcim + "/xplayer/");
//        }
        VCamera.setVideoCachePath(ApplicationConfig.videoDir);
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);

        // 解压assert里面的文件
        startService(new Intent(this, AssertService.class));

        // 初始化配置文件
        initSettings();
    }

    // 运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();
    private static XPlayerApplication instance;

    // 构造方法
    // 实例化一次
    public synchronized static XPlayerApplication getInstance() {
        if (null == instance) {
            instance = new XPlayerApplication();
        }
        return instance;
    }

    // add Activity
    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    // 关闭每一个list内的activity
    public void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    /**
     * 第一次进入应用时初始化默认配置
     */
    public void initSettings() {
        // 是否是第一次进入应用
        boolean isEnterAppFirst = SharedPreferenceUtils.getBooleanValue("enter_first", true);

        if (isEnterAppFirst) {
            SharedPreferenceUtils.putIntValues("video_quantity",  1);  //普通
            SharedPreferenceUtils.putIntValues("buffer_size",  1);  // 512K
            SharedPreferenceUtils.putIntValues("video_aspectratio",  0);  // 自动检测
            SharedPreferenceUtils.putBoolValues("display_subtitle",  true); // 加载同名字幕
            SharedPreferenceUtils.putBoolValues("auto_play",  true);    // 自动播放下一个
            SharedPreferenceUtils.putBoolValues("file_buffer",  false); // 不缓存视频到sd卡

            LogUtils.e("第一次进入应用");
            SharedPreferenceUtils.putBoolValues("enter_first", false);
        } else {
            LogUtils.e("不是第一次进入应用");
        }
    }
}
