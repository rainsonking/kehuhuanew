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
import android.widget.RelativeLayout;
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
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class RowsAddActivity extends AppCompatActivity {

    @Bind(R.id.rows_IV_back_list_item_tadd)
    ImageView rowsIVBackListItemTadd;
    @Bind(R.id.rows_tv_commit_item_tadd)
    ImageView rowsTvCommitItemTadd;
    @Bind(R.id.rows_tv_add_item_title)
    TextView rowsTvAddItemTitle;
    @Bind(R.id.rows_lv_add_item)
    ListView rowsLvAddItem;
    @Bind(R.id.rows_add_item_title)
    RelativeLayout rowsAddItemTitle;


    private String keyRelation = "";
    private String tableId;
    private String pageId;
    private String mainTableId;
    private String mainPageId;
    private String mainId;
    private Map<String, String> paramsMap;
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private Add_EditAdapter adapter;
    private String hideFieldParagram = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rows_add);
        CloseActivityClass.activityList.add(this);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        Log.e("TAG", "关联添加页面启动 ");
        getIntentData();
        Log.e("TAG", "关联添加获取上层传递数据完毕 ");
        init();
        Log.e("TAG", "关联添加初始化参数完毕 ");
        requestAdd();
        Log.e("TAG", "关联添加获取数据展示完毕 ");
    }

    private void init() {
        rowsAddItemTitle.setBackgroundColor(getResources().getColor(topBarColor));
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainTableId, mainTableId);
        paramsMap.put(Constant.mainPageId, mainPageId);
        paramsMap.put(Constant.mainId, mainId);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        String buttonSetItemStr = intent.getStringExtra("addSet");
        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);

        String buttonName = String.valueOf(buttonSetItem.get("buttonName"));
        rowsTvAddItemTitle.setText(buttonName);
        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        mainTableId = String.valueOf(buttonSetItem.get("tableIdList"));

        tableId = String.valueOf(buttonSetItem.get("tableId"));
        mainPageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        mainId = String.valueOf(buttonSetItem.get("dataId"));
        Constant.tempTableId = tableId;
        Constant.tempPageId = pageId;
    }

    @OnClick({R.id.rows_IV_back_list_item_tadd, R.id.rows_tv_commit_item_tadd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rows_IV_back_list_item_tadd:
                this.finish();
                break;
            case R.id.rows_tv_commit_item_tadd:
                requestAddCommit();
                break;
        }
    }


    private void requestAddCommit() {
        String value = DataProcess.commit(RowsAddActivity.this, fieldSet);
        if (!value.equals("no")) {
            String volleyUrl = Constant.sysUrl + Constant.commitAdd + "?" +
                    Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                    value + "&" + hideFieldParagram + "&" + keyRelation;
            Log.e("TAG", "关联添加提交地址：" + volleyUrl);
            StringRequest loginInterfaceData = new StringRequest(Request.Method.GET, volleyUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String jsonData) {//磁盘存储后转至处理
                            Log.e("TAG", "获得添加结果" + jsonData);
                            int isCommitSuccess = Integer.valueOf(jsonData);
                            if (isCommitSuccess != 0) {
                                toListActivity();
                            } else {
                                Toast.makeText(RowsAddActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleySingleton.onErrorResponseMessege(RowsAddActivity.this, volleyError);
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
    }


    public void toListActivity() {
        Toast.makeText(RowsAddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }


    //请求
    public void requestAdd() {
        String volleyUrl = Constant.sysUrl + Constant.requestRowsAdd;
        Log.e("TAG", "关联添加Url " + volleyUrl);
        Log.e("TAG", "网络获取添加参数 " + paramsMap.toString());
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加按钮数据" + jsonData);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
                        setStore(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(RowsAddActivity.this, volleyError);
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
    private void setStore(String jsonData) {
        Log.e("TAG", "解析添加数据1");
        Map<String, Object> buttonSet = JSON.parseObject(jsonData);
        try {

//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            if (pageSet.get("relationFieldId") != null) {
                Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));
                keyRelation = "t0_au_" + tableId + "_" + pageId + "_" + Constant.relationFieldId + "=" + mainId;
                Log.e("TAG", "keyRelation " + keyRelation);

                //hideFieldSet,隐藏字段
                if (pageSet.get("hideFieldSet") != null) {
                    List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                    hideFieldParagram += DataProcess.toHidePageSet(hideFieldSet);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//展示数据
        if (fieldSet != null && fieldSet.size() > 0) {
            adapter = new Add_EditAdapter(RowsAddActivity.this, fieldSet, paramsMap);
            rowsLvAddItem.setAdapter(adapter);
        }

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
//                    fieldSet.get(position).put(Constant.secondKey, "t1_au_" + fieldSet.get(position).get("relationTableId") + "_" +
//                            fieldSet.get(position).get("showFieldArr") +
//                            "_" + fieldSet.get(position).get("dialogField"));
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
            } else if (5 == resultCode) {
                //无限添加返回的listlistmap字符串
                Constant.jumpNum = 0;
                Bundle bundle = data.getBundleExtra("bundle");
                String myValue = bundle.getString("myValue");
                int positionLast = Integer.valueOf(bundle.getString("position"));
                fieldSet.get(positionLast).put("tempListValue", myValue);
                List<List<Map<String, Object>>> myValueList = new ArrayList<>();
                String secondValue = "";
                if (myValue != null && !myValue.equals("")) {
                    myValueList = JSON.parseObject(myValue,
                            new TypeReference<List<List<Map<String, Object>>>>() {
                            });
                    if (myValueList.size() > 0) {
                        //将选择结果赋值给父类dz值

                        for (int i = 0; i < myValueList.size(); i++) {
                            List<Map<String, Object>> itemMaps = myValueList.get(i);

                            for (int j = 0; j < myValueList.get(i).size(); j++) {
                                String tempKeyIdArr = String.valueOf(myValueList.get(i).get(j).get("tempKeyIdArr"));
                                if (tempKeyIdArr != null && !tempKeyIdArr.equals("")) {
                                    myValueList.get(i).get(j).put(Constant.itemValue, tempKeyIdArr.replace("t1", "t2"));
                                }
                            }
                            secondValue += "&" + DataProcess.toCommitStr(
                                    RowsAddActivity.this,
                                    myValueList.get(i));
                        }

                    }
                }
                Log.e("TAG", "secondValue " + secondValue);
                fieldSet.get(positionLast).put(Constant.itemValue, myValueList.size() + "&" + secondValue);


                adapter.notifyDataSetChanged();
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
                VolleySingleton.onErrorResponseMessege(RowsAddActivity.this, volleyError);
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


////获取默认值
//Map<String, Object> defaultValArr = (Map<String, Object>) buttonSet.get("dataInfo");

//    private void commitAdd() {
//        Log.e("TAG", "提交关联添加1");
//        int numNull = 0;
//        for (int i = 0; i < fieldSet.size(); i++) {
//            Log.e("TAG", "提交关联添加1.1");
//
//            int ifMust = 0;
//            try {
//                ifMust = Integer.valueOf(String.valueOf(fieldSet.get(i).get("ifMust")));
//            } catch (NumberFormatException e) {
//                e.printStackTrace();
//            }
//            Log.e("TAG", "提交关联添加1.2");
//            String tempValue = String.valueOf(fieldSet.get(i).get("tempValue"));
//            Log.e("TAG", "提交关联添加1.3");
//            if (ifMust == 1 && (tempValue.equals("")||tempValue.equals("null"))) {
//                Log.e("TAG", "提交关联添加1.4");
//                numNull++;
//                break;
//            }
//        }
//        Log.e("TAG", "提交关联添加2");
//        if (numNull == 0) {
//
//            Map<String, Object> commitMap1 = new HashMap<>();
//            //拼接网址
//            for (int i = 0; i < fieldSet.size(); i++) {
//                String key = String.valueOf(fieldSet.get(i).get("tempKey"));
//                String value;
//                if (fieldSet.get(i).get("tempValue") != null) {
//                    value = String.valueOf(fieldSet.get(i).get("tempValue"));
//                } else {
//                    value = "";
//                }
//                commitMap1.put(key, value);
//
//            }
//            Log.e("TAG", "提交关联添加3");
//            String pinJie1="";
//            for(Map.Entry entry:commitMap1.entrySet()){
//                pinJie1+=entry.getKey()+"="+entry.getValue()+"&";
//            }
//
//            Log.e("TAG", "pinJie1" + pinJie1);
//            for (int i = 0; i < fieldSet.size(); i++) {
//                if (fieldSet.get(i).get("idArr") != null) {
//                    String[] ids = String.valueOf(fieldSet.get(i).get("idArr")).split(",");
//                    String keyChild = String.valueOf(fieldSet.get(i).get("tempKeyIdArr"));
//
//                    String pinJie2 = "";
//                    for (String id : ids) {
//                        pinJie2 += keyChild + "=" + id + "&";
//
//                    }
//                    pinJie1 += pinJie2;
//
//                }
//            }
//            Log.e("TAG", "提交关联添加4");
//            String chooseData2 = pinJie1.substring(0, pinJie1.length() - 1);
//            String chooseData1 = "?" + Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&";
//
//            String commitUrl = Constant.sysUrl + Constant.commitAdd + chooseData1 + chooseData2+"&"+keyRelation;
//            //请求网络提交
//
//
//
//            Log.e("TAG", "pinJie1+2" + pinJie1);
//            Log.e("TAG", "添加合成数据" + commitUrl);
//            requestAddCommit();
//
//            //判断是否提交成功，如果成功返回并刷新列表，如果失败，提示用户失败，不返回
//        } else {
//            Toast.makeText(RowsAddActivity.this, "必填字段不能为空", Toast.LENGTH_SHORT).show();
//        }
//    }

//获取alterTime
//            String alterTime = String.valueOf(buttonSet.get("alterTime"));













