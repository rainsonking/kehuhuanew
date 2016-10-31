package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adapter.OperateDataAdapter;
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

import static com.kwsoft.kehuhua.config.Constant.mainId;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class OperateDataActivity extends BaseActivity {
    @Bind(R.id.lv)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;

    private CommonToolbar mToolbar;
    private String buttonName;
    private String mainTableId,mainPageId,tableId,pageId,dataId;
    private Map<String, String> paramsMap;
    private int buttonType;
    private String keyRelation = "";
    private String hideFieldParagram = "";




    private static final String TAG = "OperateDataActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate);
        ButterKnife.bind(this);
        dialog.show();
        getIntentData();
        initRefreshLayout();
        initView();
        getData();
    }

    private void getData() {
        //不同页面类型请求Url不一样
        String volleyUrl="";
        switch (buttonType){

            case 0://列表添加
                volleyUrl = Constant.sysUrl + Constant.requestAdd;
                break;
            case 12://关联修改
                volleyUrl = Constant.sysUrl + Constant.requestEdit;
                paramsMap.put("tNumber", "0");
                break;
            case 18://关联添加
                volleyUrl = Constant.sysUrl + Constant.requestRowsAdd;
                break;

        }
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(OperateDataActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+response+"  id  "+id);
                        setStore(response);
                    }
                });
    }


    private List<Map<String, Object>> fieldSet = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private void setStore(String response) {

        Log.e("TAG", "解析操作数据");
        try {
            Map<String, Object> buttonSet = JSON.parseObject(response);
//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");

//判断添加还是修改，keyRelation赋值不一样
                switch (buttonType){
                    case 0://添加无此参数
                        break;
                    case 18:
                        if (pageSet.get("relationFieldId") != null) {
                            Constant.relationFieldId = String.valueOf(pageSet.get("relationFieldId"));

                        keyRelation = "t0_au_" + tableId + "_" + pageId + "_" + Constant.relationFieldId + "=" + dataId;
                        }
                        break;
                    case 12:
                        keyRelation = "&t0_au_" + tableId + "_" + pageId + "=" + dataId;
                        break;
                }

                Log.e("TAG", "keyRelation " + keyRelation);

                //hideFieldSet,隐藏字段
                if (pageSet.get("hideFieldSet") != null) {
                    List<Map<String, Object>> hideFieldSet = (List<Map<String, Object>>) pageSet.get("hideFieldSet");
                    hideFieldParagram += DataProcess.toHidePageSet(hideFieldSet);
                }



        } catch (Exception e) {
            e.printStackTrace();
        }
             showData();
    }
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private int state = STATE_NORMAL;
    private OperateDataAdapter mAdapter;



    /**
     * 下拉刷新方法
     */
    //暂时禁止刷新，刷新会导致显示view错乱
    private void refreshData() {
        state = STATE_REFREH;
        getData();
        mRefreshLayout.finishRefresh();
    }

    public void normalRequest() {
        mAdapter = new OperateDataAdapter(fieldSet, paramsMap);
        mRecyclerView.setAdapter(mAdapter);
        dialog.dismiss();
    }

    private void showData() {

        Log.e(TAG, "showData: "+state);
        switch (state) {
            case STATE_NORMAL:
                normalRequest();
                break;
            case STATE_REFREH:
//                if (mAdapter != null) {
//                    mAdapter.clearData();
//                    mAdapter.addData(fieldSet);
//                    mRecyclerView.scrollToPosition(0);
//                    mRefreshLayout.finishRefresh();
//                    if (fieldSet.size() == 0) {
//                        Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();
//                    } else {
//                        Snackbar.make(mRecyclerView, "更新完成", Snackbar.LENGTH_SHORT).show();
//                    }
//                }
                normalRequest();
                if (fieldSet.size() == 0) {
                    Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mRecyclerView, "更新完成", Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    private void getCommit() {
        String value = DataProcess.commit(OperateDataActivity.this, fieldSet);
        if (!value.equals("no")) {
            if (hasInternetConnected()) {
                dialog.show();
                String volleyUrl1="";
                switch (buttonType){
                    case 0://添加提交地址
                       volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                value + "&" + hideFieldParagram;
                        break;
                    case 18:
                        volleyUrl1 = Constant.sysUrl + Constant.commitAdd + "?" +
                                Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                value + "&" + hideFieldParagram + "&" + keyRelation;
                        break;
                    case 12:
                        volleyUrl1 = Constant.sysUrl + Constant.commitEdit + "?" +
                                Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&" +
                                value + "&" + hideFieldParagram + "&" + keyRelation;
                        break;
                }
                //请求地址（关联添加和修改）
                String volleyUrl = volleyUrl1.replaceAll(" ", "%20").replaceAll("&&","&");
                //get请求
                OkHttpUtils
                        .get()
                        .url(volleyUrl)
                        .build()
                        .execute(new EdusStringCallback(OperateDataActivity.this) {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                ErrorToast.errorToast(mContext,e);
                                dialog.dismiss();
                                Toast.makeText(OperateDataActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Log.e(TAG, "onResponse: "+response);
                                if (response != null && !response.equals("0")) {
                                    backToInfo();
                                } else {
                                    Toast.makeText(OperateDataActivity.this, "操作失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }else{
                Toast.makeText(this, "无网络", Toast.LENGTH_SHORT).show();
            }
        }





    }

    /**
     * {
     "butWhereJs":"rowData.AFM_3 == 9",
     "tableIdList":"19",
     "buttonName":"修改",
     "buttonType":12,
     "buttonId":191,
     "butJyWhereJs":"",
     "tableId":19,
     "dataId":"90",
     "startTurnPage":1256
     }
     */
    //获取参数
    private void getIntentData() {
        //初始化参数Map
        paramsMap = new HashMap<>();
        //获取数据并解析
        Intent intent = getIntent();
        String buttonSetItemStr = intent.getStringExtra("itemSet");
        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);
        Log.e(TAG, "getIntentData: buttonSetItem "+buttonSetItem.toString());
        //赋值页面标题
        buttonName = String.valueOf(buttonSetItem.get("buttonName"));
        String buttonTypeStr=String.valueOf(buttonSetItem.get("buttonType"));
        buttonType=Integer.valueOf(buttonTypeStr);
        //获取参数并添加
        //mainTableId
        mainTableId = String.valueOf(buttonSetItem.get("tableIdList"));
        paramsMap.put(Constant.mainTableId, mainTableId);
        //mainPageId
        mainPageId = String.valueOf(buttonSetItem.get("pageIdList"));
        paramsMap.put(Constant.mainPageId, mainPageId);
        //tableId
        tableId = String.valueOf(buttonSetItem.get("tableId"));
        paramsMap.put(Constant.tableId, tableId);
        //pageId
        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        paramsMap.put(Constant.pageId, pageId);
        //dataId：在对列表操作的时候是没有的，只有行级操作的时候才有
        dataId = String.valueOf(buttonSetItem.get("dataId"));
        if (dataId!=null&&!dataId.equals("null")) {

            paramsMap.put(mainId, dataId);
        }
        Log.e(TAG, "getIntentData: paramsMap "+paramsMap.toString());
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


    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(false);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();
            }
        });
    }



    private void toCommit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(buttonName+"？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getCommit();
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

    public void backToInfo() {
        Toast.makeText(OperateDataActivity.this, "操作成功", Toast.LENGTH_SHORT).show();
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
                    mAdapter.notifyItemChanged(position);
                    //单选情况
                } else {
                    fieldSet.get(position).put(Constant.itemValue, ids);
                    Log.e("TAG", "单选回填的值 " + ids);
                    fieldSet.get(position).put(Constant.itemName, names);
                    mAdapter.notifyItemChanged(position);
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
                //无限添加返回的listListMap字符串
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
                mAdapter.notifyItemChanged(positionLast);
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
                mAdapter.notifyItemChanged(position);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void requestRule(final Map<String, String> parMap) {
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
                mAdapter.notifyItemChanged(i);

            }
        }
    }

}
