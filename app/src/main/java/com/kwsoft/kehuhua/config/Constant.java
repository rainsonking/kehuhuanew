package com.kwsoft.kehuhua.config;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Administrator on 2015/11/28 0028.
 *
 */
public class Constant {
    /**
     * 系统地址：http://192.168.6.46:8080/edus_auto/
     * <p>
     * 陈蒙地址：http://192.168.6.150:8081/edus_auto/
     * <p>
     * 阿里服务器地址  http://182.92.108.162:8124/edus_auto/
     */
    public static String sysUrl = "http://192.168.6.117:8080/edus_auto/";//红伟电脑
//    public static String sysUrl = "http://192.168.6.150:8081/edus_auto/";//陈蒙电脑项目
    public final static String sysLoginUrl = "login_interfaceProLogin.do";//项目选择方法
    public final static String projectLoginUrl = "login_interfaceLogin.do";//登陆方法

    public final static String requestListSet = "model_interfaceToList.do";//获得列表配置数据和data数据方法
    public final static String requestListData = "model_ajaxList.do";//仅获得data数据方法
    public final static String requestAdd = "add_interfaceToAdd.do";
    public final static String commitAdd = "add_interfaceAdd.do";
    public final static String requestRowsAdd = "addRelation_interfaceToAddRelationPage.do";

    public final static String requestEdit = "update_interfaceToUpdate.do";
    public final static String requestDelete = "delete_interfaceDelete.do";
    public final static String commitEdit = "update_interfaceUpdate.do";

    public final static String requestTreeDialog = "treeDialog_interfaceToList.do";
    public final static String requestMaxRule = "add_interfaceAjaxGetMaxRule.do";

    public final static String requestMessage = "mongoModel_interfaceSysMessList.do";

    public static String tmpFieldId = "";

    public static Map<String, String> commitPra;

    public static Map<String, String> paramsMapSearch = new HashMap<>();

    public final static String proIdName = "proId";

    public static String loginName = "";
    public static String menuData = "";
    public static String proId = "";
    public static String proName = "";
//    public static String stuProId = "57159822f07e75084cb8a1fe";//陈蒙学员端
    //public static String stuProId="5704e45c7cf6c0b2d9873da6";//主项目
    public static String stuCourseTableId = "19";
    public  static String stuCourseUserId="" ;

    public static String timeName = "alterTime";
    public static String menuTime = "";

    public final static String sourceName = "source";
    public final static String sourceInt = "1";


    public final static String USER_NAME = "loginName";// 登录时用户名 ,即手机号
    public final static String PASSWORD = "password";// 登录密码
    public final static String loginUserId = "loginUserId";
    public final static String delIds = "delIds";
    public static String USERID;
    public static int menuIsAlter = 0;
    public static String menuAlterTime = "";


    public static String localCookie1 = "";
    public static String tempTableId;
    public static String tempPageId;

    public static volatile String localCookie = null;//session


    public final static String baseUrl = "http://182.92.108.162:8114/phone_edus_auto/";

    public final static String ipAdress = "http://192.168.6.46:8080/";
    public final static String ipRootAdress = ipAdress + "phone_edus/";

    /* 登录界面所需信息 */
    public final static String LOGIN_URL = "login_phoneAccLogin.do";// 登录界面链接
    /* 搜索地址所需信息 */
    public final static String searchCommitUrl = "http://192.168.6.46:8080/phone_edus_auto/model_ajaxList.do?";


    /*增加学员信息*/
    public final static String tableId = "tableId";// 菜单中潜在学员的tableId
    public final static String pageId = "pageId";//菜单pageId

    public final static String mainId = "mainId";
    public final static String mainTableId = "mainTableId";
    public final static String mainPageId = "mainPageId";
    public static String mainTableIdValue = "";
    public static String mainPageIdValue = "";
    public static String mainIdValue = "";

    /*增加学员信息中提交连接*/
    public static String USERNAME_ALL;
    public static String PASSWORD_ALL;
    public static String PREURL = baseUrl + LOGIN_URL + USER_NAME + "=" + USERNAME_ALL;
    public static int jumpNum = 0;//1、修改  2、添加 3、关联添加
    public static int jumpNum1 = 0;//

    public static String relationFieldId;
    public static int relationField;

    public static List<Map<String, Object>> fieldSetTemp = new ArrayList<>();
    public static String fieldSetStr = "";
    public static List<Integer> idArrList;
    public static int deleteNum;


    //适配器固定变量

    public final static String primKey = "montageName";// 主key
    public final static String itemValue = "true_defaultShowVal";// 适配器中每一项的默认值或新添加值、也可作为内部对象的默认值位置


    //    public final static String primValue = "montageValue";// 主value，只在内部对象中表示
    public final static String secondKey = "montageName1";// 副key
    public final static String itemName = "true_defaultShowValName";// 内部对象的名称集合
    public final static String commitValue = "commitValue";// commitValue



    public static int topBarColor;


//学员端参数：

    public static String stu_index = "";// 副key
    public static String stu_homeSetId = "";// 副key

    public static void threadToast(Activity activity, String str) {
        Looper.prepare();
        Toast.makeText(activity, str,
                Toast.LENGTH_SHORT).show();
        activity.finish();
        Looper.loop();
    }

    public static void toastMeth(Activity activity, String str) {
        Looper.prepare();
        Toast.makeText(activity, str,
                Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

    public static String StringFilter(String str) throws PatternSyntaxException {
        String regEx = "[/\\:*?<>|\"\n\t]"; //要过滤掉的字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "[1][3578]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    /**
     * @param strEmail
     * @return 验证邮箱
     */
    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strEmail);
        return m.matches();
    }

    /**
     * @param strIdentity
     * @return 验证身份证号
     */
    public static boolean isIdentityID(String strIdentity) {
        String strPattern = "(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strIdentity);
        return m.matches();
    }

    /**
     * @param strName
     * @return 验证姓名
     */
    public static boolean isName(String strName) {
        String strPattern = "[^`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]{1,22}";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strName);
        return m.matches();
    }

    /**
     * @param context
     */
    public static void goHuaWeiSetting(Context context)  {
        try {
            //HUAWEI H60-l02 P8max测试通过
            //Log.d(MainActivity.class.getSimpleName(), "进入指定app悬浮窗管理页面失败，自动进入所有app悬浮窗管理页面");
            Intent intent = new Intent("com.kwsoft.version.fragment");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager",
            //      "com.huawei.permissionmanager.ui.SingleAppActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivity(intent);

        } catch (SecurityException e) {
            Intent intent = new Intent("com.kwsoft.version.fragment");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //   ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            ComponentName comp = new ComponentName("com.huawei.systemmanager",
                    "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理，跳转到本app的权限管理页面,这个需要华为接口权限，未解决
            //      ComponentName comp = new ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity");//悬浮窗管理页面
            intent.setComponent(comp);
            context.startActivity(intent);
            //Log.d(MainActivity.class.getSimpleName(), "正在进入指定app悬浮窗开启位置..");
        }catch(ActivityNotFoundException e){
            /**
             * 手机管家版本较低 HUAWEI SC-UL10
             */
            //   Toast.makeText(MainActivity.this, "act找不到", Toast.LENGTH_LONG).show();
            Intent intent = new Intent("com.kwsoft.version.fragment");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.TabItem");//权限管理页面 android4.4
            //   ComponentName comp = new ComponentName("com.android.settings","com.android.settings.permission.single_app_activity");//此处可跳转到指定app对应的权限管理页面，但是需要相关权限，未解决
            intent.setComponent(comp);
            context.startActivity(intent);
            e.printStackTrace();
        }
        catch(Exception e){
            //抛出异常时提示信息
            Toast.makeText(context, "进入设置页面失败，请手动设置", Toast.LENGTH_LONG).show();
        }
    }
}
