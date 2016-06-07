package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.model.OnRefreshListener;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectSelectActivity extends AppCompatActivity implements OnRefreshListener, View.OnClickListener {

    private PullToRefreshListView refreshListView;
    private DiskLruCacheHelper DLCH;
    private List<Map<String, Object>> projectListMap;

    SimpleAdapter adapter;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                adapter.notifyDataSetChanged();
                refreshListView.onRefreshComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_project_select);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            DLCH = new DiskLruCacheHelper(ProjectSelectActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
        Log.e("TAG", "初始化完毕");
        try {
            requestProjectList();
            Log.e("TAG", "请求数据完毕");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        refreshListView = (PullToRefreshListView) findViewById(R.id.project_list);

        assert refreshListView != null;
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }
        });
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

/**
 *点击第几项目判断是否进入登陆界面
 *
 */
                if (position > 0) {//去掉0的下拉刷新head位置
                    int proIds = 100;
                    try {

                        proIds = Integer.parseInt("" + projectListMap.get(position - 1).get("T_1_0"));
                        Constant.proId = "" + projectListMap.get(position - 1).get("proId");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    switch (proIds) {
                        case 1:
                            Intent intent = new Intent();
                            intent.setClass(ProjectSelectActivity.this, IsLoginActivity.class);

                            startActivity(intent);
                            Log.e("TAG", "准备向主项目跳转");
                            break;

                        case 2:
                            Toast.makeText(ProjectSelectActivity.this, "敬请期待", Toast.LENGTH_SHORT).show();
                            break;
                    }

                }
            }
        });




    }

    /**
     * 下拉刷新
     */
    private void pullDownToRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(null != Utils.getActiveNetwork(ProjectSelectActivity.this)) {
                    try {
                        Thread.sleep(1000);
                        if(projectListMap!=null){
                            projectListMap.clear();
                            Log.e("TAG","clear成功");
                        }else{
                            Log.e("TAG","没有clear功");}
                        try {
                            requestProjectList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }


    /**
     * 第一步，获取项目列表
     */
    public void requestProjectList() {
        final String volleyUrl = Constant.sysUrl + Constant.sysLoginUrl;
        Log.e("TAG", "请求地址" + volleyUrl);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String sysData) {
                        DLCH.put(volleyUrl, sysData);
                        analysisData(sysData);
                        Log.e("TAG", "项目列表：" + sysData);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(ProjectSelectActivity.this, volleyError);


            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("source", "1");
                return map;
            }

        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);

    }

    @Override
    public void onDownPullRefresh() {

    }

    @Override
    public void onLoadingMore() {

    }


    /**
     * 第二步：解析数据
     * <p/>
     * [
     * {
     * "T_1_0": 1,
     * "programa_name": "主项目",
     * "programa_url": "",
     * "pro_port": "8080",
     * "pro_en_name": "edus_auto",
     * "pro_type": 1,
     * "pro_show_way": 0,
     * "proId": "5704e45c7cf6c0b2d9873da6"
     * },
     * {
     * "pro_en_name": "edus_auto",
     * "programa_name": "学员端",
     * "programa_url": "192.168.6.46",
     * "pro_port": "8080",
     * "T_1_0": 2,
     * "pro_show_way": "1",
     * : "57159822f07e75084cb8a1fe"
     * }
     * ]
     */

    public void analysisData(String jsonData) {
        projectListMap = JSON.parseObject(jsonData,
                new TypeReference<List<Map<String, Object>>>() {
                });
        Log.e("TAG", "解析数据完毕"+projectListMap);
        adapter = new SimpleAdapter(this, projectListMap,
                R.layout.activity_project_select_item, new String[]{"programa_name"},
                new int[]{R.id.programa_name});
        refreshListView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {

    }

    private static long exitTime = 0;// 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {

                String msg = "再按一次退出";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                CloseActivityClass.exitClient(this);
            }
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }
}
