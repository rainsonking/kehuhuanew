package com.kwsoft.kehuhua.adcustom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.adcustom.config.Constant;
import com.kwsoft.kehuhua.adcustom.config.Url;
import com.kwsoft.kehuhua.adcustom.utils.BadgeUtil;
import com.kwsoft.kehuhua.adcustom.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/13 0013.
 * <p/>
 * 登陆界面
 */
public class LoginActivity extends BaseActivity {

    //二维码
    private EditText edt_userName;
    private EditText edt_passWord;
    private LinearLayout layout_enabled;
    ImageView iv_phone_clear;
    ImageView iv_password_clear;
    String userName;
    String passWord;
    CheckBox cb_rmb_pwd;
    final int ERCODE = 0;//二维码
    final int LOGIN = 1;//登录
    String loginUserUrl = "";
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        userName = getLoginUserSharedPre().getString("studentPhone", null);
        passWord = getLoginUserSharedPre().getString("stuPassword", null);
        initView();
    }

    @Override
    public void initView() {
        edt_userName = (EditText) findViewById(R.id.ed_userName);
        edt_passWord = (EditText) findViewById(R.id.ed_passWord);
        layout_enabled = (LinearLayout) findViewById(R.id.layout_enabled);
        iv_phone_clear = (ImageView) findViewById(R.id.iv_phone_clear);
        iv_password_clear = (ImageView) findViewById(R.id.iv_password_clear);
        iv_phone_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_userName.setText("");
            }
        });
        iv_password_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_passWord.setText("");
            }
        });
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_userName.length() == 0) {
                    Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                } else if (edt_passWord.length() == 0) {
                    Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    loginUserUrl = Url.baseUrl + Url.loginUrl;
                    requestLoginData(loginUserUrl);
                }
            }
        });

        edt_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==0){
                    iv_phone_clear.setVisibility(View.INVISIBLE);
                }else {
                    iv_phone_clear.setVisibility(View.VISIBLE);
                }

                if (s.length() > 0 && edt_passWord.getText().length() > 0) {
                    login.setVisibility(View.VISIBLE);
                    layout_enabled.setVisibility(View.GONE);
                } else {
                    login.setVisibility(View.GONE);
                    layout_enabled.setVisibility(View.VISIBLE);
                }
            }
        });

        edt_passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==0){
                    iv_password_clear.setVisibility(View.INVISIBLE);
                }else {
                    iv_password_clear.setVisibility(View.VISIBLE);
                }

                if (s.length() > 0 && edt_userName.getText().length() > 0) {
                    login.setVisibility(View.VISIBLE);
                    layout_enabled.setVisibility(View.GONE);
                } else {
                    login.setVisibility(View.GONE);
                    layout_enabled.setVisibility(View.VISIBLE);
                }
            }
        });
        edt_passWord = (EditText) findViewById(R.id.ed_passWord);
        cb_rmb_pwd = (CheckBox) findViewById(R.id.check_box);
        if (!TextUtils.isEmpty(userName)) {
            edt_userName.setText(userName);
        }
        if (!TextUtils.isEmpty(passWord)) {
            edt_passWord.setText(passWord);
            cb_rmb_pwd.setChecked(true);
        } else {
            cb_rmb_pwd.setChecked(false);
        }
    }


    private void requestLoginData(String url) {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        getProgressDialog().show();
//        RequestCall call = OkHttpUtils.getInstance().post().url(url).addParams("loginName", edt_userName.getText().toString().trim()).addParams("password", edt_passWord.getText().toString().trim()).build();
//        call.execute(new Callback() {
//            @Override
//            public Object parseNetworkResponse(Response response) throws Exception {
//                return response.body().string();
//            }
//
//            @Override
//            public void onError(Call call, Exception e) {
//                getProgressDialog().dismiss();
//                Toast.makeText(LoginActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                getProgressDialog().dismiss();
//                try {
//                    loginSuccess(response.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        getProgressDialog().dismiss();
                        try {
                            loginSuccess(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                getProgressDialog().dismiss();
                Toast.makeText(LoginActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
                VolleySingleton.onErrorResponseMessege(LoginActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("loginName", edt_userName.getText().toString().trim());
                map.put("password", edt_passWord.getText().toString().trim());
                return map;
            }

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                com.android.volley.Response<String> superResponse = super.parseNetworkResponse(response);
                Map<String, String> responseHeaders = response.headers;
                String rawCookies = responseHeaders.get("Set-Cookie");
                //Constant是一个自建的类，存储常用的全局变量
//                String localCookie = rawCookies.substring(rawCookies.indexOf("=")+1, rawCookies.indexOf(";"));
                Log.e("123", "Cookie=" + rawCookies);
                SharedPreferences.Editor editor = getLoginUserSharedPre().edit();
                editor.putString("Cookie",rawCookies);
                editor.commit();
//                Constant.cookie=rawCookies;
                return superResponse;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    //登录成功处理返回结果
    private void loginSuccess(String s) throws JSONException {
        JSONObject object = new JSONObject(s);
        String error = object.getString("error");
        Log.i("123",s);
        if ("0".equals(error)) {
            org.json.JSONArray array = object.getJSONArray("menuList");
            JSONObject userObject = object.getJSONObject("loginInfo");
            //  Toast.makeText(LoginActivity.this, "登录成功!",Toast.LENGTH_SHORT ).show();

            SharedPreferences.Editor editor = getLoginUserSharedPre().edit();
            if (cb_rmb_pwd.isChecked()) {
                editor.putString("stuPassword", edt_passWord.getText().toString());
                editor.commit();
            } else {
                editor.putString("stuPassword", "");
                edt_passWord.setText("");
                editor.commit();
            }
            editor.putString("studentPhone", edt_userName.getText().toString().trim());
            editor.putString("changePassword", edt_passWord.getText().toString().trim());
            editor.putString("USERID",userObject.getString("USERID"));
            String countStr=String.valueOf(object.getInt("notMsgCount"));
            if (!TextUtils.isEmpty(countStr) && !countStr.equals("null")) {
                int count = Integer.parseInt(countStr);
                editor.putInt("count",count);
                BadgeUtil.sendBadgeNumber(LoginActivity.this, count);
            } else {
                BadgeUtil.sendBadgeNumber(LoginActivity.this, 0);
            }
            // editor.putString("sessionId",userObject.getString("sessionId"));
            editor.commit();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            intent.putExtra("array", array.toString());
//            intent.putExtra("userObject", userObject.toString());
//            intent.putExtra("USERID",userObject.getString("USERID"));
//            intent.putExtra("USERNAME",userObject.getString("USERNAME"));
//            intent.putExtra("STU_SEX",userObject.getString("STU_SEX"));
            startActivity(intent);
            getProgressDialog().dismiss();
        } else {
            String errorMsg = object.getString("errorMsg");
            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            getProgressDialog().dismiss();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public ProgressDialog getProgressDialog() {

        return super.getProgressDialog();
    }
}

