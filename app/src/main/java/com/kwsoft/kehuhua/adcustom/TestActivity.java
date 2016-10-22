package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Url;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class TestActivity extends BaseActivity {
    private TextView tv_title,tv_content,tv_date,tv_persion;
    private String mainId,if_seeCn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_content=(TextView)findViewById(R.id.tv_content);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_persion=(TextView)findViewById(R.id.tv_persion);
        Intent intent = getIntent();
        if (null != intent) {
	        Bundle bundle = getIntent().getExtras();
	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
            String extras=bundle.getString(JPushInterface.EXTRA_EXTRA);
            Map<String,Object> map=JSON.parseObject(extras, Map.class);
            if (map != null) {
                String mainIdStr = String.valueOf(map.get("mainId"));
                String createDate = String.valueOf(map.get("createDate"));
                String notice_name = String.valueOf(map.get("notice_name"));
                String titleName = String.valueOf(map.get("titleName"));
                if (!TextUtils.isEmpty(mainIdStr)&&!mainIdStr.equals("null")) {
                    mainId = mainIdStr;
                }

                if (!TextUtils.isEmpty(createDate)&&!createDate.equals("null")) {
                    tv_date.setText(createDate);
                }

                if (!TextUtils.isEmpty(notice_name)&&!notice_name.equals("null")) {
                    tv_persion.setText(notice_name);
                }

                if (!TextUtils.isEmpty(titleName)&&!titleName.equals("null")) {
                    tv_title.setText(titleName);
                }
            } else {
                mainId = bundle.getString("mainId");
                String createDate =bundle.getString("dateStr");;
                String notice_name =bundle.getString("notice_name");
                if (!TextUtils.isEmpty(createDate)&&!createDate.equals("null")) {
                    tv_date.setText(createDate);
                }

                if (!TextUtils.isEmpty(notice_name)&&!notice_name.equals("null")) {
                    tv_persion.setText(notice_name);
                }

                if (!TextUtils.isEmpty(title)&&!title.equals("null")) {
                    tv_title.setText(title);
                }
            }
//            if_seeCn=bundle.getString("if_seeCn");
	        tv_content.setText(content);
        }

        changeReadState(Url.baseUrl+Url.changeReadState);
//        if (if_seeCn!=null&&if_seeCn.equals("否")) {
//        }
    }

    private static final String TAG = "TestActivity";
    private void changeReadState(String volleyUrl) {
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
//参数
        Map<String,String> paramsMap=new HashMap<>();
        paramsMap.put("msgMainId",mainId);
        paramsMap.put("msgTabName","SYSTEM_MESSAGE");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        getProgressDialog().dismiss();
                    }
                });
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
