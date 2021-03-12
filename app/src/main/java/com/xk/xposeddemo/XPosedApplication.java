package com.xk.xposeddemo;

import android.app.Application;
import android.content.Context;

/**
 * @author xuekai
 * @date 2021/3/12
 */
public class XPosedApplication extends Application {
    public static Application application;

    @Override
    protected void attachBaseContext(Context base) {
        application = this;
        super.attachBaseContext(base);
    }
}
