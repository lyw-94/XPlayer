package com.sdust.xplayer.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;


/**
 * Created by Liu Yongwei on 2016/5/8.
 * <p/>
 * version : 1.0
 */
public class BitmapUtils {

    /**
     * 按比例缩放bitmap
     * @param bitmap  待缩放的bitmap
     * @param newWidth 新的宽度
     * @param newHeight 新的高度
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,true);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return scaledBitmap;
    }

    /**
     * 获得视频缩略图
     * @param filePath  视频路径
     * @return  生成的缩略图
     */
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            // 获得指定位置的帧
            bitmap = retriever.getFrameAtTime(0);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return bitmap;
    }
}
