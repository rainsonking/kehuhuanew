package com.kwsoft.kehuhua.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.ListAdapter;
import com.kwsoft.kehuhua.adcustom.InfoActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/19 0019.
 *
 */
public class TabsFragment extends Fragment {
    private String tableId;
    private String pageId;
    private String mainId;

    private ListView open_lv;
    private ListAdapter listAdapter;
    private Map<String, String> paramsMap;
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private List<Map<String, Object>> dataList = new ArrayList<>();

    private List<List<Map<String, String>>> setAndData;

    private List<Map<String, Object>> childTabs=new ArrayList<>();

    private String operaButtonSet;

    AVLoadingIndicatorView animationList;
RelativeLayout animationListLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    //下拉刷新handler
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x101:
                    Log.e("TAG", "学员端开始handler通知跳转后 ");
                    if (swipeRefreshLayout.isRefreshing()){
                        listAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        Toast.makeText(getActivity().getApplicationContext(), "数据已更新", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_news_fragment, container, false);
            //优化View减少View的创建次数
            //该部分可通过xml文件设计Fragment界面，再通过LayoutInflater转换为View组件
            //这里通过代码为fragment添加一个TextView
//            TextView tvTitle=new TextView(getActivity());
//            tvTitle.setText(channelName);
//            tvTitle.setTextSize(16);
//            tvTitle.setGravity(Gravity.CENTER);
//            tvTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            view=tvTitle;
//        ImageView open_backMenu = (ImageView) view.findViewById(R.id.open_backMenu);
        open_lv=(ListView) view.findViewById(R.id.open_lv);

        animationListLayout= (RelativeLayout) view.findViewById(R.id.open_avloadingIndicatorViewLayoutList);
        animationList= (AVLoadingIndicatorView) view.findViewById(R.id.open_avloadingIndicatorView);
        //下拉刷新设置
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        //设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new LoadDataThread().start();
            }
        });
        startAnim();
        initValue();
        requestSet();
        ViewGroup parent=(ViewGroup)view.getParent();
        if(parent!=null){//如果View已经添加到容器中，要进行删除，负责会报错
            parent.removeView(view);
        }

        open_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //将需要的数据打包：需要展示的子项属性，operabutton数据
                List<Map<String, String>> itemData = new ArrayList<>();
                if (position > 0) {
                    itemData = setAndData.get(position - 1);
                }
                toItem(itemData);
            }
        });
        return view;
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
    private void initValue() {
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainId, mainId);
        paramsMap.put(Constant.mainTableId, Constant.mainTableIdValue);
        paramsMap.put(Constant.mainPageId, Constant.mainPageIdValue);

        String paramsStr = JSON.toJSONString(paramsMap);

    }

    @Override
    public void setArguments(Bundle bundle) {//接收传入的数据
        tableId=bundle.getString("tableId");
        pageId=bundle.getString("pageId");
        mainId=bundle.getString("mainId");
//        String channelName = bundle.getString("name");
    }


    /**
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     */
    @SuppressWarnings("unchecked")
    public void requestSet() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "tab列表请求地址："+volleyUrl);
        Log.e("TAG", "tab列表请求参数："+paramsMap.toString());
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "tab网络请求获取数据" + jsonData);

                        setStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(getActivity(), volleyError);
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
        VolleySingleton.getVolleySingleton(getActivity().getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    private void setStore(String jsonData) {

        Log.e("TAG", "tab解析列表数据" + jsonData);
        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
            //获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");




//行级按钮数据 for 下个页面
            if (pageSet.get("operaButtonSet") != null) {
                    List<Map<String, Object>> operaButtonSetList = (List<Map<String, Object>>) pageSet.get("operaButtonSet");
                    operaButtonSet = JSONArray.toJSONString(operaButtonSetList);
                    Log.e("TAG", "tab获取operaButtonSet" + operaButtonSet);
            }
//获得子表格：childTabs

            if (pageSet.get("childTabs")!=null) {
                String childTabss = String.valueOf(pageSet.get("childTabs"));
                childTabs= JSON.parseObject(childTabss,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                Log.e("TAG", "tab获得子表格：childTabs   " + childTabss);
            }





//数据左侧配置数据

            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
//            if (pageSet.get("buttonSet") != null) {
//                List<Map<String, Object>> buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");
//                if (buttonSet.size()>0) {
//                    //button_set_view.setVisibility(View.VISIBLE);
//                }
//            }
//获取dataList

            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "tab获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
        }
            if (dataList != null) {
                unionAnalysis(dataList);
            } else {
                    stopAnim();
                    Toast.makeText(getActivity(), "列表无数据",
                            Toast.LENGTH_SHORT).show();

            }

    }
    public void unionAnalysis(List<Map<String, Object>> dataListMap) {

        List<List<Map<String, String>>> tabDataList = new ArrayList<>();

        if (fieldSet != null && fieldSet.size() > 0) {
            for (int i = 0; i < dataListMap.size(); i++) {
                List<Map<String, String>> itemNum = new ArrayList<>();
                for (int j = 0; j < fieldSet.size(); j++) {

                    Map<String, String> property = new HashMap<>();
                    if (j == 0) {
                        property.put("isCheck", "false");

                        String mainId = "T_" + tableId + "_0";
                        if (dataListMap.get(i).get(mainId) != null) {
                            property.put("mainId", String.valueOf(dataListMap.get(i).get(mainId)));

                        } else {
                            property.put("mainId", "");

                        }
                        property.put("tableId", tableId);


                    }
                    property.put("fieldCnName", String.valueOf(fieldSet.get(j).get("fieldCnName")));
                    String fieldAliasName = String.valueOf(fieldSet.get(j).get("fieldAliasName"));
                    String fieldCnName2 = "";
                    if (dataListMap.get(i).get(fieldAliasName) != null) {
                        fieldCnName2 = String.valueOf(dataListMap.get(i).get(fieldAliasName));
                    }
                    property.put("fieldCnName2", fieldCnName2);
                    itemNum.add(property);
                }
                tabDataList.add(itemNum);
            }

            if (setAndData==null) {
                setAndData=tabDataList;
                Log.e("TAG", "tab新建adapter" + setAndData.toString());
                toAdapter();
            }else{
                setAndData.removeAll(setAndData);
                setAndData.addAll(tabDataList);
                Log.e("TAG", "tab下拉刷新" + setAndData.toString());
            }

        } else {
            Toast.makeText(getActivity(), "列表中无数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void toAdapter() {
        Log.e("TAG", "tab准备进入适配器" + setAndData);
        listAdapter = new ListAdapter(getActivity(), R.layout.activity_list_item, setAndData,childTabs);
        open_lv.setAdapter(listAdapter);
        stopAnim();
    }

    void startAnim() {
        animationList.setVisibility(View.VISIBLE);
    }

    void stopAnim() {
        animationList.setVisibility(View.GONE);
    }
    /**
     * 跳转至子菜单列表
     */
    public void toItem(List<Map<String, String>> itemData) {

        try {
            String childData = JSONArray.toJSONString(itemData);
            Intent intent = new Intent();
            intent.setClass(getActivity(), InfoActivity.class);
            intent.putExtra("childData", childData);
            Log.e("TAG", "tab的childData"+childData);
            intent.putExtra("tableId", tableId);
            Log.e("TAG", "tab的ListToItem: " + tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}