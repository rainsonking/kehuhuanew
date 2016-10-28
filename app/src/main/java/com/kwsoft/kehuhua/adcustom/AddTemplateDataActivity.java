package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.Add_EditAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class AddTemplateDataActivity extends BaseActivity {
    @Bind(R.id.unlimited_add_item_to_add)
    ImageView unlimitedAddItemToAdd;
    @Bind(R.id.unlimited_add_commit_item_tadd)
    ImageView unlimitedAddCommitItemTadd;
    @Bind(R.id.unlimited_add_item_title)
    TextView unlimitedAddItemTitle;
    @Bind(R.id.unlimited_add_toAdd_lv)
    ListView unlimitedAddToAddLv;
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private Add_EditAdapter adapter;
    private Map<String, String> paramsMap;
    private String tableId, pageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template_data);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getInfoData();

    }

    @Override
    public void initView() {

    }


    public void getInfoData() {
        Intent mIntent = this.getIntent();
        try {
            String templet = mIntent.getStringExtra("templet");
            dataList = JSON.parseObject(templet,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            tableId = mIntent.getStringExtra("tableId");
            pageId = mIntent.getStringExtra("pageId");
            Log.e("TAG", "模板添加页面获取的templet " + templet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        Log.e("TAG", "paramsMap " + paramsMap.toString());
        Log.e("TAG", "开始适配模板添加");
        adapter = new Add_EditAdapter(this, dataList, paramsMap);

        Log.e("TAG", "已经初始化适配器");
        unlimitedAddToAddLv.setAdapter(adapter);
        Log.e("TAG", "适配器设置完成");

    }


    @OnClick({R.id.unlimited_add_item_to_add, R.id.unlimited_add_commit_item_tadd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unlimited_add_item_to_add:
finish();
                break;
            case R.id.unlimited_add_commit_item_tadd:
commitValue();
                break;
        }
    }

    private void commitValue() {
        Intent intent = new Intent();
        String myValue = JSON.toJSONString(dataList);
        intent.setClass(AddTemplateDataActivity.this, UnlimitedAddActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("myValue", myValue);
        intent.putExtra("bundle", bundle);
        setResult(9, intent);
        this.finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                Constant.jumpNum1 = 0;
                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                Map<String, Object> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<Map<String, Object>>() {
                        });
                assert dataMap != null;
                String num = String.valueOf(dataMap.get("num"));
                String ids = String.valueOf(dataMap.get("ids"));
                String names = String.valueOf(dataMap.get("names"));
                String isMulti = String.valueOf(dataMap.get("isMulti"));
                int position = Integer.valueOf(String.valueOf(dataMap.get("position")));
                //多选情况
                if (isMulti.equals("true")) {
                    dataList.get(position).put("tempValueName", names);//名称列表
                    dataList.get(position).put("tempValue", num);//id列表
                    //num肯定大于0
                    dataList.get(position).put("tempKeyIdArr", "t1_au_" + dataList.get(position).get("relationTableId") + "_" +
                            dataList.get(position).get("showFieldArr") +
                            "_" + dataList.get(position).get("pageDialog"));
                    dataList.get(position).put("idArr", ids);

                    Log.e("TAG", "多选回填的值 " + ids);
                    adapter.notifyDataSetChanged();
                    //单选情况
                } else {
                    dataList.get(position).put("tempValue", ids);
                    Log.e("TAG", "单选回填的值 " + ids);
                    dataList.get(position).put("tempValueName", names);
                    adapter.notifyDataSetChanged();
                    //订单编号生成,只有
                    if (!Constant.tmpFieldId.equals("") && Constant.tmpFieldId.equals(String.valueOf(dataList.get(position).get("fieldId")))) {
                        try {
                            Map<String, String> parMap = paramsMap;
                            String key = String.valueOf(dataList.get(position).get("tempKey"));
                            String value = String.valueOf(dataList.get(position).get("tempValue"));
                            parMap.put(key, value);
                            requestRule(parMap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            } else if (5 == resultCode) {
                //无限添加返回的listlistmap字符串
                Constant.jumpNum1 = 0;
                Bundle bundle = data.getBundleExtra("bundle");
                String myValue = bundle.getString("myValue");
                int positionLast = Integer.valueOf(bundle.getString("position"));
                dataList.get(positionLast).put("tempValue", myValue);
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static final String TAG = "AddTemplateDataActivity";

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
                .execute(new EdusStringCallback(AddTemplateDataActivity.this) {
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

        for (int i = 0; i < dataList.size(); i++) {
            int fieldRole = Integer.valueOf(String.valueOf(dataList.get(i).get("fieldRole")));
            if (fieldRole == 8) {
                dataList.get(i).put("tempValue", jsonData);
                adapter.notifyDataSetChanged();

            }
        }
    }

}
