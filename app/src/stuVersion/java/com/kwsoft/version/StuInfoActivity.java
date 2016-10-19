package com.kwsoft.version;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.config.Constant.tableId;

public class StuInfoActivity extends BaseActivity {

    @Bind(R.id.stu_info_lv)
    ListView stuInfoLv;
    private SimpleAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, String>> stuInfo;
    //下拉刷新handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x101:
                    Log.e("TAG", "学员端开始handler通知跳转后 ");
                    try {
                        if (swipeRefreshLayout.isRefreshing()) {
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);//设置不刷新
                            Toast.makeText(getApplicationContext(), "数据已更新", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_info);
        ButterKnife.bind(this);
        initData();
        requestSet();
    }

    @Override
    public void initView() {

    }

    private void initData() {

        CommonToolbar mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("个人资料");
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //下拉刷新设置
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });

    }

    /**
     * 加载菜单数据的线程
     */
    class LoadDataThread extends Thread {
        @Override
        public void run() {
            //下载数据，重新设定dataList
            requestSet();
            //防止数据加载过快动画效果差
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("TAG", "学员端开始handler通知 ");
            handler.sendEmptyMessage(0x101);//通过handler发送一个更新数据的标记，适配器进行dataSetChange，然后停止刷新动画
        }
    }

    /**
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     */
    @SuppressWarnings("unchecked")
    public void requestSet() {
        if (hasInternetConnected()) {
            final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "学员端请求个人信息地址：" + volleyUrl);

            StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String jsonData) {//磁盘存储后转至处理
                            Log.e("TAG", "获取学生资料信息" + jsonData);

                            setStore(jsonData);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleySingleton.onErrorResponseMessege(StuInfoActivity.this, volleyError);

                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(tableId, StuPra.stuInfoTableId);
                    paramsMap.put(Constant.pageId, StuPra.stuInfoPageId);
                    Log.e("TAG", "学员端请求个人信息参数：" + paramsMap.toString());
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
        }else{
            try {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "无网络！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                swipeRefreshLayout.setRefreshing(false);//设置不刷新
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    List<Map<String, Object>> fieldSet = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        String jsonData1 = jsonData.replaceAll("00:00:00", "");
        Map<String, Object> stuInfoMap = Utils.str2map(jsonData1);
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> pageSet;
        try {
            dataList = (List<Map<String, Object>>) stuInfoMap.get("dataList");
            pageSet = (Map<String, Object>) stuInfoMap.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList.size() > 0) {
//            dataList.remove(dataList.size()-1);
//            dataList.remove(dataList.size()-1);
            if (stuInfo == null) {
                stuInfo = unionAnalysis(dataList);
                stuInfo.remove(stuInfo.size() - 1);

                stuInfo.remove(stuInfo.size() - 1);
                Log.e("TAG", "=================" + stuInfo.toString());
                //设置适配器
                adapter = new SimpleAdapter(StuInfoActivity.this, stuInfo, R.layout.activity_info_item,
                        new String[]{"fieldCnName", "fieldCnName2"}, new int[]{R.id.tv_name,
                        R.id.tv_entity_name});
                stuInfoLv.setAdapter(adapter);
            } else {
                stuInfo.removeAll(stuInfo);
                stuInfo.addAll(unionAnalysis(dataList));
                stuInfo.remove(stuInfo.size() - 1);
                stuInfo.remove(stuInfo.size() - 1);
            }
        }
    }


    public List<Map<String, String>> unionAnalysis(List<Map<String, Object>> dataListMap) {
        List<Map<String, String>> itemNum = new ArrayList<>();
        if (fieldSet != null && fieldSet.size() > 0) {
            for (int j = 0; j < fieldSet.size(); j++) {
                Map<String, String> property = new HashMap<>();
                property.put("fieldCnName", String.valueOf(fieldSet.get(j).get("fieldCnName")));
                String fieldAliasName = String.valueOf(fieldSet.get(j).get("fieldAliasName"));
                String fieldCnName2 = "";
                if (dataListMap.get(0).get(fieldAliasName) != null) {
                    fieldCnName2 = String.valueOf(dataListMap.get(0).get(fieldAliasName));
                }
                property.put("fieldCnName2", fieldCnName2);
                itemNum.add(property);
            }
        }
        return itemNum;
    }

}
