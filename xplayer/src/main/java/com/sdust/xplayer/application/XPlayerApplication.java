package com.sdust.xplayer.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.sdust.xplayer.service.AssertService;
import com.yixia.camera.VCamera;
import com.yixia.camera.util.DeviceUtils;

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
        mContext = getApplicationContext();

        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/xplayer/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/xplayer/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/xplayer/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(this);

        // 解压assert里面的文件
        startService(new Intent(this, AssertService.class));
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
}
