package com.kwsoft.kehuhua.adcustom;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.view.RecycleViewDivider;
import com.kwsoft.kehuhua.view.WrapContentLinearLayoutManager;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListActivity2 extends BaseActivity {


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

    private List<Map<String, Object>> buttonSet;//按钮列表数据
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


    private PopupWindow toolListPop, childListPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avtivity2);
        ButterKnife.bind(this);
        dialog.show();
        initRefreshLayout();//初始化空间
        initView();
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

                if (mAdapter.getItemCount() < totalNum) {

                    loadMoreData();
                } else {
//                    Snackbar.make(mRecyclerView, "没有更多了", Snackbar.LENGTH_SHORT).show();
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
            mToolbar.showChildIv();
//            initData();
            mToolbar.setTextTitleOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    popChildMenu(v);
                    childChose();
                }
            });

        } else {
            tableId = itemMap.get("tableId") + "";
            Log.e("TAG", "List_tableId " + tableId);
            pageId = itemMap.get("pageId") + "";
            titleName = itemMap.get("menuName") + "";
        }
        mToolbar.setTitle(titleName);
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        long dataTime = -1;
        paramsMap.put(Constant.timeName, dataTime + "");
        Constant.paramsMapSearch = paramsMap;
        Constant.mainTableIdValue = tableId;
        Constant.mainPageIdValue = pageId;
    }

    //初始化顶栏
    public void initView() {
        Context mContext = this;
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(Constant.topBarColor));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more)); //右侧pop
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() { //左侧返回按钮
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout mEmptyView = (LinearLayout) View.inflate(mContext, R.layout.view_empty, null);
    }

    /**
     * 获取字段接口数据
     */
    @SuppressWarnings("unchecked")
    public void getData() {
        if (hasInternetConnected()) {

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
                mRefreshLayout.finishRefresh();
                dialog.dismiss();
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
    }else{
            Log.e("TAG", "无网络");
            mRefreshLayout.finishRefresh();
            Snackbar.make(mRecyclerView, "请连接网络", Snackbar.LENGTH_SHORT).show();
        }
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
                String searchSet = JSONArray.toJSONString(searchSetList);
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
                    mToolbar.showRightImageButton();
                    //右侧下拉按钮
//                    initButtonsetData();
                    mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            showButtonSet();
                            buttonList();
                        }
                    });

                } else {
                    mToolbar.hideRightImageButton();
                }
            }
//获取dataList
            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
        }
//将dataList与fieldSet合并准备适配数据
//        if (dataList != null && dataList.size() > 0) {
        datas = DataProcess.combineSetData(tableId, fieldSet, dataList);
        Log.e(TAG, "setStore: 将datalist转换为datas");
        if (datas == null) {
            Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();

        }


//        } else {
//            Toast.makeText(ListActivity2.this, "列表无数据",
//                    Toast.LENGTH_SHORT).show();
//        }
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
        Log.e(TAG, "showData: "+state);
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

    private static final String TAG = "ListActivity2";
    public void normalRequest() {
        Log.e(TAG, "normalRequest: ");
        mAdapter = new ListAdapter2(datas, childTab);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(ListActivity2.this));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(mContext, LinearLayoutManager.VERTICAL));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mAdapter.setOnItemClickListener(new ListAdapter2.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, String data) {
                Log.e("TAG", "data " + data);
                toItem(data);
            }
        });
        dialog.dismiss();

    }

    @OnClick(R.id.searchButton)
    public void onClick() {
    }

    /**
     * 跳转至子菜单列表
     */
    public void toItem(String itemData) {
        try {
            Intent intent = new Intent();
            intent.setClass(this, InfoActivity.class);
            intent.putExtra("childData", itemData);
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void toPage(int position) {
        int buttonType = (int) buttonSet.get(position).get("buttonType");
        Map<String, Object> buttonSetItem = buttonSet.get(position);
        String buttonSetItemStr = JSON.toJSONString(buttonSetItem);

        switch (buttonType) {
            case 0://添加页面
                Intent intent = new Intent(ListActivity2.this, AddItemsActivity.class);
                intent.putExtra("buttonSetItemStr", buttonSetItemStr);
                startActivityForResult(intent, 5);
                break;
            case 3://批量删除操作
//              listAdapter.flag = true;
//              listAdapter.notifyDataSetChanged();
//              setGone();
                break;
        }
    }


    public void refreshPage(int position) {
        Log.e("TAG", "list子菜单position " + position);
        tableId = childList.get(position).get("tableId") + "";
        pageId = childList.get(position).get("pageId") + "";
        titleName = childList.get(position).get("menuName") + "";

        Log.e("TAG", "list子菜单position " + position);
        //重新设置顶部名称
        mToolbar.setTitle(titleName);
        //重设参数值
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        Constant.paramsMapSearch = paramsMap;
        Constant.mainTableIdValue = tableId;
        Constant.mainPageIdValue = pageId;
        //重新请求数据
        refreshData();

    }

    //顶部展开popwindow 选择子菜单切换
    public void childChose() {
        Log.e("TAG", "展开子菜单popWindow");
        try {
            if (childList.size() > 0) {

                if (childListPop != null && childListPop.isShowing()) {
                    childListPop.dismiss();
                } else {


                    final View toolLayout = getLayoutInflater().inflate(
                            R.layout.activity_list_childlist, null);
                    ListView childListPopView = (ListView) toolLayout
                            .findViewById(R.id.child_menu_List);
                    for (int i = 0; i < childList.size(); i++) {
                        childList.get(i).put("image", R.mipmap.often_drop_curriculum);
                    }

                    final SimpleAdapter adapter = new SimpleAdapter(
                            this,
                            childList,
                            R.layout.activity_list_childlist_item,
                            new String[]{"image", "menuName"},
                            new int[]{R.id.childListItemImg, R.id.childListItemName});
                    childListPopView.setAdapter(adapter);
                    // 点击listview中item的处理
                    childListPopView
                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> arg0,
                                                        View arg1, int arg2, long arg3) {
                                    refreshPage(arg2);

                                    // 隐藏弹出窗口
                                    if (childListPop != null && childListPop.isShowing()) {
                                        childListPop.dismiss();
                                    }
                                }
                            });
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
//                    childListPop = new PopupWindow(toolLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    DisplayMetrics metric = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metric);
                    int width = metric.widthPixels;     // 屏幕宽度（像素）
                    childListPop = new PopupWindow(toolLayout, (width / 3) * 2,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    //设置颜色
                    // ColorDrawable cd = new ColorDrawable(0b1);
                    // childListPop.setBackgroundDrawable(cd);
                    //childListPop.setBackgroundDrawable(getResources().getDrawable(R.mipmap.often_drop_curriculum_bg));
                    //设置半透明
                    WindowManager.LayoutParams params = getWindow().getAttributes();
//                    params.alpha = 0.7f;
                    getWindow().setAttributes(params);

                    childListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams params = getWindow().getAttributes();
                            //  params.alpha = 1f;
                            getWindow().setAttributes(params);
                        }
                    });

//                    childListPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    childListPop.setTouchable(true); // 设置popupwindow可点击
                    childListPop.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    childListPop.setFocusable(true); // 获取焦点
//                    childListPop.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    // 设置popupwindow的位置（位于顶栏下方）

                    childListPop.update();

                    toolLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int popupWidth = toolLayout.getMeasuredWidth();

                    Log.e("TAG", "popupWidth " + popupWidth);


                    //获取两者的宽度
//                    int childListPopWith=Utils.getViewHeight(toolLayout, false);

                    int rlTopBarWith = mToolbar.getWidth();
                    Log.e("TAG", "rlTopBarWith " + rlTopBarWith);


                   // int delta = (rlTopBarWith - popupWidth) / 8;
                    int delta = width / 6;
                    Log.e("TAG", "X偏移量 " + delta);
                    childListPop.showAsDropDown(mToolbar, delta, 0);

                    childListPop.setTouchInterceptor(new View.OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            // 如果点击了popupwindow的外部，popupwindow也会消失
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                childListPop.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonList() {
        try {


            if (toolListPop != null && toolListPop.isShowing()) {
                toolListPop.dismiss();
            } else {
                final View toolLayout = getLayoutInflater().inflate(
                        R.layout.activity_list_buttonlist, null);
                ListView toolListPopView = (ListView) toolLayout
                        .findViewById(R.id.buttonList);
                TextView tv_dismiss = (TextView) toolLayout.findViewById(R.id.tv_dismiss);
                tv_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toolListPop.dismiss();
                    }
                });
                final SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        buttonSet,
                        R.layout.activity_list_buttonlist_item,
                        new String[]{"buttonName"},
                        new int[]{R.id.listItem});
                toolListPopView.setAdapter(adapter);
                // 点击listview中item的处理
                toolListPopView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                toPage(arg2);
                                // 隐藏弹出窗口
                                if (toolListPop != null && toolListPop.isShowing()) {
                                    toolListPop.dismiss();
                                }
                            }
                        });
                // 创建弹出窗口
                // 窗口内容为layoutLeft，里面包含一个ListView
                // 窗口宽度跟tvLeft一样
                toolListPop = new PopupWindow(toolLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                ColorDrawable cd = new ColorDrawable(0b1);
                toolListPop.setBackgroundDrawable(cd);
                toolListPop.setAnimationStyle(R.style.PopupWindowAnimation);
                //设置半透明
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.7f;
                getWindow().setAttributes(params);

                toolListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.alpha = 1f;
                        getWindow().setAttributes(params);
                    }
                });
                toolListPop.update();
                toolListPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                toolListPop.setTouchable(true); // 设置popupwindow可点击
                toolListPop.setOutsideTouchable(true); // 设置popupwindow外部可点击
                toolListPop.setFocusable(true); // 获取焦点
                toolListPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                toolListPop.showAtLocation(toolLayout, Gravity.BOTTOM, 0, 0);

                // 设置popupwindow的位置（相对tvLeft的位置）
                int topBarHeight = mToolbar.getBottom();
                toolListPop.showAsDropDown(toolListPopView, 0,
                        (topBarHeight - toolListPopView.getHeight()) / 2);

                toolListPop.setTouchInterceptor(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // 如果点击了popupwindow的外部，popupwindow也会消失
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            toolListPop.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

            }
        } catch (Exception e) {
            Toast.makeText(ListActivity2.this, "无按钮数据", Toast.LENGTH_SHORT).show();
        }
    }
}
