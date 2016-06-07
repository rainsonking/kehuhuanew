package com.kwsoft.kehuhua.config;

import android.app.Activity;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;
import java.util.regex.*;

/**
 * Created by Administrator on 2015/11/28 0028.
 */
public class Constant {
    //phoneAccount=15535211113&phonePassword=111111
    //基础地址
    public final static String baseUrl = "http://182.92.108.162:8114/phone_edus_auto/";
    //外网http://182.92.108.162:8114/
    //内网http://192.168.6.46:8080/
    public final static String ipAdress="http://192.168.6.46:8080/";
    public final static String ipRootAdress=ipAdress+"phone_edus/";

    /* 登录界面所需信息 */
    public final static String LOGIN_URL = "login_phoneAccLogin.do";// 登录界面链接
    /* 搜索地址所需信息 */
    public final static String searchCommitUrl="http://192.168.6.46:8080/phone_edus_auto/model_ajaxList.do?";


    public final static String USER_NAME = "phoneAccount";// 登录时用户名 ,即手机号
    public final static String PASSWORD = "phonePassword";// 登录密码
    /*增加学员信息*/
    public final static String tableId = "tableId";// 菜单中潜在学员的tableId
    public final static String pageId = "pageId";//菜单pageId

    public final static String MAINID = "mainId";
    public final static String MAINTABLEID = "mainTableId";

    /*增加学员信息中提交连接*/
    public static String USERNAME_ALL;
    public static String PASSWORD_ALL;
    public static String PREURL = baseUrl + LOGIN_URL + USER_NAME + "=" + USERNAME_ALL;


    public static int getViewType(int fieldRole) {
        int viewType = 0;
        switch (fieldRole) {
            /*TextView: 25、26、27、28、23    0，1,2,5,8,11,12,13，18,19，，22,23,
            EditView:3 、4、6、7、9、10、24、13
            RadioButton:16、
            多选checkBox：17、
            时间 15
            日期选择框：14、
            内部对象多只 21
            未正常分配：2:富文本
                       0:主键
                       23:星期 与16、17一类
                       20:内部对象 22:父级引用
            */
            case 2://2:富文本
            case 3:
            case 4:
            case 6:
            case 7:
            case 9:
            case 10:
            case 13:
            case 20://20:内部对象
            case 22://22:父级引用
            case 24:
                viewType = 2;
                break;
            //0，1,2,5,8,11,12,13，18,19，，22,23,
            case 0://0:主键
            case 1:
            case 5:
            case 8:
            case 11:
            case 12:
            case 25:

            case 27:

                viewType = 1;
                break;
            case 14://日期
            case 26://创建日期
            case 28://修改日期
                viewType = 6;
                break;
            case 15://时间
                viewType = 7;
                break;
            case 16://16:单值选择项
            case 23://23:星期
                viewType = 3;
                break;
            case 17://17:多值选择项
                viewType = 4;
                break;
//            case 20:
            case 21://21:内部对象多值
                viewType = 5;
                break;
            case 18://18:单文档
            case 19://19:多文档
                viewType = 1;
                break;
            default:
                break;
        }
        return viewType;
    }

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
}
