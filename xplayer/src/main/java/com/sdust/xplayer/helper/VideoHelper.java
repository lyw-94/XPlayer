package com.sdust.xplayer.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

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
import java.util.HashMap;
import java.util.List;

/**
 * Created by Liu Yongwei on 2016/5/21.
 * <p/>
 * version : 1.0
 */
public class VideoHelper {

    private static Context context = XPlayerApplication.getContext();
    private static MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

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
//            video.resolutionH = Integer.parseInt(VideoHelper.getVideoHeight(video.url));
//            video.resolutionW = Integer.parseInt(VideoHelper.getVideoWidth(video.url));
            list.add(video);
            LogUtils.e("视频名称：" + video.name + "  视频地址:  " + video.url + "视频分辨率" + video.resolutionH + "*" + video.resolutionW);
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
     *
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
     *
     * @param position  视频在列表中位置
     * @param videoList 视频集合
     */
    public static void playVideo(Context context, int position, ArrayList<Video> videoList) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("videolist", videoList);
        context.startActivity(intent);
    }

    /**
     * 扫描指定文件路径下的视频
     *
     * @param path 目录
     * @return 视频列表
     */
    public static List<Video> scanVideos(String path) {
        List<Video> videoList = new ArrayList<>();
        return videoList;
    }

    /**
     * 生成视频详细信息
     *
     * @param video
     */
    public static String generateVideoDetails(Video video) {
        StringBuilder sb = new StringBuilder();
        sb.append(context.getString(R.string.path) + "：\n" + video.url + "\n\n");
        sb.append(context.getString(R.string.size) + "：" + StringUtils.generateFileSize(video.size) + "\n\n");
        sb.append(context.getString(R.string.duration) + "：" + StringUtils.generateTime(video.duration) + "\n\n");
        sb.append(context.getString(R.string.resolution) + "：" + video.resolutionW + "x" + video.resolutionH + "\n");

        return sb.toString();
    }

    /**
     * 获取视频宽度
     *
     * @param path 视频路径
     * @return 视频宽度
     */
    public static String getVideoWidth(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        mediaMetadataRetriever.setDataSource(path);
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
    }

    /**
     * 获取视频高度
     *
     * @param path 视频路径
     * @return 视频高度
     */
    public static String getVideoHeight(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        mediaMetadataRetriever.setDataSource(path);
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
    }

    /**
     * 获取视频时长
     *
     * @param path 视频路径
     * @return 视频时长  单位ms
     */
    public static String getVideoDuration(String path) {
        if (TextUtils.isEmpty(path)) {
            return "";
        }
        mediaMetadataRetriever.setDataSource(path);
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }

    /**
     * 获得视频缩略图
     *
     * @param filePath 视频路径
     * @return 生成的缩略图
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            // 获得指定位置的帧
            bitmap = retriever.getFrameAtTime(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * 获得视频的时长、分辨率等信息
     */
   public static class GetVideoInfoTask extends AsyncTask<ArrayList<Video>, Integer, Void> {

        @Override
        protected Void doInBackground(ArrayList<Video>... params) {
            ArrayList<Video> list = params[0];

            for (int i = 0; i < list.size(); i++) {
                Video video = list.get(i);
                video.resolutionH = Integer.parseInt(VideoHelper.getVideoHeight(video.url));
                video.resolutionW = Integer.parseInt(VideoHelper.getVideoWidth(video.url));
                if (video.duration == 0) {
                    video.duration = Long.parseLong(VideoHelper.getVideoDuration(video.url));
                }
            }
            return null;
        }
    }
}
