package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IsLoginActivity extends Activity {
    private DiskLruCacheHelper DLCH;
    private Map<String, String> paramsMap=new HashMap<>();
    private String paramsString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_login);

        ImageView welcomeImage= (ImageView) findViewById(R.id.welcomeImage);
        welcomeImage.setImageResource(R.drawable.welcomepage);
        welcomeImage.setScaleType(ImageView.ScaleType.FIT_XY);

        CloseActivityClass.activityList.add(this);
        try {
            DLCH=new DiskLruCacheHelper(IsLoginActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //取出系统保存的最后一次登陆的账号登陆

        SharedPreferences sPreferences = getSharedPreferences("session", MODE_PRIVATE);
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
            paramsString=paramsMap.toString();
           postLogin();//直接下载数据
        } else {//否则转到登录界面
            toLoginPage();
        }
    }

    public void postLogin() {
        final String volleyUrl=Constant.baseUrl+Constant.LOGIN_URL;
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST,volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String menuData) {
                        DLCH.put(volleyUrl+paramsString,menuData);
                        check(menuData);//除非用户名密码输错，否则不会到这里
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(IsLoginActivity.this, volleyError);
                   String diskData=DLCH.getAsString(volleyUrl+paramsString);
                    mainPage(diskData);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);


    }

    private void check(String menuData) {
        //获取error的值，判断
        LoginError loginError = JSON.parseObject(menuData, LoginError.class);
        if (loginError.getError() != 0) {

            Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            toLoginPage();
        } else {//当成功登陆后存储：：正确的用户名和密码
            Toast.makeText(IsLoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
            mainPage(menuData);
        }


    }

    private void mainPage(String menuStr) {
        Intent intent = new Intent();
        intent.setClass(IsLoginActivity.this, NavActivity.class);
        Log.e("TAG","menu数据"+menuStr);
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
