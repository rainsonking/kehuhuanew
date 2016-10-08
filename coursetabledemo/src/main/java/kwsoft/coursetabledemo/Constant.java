package kwsoft.coursetabledemo;

import android.app.Activity;
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
     *
     * 陈蒙地址：http://192.168.6.150:8081/edus_auto/
     *
     * 阿里服务器地址  http://182.92.108.162:8124/edus_auto/
     */
    public static String sysUrl = "http://192.168.6.150:8081/edus_auto/";
    public final static String sysLoginUrl = "login_interfaceProLogin.do";
    public final static String projectLoginUrl = "login_interfaceLogin.do";

    public final static String requestListSet = "model_interfaceToList.do";
    public final static String requestListData = "model_ajaxList.do";
    public final static String requestAdd = "add_interfaceToAdd.do";
    public final static String commitAdd = "add_interfaceAdd.do";
    public final static String requestRowsAdd = "addRelation_interfaceToAddRelationPage.do";

    public final static String requestEdit = "update_interfaceToUpdate.do";
    public final static String requestDelete = "delete_interfaceDelete.do";
    public final static String commitEdit = "update_interfaceUpdate.do";

    public final static String requestTreeDialog = "treeDialog_interfaceToList.do";
    public final static String requestMaxRule = "add_interfaceAjaxGetMaxRule.do";

    public final static String requestMessage = "mongoModel_interfaceSysMessList.do";

    public static String tmpFieldId="";

    public static Map<String, String> commitPra;

    public  static Map<String, String> paramsMapSearch=new HashMap<>();

    public final static String proIdName = "proId";

    public static String loginName = "";
    public static String menuData="";
    public static String proId = "";
    public static String proName = "";

    public static String timeName = "alterTime";
    public static String menuTime = "";

    public final static String sourceName = "source";
    public final static String sourceInt = "1";


    public final static String USER_NAME = "loginName";// 登录时用户名 ,即手机号
    public final static String PASSWORD = "password";// 登录密码
    public final static String loginUserId = "loginUserId";
    public final static String delIds = "delIds";
    public static String USERID;
    public static int menuIsAlter=0;
    public static String menuAlterTime="";

    public static String localCookie1="";
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
    public static int jumpNum=0;//1、修改  2、添加 3、关联添加
    public static int jumpNum1=0;//

    public static String relationFieldId;
    public static int relationField;

    public static List<Map<String, Object>> fieldSetTemp = new ArrayList<>();
    public static String fieldSetStr="";
    public static List<Integer> idArrList;
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
