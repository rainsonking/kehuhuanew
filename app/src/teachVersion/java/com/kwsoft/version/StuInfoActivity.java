package com.kwsoft.version;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.kwsoft.version.fragment.StuInfoAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.tableId;

public class StuInfoActivity extends AppCompatActivity {

    @Bind(R.id.stu_info_lv)
    ListView stuInfoLv;
    private StuInfoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Map<String, String>> stuInfo;
    //下拉刷新handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x101:
                    Log.e("TAG", "学员端开始handler通知跳转后 ");
                    if (swipeRefreshLayout.isRefreshing()){
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        Toast.makeText(getApplicationContext(), "数据已更新", Toast.LENGTH_SHORT).show();
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
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
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
    class LoadDataThread extends  Thread{
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
    private static final String TAG = "StuInfoActivity";
    @SuppressWarnings("unchecked")
    public void requestSet() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端请求个人信息地址：" + volleyUrl);
//参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(tableId, Constant.teachPerTABLEID);
        paramsMap.put(Constant.pageId, Constant.teachPerPAGEID);
        Log.e("TAG", "学员端请求个人信息参数：" + paramsMap.toString());
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }

    List<Map<String, Object>> fieldSet=new ArrayList<>();

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        String jsonData1=jsonData.replaceAll("00:00:00","");
        Map<String, Object> stuInfoMap = null;
        try {
            stuInfoMap = Utils.str2map(jsonData1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> pageSet;
        try {
           dataList = (List<Map<String, Object>>) stuInfoMap.get("dataList");
            pageSet= (Map<String, Object>) stuInfoMap.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("fieldSet",fieldSet.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (dataList.size() > 0) {
//            dataList.remove(dataList.size()-1);
//            dataList.remove(dataList.size()-1);
            if (stuInfo==null) {
                stuInfo = unionAnalysis(dataList);
//                stuInfo.remove(stuInfo.size()-1);
//                stuInfo.remove(stuInfo.size()-1);
                Log.e("TAG", "=================" + stuInfo.toString());
                //设置适配器
                adapter = new StuInfoAdapter(stuInfo,StuInfoActivity.this);
                stuInfoLv.setAdapter(adapter);
            }else{
                stuInfo.removeAll(stuInfo);
                stuInfo.addAll(unionAnalysis(dataList));
//                stuInfo.remove(stuInfo.size()-1);
//
//                stuInfo.remove(stuInfo.size()-1);

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
