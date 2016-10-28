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
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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

    private static final String TAG = "RowsReadActivity";


    private void requestRead() {
        final String volleyUrl = Constant.sysUrl + Constant.requestEdit;
        Log.e("TAG", "修改页面Url " + volleyUrl);
        Log.e("TAG", "修改页面paramsMap：" + paramsMap);

        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(RowsReadActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
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
