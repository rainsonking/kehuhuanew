package com.kwsoft.kehuhua.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.kwsoft.kehuhua.adcustom.R;

import java.lang.reflect.Field;

/**
 * 应用启动图标未读消息数显示 工具类  (效果如：QQ、微信、未读短信 等应用图标)

 * 依赖于第三方手机厂商(如：小米、三星)的Launcher定制、原生系统不支持该特性

 * 该工具类 支持的设备有 小米、三星、索尼【其中小米、三星亲测有效、索尼未验证】
 * @author xiebubiao@163.com
 *
 */
public class BadgeUtil {

    public static void sendBadgeNumber(Context context,int count) {
//        if (count!=0) {
//            count = Math.max(0, Math.min(count, 99));
//        }

        String manufacturer=Build.MANUFACTURER;
        Log.i("123","manufacturer====>"+manufacturer);
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(count,context);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSamsumg(count,context);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSony(count,context);
        } else {
            Toast.makeText(context, "Not Support", Toast.LENGTH_LONG).show();
        }
    }

    private static void sendToXiaoMi(int count,Context context) {

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("您有"+count+"未读消息");
            builder.setTicker("您有"+count+"未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.icon);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            if (count > 0) {
                count--;
            }
            //以上代码为notification的初始化信息，在实际应用中，可以单独使用

            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);

            field.set(miuiNotification, count);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);

            field.set(notification, miuiNotification);
            Toast.makeText(context, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra
                    ("android.intent.extra.update_application_component_name",context.getPackageName() + "/"+
                            getLauncherClassName(context));
            localIntent.putExtra("android.intent.extra.update_application_message_text",count);
            context.sendBroadcast(localIntent);
        }
        finally
        {
            if(notification!=null && isMiUIV6 )
            {
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }

    }

    private static void sendToSony(int count,Context context) {
        boolean isShow = true;
        if (count==0) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",getLauncherClassName
                (context));//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",context.getPackageName
                ());//包名
        context.sendBroadcast(localIntent);

        Toast.makeText(context, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    private static void sendToSamsumg(int count,Context context)
    {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", count);//数字
        localIntent.putExtra("badge_count_package_name", context.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name",getLauncherClassName(context)); //启动页
        context.sendBroadcast(localIntent);
        Toast.makeText(context, "Samsumg," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    /**
     * Retrieve launcher activity name of the application from the context
     *
     * @param context The context of the application package.
     * @return launcher activity name of this application. From the
     *         "android:name" attribute.
     */
    public static String getLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        // To limit the components this Intent will resolve to, by setting an
        // explicit package name.
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // All Application must have 1 Activity at least.
        // Launcher activity must be found!
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        // get a ResolveInfo containing ACTION_MAIN, CATEGORY_LAUNCHER
        // if there is no Activity which has filtered by CATEGORY_DEFAULT
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }

        return info.activityInfo.name;
    }
}
