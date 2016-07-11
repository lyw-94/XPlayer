package com.sdust.xplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.File;

/**
 * Created by Liu Yongwei on 2016/5/21.
 * <p/>
 * version : 1.0
 */
public class FileUtils {

    /**
     * 删除目录
     */
    public static void deleteDirectory(String path) {
        File dir = new File(path);
        if (dir.isDirectory() && dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {  // 递归删除
                if (files[i].isFile()) {
                    files[i].delete();
                } else if (files[i].isDirectory()) {
                    deleteDirectory(files[i].getAbsolutePath());
                }
            }
        }
    }

    /**
     * 文件重命名
     *
     * @param path     文件绝对路径
     * @param newName  新文件名
     */
    public static boolean renameFile(String path, String newName) {
        File oldFile = new File(path);
        if (oldFile.isFile() && oldFile.exists()) {
            File newFile = new File(oldFile.getAbsolutePath().replace(oldFile.getName(), newName));
            LogUtils.e("修改前：" + oldFile.getAbsolutePath());
            LogUtils.e("修改后：" + newFile.getAbsolutePath());
            if (oldFile.renameTo(newFile)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断文件名是否与该文件父目录下的其它文件冲突
     * @param fileName   文件名
     * @param dir   文件父目录
     * @return
     */
    public static boolean isSameWithExistsFile(String fileName, String dir) {
        if (TextUtils.isEmpty(fileName)) {
            return false;
        }
        File parent = new File(dir);
        File[] files = parent.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().equals(fileName)) {
                return true;
            }
        }

        return false;
    }


    public static boolean deleteFile(File f) {
        if (f != null && f.exists() && !f.isDirectory()) {
            return f.delete();
        }
        return false;
    }

    public static boolean  deleteDir(File f) {
        if (f != null && f.exists() && f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                file.delete();
            }
            f.delete();
        }

        return true;
    }

    public static boolean deleteDir(String f) {
        if (f != null && f.length() > 0) {
            if (deleteDir(new File(f))) {
                return true;
            }
        }

        return false;
    }

    public static boolean deleteFile(String f) {
        if (f != null && f.length() > 0) {
            return deleteFile(new File(f));
        }
        return false;
    }

    /**
     * 更新相册  --> FileUtils
     * @param file 待扫描的文件
     */
    public static void refreshGallery(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }
}
