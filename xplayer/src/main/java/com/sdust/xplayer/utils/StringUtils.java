package com.sdust.xplayer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 字符串工具类
 * @author Liu Yongwei
 *
 */
public class StringUtils {
	
	private static final double KB = 1024.0;
	private static final double MB = 1048576.0;
	private static final double GB = 1073741824.0;
	
	/**
	 * 将long值转换为对应的大小
	 * 
	 * @param size
	 * @return 
	 */
	public static String generateFileSize(long size) {
		String fileSize;
		
		if (size < KB)
			fileSize = size + "B";
		else if (size < MB)
			fileSize = String.format("%.1f", size / KB) + "KB";
		else if (size < GB)
			fileSize = String.format("%.1f", size / MB) + "MB";
		else
			fileSize = String.format("%.1f", size / GB) + "GB";

		return fileSize;
	}
	
	/**
	 * 转换时间显示
	 * 
	 * @param time 毫秒
	 *            
	 * @return 00:00:00格式
	 */
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}
	
	/**
	 * 得到当前系统时间
	 * @return
	 */
	public static String getSystemTime() {
		SimpleDateFormat format=new SimpleDateFormat("HH:mm");
		return format.format(new Date());
	}
	
	/**
	 * 判断是否是网络地址
	 * @param path
	 * @return
	 */
	public static boolean isNetUri(String path) {
		boolean result = false;
		if (path != null && path.contains("http") || path.contains("rtsp")
				|| path.contains("MMS")) {
			result = true;
		}
		return result;
	}
}
