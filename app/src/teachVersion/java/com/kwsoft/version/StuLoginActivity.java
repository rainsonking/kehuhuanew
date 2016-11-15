package com.kwsoft.version;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.SetIpPortActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.loadDialog.LoadingDialog;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.sangbo.autoupdate.CheckVersion;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

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

    static {
        //学员端设置成顶栏红色
        Constant.topBarColor = R.color.prim_topBarColor;
        CheckVersion.checkUrl = "http://www.xxx.com/api/versiontest.txt";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_login_sec);
        CloseActivityClass.activityList.add(this);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog=new LoadingDialog(mContext,"正在登录中...");
        initJudgeSave();
        initView();
        initPermission();
    }

    private void initPermission() {
        PermissionGen.with(StuLoginActivity.this)
                .addRequestCode(188)
                .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.INTERNET
                )
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 188)
    public void doSomething() {
        Toast.makeText(this, "权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 188)
    public void doFailSomething() {
        Toast.makeText(this, "权限获取失败", Toast.LENGTH_SHORT).show();
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

                try {
                    postLogin();
                } catch (Exception e) {
                    Toast.makeText(this, "当前项目链接可能出错", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(StuLoginActivity.this, SetIpPortActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private static final String TAG = "StuLoginActivity";
    /**
     * 根据用户输入的用户名和密码，
     * 通过网络地址获取JSON数据，
     * 返回后直接传递给主页面
     **/
    public void postLogin() {
        Log.e("TAG", "学员端登陆 ");
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
        } else {
            dialog.show();
            nameValue = mUserName.getText().toString();//trim去掉首尾空格
            pwdValue = mPassword.getText().toString();
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
                Log.e("proid",Constant.proId+"//"+Constant.menuAlterTime);
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
                                Log.e(TAG, "onError: e.getClass()"+e.getClass());
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, "onResponse: "+response+"  id  "+id);
                                check(response);
                            }
                        });
                
            } else {
                dialog.dismiss();
                Toast.makeText(StuLoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                //当成功登陆后存储正确的用户名和密码,
                Constant.USERNAME_ALL = nameValue;
                Constant.PASSWORD_ALL = pwdValue;

                getLoginName(menuData);
//                if (cb_rmb_pwd.isChecked()) {
//                    sPreferences.edit().putString("pwd", pwdValue).apply();
//                } else {
//                    sPreferences.edit().putString("pwd", "").apply();
//                    mPassword.setText("");
//                }
                sPreferences.edit().putString("name", nameValue).apply();
                sPreferences.edit().putString("pwd", pwdValue).apply();


                //跳转至主页面并传递菜单数据
                mainPage(menuData);//保存完用户名和密码，跳转到主页面
            }
        } else {
            dialog.dismiss();
            Toast.makeText(StuLoginActivity.this, "服务端无数据", Toast.LENGTH_SHORT).show();
        }
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
//            List<Map<String, Object>> menuListMap3 = (List<Map<String, Object>>) menuMap.get("hideMenuList");
            List<Map<String, Object>> menuListMap3 = (List<Map<String, Object>>) menuMap.get("personInfoList");//个人资料
            List<Map<String, Object>> menuListMap5 = (List<Map<String, Object>>) menuMap.get("feedbackInfoList");//反馈信息


            List<Map<String, Object>> menuListMap4 =null;
            if (menuMap.containsKey("homePageList")){
                menuListMap4 = (List<Map<String, Object>>) menuMap.get("homePageList");
                Log.e("menuListMap3",JSON.toJSONString(menuListMap4));
            }
            Log.e("menuListMap5",JSON.toJSONString(menuListMap5));


            Intent intent = new Intent();
            intent.setClass(StuLoginActivity.this, StuMainActivity.class);
            intent.putExtra("jsonArray", JSON.toJSONString(menuListMap1));
            intent.putExtra("menuDataMap", JSON.toJSONString(menuListMap2));
            intent.putExtra("hideMenuList", JSON.toJSONString(menuListMap3));
            intent.putExtra("homePageList",JSON.toJSONString(menuListMap4));
            intent.putExtra("feedbackInfoList",JSON.toJSONString(menuListMap5));
            startActivity(intent);
            finish();
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
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
                    Constant.roleNamesTeach=String.valueOf(loginInfo.get("roleNames"));
                    Toast.makeText(StuLoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    Constant.USERID = String.valueOf(loginInfo.get("USERID"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void toSetIpPortProject(View view) {
        Intent intent = new Intent();
        intent.setClass(StuLoginActivity.this, SetIpPortActivity.class);
        startActivity(intent);

    }
}

