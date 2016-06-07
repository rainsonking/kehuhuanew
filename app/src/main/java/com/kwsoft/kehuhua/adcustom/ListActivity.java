package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.StudentAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.Time;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.model.OnDataListener;
import com.kwsoft.kehuhua.model.OnRefreshListener;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.view.RefreshListView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 展示列表页面对应类
 *
 */
public class ListActivity extends BaseActivity implements OnDataListener, View.OnClickListener, OnRefreshListener,PopupWindow.OnDismissListener {
    private RefreshListView refreshListView;
    private StudentAdapter listAdapter;
    private boolean isMulChoice = false; //是否多选
    private LinearLayout linearLayout;
    private ImageView iv_back, buttonList;//返回按钮
    private TextView tv_cancle;
    private TextView tv_commit;
    private String timeInterface,titleName;//请求时间
    public static boolean isForeground = false;
    private Map<String,Object> phoneFieldSetMap;//解析取到的配置数据
    private Map<String,Object> phoneDataListMap;//解析取到的列表data数据
    private List<Map<String,Object>> phoneOperaButtonList;//解析取到的列表data数据
    private List<List<Map<String, String>>> listMapUnion;//解析完毕且整理好的数据
    private String timeData;
    private String operaButtonData;

    // 以下为磁盘存储变量

    private DiskLruCacheHelper DLCH;
    private int dataTableId,dataPageId;
    public String timeUrlAField,timeUrlAOperaButton,timeUrlAButton,timeUrlAData,deleteButtonTurnUrl,addButtonCommitUrlV,addButtonCommitUrl;
    public List<Map<String,Object>> phoneButtonSetList;
    public long buttonTime,dataTime,fieldTime,operaButtonTime;
    private Time time;
    private boolean isBatchDelete;//批量删除权限
    private String str="";//删除ID列表字符串
    private String delIds;//实际删除ID列表


    //弹窗搜索变量
    private String  searchSetUrl;//搜索接口url
    private Map<String, String> paramsMap;
    private String paramsString;


    //上拉分页加载
    private int start=0;
    private int limit=20;
    private boolean isLastData;//判断分页加载是否已经是最后一条数据

    //右上角下拉按钮
    private RelativeLayout rlTopBar;
    private PopupWindow toolList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            DLCH = new DiskLruCacheHelper(ListActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getDataIntent();//获取菜单数据
        init();
        allTimeDataAnalysisDisk();//先获取本地的时间接口数据
        requestAllTimeInterface(timeInterface);//请求网络的时间接口
        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (isMulChoice) {
                    List<Map<String, String>> student = listMapUnion.get(position - 1);
                    StudentAdapter.ViewHolder holder = (StudentAdapter.ViewHolder) view.getTag();
                    if (student.get(0).get("check").equals("true")) {
                        student.get(0).put("check", "false");
                        holder.cb.setChecked(false);

                    } else {
                        student.get(0).put("check", "true");
                        holder.cb.setChecked(true);
                    }
                } else {
                    Intent intent = new Intent(ListActivity.this, InfoActivity.class);
                    Bundle bundle = new Bundle();
                    String itemInfo = JSON.toJSONString(listMapUnion.get(position - 1));
                    Log.e("TAG", "向属性页面传递的元素" + itemInfo);
                    bundle.putString("itemInfo", itemInfo);
                    bundle.putString("operaButtonData", operaButtonData);
                    //bundle.putString("operaButtonData", listMapUnion.get(position - 1).get(1).get("fieldCnName"));
                    bundle.putString("tableId", dataTableId + "");
                    bundle.putString("pageId", dataPageId + "");
                    bundle.putString("parameters", JSON.toJSONString(paramsMap));
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        refreshListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListActivity.this, "请点选删除",
                        Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        tv_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listMapUnion.size(); i++) {
                    if ("true".equals(listMapUnion.get(i).get(0).get("check"))) {
                        str += listMapUnion.get(i).get(0).get("stu_id") + ",";
                    }
                }

                if (str.length() > 0) {
                    delIds = str.substring(0, str.length() - 1);
                    Log.e("TAG", "删除的id列表" + delIds);
                    deleteDialog();
                } else {
                    Toast.makeText(ListActivity.this, "至少选择一项删除！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_cancle.setVisibility(View.GONE);
                tv_commit.setVisibility(View.GONE);
                iv_back.setVisibility(View.VISIBLE);
                buttonList.setVisibility(View.VISIBLE);
                isMulChoice = false;
                refreshListView.setMulChoice(isMulChoice);
                refreshListView.setLoadingMore(isMulChoice);
                listAdapter.setIsMulChoice(isMulChoice);
                for (List<Map<String, String>> student : listMapUnion) {
                    student.get(0).put("check", "false");
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initView() {

    }


    public void init() {
        linearLayout = (LinearLayout) findViewById(R.id.llayout);
        ImageView search_title= (ImageView) findViewById(R.id.searchButton);
        search_title.setOnClickListener(this);

        rlTopBar= (RelativeLayout) findViewById(R.id.topBar);
        iv_back = (ImageView) findViewById(R.id.backMenu);
        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
//        ImageView searchButton = (ImageView) findViewById(R.id.searchButton);
//        searchButton.setOnClickListener(this);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tv_title = (TextView) findViewById(R.id.textViewTitle);
        tv_title.setText(titleName);
        buttonList = (ImageView) findViewById(R.id.addItem);
        buttonList.setOnClickListener(this);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        refreshListView = (RefreshListView) findViewById(R.id.lv);
        refreshListView.setOnRefreshListener(this);
        paramsMap=new HashMap<>();
        paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);//"15535211113"
        paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);//"111111"
        paramsMap.put(Constant.tableId, dataTableId + "");
        paramsMap.put(Constant.pageId, dataPageId + "");
        paramsString=paramsMap.toString();

    }





    /**
     * 收集整合用户选择的条件方法，提交搜索的时候调用
     *
     */




    /**
     * 删除提示对话框
     */
    protected void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext,R.style.MyAlertDialogStyle);
        builder.setMessage("注意:当你点删除时，数据库中也会同步删除");

        builder.setTitle("是否确认删除");

        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                batchDeleteCommit1(delIds);
            }
        });
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 接收菜单传递过来的时间接口以及tableId和pageId
     */
    public void getDataIntent() {
        Intent intent = getIntent();

        String  volleyUrl = intent.getStringExtra("timeInterface");
        timeInterface= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        dataTableId = Integer.parseInt(intent.getStringExtra("tableId"));
        dataPageId = Integer.parseInt(intent.getStringExtra("phonePageId"));
        titleName=intent.getStringExtra("titleName");
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        if(!isSearch){requestAllTimeInterface(timeInterface);}
        //请求时间接口
        isSearch=false;
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }



    /**
     * 初始化所有控件
     */


    /**
     * 列表全选操作
     *
     * @param view
     */

    public void selectAll(View view) {

        for (List<Map<String,String>> student: listMapUnion) {
            student.get(0).put("check","true");
        }
        listAdapter.notifyDataSetChanged();

    }

    /**
     * 勾选学员删除操作
     *
     * @param view
     */

    public void selectOppo(View view) {
        for (List<Map<String, String>> student : listMapUnion) {
            if (student.get(0).get("check").equals("true")) {
                student.get(0).put("check", "false");
            } else {
                student.get(0).put("check", "true");
            }
        }
        listAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLoadingMore() {
        if (!isLastData) {
            start+=limit;
        }
        Log.e("TAG","onloadingmore");
        requestListData(time.getDataListUrl().replaceFirst("10.252.46.80","182.92.108.162"));
    }

    /**
     * 取消批量选择操作
     *
     * @param view
     */

    public void cancle(View view) {
        linearLayout.setVisibility(View.GONE);
        tv_commit.setVisibility(View.GONE);
        isMulChoice = false;
        listAdapter.setIsMulChoice(isMulChoice);
        for (List<Map<String, String>> student : listMapUnion) {
            student.get(0).put("check", "false");
        }
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onGetDataSuccess(String jsonData) {
        int requestTag = 0;
        if (requestTag == 3) {
            batchDeleteReturn(jsonData);
        }
    }


    /**
     * 删除成功返回处理
     */
    public void batchDeleteReturn(String jsonData) {
        Log.e("TAG", "删除成功返回数据" + jsonData);
        tv_commit.setVisibility(View.GONE);
        tv_cancle.setVisibility(View.GONE);
        iv_back.setVisibility(View.VISIBLE);
        isMulChoice = false;
        refreshListView.setMulChoice(isMulChoice);
        refreshListView.setLoadingMore(isMulChoice);
        listAdapter.setIsMulChoice(isMulChoice);
        for (List<Map<String, String>> student : listMapUnion) {
            student.get(0).put("check", "false");
        }
        listAdapter.notifyDataSetChanged();
        getProgressDialog().setMessage("删除成功");
        getProgressDialog().show();

        Toast.makeText(mContext, jsonData, Toast.LENGTH_SHORT).show();

        requestAllTimeInterface(timeInterface);
    }

    @Override
    public void onGetDataError() {
        refreshListView.hideHeaderView();
        getProgressDialog().dismiss();
    }

    @Override
    public void onLoading(long total, long current) {

    }


    /**
     * 1、顶栏右侧添加学员和返回按钮事件
     * 2、顶栏左侧返回上级页面
     * 3、搜索弹窗重置条件按钮
     * 4、搜索弹窗之搜索按钮
     *
     *
     *   //右上角下拉按钮
     private RelativeLayout rlTopBar;
     private PopupWindow toolList;
     private View toolLayout;
     private ListView toolListView;
     private List<Map<String, String>> toolListData;
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addItem:
                try{
                    if (toolList != null && toolList.isShowing()) {
                        toolList.dismiss();
                    } else {
                        final View toolLayout = getLayoutInflater().inflate(
                                R.layout.activity_list_buttonlist, null);
                        ListView toolListView = (ListView) toolLayout
                                .findViewById(R.id.buttonList);

                        TextView tv_dismiss=(TextView)toolLayout.findViewById(R.id.tv_dismiss);
                        tv_dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toolList.dismiss();
                            }
                        });

                        final SimpleAdapter adapter = new SimpleAdapter(
                                this,
                                phoneButtonSetList,
                                R.layout.activity_list_buttonlist_item,
                                new String[] { "phoneButtonName" },
                                new int[] { R.id.listItem });
                        toolListView.setAdapter(adapter);

                        // 点击listview中item的处理
                        toolListView
                                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> arg0,
                                                            View arg1, int arg2, long arg3) {
                                            //分类型跳到不同的页面
                                            int phoneButtonType= (int) phoneButtonSetList.get(arg2).get("phoneButtonType");
                                            Log.e("TAG","phoneButtonType===>"+phoneButtonType);
                                            switch (phoneButtonType){
                                                case 0://添加页面
                                                            Intent intent = new Intent(ListActivity.this, AddActivity.class);
                                                            intent.putExtra("phoneButtonAdd", (Serializable) phoneButtonSetList.get(arg2));
                                                            intent.putExtra("tableId", dataTableId+"");
                                                         startActivity(intent);

                                                    break;
                                                case 3://批量删除操作
                                                    deleteButtonTurnUrl=phoneButtonSetList.get(arg2).get("buttonTurnUrl")+"";
                                                    buttonList.setVisibility(View.GONE);
                                                    tv_commit.setVisibility(View.VISIBLE);
                                                    tv_commit.setText("删除");
                                                    iv_back.setVisibility(View.GONE);
                                                    tv_cancle.setVisibility(View.VISIBLE);
                                                    isMulChoice = true;
                                                    refreshListView.setMulChoice(isMulChoice);
                                                    refreshListView.setLoadingMore(isMulChoice);
                                                    listAdapter.setIsMulChoice(isMulChoice);
                                                    listAdapter.notifyDataSetChanged();
                                                    break;
                                            }
                                            // 隐藏弹出窗口
                                            if (toolList != null && toolList.isShowing()) {
                                                toolList.dismiss();
                                            }
                                        }
                                });
                        // 创建弹出窗口
                        // 窗口内容为layoutLeft，里面包含一个ListView
                        // 窗口宽度跟tvLeft一样
                        toolList = new PopupWindow(toolLayout, ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                        ColorDrawable cd = new ColorDrawable(0b1);
                        toolList.setBackgroundDrawable(cd);
                        toolList.setAnimationStyle(R.style.PopupWindowAnimation);
                        //设置半透明
                        WindowManager.LayoutParams params=getWindow().getAttributes();
                        params.alpha=0.7f;
                        getWindow().setAttributes(params);

                        toolList.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                WindowManager.LayoutParams params=getWindow().getAttributes();
                                params.alpha=1f;
                                getWindow().setAttributes(params);
                            }
                        });
                        toolList.update();
                        toolList.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                        toolList.setTouchable(true); // 设置popupwindow可点击
                        toolList.setOutsideTouchable(true); // 设置popupwindow外部可点击
                        toolList.setFocusable(true); // 获取焦点
                        toolList.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                        toolList.showAtLocation(toolLayout,Gravity.BOTTOM,0,0);

                        // 设置popupwindow的位置（相对tvLeft的位置）
                        int topBarHeight = rlTopBar.getBottom();
                        toolList.showAsDropDown(buttonList, 0,
                                (topBarHeight - buttonList.getHeight()) / 2);

                        toolList.setTouchInterceptor(new View.OnTouchListener() {

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // 如果点击了popupwindow的外部，popupwindow也会消失
                                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                    toolList.dismiss();
                                    return true;
                                }
                                return false;
                            }
                        });

                    }}catch (Exception e){
                    Toast.makeText(ListActivity.this, "按钮数据未下载，检查网络！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.searchButton:
                try{Intent intent = new Intent(ListActivity.this, SearchActivity.class);
                intent.putExtra("searchSetUrl", searchSetUrl);
                intent.putExtra("searchParameter", JSON.toJSONString(paramsMap));
                startActivityForResult(intent,REQUEST_CODE);}
                catch (Exception e){

                }
                break;


            default:
                break;
        }
    }
    final int RESULT_CODE=101;
    final int REQUEST_CODE=1;
    /**
     * 下拉刷新方法
     */
    @Override
    public void onDownPullRefresh() {
        start=0;
        isLastData=false;
        Log.e("TAG","开始下拉刷新");
        allTimeDataAnalysisDisk();//先获取本地的时间接口数据
        requestAllTimeInterface(timeInterface);//请求网络的时间接口
    }


    /**
     *
     * 1、获取时间接口数据,如果没有网络或者其他情况则读取本地
     *
     * @param volleyUrl
     */

    public void requestAllTimeInterface(final String volleyUrl) {
        Log.e("TAG", "tableId" + dataTableId);
        Log.e("TAG", "pageId" + dataPageId);
        Log.e("TAG", "userName" + Constant.USERNAME_ALL);
        Log.e("TAG", "passWord" + Constant.PASSWORD_ALL);
        Log.e("TAG", "获取时间接口数据地址" + volleyUrl);
        String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储
                        DLCH.put(volleyUrl+paramsString, jsonData);
                        Log.e("TAG", "获取时间总口" + jsonData);
                        timeData=jsonData;
                        allTimeDataAnalysis(jsonData);//解析并转至字段数据请求
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//失败后读取本地
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
                String diskData = DLCH.getAsString(volleyUrl+paramsString);
                timeData=diskData;
                allTimeDataAnalysis(diskData);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    /**
     * 2、处理时间接口数据,方法 下一步请求字段数据
     *
     */

    public void allTimeDataAnalysisDisk() {
        String diskData = DLCH.getAsString(timeInterface+paramsString);
        Log.e("TAG", "列表页：本地获取解析时间接口数据" + diskData);
        if (diskData != null) {
            Time timeDisk = JSON.parseObject(diskData, Time.class);
            fieldTime= timeDisk.getFieldTime();
            timeUrlAField = timeDisk.getFieldUrl();
            buttonTime= timeDisk.getButtonTime();
            timeUrlAButton = timeDisk.getButtonUrl();
            dataTime= timeDisk.getDataTime();
            timeUrlAData = timeDisk.getDataListUrl();
            operaButtonTime= timeDisk.getOperaButtonTime();
            timeUrlAOperaButton = timeDisk.getOperaButtonUrl();
        }
    }

    /**
     * 判断本地时间是否与服务器时间一致
     * @param jsonData
     */
    public void allTimeDataAnalysis(String jsonData) {
        //1、方法参数获取网络时间接口数据jsonData，并解析四个时间
        Log.e("TAG", "列表页：网络获取解析时间接口数据" + jsonData);
        if (jsonData != null) {
            time = JSON.parseObject(jsonData, Time.class);
            //判断本地字段
            if(fieldTime!=time.getFieldTime()){
                requestFields1(time.getFieldUrl());
            }else{//否则直接读取本地数据
                String diskData1 = DLCH.getAsString(time.getFieldUrl()+paramsString);
                if(diskData1!=null){
                    Log.e("TAG", "field直接进入解析");
                    fieldsAnalysis(diskData1);
                }else {
                    requestFields1(time.getFieldUrl());
                }
            }
            //判断本地页面级按钮
            if(buttonTime!=time.getButtonTime()){
                requestButtonSet(time.getButtonUrl());
            }else{//否则直接读取本地数据
                String diskData2 = DLCH.getAsString(time.getButtonUrl()+paramsString);
                if(diskData2!=null){
                    Log.e("TAG", "直接读取本地页面按钮数据");
                    buttonSetAnalysis(diskData2);
                }else {
                    requestButtonSet(time.getButtonUrl());
                }
            }
            //判断本地列表数据
            if(operaButtonTime!=time.getDataTime()){
                requestListData(time.getDataListUrl());
            }else{//否则直接读取本地数据
                String diskData3 = DLCH.getAsString(time.getDataListUrl()+paramsString);
                if(diskData3!=null){
                    listDataAnalysis(diskData3);
                }else {//如果本地数据也为空，则仍从网络下载
                    requestListData(time.getDataListUrl());
                }
            }

            //判断按钮数据
            if(dataTime!=time.getOperaButtonTime()){
                requestOperaButton(time.getOperaButtonUrl());
            }else{//否则直接读取本地数据
                String diskData4 = DLCH.getAsString(time.getOperaButtonUrl()+paramsString);
                if(diskData4!=null){
                    //listDataAnalysis(diskData4);解析按钮数据
                }else {//如果本地数据也为空，则仍从网络下载
                    requestOperaButton(time.getOperaButtonUrl());
                }
            }

            searchSetUrl=time.getSearchSetUrl();
            operaButtonTime=time.getOperaButtonTime();
            timeUrlAOperaButton = time.getOperaButtonUrl();
            if (!TextUtils.isEmpty(searchSetUrl)){
                //请求筛选条件
                //requestScreenData();
            }
        }else{
            try{requestFields1(timeUrlAField);
            requestButtonSet(timeUrlAButton);
            requestListData(timeUrlAData);}
            catch (Exception e){

            }
        }
    }

    /**
     *
     * 3、获取字段接口数据,如果没有网络或者其他情况则读取本地
     * @param volleyUrl
     */

    public void requestFields1(final String volleyUrl) {
        String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        DLCH.put(volleyUrl+paramsString, jsonData);
                        Log.e("TAG", "获取字段数据" + jsonData);
                        fieldsAnalysis(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }
    /**
     * 4、处理字段接口数据,方法 下一步请求列表数据
     */
    public void fieldsAnalysis(String jsonData) {
        Log.e("TAG", "解析字段数据" + jsonData);
        phoneFieldSetMap=JSON.parseObject(jsonData, Map.class);//获取配置数据
    }

    /**
     *
     * 5、获取列表接口数据,如果没有网络或者其他情况则读取本地
     * @param volleyUrl
     */

    public void requestListData(final String volleyUrl) {
        String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        if (start==0){
                            DLCH.put(volleyUrl+paramsString, jsonData);
                        }
                        Log.e("TAG", "获取列表数据" + jsonData);
                        listDataAnalysis(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                refreshListView.hideFooterView();
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                map.put("limit",limit+"");
                map.put("start",start+"");
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    /**
     * 6、处理列表接口数据,方法 下一步请求页面级权限数据
     * @param jsonData
     */
    public void listDataAnalysis(String jsonData) {
        refreshListView.hideFooterView();
        Log.e("TAG", "解析列表数据" + jsonData);
        Log.e("TAG", "start=====>" + start);
        phoneDataListMap=JSON.parseObject(jsonData, Map.class);//获取列表数据
        List<List<Map<String,String>>> union=unionVision();
        if (union!=null&&union.size()==0) {
            isLastData=true;
        }
        if (start==0) {
            if (listMapUnion!=null){
                listMapUnion.clear();
                listMapUnion.addAll(union);
            }else {
                listMapUnion=union;
            }
        } else {
            listMapUnion.addAll(union);
        }
        Log.e("TAG", "listMapUnion=====>" + listMapUnion);
        refreshListView.hideHeaderView();
        if (listMapUnion != null&& listAdapter ==null) {
            listAdapter = new StudentAdapter(this, R.layout.activity_list_item, listMapUnion);
            refreshListView.setAdapter(listAdapter);
        }else if(listMapUnion!=null){
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     *
     * 联合解析配置数据和data数据
     * @return
     */
    public List<List<Map<String,String>>> unionVision() {

        if(phoneFieldSetMap!=null&&phoneDataListMap!=null){
            //将配置表打包成ListMap
            Log.e("TAG","配置表源数据"+phoneFieldSetMap.toString());
            List<Map<String, Object>> mapSet = (List<Map<String, Object>>) phoneFieldSetMap.get("phoneFieldSet");
            Log.e("TAG","配置表"+mapSet.toString());
            //将数据表打包成ListMap
            List<Map<String, Object>> mapData = (List<Map<String, Object>>) phoneDataListMap.get("rows");
            List<List<Map<String,String>>> union=new ArrayList<>();

            for (int j = 0; j < mapData.size(); j++) {//列表元素个数决定循环次数

                List<Map<String, String>> listItem=new ArrayList<>();
                Map<String, String> mapCheck=new HashMap<>();
                int stu_id=0;
                listItem.add(mapCheck);
                for (int i = 0; i < mapSet.size(); i++) {//决定元素属性个数
                    Map<String, String> mapItem=new HashMap<>();
                    String fieldCnName = (String) mapSet.get(i).get("fieldCnName");
                    String fieldAliasName = (String) mapSet.get(i).get("fieldAliasName");
                    mapItem.put("fieldCnName",fieldCnName);
                    int fieldRole = (int) mapSet.get(i).get("fieldRole");
                    String fieldCnName2;
                    stu_id = (int) mapData.get(j).get("T_"+mapSet.get(i).get("tableId")+"_0");
                    if(fieldRole == 16){
                        if(mapData.get(j).get("DIC_"+fieldAliasName)!=null){
                            fieldCnName2 = ""+mapData.get(j).get("DIC_"+fieldAliasName);}else{
                            fieldCnName2="";
                        }
                    }else if(fieldRole == 14){
                        if(mapData.get(j).get(fieldAliasName)!=null&&!mapData.get(j).get(fieldAliasName).equals("")){
                            String dateString=""+ mapData.get(j).get(fieldAliasName);
                            String[] dateData=dateString.split(" ");
                            if(dateData[1].equals("00:00:00")){
                                fieldCnName2 = ""+ dateData[0];}else{
                                fieldCnName2 = ""+mapData.get(j).get(fieldAliasName);
                            }
                        }else{
                            fieldCnName2="";
                        }
                    }else{
                        if(mapData.get(j).get(fieldAliasName)!=null){
                            fieldCnName2 = ""+ mapData.get(j).get(fieldAliasName);}else{
                            fieldCnName2="";
                        }
                    }
                    mapItem.put("fieldCnName2",fieldCnName2);

                    listItem.add(mapItem);
                }
                mapCheck.put("check", "false");
                mapCheck.put("stu_id",stu_id + "");
                union.add(listItem);
            }
            Log.e("TAG","列表页面打印Listlist"+union.toString());
            return union;
        }
        return null;
    }

    /**
     * 7、获取页面级权限接口数据,如果没有网络或者其他情况则读取本地
     * @param volleyUrl
     */

    public void requestButtonSet(final String volleyUrl) {
        String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        Log.e("TAG", "页面级按钮权限数据网址" + volleyUrl);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        DLCH.put(volleyUrl+paramsString, jsonData);
                        Log.e("TAG", "获取页面级按钮权限数据" + jsonData);
                        buttonSetAnalysis(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//读取本地后也转至处理
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =paramsMap;
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }


    /**
     * 8、处理页面级权限接口数据,方法
     * @param jsonData
     */

    public void buttonSetAnalysis(String jsonData) {

        assert jsonData!=null;
        Map buttonSetMap = JSON.parseObject(jsonData, Map.class);
        Log.e("TAG", "解析页面级按钮权限数据" + jsonData);
        getProgressDialog().dismiss();
        //解析listmap按钮数据
        phoneButtonSetList = (List<Map<String, Object>>) buttonSetMap.get("phoneButtonSet");
        //匹配按钮







    }

    /**
     *
     * 批量；批量删除操作
     * @param delIds
     */

    public void batchDeleteCommit1(String delIds) {
        final String delIdList=delIds;
        getProgressDialog().setMessage("正在删除中...");
        getProgressDialog().show();
        Log.e("TAG", "删除数据提交地址" + deleteButtonTurnUrl);
        String volleyUrl1= deleteButtonTurnUrl.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST,volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "删除成功返回数据" + jsonData);
                        batchDeleteReturn(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//读取本地后也转至处理
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =paramsMap;
                map.put("delIds", delIdList+"");
                map.put("buttonType", "3");
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }
    @Override
    public void onDismiss() {

    }

    /**
     * 请求按钮数据
     * @param jsonData
     */

    public void requestOperaButton(String jsonData) {//与列表请求重复，传过来就可以了
        final String  volleyUrl1=jsonData.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        DLCH.put(volleyUrl1+paramsString, jsonData);
                        Log.e("TAG", "button数据" + jsonData);
                        operaButtonData=jsonData;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//读取本地后也转至处理
                VolleySingleton.onErrorResponseMessege(ListActivity.this, volleyError);
                String diskData = DLCH.getAsString(volleyUrl1+paramsString);
                operaButtonData=diskData;
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

   private boolean isSearch;
    /**
     * 收到搜索activity返回的信息
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE) {
            if(resultCode==RESULT_CODE) {
                //result即为搜索结果
                String result=data.getStringExtra("second");
                isSearch=data.getBooleanExtra("isSearch",false);
                listMapUnion.clear();
                listDataAnalysis(result);

            }
        }






        super.onActivityResult(requestCode, resultCode, data);
    }
}