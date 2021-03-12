package com.xk.xposeddemo.app;

import android.content.pm.PackageInfo;

import com.xk.xposeddemo.XPosedApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author xuekai
 * @date 2021/3/12
 */
public class AppUtils {

    static ArrayList<AppInfo> appInfos = new ArrayList<>();

    public static List<AppInfo> getAllApps() {
        if (appInfos.isEmpty()) {
            List<PackageInfo> packages = XPosedApplication.application.getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packages.size(); i++) {
                PackageInfo packageInfo = packages.get(i);
                // AppInfo 自定义类，包含应用信息
                AppInfo appInfo = new AppInfo();
                appInfo.appName = packageInfo.applicationInfo.loadLabel(XPosedApplication.application.getPackageManager()).toString();
                appInfo.packageName = packageInfo.packageName;
                appInfo.isSystem = (packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM);
                appInfos.add(appInfo);
            }
            AppInfo appInfo = new AppInfo();
            appInfo.isSystem = 0.5f;
            appInfo.packageName = "showsystem";
            appInfo.appName = "**显示系统应用**";
            appInfos.add(appInfo);
            Collections.sort(appInfos, new Comparator<AppInfo>() {
                @Override
                public int compare(AppInfo o1, AppInfo o2) {
                    return (int) (o1.isSystem * 10 - o2.isSystem * 10);
                }
            });
        }
        return appInfos;
    }
}
