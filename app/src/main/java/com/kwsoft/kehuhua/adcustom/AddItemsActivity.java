package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
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

public class AddItemsActivity extends BaseActivity {

    @Bind(R.id.lv_operate_item)
    ListView lvAddItem;
    private String buttonName;
    private String tableId,mainTableId,mainPageId;
    private String pageId;
    private String dataId;
    private Map<String, String> paramsMap;
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private CommonToolbar mToolbar;
    private Add_EditAdapter adapter;
    private String hideFieldParagram = "";
    private String keyRelation = "";
//  private Map<String, Object> defaultValArr;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_items);
        ButterKnife.bind(this);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dialog.show();
        getIntentData();
        initView();
        requestAdd();
    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(buttonName);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more));
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

    //获取参数
    private void getIntentData() {
        Intent intent = getIntent();
        String buttonSetItemStr = intent.getStringExtra("buttonSetItemStr");

        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);

        buttonName = String.valueOf(buttonSetItem.get("buttonName"));
//        tvAddItemTitle.setText(buttonName);



        mainTableId = intent.getStringExtra("tableIdList");
        mainPageId = intent.getStringExtra("pageIdList");

        tableId = String.valueOf(buttonSetItem.get("tableId"));
        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        dataId = String.valueOf(buttonSetItem.get("dataId"));



        Constant.tempTableId = tableId;
        Constant.tempPageId = pageId;

        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainTableId, mainTableId);
        paramsMap.put(Constant.mainPageId, mainPageId);

    }

    private static final String TAG = "AddItemsActivity";
    //请求
    public void requestAdd() {
        String volleyUrl = Constant.sysUrl + Constant.requestAdd;
        Log.e("TAG", "网络获取添加Url " + volleyUrl);
        Log.e("TAG", "网络获取添加参数：" + paramsMap.toString());

        //参数
        paramsMap.put("tNumber", "0");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(mContext) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
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


//解析展示

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        Log.e("TAG", "开始解析添加数据");
        Map<String, Object> buttonSet = JSON.parseObject(jsonData);
        try {
//获取alterTime
//            String alterTime = String.valueOf(buttonSet.get("alterTime"));
//获取默认值
//            defaultValArr = (Map<String, Object>) buttonSet.get("defaultValArr");
//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("TAG", "pageSet " + pageSet.toString());
            if (pageSet.get("relationFieldId") != null) {
                Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));
                keyRelation = "t0_au_" + tableId + "_" + pageId + "_" + Constant.relationFieldId + "=" + dataId;
                Log.e("TAG", "keyRelation " + keyRelation);
            }
//hideFieldSet
            if (pageSet.get("hideFieldSet") != null) {
                List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                hideFieldParagram = DataProcess.toHidePageSet(hideFieldSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//展示数据
        Log.e("TAG", "解析添加数据开始展示 " + fieldSet.toString());
        if (fieldSet != null && fieldSet.size() > 0) {
            Constant.fieldSetTemp = fieldSet;
            adapter = new Add_EditAdapter(mContext, fieldSet, paramsMap);
            lvAddItem.setAdapter(adapter);
            dialog.dismiss();
        }else{
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
                requestAddCommit();
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

    private void requestAddCommit() {
        String value = DataProcess.commit((Activity) mContext,fieldSet);
        if (!value.equals("no")) {
            if (hasInternetConnected()) {
                dialog.show();
                String volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                        Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                        value + "&" + hideFieldParagram + "&" +
                        keyRelation;
                String volleyUrl = volleyUrl1.replaceAll(" ", "%20");
                Log.e("TAG", "添加提交地址：" + volleyUrl);
                 //get请求
                OkHttpUtils
                        .get()
                        .url(volleyUrl)
                        .build()
                        .execute(new EdusStringCallback(mContext) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                dialog.dismiss();
                                ErrorToast.errorToast(mContext,e);
                                Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                                Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, "onResponse: "+"  id  "+id);
                                Log.e("TAG", "获得添加结果" + response);
                                String isCommitSuccess = String.valueOf(response);
                                if (!isCommitSuccess.equals("0")) {
                                    toListActivity();
                                } else {
                                    Toast.makeText(mContext, "添加失败", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                makeValue(data);
            } else if (5 == resultCode) {
                unlimitedMakeValue(data);
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

    private void unlimitedMakeValue(Intent data) {
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
                            (Activity) mContext,
                            myValueList.get(i));
                }

            }
        }
        Log.e("TAG", "secondValue " + secondValue);
        fieldSet.get(positionLast).put(Constant.itemValue, myValueList.size() + "&" + secondValue);


        adapter.notifyDataSetChanged();
    }

    private void makeValue(Intent data) {
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
        //String num = String.valueOf(dataMap.get("num"));
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
                .execute(new EdusStringCallback(mContext) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dialog.dismiss();
                        ErrorToast.errorToast(mContext,e);
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

