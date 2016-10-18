package com.kwsoft.kehuhua.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.kwsoft.kehuhua.adcustom.InfoActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.view.WrapContentLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/19 0019.
 *
 */
public class TabsFragment extends Fragment {
    @Bind(R.id.open_lv)
    RecyclerView mRecyclerView;
    @Bind(R.id.refresh_layout)
    MaterialRefreshLayout mRefreshLayout;
    private String tableId;
    private String pageId;
    private String mainId;
    private Map<String, String> paramsMap;
    private String operaButtonSet;

    private int totalNum = 0;
    private int start = 0;
    private final int limit = 20;
    private static final int STATE_NORMAL = 0;
    private static final int STATE_REFREH = 1;
    private static final int STATE_MORE = 2;
    private int state = STATE_NORMAL;
    private ListAdapter2 mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news_fragment, container, false);
        ButterKnife.bind(this, view);
        initRefreshLayout();
        getData();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {//如果View已经添加到容器中，要进行删除，负责会报错
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //初始化SwipeRefreshLayout
    private void initRefreshLayout() {
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.mainId, mainId);
        paramsMap.put(Constant.mainTableId, Constant.mainTableIdValue);
        paramsMap.put(Constant.mainPageId, Constant.mainPageIdValue);
        mRefreshLayout.setLoadMore(true);
        mRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if (mAdapter!=null &&mAdapter.getItemCount() < totalNum) {

                    loadMoreData();
                } else {
                    Snackbar.make(mRecyclerView, "没有更多了", Snackbar.LENGTH_SHORT).show();
                    mRefreshLayout.finishRefreshLoadMore();
                }
            }
        });


    }

    @Override
    public void setArguments(Bundle bundle) {//接收传入的数据
        tableId = bundle.getString("tableId");
        pageId = bundle.getString("pageId");
        mainId = bundle.getString("mainId");
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
                VolleySingleton.onErrorResponseMessege(getActivity(), volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                paramsMap.put("start", start + "");
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
        VolleySingleton.getVolleySingleton(getContext()).addToRequestQueue(
                loginInterfaceData);
    }


    private List<Map<String, Object>> childTab = new ArrayList<>();
    private List<List<Map<String, String>>> datas;


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
//            if (pageSet.get("serachSet") != null) {
//                List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
//                searchSet = JSONArray.toJSONString(searchSetList);
//                //暂时设置搜索按钮为隐藏，以后做好了再展现
////                    if (searchSetList.size()==0) {
//                searchButton.setVisibility(View.GONE);
////                    }
//                Log.e("TAG", "获取serachSet" + searchSet);
//            } else {//如果彻底无搜索字段则隐藏搜索框
//                searchButton.setVisibility(View.GONE);
//            }
//获取子项内部按钮
            if (pageSet.get("operaButtonSet") != null) {
                try {
                    List<Map<String, Object>> operaButtonSetList = (List<Map<String, Object>>) pageSet.get("operaButtonSet");
                    operaButtonSet = JSONArray.toJSONString(operaButtonSetList);
                    Log.e("TAG", "获取operaButtonSet" + operaButtonSet);

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
//            if (pageSet.get("buttonSet") != null) {
//                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
//                Log.e("TAG", "获取buttonSet" + buttonSet);
//                //判断右上角按钮是否可见
//                if (buttonSet.size() > 0) {
//                    mToolbar.showRightImageButton();
//                    //右侧下拉按钮
//                    initButtonsetData();
//                    mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
////                            showButtonSet();
//                            buttonList();
//                        }
//                    });
//
//                } else {
//                    mToolbar.hideRightImageButton();
//                }
//            }
//获取dataList
            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
//            dialog.dismiss();
        }
//将dataList与fieldSet合并准备适配数据
        datas = DataProcess.combineSetData(tableId, fieldSet, dataList);
        if (datas == null) {
            Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();

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
                normalRequest();
                break;
            case STATE_REFREH:
                if (mAdapter != null) {

                    mAdapter.clearData();
                    mAdapter.addData(datas);
                    mRecyclerView.scrollToPosition(0);
                    mRefreshLayout.finishRefresh();
                    if (datas.size() == 0) {
                        Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(mRecyclerView, "更新完成", Snackbar.LENGTH_SHORT).show();
                    }

                }
                break;
            case STATE_MORE:
                if (mAdapter != null) {
                    mAdapter.addData(mAdapter.getDatas().size(), datas);
                    mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(mRecyclerView, "更新了" + datas.size() + "条数据", Snackbar.LENGTH_SHORT).show();
                }

                break;
        }
    }

    public void normalRequest() {
        mAdapter = new ListAdapter2(datas, childTab);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//      mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mAdapter.setOnItemClickListener(new ListAdapter2.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.e("TAG", "data " + data);
                toItem(data);
            }
        });

    }
    /**
     * 跳转至子菜单列表
     */
    public void toItem(String itemData) {
        try {
            Intent intent = new Intent();
            intent.setClass(getActivity(), InfoActivity.class);
            intent.putExtra("childData", itemData);
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}