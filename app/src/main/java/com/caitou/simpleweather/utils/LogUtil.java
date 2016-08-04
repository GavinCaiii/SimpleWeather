package com.caitou.simpleweather.utils;

import android.util.Log;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-04.
 */
public class LogUtil {

    // 定义消息级别
    public static final int VERBOSE     = 1;
    public static final int DEBUG       = 2;
    public static final int INFO        = 3;
    public static final int WARN        = 4;
    public static final int ERROR       = 5;
    public static final int NOTHING     = 6;
    private static int level;

    public static void v(String tag, String msg) {
        if (level <= VERBOSE)
            Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (level <= INFO)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (level <= WARN)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR)
            Log.e(tag, msg);
    }

    public static void setLogLevel(int logLevel) {
        level = logLevel;
    }
}
