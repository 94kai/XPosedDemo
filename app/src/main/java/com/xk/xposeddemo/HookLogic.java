package com.xk.xposeddemo;

import com.xk.xposeddemo.util.LogUtils;
import com.xk.xposeddemo.util.SpUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * @author DX
 * 注意：该类不要自己写构造方法，否者可能会hook不成功
 * 开发Xposed模块完成以后，建议修改xposed_init文件，并将起指向这个类,以提升性能
 * 所以这个类需要implements IXposedHookLoadPackage,以防修改xposed_init文件后忘记
 * Created by DX on 2017/10/4.
 */

public class HookLogic implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private final static String modulePackageName = HookLogic.class.getPackage().getName();
    private XSharedPreferences sharedPreferences;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Object hot = loadPackageParam.getObjectExtra("hot");
        if (hot == null) {//不走热更，app开关。如果走热更的话，app开关在hotXposed中控制
            if (!loadPackageParam.isFirstApplication) {
                return;
            }
            boolean hook = false;
            try {
                hook = SpUtils.getBoolean(loadPackageParam.packageName, false);
                if (hook) {
                    LogUtils.d("hookApp(cold)==>: " + loadPackageParam.processName);
                } else {
                    LogUtils.v("skipApp(cold)==>: " + loadPackageParam.processName);
                }
            } catch (Throwable e) {
                LogUtils.e("skipApp(cold)==>: " + loadPackageParam.processName + ". 获取开关失败. " + e.getMessage());
            }
            if (!hook) {
                return;
            }
        }
        new HookDispatcher().dispatch(loadPackageParam);
    }

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
        this.sharedPreferences = new XSharedPreferences(modulePackageName, "default");
        XposedBridge.log(modulePackageName + " initZygote");
    }
}
