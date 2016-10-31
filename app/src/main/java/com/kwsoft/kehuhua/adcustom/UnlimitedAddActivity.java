package com.kwsoft.kehuhua.adcustom;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
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

public class UnlimitedAddActivity extends AppCompatActivity {
    String tableId, pageId,showFieldArr,fieldSetStr,positionLast,unlimitedAddValue,relationTableId;
    @Bind(R.id.unlimited_add_back)
    ImageView unlimitedAddBack;
    @Bind(R.id.unlimited_add_btn)
    ImageView unlimitedAddBtn;
    @Bind(R.id.unlimited_add_commit)
    ImageView unlimitedAddCommit;
    @Bind(R.id.unlimited_add_title)
    TextView unlimitedAddTitle;
    @Bind(R.id.unlimited_add_lv)
    ListView unlimitedAddLv;


    private List<List<Map<String, Object>>> dataList = new ArrayList<>();
    private String templet="";
    private UnlimitedAddAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlimited_add);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getInfoData();
        requestData();
    }


    public void getInfoData() {
        Intent mIntent = this.getIntent();
        try {
            showFieldArr = mIntent.getStringExtra("showFieldArr");
            Constant.fieldSetStr= mIntent.getStringExtra("fieldSetStr");
            positionLast=String.valueOf(mIntent.getStringExtra("position"));
            Log.e("TAG", "适配器传过来的positionLast " + positionLast);
            unlimitedAddValue=mIntent.getStringExtra("unlimitedAddValue");
            tableId = mIntent.getStringExtra("tableId");
            pageId = mIntent.getStringExtra("pageId");
            String viewName=mIntent.getStringExtra("viewName");
            unlimitedAddTitle.setText(viewName);
            relationTableId=mIntent.getStringExtra("relationTableId");
            Log.e("TAG", "适配器传过来的tableId " + tableId);
            Log.e("TAG", "适配器传过来的showFieldArr " + showFieldArr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (unlimitedAddValue!=null&&!unlimitedAddValue.equals("")) {
                dataList=JSON.parseObject(unlimitedAddValue,
                        new TypeReference<List<List<Map<String, Object>>>>() {
                        });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new UnlimitedAddAdapter(this, R.layout.activity_unlimited_add_item, dataList);
        unlimitedAddLv.setAdapter(adapter);
        //适配无限添加列表数据到界面
        if (dataList.size() <= 0) {
            Toast.makeText(UnlimitedAddActivity.this, "请点击右上角加号添加数据", Toast.LENGTH_SHORT).show();
        }

    }

    private static final String TAG = "UnlimitedAddActivity";

    public void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestAdd;


        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, showFieldArr);
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(UnlimitedAddActivity.this) {
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

    //已获得无限添加模板数据
    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        Map<String, Object> interfaceMap = JSON.parseObject(jsonData,
                new TypeReference<Map<String, Object>>() {
                });
        Map<String, Object> pageSet = (Map<String, Object>) interfaceMap.get("pageSet");
        List<Map<String, Object>> fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
        templet=JSON.toJSONString(fieldSet);

    }

    @OnClick({R.id.unlimited_add_back, R.id.unlimited_add_btn, R.id.unlimited_add_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unlimited_add_back:
                finish();
                break;
            case R.id.unlimited_add_btn:
                try {
                    Intent mIntent = new Intent(UnlimitedAddActivity.this, AddTemplateDataActivity.class);
                    mIntent.putExtra("templet",templet);
                    mIntent.putExtra("tableId",relationTableId);
                    mIntent.putExtra("pageId",showFieldArr);
                    startActivityForResult(mIntent,9);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.unlimited_add_commit:

                commitValue();

                break;
        }
    }

    private void commitValue() {
        Intent intent = new Intent();
        String myValue = JSON.toJSONString(dataList);

        if (Constant.jumpNum1==4) {
            intent.setClass(UnlimitedAddActivity.this, AddTemplateDataActivity.class);
        }else  if (Constant.jumpNum==1) {
            intent.setClass(UnlimitedAddActivity.this, OperateDataActivity.class);
        }
//拼接参数

        Bundle bundle = new Bundle();
        bundle.putString("myValue", myValue);
        bundle.putString("position", positionLast);
        intent.putExtra("bundle", bundle);
        setResult(5, intent);
        this.finish();
    }


    /**
     * Created by Administrator on 2015/12/1 0001.
     */
    public class UnlimitedAddAdapter extends ArrayAdapter<List<Map<String, Object>>> {
        private int resourceId;
        List<Map<String, Object>> itemAdd;
        List<List<Map<String, Object>>> listData;

        public UnlimitedAddAdapter(Context context, int textViewResourceId,
                                   List<List<Map<String, Object>>> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
            listData = objects;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            itemAdd = getItem(position);
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            TextView left1 = (TextView) convertView.findViewById(R.id.unlimited_add_item_name);
            TextView right1 = (TextView) convertView.findViewById(R.id.unlimited_add_item_value);
            Button deleteItem= (Button) convertView.findViewById(R.id.unlimited_del_item_value);

            left1.setText(String.valueOf(itemAdd.get(0).get("fieldCnName")));
            right1.setText(String.valueOf(itemAdd.get(0).get("tempValue")));
            deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listData.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (9 == requestCode) {
            if (9 == resultCode) {

                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                List<Map<String,Object>> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                dataList.add(dataMap);
                adapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
