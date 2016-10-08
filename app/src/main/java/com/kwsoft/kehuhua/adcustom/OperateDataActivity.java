package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.Add_EditAdapter;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OperateDataActivity extends AppCompatActivity {
    @Bind(R.id.operate_back)
    ImageView operate_back;
    @Bind(R.id.operate_commit)
    ImageView operate_commit;
    @Bind(R.id.operate_title)
    TextView operate_title;
    @Bind(R.id.operate_lv)
    ListView operate_lv;


    private String tableId,pageId,dataId,requestUrl,buttonType,keyRelation;
    private Map<String, String> paramsMap = new HashMap<>();
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private List<Map<String, Object>> hideFieldSet = new ArrayList<>();


    private Add_EditAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_operate_data);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getIntentData();
        requestData();



    }

    private void getIntentData() {
        Log.e("TAG", "修改页面开始获得传入值 ");
        Intent mIntent = this.getIntent();
        String infoData = mIntent.getStringExtra("itemSet");
        Map<String, Object> editData = JSON.parseObject(infoData,
                new TypeReference<Map<String, Object>>() {
                });
        String buttonNameStr = String.valueOf(editData.get("buttonName"));
        operate_title.setText(buttonNameStr);


        tableId = String.valueOf(editData.get("tableId"));
        pageId = String.valueOf(editData.get("startTurnPage"));
        dataId = String.valueOf(editData.get("dataId"));


        requestUrl=mIntent.getStringExtra("itemSet");
        buttonType=mIntent.getStringExtra("buttonType");

        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainId, dataId);
        paramsMap.put(Constant.timeName, "100");


        Log.e("TAG", "操作页面Url：" + requestUrl);
        Log.e("TAG", "操作页面参数：" + paramsMap);
    }

    @SuppressWarnings("unchecked")
    public void requestData() {
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获取操作页面数据" + jsonData);

                        extractData(jsonData);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(OperateDataActivity.this, volleyError);
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


    @SuppressWarnings("unchecked")
    private void extractData(String jsonData) {
        Map<String, Object> editSet = JSON.parseObject(jsonData);
        //获取alterTime
//        String alterTime = String.valueOf(editSet.get("alterTime"));
        //获取fieldSet
        try {
            Map<String, Object> pageSet = (Map<String, Object>) editSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            if (pageSet.get("relationFieldId") != null) {
                Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));
            }
//hideFieldSet,隐藏字段
            if (pageSet.get("hideFieldSet") != null) {
                hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                keyRelation += DataProcess.toHidePageSet(hideFieldSet);
            }
        } catch (Exception e) {
            Log.e("TAG", "无添加属性" + fieldSet);
        }
        Log.e("TAG", "fieldSet" + fieldSet);
//展示数据
        if (fieldSet != null && fieldSet.size() > 0) {
            adapter = new Add_EditAdapter(OperateDataActivity.this, fieldSet, paramsMap);
            operate_lv.setAdapter(adapter);
        } else {
            Log.e("TAG", "无内容");
        }

    }

    @OnClick({R.id.operate_back, R.id.operate_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.operate_back:

                this.finish();
                break;
            case R.id.operate_commit:
                stitchingParameter();
                break;
        }
    }


    private void stitchingParameter() {

        Log.e("TAG", "操作页面提交前数据：" + fieldSet.toString());
        String value = DataProcess.commit(OperateDataActivity.this, fieldSet);
        Log.e("TAG", "操作页面提交拼接：" + value);
        String chooseData2 = value.substring(0, value.length() - 1);
        String chooseData1 = "?" + Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&";
        String commitUrl = Constant.sysUrl + Constant.commitEdit + chooseData1 + chooseData2+"&"+keyRelation+"&t0_au_" + tableId + "_" + pageId +"=" + dataId;
        Log.e("TAG", "操作提交地址：" + commitUrl);
        Log.e("TAG", "keyRelation：" + keyRelation);
        commitOpera(commitUrl);
    }

    private void commitOpera(String volleyUrl) {

        StringRequest loginInterfaceData = new StringRequest(Request.Method.GET, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获得修改结果" + jsonData);
                        if (jsonData != null && !jsonData.equals("")) {
                            Toast.makeText(OperateDataActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            OperateDataActivity.this.finish();
                        } else {
                            Toast.makeText(OperateDataActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(OperateDataActivity.this, volleyError);
            }
        }
        ) {
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






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                //返回添加页面后复位jump值
                Constant.jumpNum = 0;
                //传递数据
                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                //解析选择结果值
                Map<String, Object> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<Map<String, Object>>() {
                        });
                assert dataMap != null;
                //num代表数目，ids代表所选的id们，names所选id对应的名称，isMulti为true多选，都则单选
                //position为记录的值应该插入的位置
                String num = String.valueOf(dataMap.get("num"));
                String ids = String.valueOf(dataMap.get("ids"));
                String names = String.valueOf(dataMap.get("names"));
                String isMulti = String.valueOf(dataMap.get("isMulti"));
                int position = Integer.valueOf(String.valueOf(dataMap.get("position")));
                //多选情况
                if (isMulti.equals("true")) {

//                    fieldSet.get(position).put(Constant.primValue, num);//选择总数
                    //将id列表记录到单元中，留待提交或者回显使用
                    fieldSet.get(position).put(Constant.itemValue, ids);//id列表
                    fieldSet.get(position).put(Constant.itemName, names);//名称列表
                    //找到下一层的key值
                    fieldSet.get(position).put(Constant.secondKey, "t1_au_" + fieldSet.get(position).get("relationTableId") + "_" +
                            fieldSet.get(position).get("showFieldArr") +
                            "_" + fieldSet.get(position).get("dialogField"));
                    adapter.notifyDataSetChanged();
                    //单选情况
                } else {
                    fieldSet.get(position).put(Constant.itemValue, ids);
                    Log.e("TAG", "单选回填的值 " + ids);
                    fieldSet.get(position).put(Constant.itemName, names);
                    adapter.notifyDataSetChanged();
                    //订单编号生成,只有
                    if (!Constant.tmpFieldId.equals("") && Constant.tmpFieldId.equals(String.valueOf(fieldSet.get(position).get("fieldId")))) {
                        try {
                            Map<String, String> parMap = paramsMap;
                            String key = String.valueOf(fieldSet.get(position).get(Constant.primKey));
                            String value = String.valueOf(fieldSet.get(position).get(Constant.itemValue));
                            parMap.put(key, value);
                            requestRule(parMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestRule(final Map<String, String> parMap) {
        String volleyUrl = Constant.sysUrl + Constant.requestMaxRule;
        Log.e("TAG", "网络获取规则dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取规则table " + parMap.toString());

        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理

                        putValue(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(OperateDataActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return parMap;
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

    private void putValue(String jsonData) {
        Log.e("TAG", "规则生成结果：" + jsonData);

        for (int i = 0; i < fieldSet.size(); i++) {
            int fieldRole = Integer.valueOf(String.valueOf(fieldSet.get(i).get("fieldRole")));
            if (fieldRole == 8) {
                fieldSet.get(i).put(Constant.itemValue, jsonData);
                fieldSet.get(i).put(Constant.itemName, jsonData);
                adapter.notifyDataSetChanged();

            }
        }
    }
}
