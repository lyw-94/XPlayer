package com.sdust.xplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


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
     * 保存bitmap到本地  --> BitmapUtils
     * @param bitmap 待保存的bitmap
     * @return 图片路径
     */
    public static String saveBitmapToLocal(Context context, Bitmap bitmap, String path) {
        if (bitmap == null || TextUtils.isEmpty(path)) {
            return "";
        }
        File f = new File(path);
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            OutputStream outStream = new FileOutputStream(f);
            // 保存图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            LogUtils.e("保存成功,保存目录时：" + f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f.getAbsolutePath();
    }

    /**
     * 保存bitmap到本地  根据一定规则生成文件名
     * @param context 上下文对象
     * @param bitmap 待保存的bitmap
     * @param dirName  文件夹路径
     */
    public static String saveBitmapWithUniqueName(Context context, Bitmap bitmap, String dirName) {
        if (bitmap == null || TextUtils.isEmpty(dirName)) {
            return null;
        }

        File dir = new File(dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 随机生成图片名
        SimpleDateFormat format = new SimpleDateFormat("yyymmddhhmmss");
        String fileName = format.format(new Date()) + ".jpg";

        File file = new File(dirName, fileName);
        try {
            OutputStream outStream = new FileOutputStream(file);
            // 保存图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            FileUtils.refreshGallery(context, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }
}
