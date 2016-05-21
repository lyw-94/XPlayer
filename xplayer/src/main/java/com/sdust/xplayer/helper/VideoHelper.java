package com.sdust.xplayer.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;

import com.sdust.xplayer.R;
import com.sdust.xplayer.activities.VideoPlayerActivity;
import com.sdust.xplayer.application.XPlayerApplication;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.utils.FileUtils;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu Yongwei on 2016/5/21.
 * <p/>
 * version : 1.0
 */
public class VideoHelper {

    private static Context context = XPlayerApplication.getContext();

    /**
     * 获取手机上所有视频(>=3M)
     */
    public static List<Video> getVideos() {
        List<Video> list = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();
        String[] projection = {
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA
        };
        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null,
                null);
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            Long size = cursor.getLong(2);
            if (size < ApplicationConfig.FILTER_VIDEO_SIZE) //TODO: 在设置中修改
                continue;    // 屏蔽小于3M的文件
            Video video = new Video();
            video.name = cursor.getString(0);
            video.duration = cursor.getLong(1);
            video.size = size;
            video.url = cursor.getString(3);
            list.add(video);
            LogUtils.e("视频名称：" + video.name + "  视频地址:  " + video.url);
        }
        cursor.close();

        return list;
    }


    /**
     * 删除视频
     *
     * @param video
     */
    public static void deleteVideo(Video video) {
        FileUtils.deleteFile(video.url);
        LogUtils.e("从本地删除视频: " + video.url);
        context.getContentResolver().delete(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                "_data=?",
                new String[]{video.url});
        LogUtils.e("从媒体数据库删除视频:");
    }

    /**
     * 视频重命名
     * @param video
     * @param newName
     * @return
     */
    public static boolean renameVideo(Video video, String newName) {

        // 文件中修改

        File oldFile = new File(video.url);
        File newFile = null;
        if (oldFile.isFile() && oldFile.exists()) {
            newFile = new File(oldFile.getAbsolutePath().replace(oldFile.getName(), newName));
            LogUtils.e("修改前：" + oldFile.getAbsolutePath());
            LogUtils.e("修改后：" + newFile.getAbsolutePath());
            if (!oldFile.renameTo(newFile)) {
                return false;
            }
        }

        // 媒体数据库中修改

        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Video.Media.DISPLAY_NAME, newName);
        values.put(MediaStore.Video.Media.DATA, newFile.getAbsolutePath());
        resolver.update(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                values,
                "_data=?",
                new String[]{video.url});

        // 实体更改
        video.name = newName;
        video.url = newFile.getAbsolutePath();

        return true;
    }

    /**
     * 播放视频
     * @param position   视频在列表中位置
     * @param videoList  视频集合
     */
    public static void playVideo(Context context, int position, ArrayList<Video> videoList) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("videolist", videoList);
        context.startActivity(intent);
    }

    /**
     * 扫描指定文件路径下的视频
     * @param path  目录
     * @return      视频列表
     */
    public static List<Video> scanVideos(String path) {
        List<Video> videoList = new ArrayList<>();
        return videoList;
    }

    /**
     * 生成视频详细信息
     * @param video
     */
    public static String generateVideoDetails(Video video) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.path) + "：\n"+ video.url + "\n\n");
        sb.append(context.getString(R.string.size) + "："+ StringUtils.generateFileSize(video.size) + "\n\n");
        sb.append(context.getString(R.string.duration) + "："+ StringUtils.generateTime(video.duration) + "\n\n");
        sb.append(context.getString(R.string.resolution) + "："+ video.resolutionW + "x" + video.resolutionH + "\n");

        return sb.toString();
    }
}
