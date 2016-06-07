package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kwsoft.kehuhua.adapter.Add_EditAdapter;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RowsEditActivity extends AppCompatActivity {


    @Bind(R.id.tv_cancel_edit)
    ImageView tvCancelEdit;
    @Bind(R.id.tv_commit_edit)
    ImageView tvCommitEdit;
    @Bind(R.id.lv_edit_item)
    PullToRefreshListView lvEditItem;
    private Map<String, Object> defaultValArr=new HashMap<>();

    private String tableId, dataId, pageId, buttonName;

    private Map<String, String> paramsMap = new HashMap<>();
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private String alterTime;
    private Add_EditAdapter adapter;
    private int pos;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private Map<Integer, String> idArrMap = new HashMap<>();
    private Map<String, Object> defaultValue = new HashMap<>();
    private String commitUrl;

    private Map<Integer, Map<String,Object>> commitMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rows_edit);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getIntentData();
        Log.e("TAG", "修改页面获取信息完毕 ");
        requestEdit();
    }

    private void getIntentData() {

        Intent mIntent = this.getIntent();
        String infoData = mIntent.getStringExtra("editSet");
        Map<String, Object> editData = JSON.parseObject(infoData,
                new TypeReference<Map<String, Object>>() {
                });

        tableId = String.valueOf(editData.get("tableId"));
        pageId = String.valueOf(editData.get("startTurnPage"));
        dataId = String.valueOf(editData.get("dataId"));
        buttonName = String.valueOf(editData.get("buttonName"));
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainId, dataId);
        paramsMap.put(Constant.timeName, "100");
    }

    /**
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     */
    @SuppressWarnings("unchecked")
    public void requestEdit() {
        final String volleyUrl = Constant.sysUrl + Constant.requestEdit;
        Log.e("TAG", "修改页面Url " + volleyUrl);
        Log.e("TAG", "修改页面paramsMap：" + paramsMap);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获取修改数据" + jsonData);
                        setStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(RowsEditActivity.this, volleyError);
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


//解析展示

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        Map<String, Object> editSet = JSON.parseObject(jsonData);
        //获取alterTime
        alterTime = String.valueOf(editSet.get("alterTime"));
        //获取fieldSet
        try {
            Map<String, Object> pageSet = (Map<String, Object>) editSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
        } catch (Exception e) {
            Log.e("TAG", "无添加属性" + fieldSet);
        }
        Log.e("TAG", "fieldSet" + fieldSet);

//获取修改页面默认值
//        "49_11_0_toUpdateSql_16": [
//        {
//            "AFM_1": "天下",
//                "AFM_2": "中关村",
//                "T_49_0": 16
//        }
//        ]
        try{
            defaultValArr = (Map<String, Object>) editSet.get("dataInfo");
        }catch (Exception e){
            e.printStackTrace();
        }


//展示数据
        if (fieldSet != null && fieldSet.size() > 0) {
            adapter = new Add_EditAdapter(RowsEditActivity.this,fieldSet,paramsMap,defaultValArr);
            lvEditItem.setAdapter(adapter);
        } else {
            Log.e("TAG", "无内容");
        }

    }

    @OnClick({R.id.tv_cancel_edit, R.id.tv_commit_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel_edit:

                this.finish();
                break;
            case R.id.tv_commit_edit:
                commitEdit();
                break;
        }
    }

    private void commitEdit() {

        int numNull = 0;
        for (int i = 0; i < fieldSet.size(); i++) {

            int ifMust=0;
            if (fieldSet.get(i).get("ifMust")!=null) {
                ifMust = Integer.valueOf(String.valueOf(fieldSet.get(i).get("ifMust")));
            }

            String tempValue = String.valueOf(fieldSet.get(i).get("tempValue"));

            if (ifMust == 1 && (tempValue.equals("")||tempValue.equals("null"))) {

                numNull++;
                break;
            }
        }

        if (numNull == 0) {

            Map<String, Object> commitMap1 = new HashMap<>();
            //拼接网址
            for (int i = 0; i < fieldSet.size(); i++) {
                String key = String.valueOf(fieldSet.get(i).get("tempKey"));
                String value;
                if (fieldSet.get(i).get("tempValue") != null) {
                    value = String.valueOf(fieldSet.get(i).get("tempValue"));
                } else {
                    value = "";
                }
                commitMap1.put(key, value);

            }
            String pinJie1="t0_au_"+tableId+"_"+pageId+"="+dataId+"&";
            for(Map.Entry entry:commitMap1.entrySet()){
                pinJie1+=entry.getKey()+"="+entry.getValue()+"&";
            }



            //String pinJie1 = commitMap1.toString().substring(1, commitMap1.toString().length() - 1).replace(",", "&") + "&";//replace(" ", "").

            for (int i = 0; i < fieldSet.size(); i++) {
                if (fieldSet.get(i).get("idArr") != null) {
                    String[] ids = String.valueOf(fieldSet.get(i).get("idArr")).split(",");
                    String keyChild = String.valueOf(fieldSet.get(i).get("tempKeyIdArr"));

                    String pinJie2 = "";
                    for (String id : ids) {
                        pinJie2 += keyChild + "=" + id + "&";

                    }
                    pinJie1 += pinJie2;

                }
            }

            String chooseData2 = pinJie1.substring(0, pinJie1.length() - 1);
            String chooseData1 = "?" + Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&";

            commitUrl = Constant.sysUrl + Constant.commitEdit + chooseData1 + chooseData2;
            //请求网络提交
            Log.e("TAG", "修改合成地址" + commitUrl);
            requestEditCommit(commitUrl);

            //判断是否提交成功，如果成功返回并刷新列表，如果失败，提示用户失败，不返回
        } else {
            Toast.makeText(RowsEditActivity.this, "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestEditCommit(String volleyUrl) {

        StringRequest loginInterfaceData = new StringRequest(Request.Method.GET, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获得修改结果" + jsonData);
                        int isCommitSuccess = Integer.valueOf(jsonData);
                        if (isCommitSuccess == 1) {
                            toListActivity();
                        } else {
                            Toast.makeText(RowsEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(RowsEditActivity.this, volleyError);
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




    /**
     * 跳转至子菜单列表
     */
    public void toListActivity() {
        Toast.makeText(RowsEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                Constant.jumpNum=0;
                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                Log.e("TAG","跳转后回到修改页面1");
                Map<String, Object> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<Map<String, Object>>() {
                        });
                Log.e("TAG","跳转后回到修改页面2");
                assert dataMap != null;
                String num = String.valueOf(dataMap.get("num"));
                String ids = String.valueOf(dataMap.get("ids"));
                String names = String.valueOf(dataMap.get("names"));
                String isMulti = String.valueOf(dataMap.get("isMulti"));
                Log.e("TAG","跳转后回到修改页面2.1 "+String.valueOf(dataMap.get("position")));

                int position = Integer.valueOf(String.valueOf(dataMap.get("position")));
                Log.e("TAG","跳转后回到修改页面3");
                if (isMulti.equals("true")) {
                    fieldSet.get(position).put("tempValueName", names);
                    fieldSet.get(position).put("tempValue", num);
                    //num肯定大于0
                    fieldSet.get(position).put("tempKeyIdArr", "t1_au_" + fieldSet.get(position).get("relationTableId") + "_" +
                            fieldSet.get(position).get("showFieldArr") +
                            "_" + fieldSet.get(position).get("pageDialog"));
                    fieldSet.get(position).put("idArr", ids);
                    idArrMap.put(position, ids);
                    adapter.notifyDataSetChanged();
                    Log.e("TAG","跳转后回到修改页面4");
                }else {
                    Log.e("TAG","跳转后回到修改页面5");
                    fieldSet.get(position).put("tempValue", ids);
                    fieldSet.get(position).put("tempValueName", names);
                    adapter.notifyDataSetChanged();
                    Log.e("TAG","跳转后回到修改页面6");
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}