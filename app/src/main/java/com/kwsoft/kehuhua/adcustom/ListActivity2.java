package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class ListActivity2 extends AppCompatActivity {


    @Bind(R.id.lv)
    RecyclerView lv;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;
    CommonToolbar mToolbar;
    private List<Map<String, Object>> childList = new ArrayList<>();
    private Map<String, String> paramsMap;
    private String paramsStr;
    private String tableId, pageId;
    private String titleName;//顶栏名称
    private long dataTime = -1;//请求的时间，设为1，总是请求






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avtivity2);
        ButterKnife.bind(this);
        initRefreshLayout();
        getDataIntent();//获取菜单数据
//        requestSet();






    }
//初始化SwipeRefreshLayout
    private  void initRefreshLayout(){

        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

//                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

//                if(currPage <=totalPage)
////                    loadMoreData();
//                else{
//                    Toast.makeText()
                    mRefreshLayout.finishRefreshLoadMore();
//                }
            }
        });
    }

    /**
     * 接收菜单传递过来的模块数据包
     */
    public void getDataIntent() {
        Map<String, Object> itemMap = new HashMap<>();

        Intent intent = getIntent();
        String itemData = intent.getStringExtra("itemData");

        if (intent.getStringExtra("childData")!=null) {

            String childData = intent.getStringExtra("childData");
            childList= JSON.parseObject(childData,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        }
        try {
            itemMap = JSON.parseObject(itemData,
                    new TypeReference<Map<String, Object>>() {
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (childList.size()>0) {
            tableId = childList.get(0).get("tableId") + "";
            pageId = childList.get(0).get("pageId") + "";
            titleName = childList.get(0).get("menuName") + "";
//            list_more_menu.setVisibility(View.VISIBLE);
        }else{
            tableId = itemMap.get("tableId") + "";
            Log.e("TAG", "List_tableId " + tableId);
            pageId = itemMap.get("pageId") + "";
            titleName = itemMap.get("menuName") + "";
        }
        mToolbar.setTitle("属性");
        paramsMap = new HashMap<>();
        paramsMap.put(tableId, tableId);
        paramsMap.put(pageId, pageId);
        paramsMap.put(Constant.timeName, dataTime + "");
        paramsStr = JSON.toJSONString(paramsMap);
        Constant.paramsMapSearch=paramsMap;
        Constant.mainTableIdValue=tableId;
        Constant.mainPageIdValue=pageId;
    }
//初始化顶栏
    public void initView() {

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

//        //判断条件显示右侧按钮
//        Log.e("TAG", "详情页operaButtonSet " +operaButtonSet.toString());
//        if (operaButtonSet.size()>0) {
//            mToolbar.showRightImageButton();
//            //右侧下拉按钮
//            mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popButton();
//                }
//            });
//
//        }


    }



//
//    /**
//     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
//     */
//    @SuppressWarnings("unchecked")
//    public void requestSet() {
//
//        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
//        Log.e("TAG", "列表请求地址："+volleyUrl);
//
//        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String jsonData) {//磁盘存储后转至处理
//                        Log.e("TAG", "获取set" + jsonData);
//
////                        setStore(jsonData);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                VolleySingleton.onErrorResponseMessege(ListActivity2.this, volleyError);
//
//            }
//        }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                paramsMap.put("start", start + "");
//                if (!Constant.stu_index.equals("")) {
//                    paramsMap.put("ctType",Constant.stu_index);
//                    paramsMap.put("SourceDataId",Constant.stu_homeSetId);
//                    paramsMap.put("pageType","1");
//                    Log.e("TAG", "走了学员端请求");
//                }
//
//                paramsMap.put("limit", limit + "");
//                Log.e("TAG", "列表请求参数："+paramsMap.toString());
//                return paramsMap;
//            }
//
//            //重写getHeaders 默认的key为cookie，value则为localCookie
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("cookie", Constant.localCookie);
//                    //Log.d("调试", "headers----------------" + headers);
//                    return headers;
//                } else {
//                    return super.getHeaders();
//                }
//            }
//        };
//        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
//                loginInterfaceData);
//    }
//
//
//
//
//    /**
//     * 4、处理字段接口数据,方法 下一步请求列表数据
//     */
//    @SuppressWarnings("unchecked")
//    public void setStore(String jsonData) {
//        Log.e("TAG", "解析set" + jsonData);
//        try {
//            Map<String, Object> setMap = JSON.parseObject(jsonData,
//                    new TypeReference<Map<String, Object>>() {
//                    });
//            //获取fieldSet
//            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
//////时间戳
////            if (setMap.get("alterTime") != null) {
////                dataTime = Utils.ObjectTOLong(setMap.get("alterTime"));
////                //Constant.dataTime= (long) pageSet.get("alterTime");
////                Log.e("TAG", "获取Constant.dataTime" + dataTime);
////            }
////
//////条目数
////            if (setMap.get("dataCount") != null) {
////                int dataCount = Integer.valueOf(String.valueOf(setMap.get("dataCount")));
////                Log.e("TAG", "获取dataCount" + dataCount);
////            }
////            }
////搜索数据
//            //如果有搜索数据但是仅仅是方括号没内容则隐藏搜索框
//            if (pageSet.get("serachSet") != null) {
//                try {
//                    List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
//                    searchSet = JSONArray.toJSONString(searchSetList);
//                    //暂时设置搜索按钮为隐藏，以后做好了再展现
////                    if (searchSetList.size()==0) {
//                    search_title.setVisibility(View.GONE);
////                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Log.e("TAG", "获取serachSet" + searchSet);
//            }else{//如果彻底无搜索字段则隐藏搜索框
//                search_title.setVisibility(View.GONE);
//            }
//
////行级按钮数据 for 下个页面
//            if (pageSet.get("operaButtonSet") != null) {
//                try {
//
//                    List<Map<String, Object>> operaButtonSetList = (List<Map<String, Object>>) pageSet.get("operaButtonSet");
//                    operaButtonSet = JSONArray.toJSONString(operaButtonSetList);
//                    Log.e("TAG", "获取operaButtonSet" + operaButtonSet);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
////获得子表格：childTabs
//
//            String childTabss= null;
//
//            if (pageSet.get("childTabs")!=null) {
//
//                childTabss = String.valueOf(pageSet.get("childTabs"));
//                childTabs= JSON.parseObject(childTabss,
//                        new TypeReference<List<Map<String, Object>>>() {
//                        });
//            }
//
//
//
////数据左侧配置数据
//
//            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
//            Log.e("TAG", "获取fieldSet" + fieldSet.toString());
//            if (pageSet.get("buttonSet") != null) {
//                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
//                Log.e("TAG", "获取buttonSet" + buttonSet);
//                if (buttonSet.size()>0) {
//                    button_set_view.setVisibility(View.VISIBLE);
//                }
//            }
////获取dataList
//
//            dataList = (List<Map<String, Object>>) setMap.get("dataList");
//            Log.e("TAG", "获取dataList" + dataList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            if (dataList != null && dataList.size() > 0) {
//                unionAnalysis(dataList);
//            } else {
//                String data1 = diskLruCache.getAsString(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + paramsStr);
//                if (data1 != null) {
//
//                    setAndData = (List<List<Map<String, String>>>) JSONArray.parse(data1);
//                    toAdapter();
//                } else {
//                    stopAnim();
//                    Toast.makeText(ListActivity.this, "列表无数据",
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @OnClick(R.id.searchButton)
    public void onClick() {
    }
}
