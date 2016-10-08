package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.adcustom.config.Constant;
import com.kwsoft.kehuhua.adcustom.config.Url;
import com.kwsoft.kehuhua.adcustom.utils.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class TestActivity extends BaseActivity {
    private TextView tv_title,tv_content;
    private String mainId,if_seeCn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_content=(TextView)findViewById(R.id.tv_content);
        Intent intent = getIntent();
        if (null != intent) {
	        Bundle bundle = getIntent().getExtras();
	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras=bundle.getString(JPushInterface.EXTRA_EXTRA);
            Map<String,Object> map=JSON.parseObject(extras, Map.class);
            if (map != null) {
                String mainIdStr = String.valueOf(map.get("mainId"));
                if (!TextUtils.isEmpty(mainIdStr)&&!mainIdStr.equals("null")) {
                    mainId = mainIdStr;
                }
            } else {
                mainId = bundle.getString("mainId");
            }
//            if_seeCn=bundle.getString("if_seeCn");
	        tv_title.setText(title);
	        tv_content.setText(content);
        }

        changeReadState(Url.baseUrl+ Url.changeReadState);
//        if (if_seeCn!=null&&if_seeCn.equals("否")) {
//        }
    }

    private void changeReadState(String url) {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        getProgressDialog().show();
//        RequestCall call = OkHttpUtils.getInstance().post().url(url).build();
//        call.execute(new Callback() {
//            @Override
//            public Object parseNetworkResponse(Response response) throws Exception {
//                return response.body().string();
//            }
//
//            @Override
//            public void onError(Call call, Exception e) {
//                getProgressDialog().dismiss();
//                Toast.makeText(MainActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                getProgressDialog().dismiss();
//                Log.i("123",response.toString());
//            }
//        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        getProgressDialog().dismiss();

                        Log.e("123", "单独获取的获取列表数据" + jsonData);
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //refreshListView.hideFooterView();
                getProgressDialog().dismiss();
                Log.i("123", "请求失败！");
//                Toast.makeText(TestActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
                VolleySingleton.onErrorResponseMessege(TestActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("msgMainId",mainId);
                map.put("msgTabName","SYSTEM_MESSAGE");
                return map;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String localCookie=getLoginUserSharedPre().getString("Cookie",null);
//                String localCookie= Constant.cookie;
                if (localCookie != null && localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Cookie", localCookie);
                    Log.i("123", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                stringRequest);
    }

    @Override
    public void initView() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 处理返回逻辑
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
