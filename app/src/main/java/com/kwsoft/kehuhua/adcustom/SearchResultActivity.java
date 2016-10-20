package com.kwsoft.kehuhua.adcustom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.kwsoft.kehuhua.utils.CloseActivityClass;

import butterknife.ButterKnife;

public class SearchResultActivity extends AppCompatActivity {

//    @Bind(R.id.back_search)
//    ImageView backSearch;
//    @Bind(R.id.button_set_search)
//    ImageView buttonSetSearch;
//    @Bind(R.id.button_set_search_delete_commit)
//    ImageView buttonSetSearchDeleteCommit;
//    @Bind(R.id.textViewTitle_search)
//    TextView textViewTitleSearch;
//    @Bind(R.id.lv_search)
//    PullToRefreshListView lvSearch;
//    @Bind(R.id.topBar_search)
//    RelativeLayout topBarSearch;
//    @Bind(R.id.IV_back_search_list)
//    ImageView IVBackSearchList;
//    private PopupWindow toolListPop;
//    private int start = 0;
//    private final int limit = 20;
//    private String delIdStr = "";
//    private List<String> delIdList;
//    private List<Map<String, Object>> childTabs=new ArrayList<>();
//    private long dataTime = -1;
//    private List<List<Map<String, String>>> setAndData = new ArrayList<>();
//    private ListAdapter listAdapter;
//    private String operaButtonSet;
//    private List<Map<String, Object>> fieldSet = new ArrayList<>();
//    private List<Map<String, Object>> dataList = new ArrayList<>();
//    private List<Map<String, Object>> buttonSet;//按钮列表数据
//    private Map<String, String> paramsMapNew = new HashMap<>();
//
//
//
//    private DiskLruCacheHelper DLCH;
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                listAdapter.notifyDataSetChanged();
//                lvSearch.onRefreshComplete();
//                DLCH.put(Constant.sysUrl + Constant.requestListSet +
//                                Constant.USERNAME_ALL + Constant.paramsMapSearch.toString()+"search",
//                        JSONArray.toJSONString(setAndData));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.bind(this);
//        try {
//            DLCH = new DiskLruCacheHelper(SearchResultActivity.this);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        getInfoData();
//        lvSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                pullDownToRefresh();
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//        });
//
//        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //将需要的数据打包：需要展示的子项属性，operabutton数据
//                List<Map<String, String>> itemData = new ArrayList<>();
//                if (position > 0) {
//                    itemData = setAndData.get(position - 1);
//                }
//                toItem(itemData);
//            }
//        });

    }
//    /**
//     * 跳转至子菜单列表
//     */
//    public void toItem(List<Map<String, String>> itemData) {
//
//        String childData = JSONArray.toJSONString(itemData);
//        Intent intent = new Intent();
//        intent.setClass(this, InfoActivity.class);
//        intent.putExtra("childData", childData);
//        intent.putExtra("tableId", Constant.paramsMapSearch.get(Constant.tableId));
//        Log.e("TAG", "ListToItem: " + Constant.paramsMapSearch.get(Constant.tableId));
//        intent.putExtra("operaButtonSet", operaButtonSet);
//        startActivity(intent);
//    }
//    @OnClick({R.id.back_search, R.id.button_set_search, R.id.button_set_search_delete_commit})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.back_search:
//                this.finish();
//                break;
//            case R.id.button_set_search:
//
//                buttonList();
//
//
//                break;
//            case R.id.button_set_search_delete_commit:
//                listDeleteCommit();
//                break;
//        }
//    }
//
//    public void getInfoData() {
//        topBarSearch.setBackgroundColor(getResources().getColor(topBarColor));
//        Intent mIntent = this.getIntent();
//        String searchData = "";
//        try {
//            searchData = mIntent.getStringExtra("result_search");
//           String paramsNext= mIntent.getStringExtra("paramsNext");
//            paramsMapNew=JSON.parseObject(paramsNext,
//                    new TypeReference<Map<String, String>>() {
//                    });
//
//            Log.e("TAG", "result_search " + searchData);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        setStore(searchData);
//
//
//    }
//
//
//    public void setStore(String jsonData) {
//        Log.e("TAG", "解析set" + jsonData);
//        try {
//            Map<String, Object> setMap = JSON.parseObject(jsonData,
//                    new TypeReference<Map<String, Object>>() {
//                    });
//            //获取fieldSet
//            Map<String, Object> pageSet = (Map<String, Object>) setMap.get("pageSet");
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
//            try {
//                childTabss = String.valueOf(pageSet.get("childTabs"));
//                childTabs= JSON.parseObject(childTabss,
//                        new TypeReference<List<Map<String, Object>>>() {
//                        });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            Log.e("TAG", "获得子表格：childTabs   " + childTabss);
////数据左侧配置数据
//
//            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
//            if (pageSet.get("buttonSet") != null) {
//                buttonSet = (List<Map<String, Object>>) pageSet.get("buttonSet");//初始化下拉按钮数据
//                Log.e("TAG", "获取buttonSet" + buttonSet);
//            }
////获取dataList
//
//            dataList = (List<Map<String, Object>>) setMap.get("dataList");
//            Log.e("TAG", "获取dataList" + dataList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (dataList != null && dataList.size() > 0) {
//            unionAnalysis(dataList);
//        } else {
//            String data1 = DLCH.getAsString(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + Constant.paramsMapSearch.toString()+"search");
//            if (data1 != null) {
//
//                setAndData = (List<List<Map<String, String>>>) JSONArray.parse(data1);
//                toAdapter();
//            } else {
//                Toast.makeText(SearchResultActivity.this, "无数据",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//        }
//    }
//
//    public void unionAnalysis(List<Map<String, Object>> dataListMap) {
//
//        int flag = 0;
//        if (setAndData.size() != 0) {
//            flag = 1;
//        }
//        if (dataListMap != null && fieldSet != null && dataListMap.size() > 0 && fieldSet.size() > 0) {
//            for (int i = 0; i < dataListMap.size(); i++) {
//                List<Map<String, String>> itemNum = new ArrayList<>();
//                for (int j = 0; j < fieldSet.size(); j++) {
//
//                    Map<String, String> property = new HashMap<>();
//                    if (j == 0) {
//                        property.put("isCheck", "false");
//
//                        String mainId = "T_" + Constant.paramsMapSearch.get(Constant.tableId) + "_0";
//                        if (dataListMap.get(i).get(mainId) != null) {
//                            property.put("mainId", String.valueOf(dataListMap.get(i).get(mainId)));
//
//                        } else {
//                            property.put("mainId", "");
//
//                        }
//                        property.put("tableId", Constant.paramsMapSearch.get(Constant.tableId));
//
//
//                    }
//                    property.put("fieldCnName", String.valueOf(fieldSet.get(j).get("fieldCnName")));
//                    String fieldAliasName = String.valueOf(fieldSet.get(j).get("fieldAliasName"));
//                    String fieldCnName2 = "";
//                    if (dataListMap.get(i).get(fieldAliasName) != null) {
//                        fieldCnName2 = String.valueOf(dataListMap.get(i).get(fieldAliasName));
//                    }
//                    property.put("fieldCnName2", fieldCnName2);
//                    itemNum.add(property);
//                }
//                setAndData.add(itemNum);
//            }
//
//
//            DLCH.put(Constant.sysUrl + Constant.requestListSet + Constant.USERNAME_ALL + Constant.paramsMapSearch.toString()+"search", JSON.toJSONString(setAndData));
//            //放到adapter中展示
//            if (flag == 0) {
//                toAdapter();
//            }
//
//        } else {
//            Toast.makeText(this, "列表中无数据", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    public void toAdapter() {
//        Log.e("TAG", "进入解析" + setAndData);
//        listAdapter = new ListAdapter(this, R.layout.activity_list_item, setAndData,childTabs);
//        lvSearch.setAdapter(listAdapter);
//    }
//
//
//    public void buttonList() {
//        try {
//            if (buttonSet != null && buttonSet.size() > 0) {
//
//                if (toolListPop != null && toolListPop.isShowing()) {
//                    toolListPop.dismiss();
//                } else {
//                    final View toolLayout = getLayoutInflater().inflate(
//                            R.layout.activity_list_buttonlist, null);
//                    ListView toolListPopView = (ListView) toolLayout
//                            .findViewById(R.id.buttonList);
//                    TextView tv_dismiss = (TextView) toolLayout.findViewById(R.id.tv_dismiss);
//                    tv_dismiss.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            toolListPop.dismiss();
//                        }
//                    });
//                    final SimpleAdapter adapter = new SimpleAdapter(
//                            this,
//                            buttonSet,
//                            R.layout.activity_list_buttonlist_item,
//                            new String[]{"buttonName"},
//                            new int[]{R.id.listItem});
//                    toolListPopView.setAdapter(adapter);
//                    // 点击listview中item的处理
//                    toolListPopView
//                            .setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                                @Override
//                                public void onItemClick(AdapterView<?> arg0,
//                                                        View arg1, int arg2, long arg3) {
//                                    //分类型跳到不同的页面
//                                    int buttonType = (int) buttonSet.get(arg2).get("buttonType");
//                                    Map<String, Object> buttonSetItem = buttonSet.get(arg2);
//                                    String buttonSetItemStr = JSON.toJSONString(buttonSetItem);
//
//                                    switch (buttonType) {
//                                        case 0://添加页面
//                                            Intent intent = new Intent(SearchResultActivity.this, AddItemsActivity.class);
//                                            intent.putExtra("buttonSetItemStr", buttonSetItemStr);
//                                            Log.e("TAG", "buttonSet " + buttonSet.get(arg2));
//                                            startActivityForResult(intent, 5);
//                                            break;
//                                        case 3://批量删除操作
//                                            Log.e("TAG", "buttonSet " + buttonSet.get(arg2));
//
//                                            listAdapter.flag = true;
//                                            listAdapter.notifyDataSetChanged();
//                                            setGone();
//                                            break;
//                                    }
//                                    // 隐藏弹出窗口
//                                    if (toolListPop != null && toolListPop.isShowing()) {
//                                        toolListPop.dismiss();
//                                    }
//                                }
//                            });
//                    // 创建弹出窗口
//                    // 窗口内容为layoutLeft，里面包含一个ListView
//                    // 窗口宽度跟tvLeft一样
//                    toolListPop = new PopupWindow(toolLayout, ViewGroup.LayoutParams.MATCH_PARENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT);
//
//                    ColorDrawable cd = new ColorDrawable(0b1);
//                    toolListPop.setBackgroundDrawable(cd);
//                    toolListPop.setAnimationStyle(R.style.PopupWindowAnimation);
//                    //设置半透明
//                    WindowManager.LayoutParams params = getWindow().getAttributes();
//                    params.alpha = 0.7f;
//                    getWindow().setAttributes(params);
//
//                    toolListPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
//                        @Override
//                        public void onDismiss() {
//                            WindowManager.LayoutParams params = getWindow().getAttributes();
//                            params.alpha = 1f;
//                            getWindow().setAttributes(params);
//                        }
//                    });
//                    toolListPop.update();
//                    toolListPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
//                    toolListPop.setTouchable(true); // 设置popupwindow可点击
//                    toolListPop.setOutsideTouchable(true); // 设置popupwindow外部可点击
//                    toolListPop.setFocusable(true); // 获取焦点
//                    toolListPop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//                    toolListPop.showAtLocation(toolLayout, Gravity.BOTTOM, 0, 0);
//
//                    // 设置popupwindow的位置（相对tvLeft的位置）
//                    int topBarHeight = topBarSearch.getBottom();
//                    toolListPop.showAsDropDown(toolListPopView, 0,
//                            (topBarHeight - toolListPopView.getHeight()) / 2);
//
//                    toolListPop.setTouchInterceptor(new View.OnTouchListener() {
//
//                        @Override
//                        public boolean onTouch(View v, MotionEvent event) {
//                            // 如果点击了popupwindow的外部，popupwindow也会消失
//                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
//                                toolListPop.dismiss();
//                                return true;
//                            }
//                            return false;
//                        }
//                    });
//
//                }
//            } else {
//                Toast.makeText(this, "请联系管理员分配权限", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(SearchResultActivity.this, "无按钮数据", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    public void setGone() {
//
//        buttonSetSearch.setVisibility(View.GONE);
//        backSearch.setVisibility(View.GONE);
//
//        buttonSetSearchDeleteCommit.setVisibility(View.VISIBLE);
//        IVBackSearchList.setVisibility(View.VISIBLE);
//
//    }
//
//    public void setVisible() {
//
//        buttonSetSearch.setVisibility(View.VISIBLE);
//        backSearch.setVisibility(View.VISIBLE);
//        buttonSetSearchDeleteCommit.setVisibility(View.GONE);
//        IVBackSearchList.setVisibility(View.GONE);
//        listAdapter.flag = false;
//        pullDownToRefresh();
//    }
//
//
//    @OnClick(R.id.IV_back_search_list)
//    public void onClick() {
//        setVisible();
//    }
//
//
//
//
//    private void listDeleteCommit() {
//        delIdList = new ArrayList<>();
//        for (int i = 0; i < setAndData.size(); i++) {
//            if (setAndData.get(i).get(0).get("isCheck").equals("true")) {
//                delIdList.add(setAndData.get(i).get(0).get("mainId"));
//            }
//        }
//        String str = delIdList.toString();
//        delIdStr = str.substring(1, str.length() - 1).replace(" ", "");
//        ;
//        Log.e("TAG", "delIdStr:" + delIdStr);
////准备请求删除
//        if (delIdList.size() == 0) {
//            Toast.makeText(SearchResultActivity.this, "请选择", Toast.LENGTH_SHORT).show();
//        } else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("数据库中会同步删除");
//            builder.setTitle("删除学员");
//            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    deleteItems();
//                }
//            });
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            builder.create().show();
//        }
//    }
//
//    private void deleteItems() {
//        final String volleyUrl = Constant.sysUrl + Constant.requestDelete;
//        Log.e("TAG", "获取dataUrl " + volleyUrl);
//        Log.e("TAG", "获取tableId " + Constant.paramsMapSearch.get(Constant.tableId));
//        Log.e("TAG", "获取pageId " + Constant.paramsMapSearch.get(Constant.pageId));
//        Log.e("TAG", "获取delIds " + delIdStr);
//        Log.e("TAG", "获取buttonType " + 3);
//
//        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String jsonData) {//磁盘存储后转至处理
//                        Log.e("TAG", "删除返回数据" + jsonData);
//
//                        Toast.makeText(SearchResultActivity.this, jsonData, Toast.LENGTH_SHORT).show();
//
//                        setVisible();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                //refreshListView.hideFooterView();
//                VolleySingleton.onErrorResponseMessege(SearchResultActivity.this, volleyError);
//
//            }
//        }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put(Constant.tableId, Constant.paramsMapSearch.get(Constant.tableId));
//                map.put(Constant.pageId, Constant.paramsMapSearch.get(Constant.pageId));
//                map.put(Constant.delIds, delIdStr);
//                map.put("buttonType", "3");
//                return map;
//            }
//
//            //重写getHeaders 默认的key为cookie，value则为localCookie
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("cookie", Constant.localCookie);
//                    return headers;
//                } else {
//                    return super.getHeaders();
//                }
//            }
//        };
//        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
//                loginInterfaceData);
//
//
//    }
//
//
//    private void pullDownToRefresh() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (null != Utils.getActiveNetwork(SearchResultActivity.this)) {
//                    try {
//                        Thread.sleep(1000);
//                        if (setAndData != null) {
//                            setAndData.clear();
//                            dataTime = -1;
//                            Log.e("TAG", "clear成功");
//
//                        } else {
//
//                            Log.e("TAG", "没有clear功");
//                        }
//                        dataTime = 0;
//                        try {
//                            requestSearch();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                mHandler.sendEmptyMessage(0);
//            }
//        }).start();
//    }
//
//
//
//    private void requestSearch() {
//        String volleyUrl = Constant.sysUrl + Constant.requestListSet;
//        Log.e("TAG", "网络提交搜索Url " + volleyUrl);
//        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String jsonData) {//磁盘存储后转至处理
//                        Log.e("TAG", "搜索请求返回结果打印" + jsonData);
//                        setStore(jsonData);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                VolleySingleton.onErrorResponseMessege(SearchResultActivity.this, volleyError);
//            }
//        }
//        ) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                return paramsMapNew;
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

}
