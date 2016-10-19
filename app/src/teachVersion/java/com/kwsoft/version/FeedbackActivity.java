package com.kwsoft.version;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwsoft.kehuhua.config.Constant.pageId;
import static com.kwsoft.kehuhua.config.Constant.tableId;

public class FeedbackActivity extends AppCompatActivity {

    EditText edt_feedback;
    public String tableIdLeft, pageIdLeft;
    public String startTurnPagebtn, tableIdbtn;//用登陆返回的获取的
    public static String contentLeft,userLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        edt_feedback = (EditText) findViewById(R.id.edt_feedback);
        setupActionBar();
        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("意见反馈");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        requestData();
    }

    public void requestData() {
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加数据" + jsonData);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
//                        setStore(jsonData);
                        requestButton(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(FeedbackActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(tableId, Constant.teachBackTABLEID);
                paramsMap.put(pageId, Constant.teachBackPAGEID);
                paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);
                paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
                Log.e("taginfo", Constant.USERNAME_ALL + "**" + Constant.PASSWORD_ALL);
                return paramsMap;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }


    public void requestButton(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONObject object1 = object.getJSONObject("pageSet");

            Map<String, Object> menuMap = JSON.parseObject(object1.toString(),
                    new TypeReference<Map<String, Object>>() {
                    });
            List<Map<String, Object>> menuListMap1 = (List<Map<String, Object>>) menuMap.get("buttonSet");
            Log.e("menuListMap1=", JSON.toJSONString(menuListMap1));
            Map<String, Object> map = menuListMap1.get(0);

            //JSONObject object2 = object1.getJSONObject("buttonSet");

//            JSONArray array = object1.getJSONArray("buttonSet");
//            JSONObject object2 = (JSONObject) array.get(0);
            startTurnPagebtn = map.get("startTurnPage") + "";
            tableIdbtn = map.get("tableId") + "";
            Log.e("tableIdbtn", startTurnPagebtn + "**" + tableIdbtn);

            requestContentLetf(startTurnPagebtn, tableIdbtn);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void requestContentLetf(final String startTurnPagebtn, final String tableIdbtn) {
        String volleyUrl = Constant.sysUrl + Constant.requestAdd;
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加数据conter" + jsonData);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
//                        setStore(jsonData);
                        requestContData(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(FeedbackActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(tableId, tableIdbtn);
                paramsMap.put(pageId, startTurnPagebtn);
                paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);
                paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
                Log.e("taginfo", Constant.USERNAME_ALL + "**" + Constant.PASSWORD_ALL);
                return paramsMap;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    private void requestContData(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONObject object1 = object.getJSONObject("pageSet");
            Map<String, Object> menuMap = JSON.parseObject(object1.toString(),
                    new TypeReference<Map<String, Object>>() {
                    });
            List<Map<String, Object>> menuListMap1 = (List<Map<String, Object>>) menuMap.get("fieldSet");
            Log.e("menuListMap1=", JSON.toJSONString(menuListMap1));
            if (menuListMap1.size() > 0) {
                for (int i = 0; i < menuListMap1.size(); i++) {
                    Map<String, Object> map = menuListMap1.get(i);
                    String cnname = map.get("fieldCnName") + "";
                    if (cnname.contains("反馈内容")) {
                        contentLeft = map.get("montageName") + "";
                    }else if (cnname.contains("用户")){
                        userLeft = map.get("montageName") + "";
                    }
                }
                Log.e("contentleft", contentLeft);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public void summit(View view) {
        requestAdd();
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

    //请求
    public void requestAdd() {
        String volleyUrl = Constant.sysUrl + Constant.commitAdd;
        final String text = edt_feedback.getText().toString();
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加数据第三方" + jsonData);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
                        setStore(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(FeedbackActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(tableId, tableIdbtn);
                paramsMap.put(pageId, startTurnPagebtn);
                paramsMap.put(contentLeft, text);
                paramsMap.put(userLeft, Constant.USERID);
                Log.e("content", tableIdbtn + "/" + Constant.USERID+"/" + contentLeft+"/" + startTurnPagebtn);
                return paramsMap;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    private void setStore(String jsonData) {
        if (jsonData.length() > 0) {

            finish();
            Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();


        } else {
            Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
        }
    }

}
