package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adapter.ListAdapter2;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zfdang.multiple_images_selector.DividerItemDecoration;

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
    RecyclerView mRecyclerView;
    @Bind(R.id.searchButton)
    ImageView searchButton;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;
    private CommonToolbar mToolbar;
    private String titleName;//顶栏名称

    private String tableId, pageId;
    private Map<String, String> paramsMap;

    private String paramsStr;
    private long dataTime = -1;//请求的时间，设为1，总是请求
    private List<Map<String, Object>> buttonSet;//按钮列表数据
    private String searchSet = "";
    private String operaButtonSet;
    private List<List<Map<String, String>>> datas;
    private ListAdapter2 mAdapter;
    private List<Map<String, Object>> childTab = new ArrayList<>();
    private List<Map<String, Object>> childList = new ArrayList<>();

    private int totalNum = 0;
    private int start = 0;
    private final int limit = 20;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avtivity2);
        ButterKnife.bind(this);
        initRefreshLayout();//初始化空间
        getDataIntent();//获取初始化数据
        getData();


    }

    //初始化SwipeRefreshLayout
    private void initRefreshLayout() {
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshData();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (mAdapter.getItemCount() <= totalNum)
                    loadMoreData();
                else {
                    Toast.makeText(ListActivity2.this, "没有更多了", Toast.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
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

        if (intent.getStringExtra("childData") != null) {

            String childData = intent.getStringExtra("childData");
            childList = JSON.parseObject(childData,
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

        if (childList.size() > 0) {
            tableId = childList.get(0).get("tableId") + "";
            pageId = childList.get(0).get("pageId") + "";
            titleName = childList.get(0).get("menuName") + "";
//            list_more_menu.setVisibility(View.VISIBLE);
        } else {
            tableId = itemMap.get("tableId") + "";
            Log.e("TAG", "List_tableId " + tableId);
            pageId = itemMap.get("pageId") + "";
            titleName = itemMap.get("menuName") + "";
        }
        mToolbar.setTitle(titleName);
        paramsMap = new HashMap<>();
        paramsMap.put(tableId, tableId);
        paramsMap.put(pageId, pageId);
        paramsMap.put(Constant.timeName, dataTime + "");
        paramsStr = JSON.toJSONString(paramsMap);
        Constant.paramsMapSearch = paramsMap;
        Constant.mainTableIdValue = tableId;
        Constant.mainPageIdValue = pageId;
    }

    //初始化顶栏
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more)); //右侧pop
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() { //左侧返回按钮
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 获取字段接口数据
     */
    @SuppressWarnings("unchecked")
    public void getData() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "列表请求地址：" + volleyUrl);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获取set" + jsonData);
                        setStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(ListActivity2.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                paramsMap.put("start", start + "");
                if (!Constant.stu_index.equals("")) {
                    paramsMap.put("ctType", Constant.stu_index);
                    paramsMap.put("SourceDataId", Constant.stu_homeSetId);
                    paramsMap.put("pageType", "1");
                    Log.e("TAG", "去看板的列表请求");
                }
                paramsMap.put("limit", limit + "");
                Log.e("TAG", "列表请求参数：" + paramsMap.toString());
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
    }


    /**
     * 4、处理字段接口数据,方法 下一步请求列表数据
     */
    @SuppressWarnings("unchecked")
    public void setStore(String jsonData) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> fieldSet = new ArrayList<>();
        Log.e("TAG", "解析set" + jsonData);
        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
//获取各项总配置pageSet父级
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
////获取时间戳，暂时屏蔽
//            if (setMap.get("alterTime") != null) {
//                dataTime = Utils.ObjectTOLong(setMap.get("alterTime"));
//                //Constant.dataTime= (long) pageSet.get("alterTime");
//                Log.e("TAG", "获取Constant.dataTime" + dataTime);
//            }
//获取条目总数
            totalNum = Integer.valueOf(String.valueOf(setMap.get("dataCount")));


//获取搜索数据，如果有搜索数据但是仅仅是方括号没内容则隐藏搜索框
            if (pageSet.get("serachSet") != null) {
                List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
                searchSet = JSONArray.toJSONString(searchSetList);
                //暂时设置搜索按钮为隐藏，以后做好了再展现
//                    if (searchSetList.size()==0) {
                searchButton.setVisibility(View.GONE);
//                    }
                Log.e("TAG", "获取serachSet" + searchSet);
            } else {//如果彻底无搜索字段则隐藏搜索框
                searchButton.setVisibility(View.GONE);
            }
//获取子项内部按钮
            if (pageSet.get("operaButtonSet") != null) {
                try {
                    List<Map<String, Object>> operaButtonSetList = (List<Map<String, Object>>) pageSet.get("operaButtonSet");
                    operaButtonSet = JSONArray.toJSONString(operaButtonSetList);
                    Log.e("TAG", "获取operaButtonSet" + operaButtonSet);

                    //        //判断条件显示右侧按钮
                    Log.e("TAG", "详情页operaButtonSet " + operaButtonSet);
                    if (operaButtonSetList.size() > 0) {
                        mToolbar.showRightImageButton();
                        //右侧下拉按钮
                        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                popButton();
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//获得子表格：childTabs
            String childTabs;
            if (pageSet.get("childTabs") != null) {
                childTabs = String.valueOf(pageSet.get("childTabs"));
                childTab = JSON.parseObject(childTabs,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            }

//数据左侧配置数据
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("TAG", "获取fieldSet" + fieldSet.toString());
//获取buttonSet
            if (pageSet.get("buttonSet") != null) {
                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
                Log.e("TAG", "获取buttonSet" + buttonSet);
                //判断右上角按钮是否可见
                if (buttonSet.size() > 0) {
                    mToolbar.hideLeftImageButton();
                }
            }
//获取dataList
            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
        }
//将dataList与fieldSet合并准备适配数据
        if (dataList != null && dataList.size() > 0) {
            datas = combineSetData(fieldSet, dataList);
        } else {
            Toast.makeText(ListActivity2.this, "列表无数据",
                    Toast.LENGTH_SHORT).show();
        }
//用适配器并判断展示数据
        showData();
    }

    /**
     * 下拉刷新方法
     */
    private void refreshData() {
        start = 0;
        state = STATE_REFREH;
        getData();

    }
    /**
     * 上拉加载方法
     */
    private void loadMoreData() {

        start += limit;
        state = STATE_MORE;
        getData();

    }

    /**
     * 分动作展示数据
     */
    private void showData() {
        switch (state) {
            case STATE_NORMAL:
                mAdapter = new ListAdapter2(datas, childTab);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(ListActivity2.this));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
                break;
            case STATE_REFREH:
                mAdapter.clearData();
                mAdapter.addData(datas);
                mRecyclerView.scrollToPosition(0);
                mRefreshLayout.finishRefresh();
                break;
            case STATE_MORE:
                mAdapter.addData(mAdapter.getDatas().size(), datas);
                mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
                mRefreshLayout.finishRefreshLoadMore();
                break;
        }
    }

    /**
     * 合并配置和数据，并添加参数
     */
    public List<List<Map<String, String>>> combineSetData(List<Map<String, Object>> set, List<Map<String, Object>> data) {
        List<List<Map<String, String>>> newData = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            List<Map<String, String>> itemNum = new ArrayList<>();
            for (int j = 0; j < set.size(); j++) {
                Map<String, String> property = new HashMap<>();
                if (j == 0) {
                    property.put("isCheck", "false");
                    String mainId = "T_" + tableId + "_0";
                    if (data.get(i).get(mainId) != null) {
                        property.put("mainId", String.valueOf(data.get(i).get(mainId)));
                    } else {
                        property.put("mainId", "");
                    }
                    property.put("tableId", tableId);
                    property.put("allItemData", data.get(i).toString());
                }
                property.put("fieldCnName", String.valueOf(set.get(j).get("fieldCnName")));
                String fieldAliasName = String.valueOf(set.get(j).get("fieldAliasName"));
                String fieldCnName2 = "";
                if (data.get(i).get(fieldAliasName) != null) {
                    fieldCnName2 = String.valueOf(data.get(i).get(fieldAliasName));
                }
                property.put("fieldCnName2", fieldCnName2);
                itemNum.add(property);
            }
            newData.add(itemNum);
        }
        return newData;
    }
    @OnClick(R.id.searchButton)
    public void onClick() {
    }
}
