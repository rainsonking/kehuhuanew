package com.kwsoft.kehuhua.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kwsoft.kehuhua.urlCnn.EdusLoggerInterceptor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

import static com.kwsoft.kehuhua.config.Constant.sysUrl;

public class MyApplication extends Application {
    Context mContext;
    public static List<Activity> mActivityList = new ArrayList<Activity>();
    private static MyApplication instance;
    private static final String TAG = "JPush";
    private SharedPreferences sPreferences;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext=getApplicationContext();
//        Fresco.initialize(getApplicationContext());//拍照上传初始化
        JPushInterface.setDebugMode(false);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        initOkHttp();
        initIpPort();

    }
    @SuppressWarnings("unchecked")
    private void initIpPort() {
        try {
            sPreferences = getSharedPreferences("edusIpPortProjectName", MODE_PRIVATE);
            Map<String, String> map = (Map<String, String>) sPreferences.getAll();
            if (map.size() > 0) {//如果存在储存值
               String ip=sPreferences.getString("edus_ip", "");
               String port=sPreferences.getString("edus_port", "");
               String project=sPreferences.getString("edus_project", "");
                if (port.equals("")) {
                    sysUrl=ip+"/"+project+"/";
                }else{
                    sysUrl=ip+":"+port+"/"+project+"/";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initOkHttp() {
//      //创建cookieJar
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new EdusLoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)//增加cookieJar
                //可以添加其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
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

    public void exit() {

    }

}
