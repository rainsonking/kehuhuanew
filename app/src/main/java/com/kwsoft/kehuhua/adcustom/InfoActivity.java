package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;


/**
 * Created by Administrator on 2015/12/3 0003.
 *
 *
 */
public class InfoActivity extends BaseActivity implements View.OnClickListener {
    private ListView mListView;
    private String tableId, pageId;
    //右上角下拉按钮
    private RelativeLayout rlTopBar;
    private PopupWindow popupWindow;
    private Map<String,String> delMapParams=new HashMap<>();
    private CommonToolbar mToolbar;

    //新接口参数

    private List<Map<String, String>> infoDataList=new ArrayList<>();
    private List<Map<String, Object>> operaButtonSet=new ArrayList<>();
    private String mainId;
    private String titleName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        CloseActivityClass.activityList.add(this);
        getInfoData(); //获取上一层列表传递的数据,以及对数据操作的权限
        initView();//初始化控件
        presentData(); //展示信息
    }

    public void getInfoData() {
        Intent mIntent = this.getIntent();
        try {
            String infoData = mIntent.getStringExtra("childData");
            Log.e("TAG","infoData "+infoData);
            String operaData = mIntent.getStringExtra("operaButtonSet");
            infoDataList = JSON.parseObject(infoData,
                    new TypeReference<List<Map<String, String>>>() {
                    });
            Log.e("TAG","infoData "+infoData);
            Log.e("TAG","infoData "+infoData);
            mainId=infoDataList.get(0).get("mainId");
            tableId=infoDataList.get(0).get("tableId");
            titleName=infoDataList.get(0).get("fieldCnName2");
            pageId=infoDataList.get(0).get("pageId");
            Constant.mainIdValue=mainId;
            Log.e("TAG","Info  getIntent+mainId: "+mainId);



            List<Map<String, Object>> operaButtonSet0=JSON.parseObject(operaData,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

//判断属性界面有数据
            if (infoDataList.size()>0) {
                //判断按钮数据非空
                if (operaButtonSet0.size()!=0) {

                    operaButtonSet=operaButtonSet0;
                    Log.e("TAG", "详情页operaButtonSet " +operaButtonSet.toString());
                }
            } else {
                Toast.makeText(InfoActivity.this, "无详情数据", Toast.LENGTH_SHORT).show();

            }

            // operaButtonSet

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void presentData() {

        SimpleAdapter adapter = new SimpleAdapter(InfoActivity.this, infoDataList, R.layout.activity_info_item,
                new String[]{"fieldCnName", "fieldCnName2"}, new int[]{R.id.tv_name,
                R.id.tv_entity_name});
        mListView.setAdapter(adapter);

    }


    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_stu_info);
        rlTopBar= (RelativeLayout) findViewById(R.id.info_title);
        //mTextViewTitle.setText(listMap.get(0).get("fieldCnName2")+"");
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(titleName);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //判断条件显示右侧按钮
        Log.e("TAG", "详情页operaButtonSet " +operaButtonSet.toString());
        if (operaButtonSet.size()>0) {
            mToolbar.showRightImageButton();
            //右侧下拉按钮
            mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popButton();
                }
            });

        }else{
            mToolbar.hideRightImageButton();
        }


    }

    @Override
    public void onClick(View v) {
    }

    //右上角下拉按钮方法

    private void  popButton(){
        try {
            if (popupWindow != null && popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                View toolLayout = getLayoutInflater().inflate(
                        R.layout.activity_list_buttonlist, null);
                ListView toolListView = (ListView) toolLayout
                        .findViewById(R.id.buttonList);

                TextView tv_dismiss = (TextView) toolLayout.findViewById(R.id.tv_dismiss);
                tv_dismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                final SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        operaButtonSet,
                        R.layout.activity_list_buttonlist_item,
                        new String[]{"buttonName"},
                        new int[]{R.id.listItem});
                toolListView.setAdapter(adapter);

                // 点击listview中item的处理
                toolListView
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> arg0,
                                                    View arg1, int arg2, long arg3) {
                                // 改变顶部对应TextView值
                                if (arg2 >= 0) { //分类型跳到不同的页面
                                    int buttonType = (int) operaButtonSet.get(arg2).get("buttonType");

                                    Map<String, Object> operaButtonSetMap = operaButtonSet.get(arg2);
                                    operaButtonSetMap.put("tableIdList", tableId);
                                    operaButtonSetMap.put("dataId", mainId);
                                    operaButtonSetMap.put("pageIdList", pageId);

                                    String operaButtonSetMapStr = JSON.toJSONString(operaButtonSetMap);
                                    Log.e(TAG, "onItemClick: operaButtonSetMapStr"+operaButtonSetMapStr);
                                    switch (buttonType) {
                                        case 12://修改页面
                                            Intent mIntentEdit = new Intent(InfoActivity.this, RowsEditActivity.class);
                                            String requestUrl = Constant.sysUrl + Constant.requestEdit;
                                            mIntentEdit.putExtra("itemSet", operaButtonSetMapStr);
                                            mIntentEdit.putExtra("isReadOnly", String.valueOf(buttonType));
                                            delMapParams.put("buttonType", String.valueOf(operaButtonSetMap.get("buttonType")));
                                            startActivity(mIntentEdit);
                                            break;
                                        case 18://关联添加类型操作
                                            Intent mIntentRowAdd = new Intent(InfoActivity.this, RowsAddActivity.class);
                                            mIntentRowAdd.putExtra("addSet", operaButtonSetMapStr);
                                            startActivity(mIntentRowAdd);
                                            break;
                                        case 15://查看
                                            Intent mIntentOnlySee = new Intent(InfoActivity.this, RowsEditActivity.class);
                                            mIntentOnlySee.putExtra("itemSet", operaButtonSetMapStr);
                                            mIntentOnlySee.putExtra("isReadOnly", String.valueOf(buttonType));
                                            delMapParams.put("buttonType", String.valueOf(operaButtonSetMap.get("buttonType")));
                                            startActivity(mIntentOnlySee);
                                            break;

                                        case 13://单项删除操作
                                            delMapParams.put(Constant.tableId, tableId);
                                            delMapParams.put(Constant.pageId, String.valueOf(operaButtonSetMap.get("startTurnPage")));
                                            delMapParams.put(Constant.delIds, String.valueOf(operaButtonSetMap.get("dataId")));
                                            delMapParams.put("buttonType", String.valueOf(operaButtonSetMap.get("buttonType")));
                                            toDelete();
                                            break;
                                    }
                                    // 隐藏弹出窗口
                                    if (popupWindow != null && popupWindow.isShowing()) {
                                        popupWindow.dismiss();
                                    }
                                }
                            }
                        });
                // 创建弹出窗口
                // 窗口内容为layoutLeft，里面包含一个ListView
                // 窗口宽度跟tvLeft一样

                popupWindow = new PopupWindow(toolLayout, mToolbar.getRightButton().getWidth(),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                ColorDrawable cd = new ColorDrawable(0b1);
                popupWindow.setBackgroundDrawable(cd);
                popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
                //设置半透明
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 0.7f;
                getWindow().setAttributes(params);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams params = getWindow().getAttributes();
                        params.alpha = 1f;
                        getWindow().setAttributes(params);
                    }
                });
                popupWindow.update();
                popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                popupWindow.setTouchable(true); // 设置popupwindow可点击
                popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
                popupWindow.setFocusable(true); // 获取焦点
                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                popupWindow.showAtLocation(toolLayout, Gravity.BOTTOM, 0, 0);

                // 设置popupwindow的位置（相对tvLeft的位置）
                int topBarHeight = rlTopBar.getBottom();
                popupWindow.showAsDropDown(mToolbar.getRightButton(), 0,
                        (topBarHeight - mToolbar.getRightButton().getHeight()) / 2);

                popupWindow.setTouchInterceptor(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // 如果点击了popupwindow的外部，popupwindow也会消失
                        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                            popupWindow.dismiss();
                            return true;
                        }
                        return false;
                    }
                });

            }
        } catch (Exception e) {
//            Toast.makeText(InfoActivity.this, "无按钮", Toast.LENGTH_SHORT).show();
        }
    }




    private void toDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("数据库中会同步删除");
        builder.setTitle("删除");
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

    private static final String TAG = "InfoActivity";

    private void deleteItems() {
        final String volleyUrl = Constant.sysUrl + Constant.requestDelete;
        Log.e("TAG", "获取dataUrl " + volleyUrl);
        Log.e(TAG, "删除参数  "+delMapParams.toString());
        //请求
        OkHttpUtils
                .post()
                .params(delMapParams)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(InfoActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        Log.e("TAG", "删除返回数据" + response);
                        String isSuccess=response.substring(0,1);
                        if(isSuccess.equals("1")){
                            Intent intent = new Intent();
                            intent.setClass(InfoActivity.this,ListActivity2.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(InfoActivity.this, response+"请检查表关联", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}