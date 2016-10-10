package com.kwsoft.version;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A login screen that offers login via email/password.
 */
public class StuLoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText mUserName;
    private EditText mPassword;
    private String nameValue, pwdValue;
    private SharedPreferences sPreferences;
    private LinearLayout layout_enabled;
    private Button login;
    private ImageView iv_phone_clear;
    private ImageView iv_password_clear;
    private RelativeLayout avloadingIndicatorViewLayout;
    static {
        //学员端设置成顶栏红色
        Constant.topBarColor = R.color.stu_topBarColor;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_login_sec);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initJudgeSave();
        initView();
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
        }
    }

    @SuppressWarnings("unchecked")
    public void initView() {
        avloadingIndicatorViewLayout= (RelativeLayout) findViewById(R.id.avloadingIndicatorViewLayout);
        avloadingIndicatorViewLayout.setOnClickListener(null);
        mUserName = (EditText) findViewById(R.id.ed_userName);
        mPassword = (EditText) findViewById(R.id.ed_passWord);
        layout_enabled = (LinearLayout) findViewById(R.id.layout_enabled);
        login = (Button) findViewById(R.id.btn_login);
        iv_phone_clear = (ImageView) findViewById(R.id.iv_phone_clear);
        iv_password_clear = (ImageView) findViewById(R.id.iv_password_clear);
        CheckBox cb_rmb_pwd = (CheckBox) findViewById(R.id.check_box);

        login.setOnClickListener(this);
        iv_phone_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName.setText("");
            }
        });
        iv_password_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword.setText("");
            }
        });
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    iv_phone_clear.setVisibility(View.INVISIBLE);
                } else {
                    iv_phone_clear.setVisibility(View.VISIBLE);
                }

                if (s.length() > 0 && mPassword.getText().length() > 0) {
                    login.setVisibility(View.VISIBLE);
                    layout_enabled.setVisibility(View.GONE);
                } else {
                    login.setVisibility(View.GONE);
                    layout_enabled.setVisibility(View.VISIBLE);
                }
            }
        });
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    iv_password_clear.setVisibility(View.INVISIBLE);
                } else {
                    iv_password_clear.setVisibility(View.VISIBLE);
                }

                if (s.length() > 0 && mUserName.getText().length() > 0) {
                    login.setVisibility(View.VISIBLE);
                    layout_enabled.setVisibility(View.GONE);
                } else {
                    login.setVisibility(View.GONE);
                    layout_enabled.setVisibility(View.VISIBLE);
                }
            }
        });
        //初始化EditText、checkbox
        if (!TextUtils.isEmpty(nameValue)) {
            mUserName.setText(nameValue);
        }
        if (!TextUtils.isEmpty(pwdValue)) {
            mPassword.setText(pwdValue);
            cb_rmb_pwd.setChecked(true);
        } else {
            cb_rmb_pwd.setChecked(false);
        }

    }


    //按钮事件控制
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                postLogin();
                break;
            default:
                break;
        }
    }


    /**
     * 根据用户输入的用户名和密码，
     * 通过网络地址获取JSON数据，
     * 返回后直接传递给主页面
     **/
    public void postLogin() {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressDialog proDia = new ProgressDialog(StuLoginActivity.this);
//            proDia.setTitle("正在登陆。。。");
//            proDia.show();
            startAnim();
            nameValue = mUserName.getText().toString();//trim去掉首尾空格
            pwdValue = mPassword.getText().toString();
            if (!nameValue.equals("") && !pwdValue.equals("")) {//判断用户名密码非空
                final String volleyUrl = Constant.sysUrl + Constant.projectLoginUrl;
                Log.e("TAG", "学员端登陆地址 " + Constant.sysUrl + Constant.projectLoginUrl);
                StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String menuData) {
                                //      stopAnim();
                                check(menuData, proDia);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        VolleySingleton.onErrorResponseMessege(StuLoginActivity.this, volleyError);
                        //  stopAnim();
                        proDia.dismiss();
                    }
                }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put(Constant.USER_NAME, nameValue);
                        map.put(Constant.PASSWORD, pwdValue);
                        map.put(Constant.proIdName, Constant.proId);
                        map.put(Constant.timeName, Constant.menuAlterTime);
                        map.put(Constant.sourceName, Constant.sourceInt);
                        Log.e("TAG", "学员端登陆参数 " + map.toString());
                        return map;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        Response<String> superResponse = super.parseNetworkResponse(response);
                        Map<String, String> responseHeaders = response.headers;
                        String rawCookies = responseHeaders.get("Set-Cookie");
                        //xiebubiao修改
                        SharedPreferences.Editor editor = getLoginUserSharedPre().edit();
                        editor.putString("Cookie", rawCookies);
                        editor.apply();
                        //Constant是一个自建的类，存储常用的全局变量
//                    Constant.localCookie = rawCookies.substring(0, rawCookies.indexOf(";"));
                        Constant.localCookie = rawCookies;
                        return superResponse;
                    }
                };
                VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
            } else {
                stopAnim();
                Toast.makeText(StuLoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //解析获得的data数据中的error值，如果它为1
    // 则提示用户名密码输入问题，sp中并不存储
    // 新密码，为0则跳转，sp存储新密码

    private void check(String menuData, ProgressDialog proDia) {
        if (menuData != null) {
            //获取error的值，判断
            LoginError loginError = JSON.parseObject(menuData, LoginError.class);
            if (loginError.getError() != 0) {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                stopAnim();
            } else {
                //当成功登陆后存储正确的用户名和密码,
                Constant.USERNAME_ALL = nameValue;
                Constant.PASSWORD_ALL = pwdValue;
                //跳转至主页面并传递菜单数据
                getLoginName(menuData);
//                if (cb_rmb_pwd.isChecked()) {
//                    sPreferences.edit().putString("pwd", pwdValue).apply();
//                } else {
//                    sPreferences.edit().putString("pwd", "").apply();
//                    mPassword.setText("");
//                }
                sPreferences.edit().putString("name", nameValue).apply();
                sPreferences.edit().putString("pwd", pwdValue).apply();
                mainPage(menuData, proDia);//保存完用户名和密码，跳转到主页面
            }
        } else {
            stopAnim();
            Toast.makeText(StuLoginActivity.this, "服务器超时", Toast.LENGTH_SHORT).show();
        }
    }

    //此方法传递菜单JSON数据
    @SuppressWarnings("unchecked")
    private void mainPage(String menuData, ProgressDialog proDia) {
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

            Intent intent = new Intent();
            intent.setClass(StuLoginActivity.this, StuMainActivity.class);
            intent.putExtra("jsonArray", JSON.toJSONString(menuListMap1));
            intent.putExtra("menuDataMap", JSON.toJSONString(menuListMap2));
            startActivity(intent);
            finish();
            proDia.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            proDia.dismiss();
        }
    }

    void startAnim() {

        avloadingIndicatorViewLayout.setVisibility(View.VISIBLE);

    }

    void stopAnim() {
        avloadingIndicatorViewLayout.setVisibility(View.GONE);
    }

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
            BadgeUtil.sendBadgeNumber(StuLoginActivity.this, count);
        } else {
            BadgeUtil.sendBadgeNumber(StuLoginActivity.this, 0);
        }
        if (menuMap.get("loginInfo") != null) {
            try {
                Map<String, Object> loginInfo = (Map<String, Object>) menuMap.get("loginInfo");
                if (loginInfo.get("USERNAME") != null) {
                    Log.e("TAG", "USERNAME" + loginInfo.get("USERNAME"));
                    Constant.loginName = String.valueOf(loginInfo.get("USERNAME"));
                    Toast.makeText(StuLoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Constant.USERID = String.valueOf(loginInfo.get("USERID"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

