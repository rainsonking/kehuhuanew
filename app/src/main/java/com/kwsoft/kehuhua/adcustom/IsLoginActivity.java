package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.login.LoginActivity;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

import static android.content.ContentValues.TAG;

public class IsLoginActivity extends Activity {
    private DiskLruCacheHelper DLCH;
    private Map<String, String> paramsMap=new HashMap<>();
    private String paramsString;
    private String loginUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_login);

        ImageView welcomeImage= (ImageView) findViewById(R.id.welcomeImage);
        welcomeImage.setImageResource(R.drawable.welcomepage);
        welcomeImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Log.e("TAG","已进入主项目");
        CloseActivityClass.activityList.add(this);
        try {
            DLCH=new DiskLruCacheHelper(IsLoginActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //取出系统保存的最后一次登陆的账号登陆

        SharedPreferences sPreferences = getSharedPreferences(Constant.proId, MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sPreferences.getAll();
        int i = map.size()/2;
        if (i > 0) {//如果存在账户
            //取出用户名和密码并直接跳转至登录页面
            String userName = sPreferences.getString("name" + (i - 1), "");
            String passWord = sPreferences.getString("pwd" + (i - 1), "");
            Constant.USERNAME_ALL = userName;
            Constant.PASSWORD_ALL = passWord;
            paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);
            paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
            paramsMap.put(Constant.sourceName, Constant.sourceInt);
            paramsMap.put(Constant.timeName, Constant.menuTime);
            paramsString=paramsMap.toString();
           postLogin();//直接下载数据
        } else {//否则转到登录界面
            toLoginPage();
        }
    }


    public void postLogin() {
        loginUrl=Constant.sysUrl+Constant.projectLoginUrl;
        Log.e("TAG","loginUrl"+loginUrl);
        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);
        paramsMap.put(Constant.PASSWORD,  Constant.PASSWORD_ALL);
        paramsMap.put(Constant.proIdName, Constant.proId);
        paramsMap.put(Constant.timeName, Constant.menuTime);
        paramsMap.put(Constant.sourceName, Constant.sourceInt);
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(loginUrl)
                .build()
                .execute(new EdusStringCallback(IsLoginActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
//                        mainPage(diskData);
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        check(response);//除非用户名密码输错，否则不会到这里
                    }
                });
    }

    private void check(String menuData) {
        //获取error的值，判断
        LoginError loginError = JSON.parseObject(menuData, LoginError.class);
        if (loginError.getError() != 0) {

            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            toLoginPage();
        } else {//当成功登陆后存储：：正确的用户名和密码
            DLCH.put(Constant.sysUrl+Constant.projectLoginUrl+ Constant.USERNAME_ALL + Constant.proId,menuData);
            getLoginName(menuData);


            mainPage(menuData);
        }


    }
    public void getLoginName(String menuData) {

        Map<String, Object> menuMap = JSON.parseObject(menuData, Map.class);
        if (menuMap.get("loginInfo") != null) {
            Map<String, Object> loginInfo = (Map<String, Object>) menuMap.get("loginInfo");
            if (loginInfo.get("USERNAME") != null) {
                Log.e("TAG", "USERNAME" + loginInfo.get("USERNAME"));
                Constant.loginName = (String) loginInfo.get("USERNAME");
                Constant.USERID=String.valueOf(loginInfo.get("USERID"));
                Constant.menuTime=String.valueOf(menuMap.get("menuTime"));
            }
        }
    }







    private void mainPage(String menuStr) {
        Intent intent = new Intent();
        intent.setClass(IsLoginActivity.this, NavActivity.class);

        intent.putExtra("menu", menuStr);
        startActivity(intent);
        finish();
    }

    private void toLoginPage() {
        Intent intent = new Intent();
        intent.setClass(IsLoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
