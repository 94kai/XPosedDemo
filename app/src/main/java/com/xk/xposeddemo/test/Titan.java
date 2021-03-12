package com.xk.xposeddemo.test;

import android.widget.TextView;

import com.xk.xposeddemo.util.LogUtils;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author liquanfei
 */
public class Titan {

    public static void test(final XC_LoadPackage.LoadPackageParam lpparam, HashMap<String, Runnable> hookActions) {
        hookActions.put("hookSetText", new Runnable() {
            @Override
            public void run() {
                LogUtils.e("hookSetText actions被执行了，这里hook了xsettext");

                XposedHelpers.findAndHookMethod(TextView.class, "setText", CharSequence.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        param.args[0] = "个ss啊aaa";
//                    Log.e("xx", "before list" + param.args[0]);
//                    if ("".equals(param.args[0])) {
//                        String[] ss = new String[]{"thanos_log"};
//                        param.setResult(ss);
//                    }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
//                    Log.e("xx", "end list");
                    }
                });
            }
        });
    }
}
