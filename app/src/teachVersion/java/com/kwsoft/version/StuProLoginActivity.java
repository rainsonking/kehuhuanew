package com.kwsoft.version;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class StuProLoginActivity extends BaseActivity {
    private SharedPreferences sPreferences;
    private String nameValue, pwdValue;


    static {
        //教师端设置成顶栏黑灰色
        Constant.topBarColor = R.color.prim_topBarColor;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_pro_login);
        initJudgeSave();
    }

    @Override
    public void initView() {

    }


    /**
     * 初始化判断sharePreference
     */
    @SuppressWarnings("unchecked")
    private void initJudgeSave() {
        Constant.proId = StuPra.studentProId;
        sPreferences = getSharedPreferences(Constant.proId, MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sPreferences.getAll();
        int k = map.size();
        if (k > 0) {//如果存在账户
            //取出用户名和密码并直接跳转至登录页面
            nameValue = sPreferences.getString("name", "");
            pwdValue = sPreferences.getString("pwd", "");

                postLogin();


        }else{
            toLoginPage();
        }
    }

    private static final String TAG = "StuProLoginActivity";

    /**
     * 根据用户输入的用户名和密码，
     * 通过网络地址获取JSON数据，
     * 返回后直接传递给主页面
     **/
    public void postLogin() {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            toLoginPage();
        } else {
            if (!nameValue.equals("") && !pwdValue.equals("")) {//判断用户名密码非空
                final String volleyUrl = Constant.sysUrl + Constant.projectLoginUrl;
                Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.projectLoginUrl);


                //参数
                Map<String, String> map = new HashMap<>();
                map.put(Constant.USER_NAME, nameValue);
                map.put(Constant.PASSWORD, pwdValue);
                map.put(Constant.proIdName, Constant.proId);
                map.put(Constant.timeName, Constant.menuAlterTime);
                map.put(Constant.sourceName, Constant.sourceInt);
                //请求
                OkHttpUtils
                        .post()
                        .params(map)
                        .url(volleyUrl)
                        .build()
                        .execute(new EdusStringCallback(mContext) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ErrorToast.errorToast(mContext,e);
                                dialog.dismiss();
                                ErrorToast.errorToast(mContext,e);
                                toLoginPage();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, "onResponse: "+response+"  id  "+id);
                                check(response);
                            }
                        });
            } else {

                toLoginPage();
            }
        }
    }

    //解析获得的data数据中的error值，如果它为1
    // 则提示用户名密码输入问题，sp中并不存储
    // 新密码，为0则跳转，sp存储新密码

    private void check(String menuData) {
        if (menuData != null) {
            //获取error的值，判断
            LoginError loginError = JSON.parseObject(menuData, LoginError.class);
            if (loginError.getError() != 0) {
                Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT).show();
                toLoginPage();

                finish();
            } else {
                //当成功登陆后存储正确的用户名和密码,
                Constant.USERNAME_ALL = nameValue;
                Constant.PASSWORD_ALL = pwdValue;
                //跳转至主页面并传递菜单数据
                getLoginName(menuData);

                mainPage(menuData);//保存完用户名和密码，跳转到主页面
            }
        } else {

            Toast.makeText(StuProLoginActivity.this, "服务器超时", Toast.LENGTH_SHORT).show();
        }
    }

    private void toLoginPage() {
        Intent intent = new Intent();
        intent.setClass(StuProLoginActivity.this, StuLoginActivity.class);
        startActivity(intent);
        this.finish();
    }

    //此方法传递菜单JSON数据
    @SuppressWarnings("unchecked")
    private void mainPage(String menuData) {
        try {
            Map<String, Object> menuMap = JSON.parseObject(menuData,
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> loginfo = (Map<String, Object>) menuMap.get("loginInfo");
            String userid = String.valueOf(loginfo.get("USERID"));
            Constant.USERID = String.valueOf(loginfo.get("USERID"));
            sPreferences.edit().putString("userid", userid).apply();
            List<Map<String, Object>> menuListMap1 = (List<Map<String, Object>>) menuMap.get("roleFollowList");
            List<Map<String, Object>> menuListMap2 = (List<Map<String, Object>>) menuMap.get("menuList");
            List<Map<String, Object>> menuListMap3 = (List<Map<String, Object>>) menuMap.get("hideMenuList");


            Intent intent = new Intent();
            intent.setClass(StuProLoginActivity.this, StuMainActivity.class);
            intent.putExtra("jsonArray", JSON.toJSONString(menuListMap1));
            intent.putExtra("menuDataMap", JSON.toJSONString(menuListMap2));
            intent.putExtra("hideMenuList", JSON.toJSONString(menuListMap3));
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

//    void startAnim() {
//        findViewById(R.id.avloadingIndicatorViewLayout).setVisibility(View.VISIBLE);
//    }
//
//    void stopAnim() {
//        findViewById(R.id.avloadingIndicatorViewLayout).setVisibility(View.GONE);
//    }


    //获得用户名方法
    @SuppressWarnings("unchecked")
    public void getLoginName(String menuData) {

        Map<String, Object> menuMap = JSON.parseObject(menuData,
                new TypeReference<Map<String, Object>>() {
                });
        String countStr = String.valueOf(menuMap.get("notMsgCount"));
        if (!TextUtils.isEmpty(countStr) && !countStr.equals("null")) {
            int count = Integer.parseInt(countStr);
            getLoginUserSharedPre().edit().putInt("count", count).apply();
            BadgeUtil.sendBadgeNumber(StuProLoginActivity.this, count);
        } else {
            BadgeUtil.sendBadgeNumber(StuProLoginActivity.this, 0);
        }
        if (menuMap.get("loginInfo") != null) {
            try {
                Map<String, Object> loginInfo = (Map<String, Object>) menuMap.get("loginInfo");
                if (loginInfo.get("USERNAME") != null) {
                    Log.e("TAG", "USERNAME" + loginInfo.get("USERNAME"));
                    Constant.loginName = String.valueOf(loginInfo.get("USERNAME"));
                    Toast.makeText(StuProLoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Constant.USERID = String.valueOf(loginInfo.get("USERID"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
