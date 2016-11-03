package com.kwsoft.kehuhua.adcustom;

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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.kwsoft.kehuhua.adapter.ListAdapter2;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.view.RecycleViewDivider;
import com.kwsoft.kehuhua.view.WrapContentLinearLayoutManager;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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

                if (mAdapter!=null&&mAdapter.getItemCount() < totalNum) {

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
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(Constant.topBarColor));
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
        if (hasInternetConnected()) {

            //地址
            String volleyUrl = Constant.sysUrl + Constant.requestListSet;
            Log.e("TAG", "列表请求地址：" + volleyUrl);

            //参数
            paramsMap.put("start", start + "");
            if (!Constant.stu_index.equals("")) {
                paramsMap.put("ctType", Constant.stu_index);
                paramsMap.put("SourceDataId", Constant.stu_homeSetId);
                paramsMap.put("pageType", "1");
                Log.e("TAG", "去看板的列表请求");
            }
            paramsMap.put("limit", limit + "");

            Log.e(TAG, "getData: paramsMap "+paramsMap.toString());
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(ListActivity2.this) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext,e);
                            mRefreshLayout.finishRefresh();
                            dialog.dismiss();
                            backStart();
                            Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: "+"  id  "+id);

                            setStore(response);
                        }
                    });
    }else{

            dialog.dismiss();
            mRefreshLayout.finishRefresh();
            Toast.makeText(ListActivity2.this, "请连接网络", Toast.LENGTH_SHORT).show();
            backStart();
        }
    }


    public void backStart(){

        //下拉失败后需要将加上limit的strat返还给原来的start，否则会获取不到数据
        if ( state == STATE_MORE) {
            //start只能是limit的整数倍
            if (start>limit) {
                start-=limit;
            }
            mRefreshLayout.finishRefreshLoadMore();
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
        datas = DataProcess.combineSetData(tableId,pageId, fieldSet, dataList);
        Log.e(TAG, "setStore: 将datalist转换为datas");


//        } else {
//            Toast.makeText(ListActivity2.this, "列表无数据",
//                    Toast.LENGTH_SHORT).show();
//        }
//用适配器并判断展示数据
        showData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshData();
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
                        Snackbar.make(mRecyclerView, "刷新完成，共"+totalNum+"条", Snackbar.LENGTH_SHORT).show();
                    }

                }
                break;
            case STATE_MORE:
                if (mAdapter != null) {
                    mAdapter.addData(mAdapter.getDatas().size(), datas);
                    mRecyclerView.scrollToPosition(mAdapter.getDatas().size());
                    mRefreshLayout.finishRefreshLoadMore();
                    Snackbar.make(mRecyclerView, "增加了" + datas.size() + "条数据", Snackbar.LENGTH_SHORT).show();
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
        if (totalNum>0) {
            Snackbar.make(mRecyclerView, "加载完成，共"+totalNum+"条", Snackbar.LENGTH_SHORT).show();
        }else{
            Snackbar.make(mRecyclerView, "本页无数据", Snackbar.LENGTH_SHORT).show();

        }

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
        buttonSetItem.put("tableIdList", tableId);
        buttonSetItem.put("pageIdList", pageId);
        switch (buttonType) {
            case 0://添加页面
                Intent intent = new Intent(mContext, OperateDataActivity.class);
                intent.putExtra("itemSet", JSON.toJSONString(buttonSetItem));
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
                    DisplayMetrics metric = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metric);
                    int width = metric.widthPixels;     // 屏幕宽度（像素）
                    childListPop = new PopupWindow(toolLayout, (width / 3) * 2,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    //设置半透明
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    getWindow().setAttributes(params);

                    childListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams params = getWindow().getAttributes();
                            getWindow().setAttributes(params);
                        }
                    });
                    childListPop.setTouchable(true); // 设置popupwindow可点击
                    childListPop.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    childListPop.setFocusable(true); // 获取焦点
                    childListPop.update();
                    toolLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    int delta = width / 6;
                    childListPop.showAsDropDown(mToolbar, delta, 0);
                    childListPop.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
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


    public void initPopWindowDropdown(View view) {
        //内容，高度，宽度
        toolListPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //动画效果
        toolListPop.setAnimationStyle(R.style.PopupWindowAnimation);
        //菜单背景色
        ColorDrawable dw = new ColorDrawable(0xffffffff);
        toolListPop.setBackgroundDrawable(dw);
        //显示位置
        toolListPop.showAtLocation(getLayoutInflater().inflate(R.layout.activity_list_avtivity2, null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.7f);
        //关闭事件
        toolListPop.setOnDismissListener(new popupDismissListener());
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if( popupWindow!=null && popupWindow.isShowing()){
                    popupWindow.dismiss();
                    popupWindow=null;
                }*/
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                return false;
            }
        });
    }
    /**
     * 设置添加屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     */
    class popupDismissListener implements PopupWindow.OnDismissListener{

        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }

    }

    public void buttonList() {
        try {
            if (toolListPop != null && toolListPop.isShowing()) {
                toolListPop.dismiss();
            } else {
                final View popInflateView = getLayoutInflater().inflate(
                        R.layout.activity_list_buttonlist, null);
                ListView toolListPopView = (ListView) popInflateView
                        .findViewById(R.id.buttonList);
                TextView tv_dismiss = (TextView) popInflateView.findViewById(R.id.tv_dismiss);
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
                initPopWindowDropdown(popInflateView);
            }
        } catch (Exception e) {
            Toast.makeText(ListActivity2.this, "无按钮数据", Toast.LENGTH_SHORT).show();
        }
    }
}
