package com.xk.xposeddemo;

import com.xk.xposeddemo.test.Thanos;
import com.xk.xposeddemo.test.Titan;
import com.xk.xposeddemo.util.LogUtils;
import com.xk.xposeddemo.util.SpUtils;

import java.util.HashMap;
import java.util.Set;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookDispatcher {
    private static final String TAG = "HookDispatcher";
    public HashMap<String, Runnable> hookActions = new HashMap<>();

    public void dispatch(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        addActions(lpparam);
        Set<String> actions = hookActions.keySet();
        for (String action : actions) {
            boolean needHook = SpUtils.getBoolean(action, false);
            if (needHook) {
                LogUtils.d("hookAction: " + action);
            } else {
                LogUtils.v("skipAction: " + action);
            }
            if (needHook) {
                hookActions.get(action).run();
            }
        }
    }


    public void addActions(final XC_LoadPackage.LoadPackageParam lpparam) {
        Titan.test(lpparam, hookActions);
        Thanos.test(lpparam, hookActions);
    }
}
