package com.xk.xposeddemo.util;

import android.util.Log;

/**
 * @author xuekai
 * @date 2021/3/12
 */
public class LogUtils {
    private static final String TAG = "XPosedDemoTag";

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }
}
