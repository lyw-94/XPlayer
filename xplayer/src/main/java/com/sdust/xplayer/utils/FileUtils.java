package com.sdust.xplayer.utils;

import java.io.File;

/**
 * Created by Liu Yongwei on 2016/5/21.
 * <p/>
 * version : 1.0
 */
public class FileUtils {

    /**
     * 删除文件
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

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
     * @param path
     * @param newName
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
}
