package com.kwsoft.kehuhua.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {
    Context mContext;
    public static List<Activity> mActivityList = new ArrayList<Activity>();
    private static MyApplication instance;
    private static final String TAG = "JPush";

    @Override
    public void onCreate() {
        super.onCreate();

        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
    }

    public static MyApplication getInstance() {
        if (null == instance) {
            instance = new MyApplication();
        }
        return instance;
    }



    // 开启的activity 添加到List集合中
    public void addActivity(Activity activity) {
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    // 遍历所有Activity并finish
    public void exitApp(Context context, boolean isAll) {
        Activity login = null;
        for (Activity activity : mActivityList) {
        }
        mActivityList.clear();
        mActivityList.add(login);
        if (isAll) {
            System.exit(0);
        }
    }

    public void exitLogin(Context context) {
        Activity login = null;
        for (Activity activity : mActivityList) {
        }
        mActivityList.clear();
        mActivityList.add(login);

    }

    @SuppressWarnings("deprecation")
    public void exitAppAll(Context context) {
        try {

            for (Activity activity : mActivityList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mActivityList.clear();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);//
            activityMgr.killBackgroundProcesses("cn.keweisoft");
            System.exit(0);
        }
    }
public void exit(){

}

}
