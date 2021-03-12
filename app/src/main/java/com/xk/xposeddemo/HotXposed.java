package com.xk.xposeddemo;

import android.app.Application;
import android.content.Context;

import com.xk.xposeddemo.util.LogUtils;
import com.xk.xposeddemo.util.SpUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;

import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author DX
 * 这种方案建议只在开发调试的时候使用，因为这将损耗一些性能(需要额外加载apk文件)，调试没问题后，直接修改xposed_init文件为正确的类即可
 * 可以实现免重启，由于存在缓存，需要杀死宿主程序以后才能生效
 * Created by DX on 2017/10/4.
 * Modified by chengxuncc on 2019/4/16.
 */

public class HotXposed implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    //按照实际使用情况修改下面几项的值
    /**
     * 当前Xposed模块的包名,方便寻找apk文件
     */
    private final static String modulePackageName = "com.xk.xposeddemo";

    /**
     * 实际hook逻辑处理类
     */
    private final String handleHookClass = HookLogic.class.getName();
    /**
     * 实际hook逻辑处理类的入口方法
     */
    private final String handleHookMethod = "handleLoadPackage";

    private final String initMethod = "initZygote";

    private IXposedHookZygoteInit.StartupParam startupparam;

    /**
     * 重定向handleLoadPackage函数前会执行initZygote
     *
     * @param loadPackageParam
     * @throws Throwable
     */
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.isFirstApplication) {
            return;
        }
        //没有文件读写权限的，这里会抛出异常，最后取默认false{
        boolean hook = false;
        try {
            hook = SpUtils.getBoolean(loadPackageParam.packageName, false);
            if (hook) {
                LogUtils.d("hook==>: " + loadPackageParam.packageName + ":" + loadPackageParam.processName);
            } else {
                LogUtils.v("skip: " + loadPackageParam.packageName + ":" + loadPackageParam.processName);
            }
        } catch (Throwable e) {
            LogUtils.e("skip==>: " + loadPackageParam.packageName + ":" + loadPackageParam.processName + ". 获取开关失败." + e.getMessage());
        }
        if (!hook) {
            return;
        }

        //将loadPackageParam的classloader替换为宿主程序Application的classloader,解决宿主程序存在多个.dex文件时,有时候ClassNotFound的问题
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    Context context = (Context) param.args[0];
                    loadPackageParam.classLoader = context.getClassLoader();
                    File apkFile = getApkFile(modulePackageName);
                    File hotFile = new File(context.getFilesDir(), "hot.apk");
                    FileUtils.copyFile(apkFile, hotFile);
                    Class<?> cls = getApkClass(hotFile.getAbsolutePath(), handleHookClass);

                    Object instance = cls.newInstance();

                    try {
                        cls.getDeclaredMethod(initMethod, startupparam.getClass()).invoke(instance, startupparam);
                    } catch (NoSuchMethodException e) {
                        // 找不到initZygote方法
                        e.printStackTrace();
                    }
                    loadPackageParam.setObjectExtra("hot", true);
                    cls.getDeclaredMethod(handleHookMethod, loadPackageParam.getClass()).invoke(instance, loadPackageParam);
                } catch (Throwable e) {
                    LogUtils.e("热更失效了");
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 实现initZygote，保存启动参数。
     *
     * @param startupParam
     */
    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        this.startupparam = startupParam;
    }

    private Class<?> getApkClass(String apkPath, String handleHookClass) throws Throwable {
        //加载指定的hook逻辑处理类，并调用它的handleHook方法
        PathClassLoader pathClassLoader = new PathClassLoader(apkPath, ClassLoader.getSystemClassLoader());
        Class<?> cls = Class.forName(handleHookClass, true, pathClassLoader);
        return cls;
    }

    private static File getApkFile(String packageName) {
        String filePath = String.format("/data/app/%s-%s/base.apk", packageName, 1);
        File apkFile = new File(filePath);
        if (!apkFile.exists()) {
            filePath = String.format("/data/app/%s-%s/base.apk", packageName, 2);
            apkFile = new File(filePath);
        }
        return apkFile;
    }
}