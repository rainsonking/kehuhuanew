package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kwsoft.kehuhua.adapter.MultiValueAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.Utils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class MultiValueActivity extends BaseActivity {

    Map<String, Object> dataMap;
    Map<String, String> paramsMap = new HashMap<>();
    String paramsStr, tableId, pageId;
    long dataTime;


    @Bind(R.id.lv_multi_value)
    PullToRefreshListView lvMultiValue;
    @Bind(R.id.back_add)
    ImageView backAdd;
    @Bind(R.id.add_list_commint)
    ImageView addListCommint;
    @Bind(R.id.textViewTitle)
    TextView textViewTitle;
    private String searchSet;
    private List<Map<String, Object>> fieldSet;
    private List<Map<String, Object>> dataList;
    private List<List<Map<String, Object>>> setAndData = new ArrayList<>();
    private MultiValueAdapter adapter;
    private List<String> idArrList = new ArrayList<>();
    private String isMulti;
    private String position;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_value);
        ButterKnife.bind(this);
//        try {
//            DLCH = new DiskLruCacheHelper(MultiValueActivity.this);
//        } catch (IOException e) {
//            e.printStackTrace();
        //       }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        dialog.show();
        getIntentData();
        requestData();
        lvMultiValue.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map<String, Object>> itemData = setAndData.get(position);
                toItem(itemData);
            }
        });
    }

    @Override
    public void initView() {

    }

    public void toItem(List<Map<String, Object>> itemData) {
        String itemDataStr = JSON.toJSONString(itemData);
        Intent intent = new Intent();
        intent.setClass(MultiValueActivity.this, MultiValueItemActivity.class);
        intent.putExtra("itemDataStr", itemDataStr);

        startActivity(intent);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        String multiValueData = intent.getStringExtra("multiValueData");
        dataMap = JSON.parseObject(multiValueData,
                new TypeReference<Map<String, Object>>() {
                });

        tableId = String.valueOf(dataMap.get("tableId"));
        pageId = String.valueOf(dataMap.get("pageDialog"));
        String idArrStr = intent.getStringExtra("idArrs");
        isMulti = String.valueOf(intent.getStringExtra("isMulti"));
        Log.e("TAG", "position " + intent.getStringExtra("position"));
        position = String.valueOf(intent.getStringExtra("position"));

        String viewName = intent.getStringExtra("viewName");

        textViewTitle.setText(viewName);


        String needFilterListStr = String.valueOf(intent.getStringExtra("needFilterListStr"));

        List<Map<String, String>> needFilterList = JSON.parseObject(needFilterListStr,
                new TypeReference<List<Map<String, String>>>() {
                });


        Log.e("TAG", "传递到普通多选activity中多值的Id" + idArrStr);

        try {
            String[] idArr = idArrStr.split(",");
            Collections.addAll(idArrList, idArr);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.e("TAG", "传递到多选activity中多值后转换的Id" + idArrList);

        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainTableId, "");
        paramsMap.put(Constant.mainPageId, "");
        paramsMap.put(Constant.mainId, "");

        if (needFilterList != null && needFilterList.size() > 0) {
            for (int i = 0; i < needFilterList.size(); i++) {
                paramsMap.putAll(needFilterList.get(i));
            }
        }
        paramsStr = JSON.toJSONString(paramsMap);

    }

    private static final String TAG = "MultiValueActivity";
    
    public void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;

        Log.e("TAG", "网络获取内多dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取内多table " + paramsMap.toString());

        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(MultiValueActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        dialog.dismiss();
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
            //获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
//时间戳
            if (setMap.get("alterTime") != null) {
                dataTime = Utils.ObjectTOLong(setMap.get("alterTime"));


            }
//搜索数据
            if (pageSet.get("serachSet") != null) {
                try {
                    List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
                    searchSet = JSONArray.toJSONString(searchSetList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
//数据左侧配置数据
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
//获取dataList

            dataList = (List<Map<String, Object>>) setMap.get("dataList");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dataList != null) {//&& dataList.size() > 0
            unionAnalysis(dataList);
        } else {
            dialog.dismiss();
            Toast.makeText(MultiValueActivity.this, "无数据",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void unionAnalysis(List<Map<String, Object>> dataList) {
        if (fieldSet != null && fieldSet.size() > 0 && dataList.size() > 0) {
            for (int i = 0; i < dataList.size(); i++) {
                List<Map<String, Object>> itemNum = new ArrayList<>();
                for (int j = 0; j < fieldSet.size(); j++) {
                    Map<String, Object> property = new HashMap<>();
                    if (j == 0) {

                        String mainId = "T_" + tableId + "_0";
                        String mainIdValue = "";
                        if (dataList.get(i).get(mainId) != null) {
                            mainIdValue = String.valueOf(dataList.get(i).get(mainId));
                        }
                        property.put("mainId", mainIdValue);
                        property.put("tableId", tableId);

                        if (idArrList.contains(mainIdValue)) {
                            property.put("isCheck", true);
                        } else {
                            property.put("isCheck", false);
                        }
                    }
                    property.put("fieldCnName", fieldSet.get(j).get("fieldCnName"));
                    String fieldAliasName = String.valueOf(fieldSet.get(j).get("fieldAliasName"));
                    if (dataList.get(i).get(fieldAliasName) != null) {
                        property.put("fieldCnName2", dataList.get(i).get(fieldAliasName));
                    } else {
                        property.put("fieldCnName2", "");
                    }
                    itemNum.add(property);
                }
                setAndData.add(itemNum);
            }
            //DLCH.put(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL+paramsStr,  JSON.toJSONString(setAndData));
            //放到adapter中展示
            toAdapter();
        } else {
            dialog.dismiss();
            Toast.makeText(this, "列表中无数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void toAdapter() {
        adapter = new MultiValueAdapter(this, setAndData, isMulti);
        lvMultiValue.setAdapter(adapter);
        dialog.dismiss();

    }


    private void jump2Activity() {
        Map<String, Object> map = new HashMap<>();
        String ids = "";
        String names = "";
        int num = 0;
        for (int i = 0; i < setAndData.size(); i++) {

            if (Boolean.valueOf(String.valueOf(setAndData.get(i).get(0).get("isCheck")))) {
                ids += String.valueOf(setAndData.get(i).get(0).get("mainId")) + ",";
                names += String.valueOf(setAndData.get(i).get(0).get("fieldCnName2")) + " ";
                num++;
            }
        }
        map.put("num", num);
        map.put("isMulti", isMulti);
        map.put("position", position);
        if (!ids.equals("")) {
            map.put("ids", ids.substring(0, ids.length() - 1));
            map.put("names", names.substring(0, names.length() - 1));
        } else {
            map.put("ids", ids);
            map.put("names", names);
        }

        Log.e("TAG", "选中后的值 ： " + ids);

        String myValue = JSON.toJSONString(map);
        Intent intent = new Intent();
        if (Constant.jumpNum1 == 4) {
            intent.setClass(MultiValueActivity.this, AddTemplateDataActivity.class);
        } else if (Constant.jumpNum == 1) {
            intent.setClass(MultiValueActivity.this, OperateDataActivity.class);
        }

        Bundle bundle = new Bundle();
        bundle.putString("myValue", myValue);
        intent.putExtra("bundle", bundle);
        setResult(2, intent);
        this.finish();

    }

    @OnClick({R.id.back_add, R.id.add_list_commint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_add:
                this.finish();
                break;
            case R.id.add_list_commint:
                jump2Activity();
                break;
        }
    }
}
