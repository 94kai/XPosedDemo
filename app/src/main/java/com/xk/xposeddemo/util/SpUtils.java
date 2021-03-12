package com.xk.xposeddemo.util;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author xuekai
 * @date 2021/3/12
 */
public class SpUtils {
    private static File info;

    public static void ensureFile() throws IOException {
        if (info == null) {
            info = new File("/sdcard/axposeddemo/info.txt");
            if (!info.getParentFile().exists()) {
                info.getParentFile().mkdirs();
            }
            if (!info.exists()) {
                info.createNewFile();
            }
        }
    }

    public static void ensureJson() {
        if (json != null) {
            return;
        }
        try {
            ensureFile();
            String jsonString = FileUtils.readFileToString(info);
            if (TextUtils.isEmpty(jsonString)) {
                json = new JSONObject();
            } else {
                json = new JSONObject(jsonString);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static JSONObject json;

    public static void putBoolean(@NonNull String key, boolean value) {
        try {
            ensureJson();
            json.put(key, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean getBoolean(@NonNull String key, boolean defValue) {
        ensureJson();
        return json.optBoolean(key, defValue);
    }

    public static boolean save() {
        if (json != null) {
            try {
                FileUtils.write(info, json.toString());
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
//    public static void putInt(@NonNull String key, int value) {
//        getSharedPreferences().edit().putInt(key, value).apply();
//    }
//
//    public int getInt(@NonNull String key, int defValue) {
//        return getSharedPreferences().getInt(key, defValue);
//    }
//
//    public static void putString(@NonNull String key, @Nullable String value) {
//        getSharedPreferences().edit().putString(key, value).apply();
//    }
//
//    @NonNull
//    public String getString(@NonNull String key) {
//        return getSharedPreferences().getString(key, "");
//    }
//
//    public static void removeKey(@NonNull String key) {
//        getSharedPreferences().edit().remove(key).apply();
//    }
}
