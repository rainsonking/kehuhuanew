package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kwsoft.kehuhua.adapter.ListAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.adcustom.R.id.topBar;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;


/**
 * 展示列表页面对应类
 */
public class ListActivity extends BaseActivity implements View.OnClickListener {

    @Bind(android.R.id.list)
    PullToRefreshListView refreshListView;
    private String titleName;//请求时间
    //磁盘存储变量
    private DiskLruCacheHelper diskLruCache;
    //右上角下拉按钮
    private PopupWindow toolListPop, childListPop;
    //新接口梳理变量
    private Map<String, String> paramsMap;
    private String paramsStr;
    private String tableId, pageId;
    private ListAdapter listAdapter;
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private List<Map<String, Object>> dataList = new ArrayList<>();
    private String operaButtonSet;
    private String searchSet = "";
    private List<List<Map<String, String>>> setAndData = new ArrayList<>();
    //整个列表数据
    private List<Map<String, Object>> buttonSet;//按钮列表数据

    private List<Map<String, Object>> childTabs = new ArrayList<>();
    //上拉分页加载
    private int start = 0;
    private final int limit = 20;
    private String delIdStr = "";

    private long dataTime = -1;
    @Bind(R.id.button_set)
    ImageView button_set_view;
    @Bind(R.id.button_set_delete_commint)
    ImageView deleteCommit;
    @Bind(R.id.searchButton)
    ImageView search_title;
    @Bind(R.id.IV_back_list)
    ImageView backList;
    @Bind(R.id.backMenu)
    ImageView backMenu;
    @Bind(topBar)
    RelativeLayout rlTopBar;

    @Bind(R.id.list_more_menu)
    ImageView list_more_menu;

    @Bind(R.id.textViewTitle)
    TextView tv_title;

    private List<Map<String, Object>> childList = new ArrayList<>();
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                listAdapter.notifyDataSetChanged();
                refreshListView.onRefreshComplete();
                diskLruCache.put(Constant.sysUrl + Constant.requestListSet +
                                Constant.USERNAME_ALL + paramsStr,
                        JSONArray.toJSONString(setAndData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            diskLruCache = new DiskLruCacheHelper(ListActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startAnim();
        getDataIntent();//获取菜单数据
        init();
        try {
            requestSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pullDownToRefresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                pullUpToRefresh();
            }
        });
        Log.e("TAG", "检查点：点击子项");


        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            list_more_menu.setVisibility(View.VISIBLE);
        } else {
            tableId = itemMap.get("tableId") + "";
            Log.e("TAG", "List_tableId " + tableId);
            pageId = itemMap.get("pageId") + "";
            titleName = itemMap.get("menuName") + "";
        }

        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsMap.put(Constant.timeName, dataTime + "");
        paramsStr = JSON.toJSONString(paramsMap);
        Constant.paramsMapSearch = paramsMap;
        Constant.mainTableIdValue = tableId;
        Constant.mainPageIdValue = pageId;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.textViewTitle:
                childChose();
                break;


            case R.id.button_set:
                buttonList();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.IV_back_list:
                setVisible();
                break;

            case R.id.searchButton:
                toSearchActivity();
                break;
            case R.id.backMenu:
                finish();
                break;
            case R.id.button_set_delete_commint:
                listDeleteCommit();
                break;
            default:
                break;
        }
    }

    private void listDeleteCommit() {
        List<String> delIdList = new ArrayList<>();
        for (int i = 0; i < setAndData.size(); i++) {
            if (setAndData.get(i).get(0).get("isCheck").equals("true")) {
                delIdList.add(setAndData.get(i).get(0).get("mainId"));
            }
        }
        String str = delIdList.toString();
        delIdStr = str.substring(1, str.length() - 1).replace(" ", "");
        Log.e("TAG", "delIdStr:" + delIdStr);
//准备请求删除
        if (delIdList.size() == 0) {
            Toast.makeText(ListActivity.this, "请选择", Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("数据库中会同步删除");
            builder.setTitle("删除学员");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    deleteItems();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private void deleteItems() {
        final String volleyUrl = Constant.sysUrl + Constant.requestDelete;
        Log.e("TAG", "获取dataUrl " + volleyUrl);
        Log.e("TAG", "获取tableId " + tableId);
        Log.e("TAG", "获取pageId " + pageId);
        Log.e("TAG", "获取delIds " + delIdStr);
        Log.e("TAG", "获取buttonType " + 3);

        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "删除返回数据" + jsonData);

                        Toast.makeText(ListActivity.this, jsonData, Toast.LENGTH_SHORT).show();

                        setVisible();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //refreshListView.hideFooterView();
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put(Constant.tableId, tableId);
                map.put(Constant.pageId, pageId);
                map.put(Constant.delIds, delIdStr);
                map.put("buttonType", "3");
                return map;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }


    @Override
    public void initView() {

    }


    public void init() {

        deleteCommit.setOnClickListener(this);
        search_title.setOnClickListener(this);
        backMenu.setOnClickListener(this);
        backList.setOnClickListener(this);
        button_set_view.setOnClickListener(this);
        tv_title.setOnClickListener(this);
        tv_title.setText(titleName);
        rlTopBar.setBackgroundColor(getResources().getColor(topBarColor));
//        diskCheck();

    }

    /**
     * 1、顶栏右侧添加学员和返回按钮事件
     * 2、顶栏左侧返回上级页面
     * 3、搜索弹窗重置条件按钮
     * 4、搜索弹窗之搜索按钮
     */


    public void toSearchActivity() {
        try {
            Intent intent = new Intent();
            intent.setClass(ListActivity.this, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("searchSet", searchSet);
            bundle.putString("paramsStr", paramsStr);
            intent.putExtras(bundle);
            startActivity(intent);//这里采用startActivityForResult来做跳转，此处的0为一个依据，可以写其他的值，但一定要>=0
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buttonList() {
        try {
            if (buttonSet != null && buttonSet.size() > 0) {

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
                                    //分类型跳到不同的页面
                                    int buttonType = (int) buttonSet.get(arg2).get("buttonType");
                                    Map<String, Object> buttonSetItem = buttonSet.get(arg2);
                                    String buttonSetItemStr = JSON.toJSONString(buttonSetItem);

                                    switch (buttonType) {
                                        case 0://添加页面
                                            Intent intent = new Intent(ListActivity.this, AddItemsActivity.class);
                                            intent.putExtra("buttonSetItemStr", buttonSetItemStr);
                                            startActivityForResult(intent, 5);
                                            break;
                                        case 3://批量删除操作
                                            listAdapter.flag = true;
                                            listAdapter.notifyDataSetChanged();
                                            setGone();
                                            break;
                                    }
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
                    int topBarHeight = rlTopBar.getBottom();
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
            } else {
                Toast.makeText(this, "请联系管理员分配权限", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(ListActivity.this, "无按钮数据", Toast.LENGTH_SHORT).show();
        }
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
                                    //
                                    Map<String, Object> childItem = childList.get(arg2);

                                    tableId = childList.get(arg2).get("tableId") + "";
                                    pageId = childList.get(arg2).get("pageId") + "";
                                    titleName = childList.get(arg2).get("menuName") + "";
                                    //重新设置顶部名称
                                    tv_title.setText(titleName);
                                    //重设参数值
                                    paramsMap.put(Constant.tableId, tableId);
                                    paramsMap.put(Constant.pageId, pageId);
                                    paramsStr = JSON.toJSONString(paramsMap);
                                    Constant.paramsMapSearch = paramsMap;
                                    Constant.mainTableIdValue = tableId;
                                    Constant.mainPageIdValue = pageId;

                                    //重新刷新页面并装填数据

                                    refreshListView();

                                    // 隐藏弹出窗口
                                    if (childListPop != null && childListPop.isShowing()) {
                                        childListPop.dismiss();
                                    }
                                }
                            });
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    childListPop = new PopupWindow(toolLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    //设置颜色
                    ColorDrawable cd = new ColorDrawable(0b1);
                    childListPop.setBackgroundDrawable(cd);
                    //设置半透明
                    WindowManager.LayoutParams params = getWindow().getAttributes();
                    params.alpha = 0.7f;
                    getWindow().setAttributes(params);

                    childListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams params = getWindow().getAttributes();
                            params.alpha = 1f;
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

                    int rlTopBarWith = rlTopBar.getWidth();
                    Log.e("TAG", "rlTopBarWith " + rlTopBarWith);


                    int delta = (rlTopBarWith - popupWidth) / 8;
                    Log.e("TAG", "X偏移量 " + delta);
                    childListPop.showAsDropDown(rlTopBar, delta, 0);

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

    public void setGone() {
        search_title.setVisibility(View.GONE);
        button_set_view.setVisibility(View.GONE);
        backMenu.setVisibility(View.GONE);

        deleteCommit.setVisibility(View.VISIBLE);
        backList.setVisibility(View.VISIBLE);

    }

    public void setVisible() {
//        search_title.setVisibility(View.VISIBLE);
        button_set_view.setVisibility(View.VISIBLE);
        backMenu.setVisibility(View.VISIBLE);
        deleteCommit.setVisibility(View.GONE);
        backList.setVisibility(View.GONE);
        if (listAdapter != null) {
            listAdapter.flag = false;
        }

        pullDownToRefresh();
    }


    public void diskCheck() {

        String data1 = diskLruCache.getAsString(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + paramsStr);
        if (data1 != null) {
            dataTime = Utils.getAlterTime(data1);
        }
    }


    /**
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     */
    @SuppressWarnings("unchecked")
    public void requestSet() {

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
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
                Log.e("TAG", "获取本地");
                String data1 = diskLruCache.getAsString(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + paramsStr);
                if (data1 != null) {
                    try {
                        setAndData = (List<List<Map<String, String>>>) JSONArray.parse(data1);
                        Log.e("TAG", "开始适配器启动");
                        toAdapter();
                        Log.e("TAG", "启动完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    stopAnim();
                }
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
                    Log.e("TAG", "走了学员端请求");
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
        Log.e("TAG", "解析set" + jsonData);
        try {
            Map<String, Object> setMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
            //获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
////时间戳
//            if (setMap.get("alterTime") != null) {
//                dataTime = Utils.ObjectTOLong(setMap.get("alterTime"));
//                //Constant.dataTime= (long) pageSet.get("alterTime");
//                Log.e("TAG", "获取Constant.dataTime" + dataTime);
//            }
//
////条目数
//            if (setMap.get("dataCount") != null) {
//                int dataCount = Integer.valueOf(String.valueOf(setMap.get("dataCount")));
//                Log.e("TAG", "获取dataCount" + dataCount);
//            }
//            }
//搜索数据
            //如果有搜索数据但是仅仅是方括号没内容则隐藏搜索框
            if (pageSet.get("serachSet") != null) {
                try {
                    List<Map<String, Object>> searchSetList = (List<Map<String, Object>>) pageSet.get("serachSet");
                    searchSet = JSONArray.toJSONString(searchSetList);
                    //暂时设置搜索按钮为隐藏，以后做好了再展现
//                    if (searchSetList.size()==0) {
                    search_title.setVisibility(View.GONE);
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("TAG", "获取serachSet" + searchSet);
            } else {//如果彻底无搜索字段则隐藏搜索框
                search_title.setVisibility(View.GONE);
            }

//行级按钮数据 for 下个页面
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

            String childTabss = null;

            if (pageSet.get("childTabs") != null) {

                childTabss = String.valueOf(pageSet.get("childTabs"));
                childTabs = JSON.parseObject(childTabss,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
            }


//数据左侧配置数据

            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
            Log.e("TAG", "获取fieldSet" + fieldSet.toString());
            if (pageSet.get("buttonSet") != null) {
                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
                Log.e("TAG", "获取buttonSet" + buttonSet);
                if (buttonSet.size() > 0) {
                    button_set_view.setVisibility(View.VISIBLE);
                }
            }
//获取dataList

            dataList = (List<Map<String, Object>>) setMap.get("dataList");
            Log.e("TAG", "获取dataList" + dataList);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (dataList != null && dataList.size() > 0) {
                unionAnalysis(dataList);
            } else {
                String data1 = diskLruCache.getAsString(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + paramsStr);
                if (data1 != null) {

                    setAndData = (List<List<Map<String, String>>>) JSONArray.parse(data1);
                    toAdapter();
                } else {
                    stopAnim();
                    Toast.makeText(ListActivity.this, "列表无数据",
                            Toast.LENGTH_SHORT).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 5、获取列表接口数据,如果没有网络或者其他情况则读取本地
     */

    public void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestListData;


        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "单独获取的获取列表数据" + jsonData);
                        dataStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //refreshListView.hideFooterView();
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                map.put("limit", limit + "");
                map.put("start", start + "");
                if (!Constant.stu_index.equals("")) {
                    map.put("index", Constant.stu_index);
                    map.put("homeSetId", Constant.stu_homeSetId);
                }
                map.put(Constant.timeName, dataTime + "");
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
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }

    @SuppressWarnings("unchecked")
    public void dataStore(String jsonData) {

        try {
            Map<String, Object> dataMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });

            if (dataMap.get("rows") == null) {

                Toast.makeText(ListActivity.this, "获取失败",
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG", "下拉刷新" + dataMap);
                List<Map<String, Object>> dataList1 =
                        (List<Map<String, Object>>) dataMap.get("rows");
                if (dataList1.size() > 0) {
                    unionAnalysis(dataList1);

                } else {
                    Toast.makeText(ListActivity.this, "无更多数据",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unionAnalysis(List<Map<String, Object>> dataListMap) {
        Log.e("TAG", "进入联合解析" + dataListMap.toString());
        int flag = 0;
        if (setAndData.size() != 0) {
            flag = 1;
        }
        if (fieldSet != null && dataListMap.size() > 0 && fieldSet.size() > 0) {
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
                        property.put("allItemData", dataListMap.get(i).toString());

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
                setAndData.add(itemNum);
            }
            Log.e("TAG", "获取的增加的setAndData" + setAndData.toString());

            diskLruCache.put(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + paramsStr, JSON.toJSONString(setAndData));
            //放到adapter中展示
            if (flag == 0) {
                toAdapter();
            } else {
                Log.e("TAG", "提醒适配器更新数据1");
                listAdapter.notifyDataSetChanged();
                Log.e("TAG", "提醒适配器更新数据2");
                refreshListView.onRefreshComplete();
                Log.e("TAG", "提醒适配器更新数据3");
            }

        } else {
            Toast.makeText(this, "列表中无数据", Toast.LENGTH_SHORT).show();
        }
    }

    public void toAdapter() {
        Log.e("TAG", "准备进入适配器：setAndData " + setAndData.toString());
        Log.e("TAG", "获得子表格：childTabs " + childTabs.toString());
        listAdapter = new ListAdapter(this, R.layout.activity_list_item, setAndData, childTabs);
        refreshListView.setAdapter(listAdapter);
        stopAnim();
    }

    //适配方法


    /**
     * 下拉刷新
     */
    private void pullDownToRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                refreshListView();
            }
        }).start();
    }

    /**
     * 上拉加载
     */
    private void pullUpToRefresh() {

        if (null != Utils.getActiveNetwork(ListActivity.this)) {
            try {
                Thread.sleep(1000);
                start += limit;
                requestData();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessage(0);


    }

    /**
     * 跳转至子菜单列表
     */
    public void toItem(List<Map<String, String>> itemData) {

        try {
            String childData = JSONArray.toJSONString(itemData);
            Intent intent = new Intent();
            intent.setClass(this, InfoActivity.class);
            intent.putExtra("childData", childData);
            intent.putExtra("tableId", tableId);
            intent.putExtra("operaButtonSet", operaButtonSet);
            Log.e("TAG", "operaButtonSet" + operaButtonSet);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//刷新主列表通用方法

    public void refreshListView() {
        if (null != Utils.getActiveNetwork(ListActivity.this)) {
            try {

                if (setAndData != null) {
                    setAndData.clear();
                    dataTime = -1;
                    start = 0;
                    Log.e("TAG", "clear成功");

                } else {

                    Log.e("TAG", "没有clear功");
                }
                dataTime = 0;
                try {
                    requestSet();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessage(0);
    }

//    @Override
//    protected void onResume() {
//
//        pullDownToRefresh();
//        super.onResume();
//    }

    @Override
    protected void onRestart() {
        listAdapter.notifyDataSetChanged();
        super.onRestart();
    }

    void startAnim() {
        findViewById(R.id.avloadingIndicatorViewLayoutList).setVisibility(View.VISIBLE);
    }

    void stopAnim() {
        findViewById(R.id.avloadingIndicatorViewLayoutList).setVisibility(View.GONE);
    }
}
