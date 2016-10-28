package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.Add_EditAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class RowsEditActivity extends BaseActivity {

    @Bind(R.id.lv_operate_item)
    ListView lvAddItem;
    private String buttonName;
    private CommonToolbar mToolbar;
    private String hideFieldParagram = "";
    private String tableId,mainTableId,mainPageId;
    private String dataId;
    private String pageId;
    private Map<String, String> paramsMap = new HashMap<>();
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private Add_EditAdapter adapter;
    private static final int REQUEST_CODE = 732;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_items);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.show();
        Log.e("TAG", "跳转到修改页面 ");
        getIntentData();
        initView();
        Log.e("TAG", "修改页面获取信息完毕 ");
        requestEdit();
    }

    @Override
    public void initView() {

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(buttonName);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.edit_commit1));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCommit();
            }
        });


    }

    private void getIntentData() {
        Intent mIntent = this.getIntent();
        String infoData = mIntent.getStringExtra("itemSet");
        Map<String, Object> editData = JSON.parseObject(infoData,
                new TypeReference<Map<String, Object>>() {
                });
        mainTableId = String.valueOf(editData.get("tableIdList"));
        mainPageId = String.valueOf(editData.get("pageIdList"));


        tableId = String.valueOf(editData.get("tableId"));
        pageId = String.valueOf(editData.get("startTurnPage"));

        dataId = String.valueOf(editData.get("dataId"));
        buttonName = String.valueOf(editData.get("buttonName"));
        Log.e("TAG", "buttonName: "+buttonName);
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainTableId, mainTableId);
        paramsMap.put(Constant.mainPageId, mainPageId);
        paramsMap.put(Constant.mainId, dataId);
        paramsMap.put(Constant.timeName, "100");
    }

    private static final String TAG = "RowsEditActivity";
    /**
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     */
    @SuppressWarnings("unchecked")
    public void requestEdit() {
        final String volleyUrl = Constant.sysUrl + Constant.requestEdit;
        Log.e("TAG", "修改页面Url " + volleyUrl);
        Log.e("TAG", "修改页面paramsMap：" + paramsMap);

        //参数
        paramsMap.put("tNumber", "0");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(RowsEditActivity.this) {
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


//解析展示

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
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
                List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                hideFieldParagram += DataProcess.toHidePageSet(hideFieldSet);
            }


        } catch (Exception e) {
            Log.e("TAG", "无添加属性" + fieldSet);
        }
        Log.e("TAG", "fieldSet" + fieldSet);


//展示数据
        if (fieldSet != null && fieldSet.size() > 0) {
            adapter = new Add_EditAdapter(RowsEditActivity.this, fieldSet, paramsMap);
            lvAddItem.setAdapter(adapter);
            dialog.dismiss();
        } else {
            dialog.dismiss();

            Snackbar.make(lvAddItem,"本页无数据",Snackbar.LENGTH_SHORT).show();
        }

    }
    private void toCommit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(buttonName+"？");
//        builder.setTitle("删除");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestEditCommit();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();



    }
    private void requestEditCommit() {



        String value = DataProcess.commit(RowsEditActivity.this, fieldSet);
        if (!value.equals("no")) {
            if (hasInternetConnected()) {
                dialog.show();
                String volleyUrl1 = Constant.sysUrl + Constant.commitEdit + "?" +
                        Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                        value + "&" + hideFieldParagram + "&t0_au_" + tableId + "_" + pageId + "=" + dataId;
                String volleyUrl = volleyUrl1.replaceAll(" ", "%20").replaceAll("&&","&");
                Log.e("TAG", "修改页面提交地址：" + volleyUrl);

                //get请求
                OkHttpUtils
                        .get()
                        .url(volleyUrl)
                        .build()
                        .execute(new EdusStringCallback(RowsEditActivity.this) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ErrorToast.errorToast(mContext,e);
                                dialog.dismiss();
                                Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                                Toast.makeText(RowsEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e("TAG", "获得修改结果" + response);
                                if (response != null && !response.equals("")) {
                                    toListActivity();
                                } else {
                                    Toast.makeText(RowsEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this, "无网络", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 跳转至子菜单列表
     */
    public void toListActivity() {
        Toast.makeText(RowsEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        this.finish();
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
            }else if (resultCode == 101) {
                //返回添加页面后复位jump值
                Constant.jumpNum = 0;
                Log.e("TAG", "RESULT_OK " + 101);
                Bundle bundle = data.getBundleExtra("bundle");
                String positionStr = bundle.getString("position");
                String codeListStr = bundle.getString("codeListStr");
                int position=Integer.valueOf(positionStr);
                fieldSet.get(position).put(Constant.itemValue, codeListStr);
                fieldSet.get(position).put(Constant.itemName, codeListStr);
                Log.e("TAG", "fieldSet.get(picturePosition) " + fieldSet.get(position).toString());
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestRule(Map<String, String> parMap) {
        String volleyUrl = Constant.sysUrl + Constant.requestMaxRule;
        Log.e("TAG", "网络获取规则dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取规则table " + parMap.toString());

        //请求
        OkHttpUtils
                .post()
                .params(parMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(RowsEditActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        dialog.dismiss();
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        putValue(response);
                    }
                });
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


//        int numNull = 0;
//        for (int i = 0; i < fieldSet.size(); i++) {
//
//            int ifMust = 0;
//            if (fieldSet.get(i).get("ifMust") != null) {
//                ifMust = Integer.valueOf(String.valueOf(fieldSet.get(i).get("ifMust")));
//            }
//
//            String tempValue = String.valueOf(fieldSet.get(i).get("tempValue"));
//
//            if (ifMust == 1 && (tempValue.equals("") || tempValue.equals("null"))) {
//
//                numNull++;
//                break;
//            }
//        }
//
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
//            String pinJie1 = "t0_au_" + tableId + "_" + pageId + "=" + dataId + "&";
//            for (Map.Entry entry : commitMap1.entrySet()) {
//                pinJie1 += entry.getKey() + "=" + entry.getValue() + "&";
//            }
//
//
//            //String pinJie1 = commitMap1.toString().substring(1, commitMap1.toString().length() - 1).replace(",", "&") + "&";//replace(" ", "").
//
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
//
//            String chooseData2 = pinJie1.substring(0, pinJie1.length() - 1);
//            String chooseData1 = "?" + Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&";
//
//            commitUrl = Constant.sysUrl + Constant.commitEdit + chooseData1 + chooseData2;
//            //请求网络提交
//            Log.e("TAG", "修改合成地址" + commitUrl);
//            requestEditCommit(commitUrl);
//
//            //判断是否提交成功，如果成功返回并刷新列表，如果失败，提示用户失败，不返回
//        } else {
//            Toast.makeText(RowsEditActivity.this, "必填字段不能为空", Toast.LENGTH_SHORT).show();
//        }

//获取修改页面默认值
//        "49_11_0_toUpdateSql_16": [
//        {
//            "AFM_1": "天下",
//                "AFM_2": "中关村",
//                "T_49_0": 16
//        }
//        ]
//        try {
//            Map<String, Object> defaultValArr = (Map<String, Object>) editSet.get("dataInfo");
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


//    private void commitEdit() {
//        String value = DataProcess.commit(RowsEditActivity.this, fieldSet);
//        String commitUrl = Constant.sysUrl + Constant.commitEdit +"?" +
//                Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&"+
//                value+"&"+hideFieldParagram+"&t0_au_" + tableId + "_" + pageId +"=" + dataId;
//        Log.e("TAG", "修改页面提交地址：" + commitUrl);
//        requestEditCommit(commitUrl);
//    }




















