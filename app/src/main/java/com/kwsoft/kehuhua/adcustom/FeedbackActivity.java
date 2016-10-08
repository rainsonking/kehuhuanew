package com.kwsoft.kehuhua.adcustom;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.HashMap;
import java.util.Map;

import static com.kwsoft.kehuhua.config.Constant.pageId;
import static com.kwsoft.kehuhua.config.Constant.tableId;

public class FeedbackActivity extends AppCompatActivity {

    EditText edt_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        edt_feedback=(EditText)findViewById(R.id.edt_feedback);
        setupActionBar();
        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("意见反馈");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    public void summit(View view){



        requestAdd();







    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager!=null){
                    inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
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

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
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
        final String text=edt_feedback.getText().toString();
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加数据" + jsonData);
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
                Map<String, String> paramsMap=new HashMap<>();
                paramsMap.put(tableId, "280");
                paramsMap.put(pageId, "2575");
                paramsMap.put("t0_au_280_2575_3573", text);
                paramsMap.put("t0_au_280_2575_3571", Constant.USERID);
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
        if (jsonData.equals("1")) {

            finish();
            Toast.makeText(this, "提交成功", Toast.LENGTH_SHORT).show();



        }else{
            Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
        }
    }

}
