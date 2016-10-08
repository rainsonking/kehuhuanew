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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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


    public void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestAdd;
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "无限添加获取的数据" + jsonData);
                        dataStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //refreshListView.hideFooterView();
                VolleySingleton.onErrorResponseMessege(UnlimitedAddActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(Constant.tableId, tableId);
                map.put(Constant.pageId, showFieldArr);
                return map;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    //已获得无限添加模板数据
    @SuppressWarnings("unchecked")
    private void dataStore(String jsonData) {
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
            intent.setClass(UnlimitedAddActivity.this, AddItemsActivity.class);
        }else if(Constant.jumpNum==2){
            intent.setClass(UnlimitedAddActivity.this, RowsEditActivity.class);
        }else if(Constant.jumpNum==3){
            intent.setClass(UnlimitedAddActivity.this, RowsAddActivity.class);
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
