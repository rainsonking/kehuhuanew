package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class RowsReadActivity extends AppCompatActivity {

    @Bind(R.id.only_read_back_info)
    ImageView onlyReadBackInfo;
    @Bind(R.id.topBar)
    RelativeLayout topBar;
    @Bind(R.id.lv_only_read)
    ListView lvOnlyRead;
    @Bind(R.id.only_read_title)
    TextView onlyReadTitle;

    private Map<String, String> paramsMap = new HashMap<>();
    private SimpleAdapter adapter;


    Map<String, Object> defaultValArr = new HashMap<>();
    List<Map<String, Object>> fieldSet = new ArrayList<>();
    String tableId, pageId, dataId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_only_read);
        ButterKnife.bind(this);


        getInfoData();
        requestRead();
    }


    public void getInfoData() {
        topBar.setBackgroundColor(getResources().getColor(topBarColor));
        Intent mIntent = this.getIntent();
        String infoData = mIntent.getStringExtra("editSet");
        Map<String, Object> editData = JSON.parseObject(infoData,
                new TypeReference<Map<String, Object>>() {
                });
        tableId = String.valueOf(editData.get("tableId"));
        pageId = String.valueOf(editData.get("startTurnPage"));
        String buttonName = String.valueOf(editData.get("buttonName"));
        dataId = String.valueOf(editData.get("dataId"));
        onlyReadTitle.setText(buttonName);

        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainId, dataId);
        paramsMap.put(Constant.mainTableId, Constant.mainTableIdValue);
        paramsMap.put(Constant.mainPageId, Constant.mainPageIdValue);
    }

    @OnClick(R.id.only_read_back_info)
    public void onClick() {
    }


    private void requestRead() {
        final String volleyUrl = Constant.sysUrl + Constant.requestEdit;
        Log.e("TAG", "修改页面Url " + volleyUrl);
        Log.e("TAG", "修改页面paramsMap：" + paramsMap);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获取只读数据" + jsonData);
                        setStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(RowsReadActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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
        Map<String, Object> editSet = JSON.parseObject(jsonData);
        Log.e("TAG", "解析修改按钮数据" + editSet);


        Map<String, Object> pageSet = (Map<String, Object>) editSet.get("pageSet");
        fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");


        Map<String, Object> defaultValArrMap = (Map<String, Object>) editSet.get("dataInfo");
        Log.e("TAG", "获取默认值dataInfo");
        String keyList = tableId + "_" + pageId + "_0_toUpdateSql_" + dataId;
        if (defaultValArrMap.get(keyList) != null) {
            List<Map<String, Object>> defaultValArrList = (List<Map<String, Object>>) defaultValArrMap.get(keyList);
            if (defaultValArrList.size() > 0) {
                defaultValArr = defaultValArrList.get(0);
            }
        }
//展示数据

        List<Map<String, String>> onlyReadData = new ArrayList<>();
        if (fieldSet != null && fieldSet.size() > 0) {
            Map<String, String> onlyReadDataMap = new HashMap<>();
            for (int i = 0; i < fieldSet.size(); i++) {
                int fieldRole = Integer.valueOf(String.valueOf(fieldSet.get(i).get("fieldRole")));
                if (fieldRole != 21) {
                    onlyReadDataMap.put("key", String.valueOf(fieldSet.get(i).get("fieldCnName")));
                    String fieldAliasName = String.valueOf(fieldSet.get(i).get("fieldAliasName"));
                }
            }


//            editAdapter = new RowsEditActivity.EditAdapter();
//            lvEditItem.setAdapter(editAdapter);
        } else {
            Log.e("TAG", "无内容");
        }

    }


}
