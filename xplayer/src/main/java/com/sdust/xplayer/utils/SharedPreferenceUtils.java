package com.sdust.xplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.sdust.xplayer.application.XPlayerApplication;

/**
 * Created by Liu Yongwei on 2016/5/26.
 * <p/>
 * version : 1.0
 */
public class SharedPreferenceUtils {

    private static Context context;
    private static SharedPreferences sf;

    static {
        context = XPlayerApplication.getContext();
        sf = context.getSharedPreferences("xplayer", context.MODE_PRIVATE);
    }

    public static void putIntValues(String key, int value) {
        SharedPreferences.Editor editor = sf.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putBoolValues(String key, boolean value) {
        SharedPreferences.Editor editor = sf.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putFloatValues(String key, float value) {
        SharedPreferences.Editor editor = sf.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static int getIntValues(String key) {
        return sf.getInt(key, 1);
    }

    public static boolean getBooleanValue(String key) {
        return sf.getBoolean(key, true);
    }

    public static boolean getBooleanValue(String key, boolean defaultValue) {
        return sf.getBoolean(key, defaultValue);
    }

    public static float getFloatValues(String key) {
        return sf.getFloat(key, 0);
    }
}
