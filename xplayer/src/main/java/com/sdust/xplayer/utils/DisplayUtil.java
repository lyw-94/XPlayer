package com.sdust.xplayer.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

/**
 * dp、sp 转换的工具类
 * 
 * @author
 * 
 */
public class DisplayUtil {

	/**
	 * 将px值转换为dip或dp值，保证尺寸大小不变
	 * 
	 * @param pxValue
	 *
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		System.out.println("scale" + scale);
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * 
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 *
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 *
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/** 设置控件的Margin属性 */
	public static void px2dpMargin(Context context, View view, int top,
			int left, int bottom, int right) {
		LayoutParams params = (LayoutParams) view
				.getLayoutParams();
		if (top != 0) {
			params.topMargin = DisplayUtil.px2dip(context, top);
		}
		if (left != 0) {
			params.leftMargin = DisplayUtil.px2dip(context, left);
		}
		if (bottom != 0) {
			params.bottomMargin = DisplayUtil.px2dip(context, bottom);
		}
		if (right != 0) {
			params.rightMargin = DisplayUtil.px2dip(context, right);
		}
		view.setLayoutParams(params);
	}

	/**
	 * 获取屏幕宽度和高度，单位为px
	 * 
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;

		return new Point(w_screen, h_screen);
	}

	/**
	 * 获取屏幕长宽比
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context) {
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H / W);
	}

}
