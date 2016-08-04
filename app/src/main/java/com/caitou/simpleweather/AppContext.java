package com.caitou.simpleweather;

import android.app.Application;
import android.content.Context;

import com.caitou.simpleweather.utils.LogUtil;

/**
 * @className:
 * @classDescription:
 * @Author: Guangzhao Cai
 * @createTime: 2016-08-04.
 */
public class AppContext extends Application {
    private static final String TAG = "AppContext";

    private static AppContext instance;

    public static AppContext getInstance() {
        return instance;
    }

    public Context getContext() {
        if (instance == null)
            return null;
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // set log level
        LogUtil.setLogLevel(LogUtil.VERBOSE);

        LogUtil.d(TAG, "AppContext init finished!");
    }
}
