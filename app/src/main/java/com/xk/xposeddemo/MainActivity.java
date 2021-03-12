package com.xk.xposeddemo;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xk.xposeddemo.app.AppInfo;
import com.xk.xposeddemo.app.AppUtils;
import com.xk.xposeddemo.util.SpUtils;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupApps();
        setupActions();

    }


    @Override
    protected void onStop() {
        super.onStop();
        SpUtils.save();
    }

    private void setupActions() {
        // 获取并展示所有的actions
        HookDispatcher hookDispatcher = new HookDispatcher();
        hookDispatcher.addActions(null);
        HashMap<String, Runnable> hookActions = hookDispatcher.hookActions;

        LinearLayout actions = (LinearLayout) findViewById(R.id.actions);
        TextView textView = new TextView(this);
        textView.setText("选中某个action，表示开启");
        actions.addView(textView);
        for (String action : hookActions.keySet()) {
            View inflate = View.inflate(this, R.layout.item_app, null);
            CheckBox checkBox = inflate.findViewById(R.id.checkbox);
            checkBox.setText(action);
            boolean hook = SpUtils.getBoolean(action, false);
            checkBox.setChecked(hook);
            checkBox.setTag(action);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox v1 = (CheckBox) v;
                    String action = (String) v1.getTag();
                    SpUtils.putBoolean(action, v1.isChecked());
                }
            });
            actions.addView(inflate);
        }
    }

    private void setupApps() {
        LinearLayout content = (LinearLayout) findViewById(R.id.apps);
        TextView textView = new TextView(this);
        textView.setText("这里能控制开关的App，必须声明了磁盘读权限，因为被hoo的App启动时会从磁盘中读取配置");
        content.addView(textView);
        boolean showSystem = false;
        for (AppInfo appInfo : AppUtils.getAllApps()) {
            if (appInfo.isSystem == 1 && !showSystem) {
                //系统app
                continue;
            }
            if ("de.robv.android.xposed.installer".equals(appInfo.packageName) //xposed框架
                    || "com.xk.xposeddemo".equals(appInfo.packageName)//我的xposed模块
            ) {
                //白名单app
                continue;
            }
            View inflate = View.inflate(this, R.layout.item_app, null);
            CheckBox checkBox = inflate.findViewById(R.id.checkbox);
            checkBox.setText(appInfo.appName);
            boolean hook = SpUtils.getBoolean(appInfo.packageName, false);
            if (hook && "showsystem".equals(appInfo.packageName)) {
                showSystem = true;
            }
            checkBox.setChecked(hook);
            checkBox.setTag(appInfo);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox v1 = (CheckBox) v;
                    AppInfo appInfo = (AppInfo) v1.getTag();
                    SpUtils.putBoolean(appInfo.packageName, v1.isChecked());
                }
            });
            content.addView(inflate);
        }
    }
}