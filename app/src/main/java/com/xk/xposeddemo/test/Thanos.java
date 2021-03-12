package com.xk.xposeddemo.test;

import android.content.Context;
import android.content.res.AssetManager;

import com.xk.xposeddemo.util.LogUtils;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Thanos {

    public static void test(final XC_LoadPackage.LoadPackageParam lpparam, HashMap<String, Runnable> hookActions) {
        hookActions.put("setPrintToConsole优化带log", new Runnable() {
            @Override
            public void run() {
                LogUtils.e("setPrintToConsole优化 功能开启, getAssets()list带log");

                XposedHelpers.findAndHookMethod(AssetManager.class, "list", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if ("".equals(param.args[0])) {
                            String[] ss = new String[]{"thanos_log"};
                            param.setResult(ss);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
            }
        });
        hookActions.put("setPrintToConsole优化", new Runnable() {
            @Override
            public void run() {
                LogUtils.e("setPrintToConsole优化 功能开启, getAssets()list不带log");

                XposedHelpers.findAndHookMethod(AssetManager.class, "list", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if ("".equals(param.args[0])) {
                            String[] ss = new String[]{};
                            param.setResult(ss);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                    }
                });
            }
        });

        hookActions.put("打开thanos日志", new Runnable() {
            @Override
            public void run() {

                try {
                    XposedHelpers.findAndHookMethod(lpparam.classLoader.loadClass("com.bytedance.thanos.ThanosApplication"), "attachBaseContext", Context.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            try {
                                lpparam.classLoader.loadClass("com.bytedance.thanos.common.util.ThanosLog").getMethod("setPrintToConsole", boolean.class).invoke(null, true);
                                LogUtils.e("打开thanos日志成功");
                            } catch (Throwable t) {
                                LogUtils.e("打开thanos日志失败" + t.getMessage());
                            }
                        }
                    });
                } catch (ClassNotFoundException e) {
                    LogUtils.e("打开thanos日志hook失败" + e.getMessage());
                }
            }
        });

    }
}
