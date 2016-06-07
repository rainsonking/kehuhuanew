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
import android.widget.ImageView;
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
import com.kwsoft.kehuhua.model.DataModelImpl;
import com.kwsoft.kehuhua.model.OnDataListener;
import com.kwsoft.kehuhua.utils.CloseActivityClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/3 0003.
 */
public class InfoActivity extends BaseActivity implements OnDataListener,View.OnClickListener {
    private ImageView IV_back_list;
    private TextView mTextViewTitle;
    private ListView mListView;
    private String tableId,pageId,mainId;
    private Bundle mBundle;
    private String userName, userPassword;
    private int requestCode = 0; //请求标识
    private String operaBtnTurnUrl, changeUrl, commitUrl;//删除url和修改url,提交url.
    private int deletePageId, changePageId;//删除的pageId,修改的pageId;
    private String oprBtnPageDataUrl;//修改字段的值 的url
    private int deleteButtonType;
    private List<Map<String, Object>> listMap;//封装学员所有属性信息
    private List<Map<String,Object>> operaButtonMapList=new ArrayList<>();
    private Map<String,String> paraMap;//解析取到的配置数据
    private ImageView item_button_list;
    //右上角下拉按钮
    private RelativeLayout rlTopBar;
    private PopupWindow popupWindow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_info);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getStuInfoData(); //获取上一层列表传递的数据,以及对数据操作的权限
        initView();//初始化控件
        stuInfoShow(); //展示学员信息
    }

    public void getStuInfoData() {
        Intent mIntent = this.getIntent();
        mBundle = mIntent.getExtras();
        listMap = JSON.parseObject(mBundle.getString("itemInfo"),
                new TypeReference<List<Map<String, Object>>>() {
                });

        mainId= (String) listMap.get(0).get("stu_id");
        listMap.remove(0);
        String operaButtonData=mBundle.getString("operaButtonData");
        Log.e("TAG","验证传递"+operaButtonData);
        Map<String,Object> operaButtonMap=JSON.parseObject(operaButtonData,Map.class);

        operaButtonMapList= (List<Map<String, Object>>) operaButtonMap.get("operaButtonSet");
        Log.e("TAG",""+operaButtonMapList.get(0));
        String  parameters = mBundle.getString("parameters");
        paraMap=JSON.parseObject(parameters,Map.class);
        pageId = mBundle.getString("pageId");
        tableId = mBundle.getString("tableId");
    }
    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_stu_info);
        rlTopBar= (RelativeLayout) findViewById(R.id.info_title);
        mTextViewTitle = (TextView) findViewById(R.id.tv_item_title);
        mTextViewTitle.setText(listMap.get(0).get("fieldCnName2")+"");

        IV_back_list= (ImageView) findViewById(R.id.IV_back_list);
        IV_back_list.setOnClickListener(this);
        item_button_list= (ImageView) findViewById(R.id.item_button_list);
        item_button_list.setOnClickListener(this);
    }

    private void deleteStudent() {
        requestCode = 3;
        DataModelImpl dataModelImpl = new DataModelImpl();
        String[] paramsName = {Constant.USER_NAME, Constant.PASSWORD, "tableId", "pageId", "delIds", "buttonType"};

        String[] paramsValues = {Constant.USERNAME_ALL, Constant.PASSWORD_ALL, tableId + "", deletePageId + "", mainId + "", deleteButtonType + ""};
        dataModelImpl.getData(operaBtnTurnUrl.replaceFirst("10.252.46.80","182.92.108.162"), paramsName, paramsValues, this);
    }

    //删除对话框
    protected void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("注意:当你点删除时，数据库中也会同步删除");
        builder.setTitle("删除学员");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteStudent();
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

    @Override
    public void onGetDataSuccess(String jsonData) {
        if (requestCode == 3) {
            Toast.makeText(InfoActivity.this, jsonData, Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    @Override
    public void onGetDataError() {

    }

    @Override
    public void onLoading(long total, long current) {

    }

    /**
     * 1、展示属性列表
     */
    public void stuInfoShow() {
        SimpleAdapter adapter = new SimpleAdapter(InfoActivity.this, listMap, R.layout.activity_info_item,
                new String[] { "fieldCnName", "fieldCnName2" }, new int[] { R.id.tv_name,
                R.id.tv_entity_name });
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.IV_back_list:
                finish();
                break;
            case R.id.item_button_list:
                try{ if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    View toolLayout = getLayoutInflater().inflate(
                            R.layout.activity_list_buttonlist, null);
                    ListView toolListView = (ListView) toolLayout
                            .findViewById(R.id.buttonList);

                    TextView tv_dismiss=(TextView)toolLayout.findViewById(R.id.tv_dismiss);
                    tv_dismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupWindow.dismiss();
                        }
                    });

                    final SimpleAdapter adapter = new SimpleAdapter(
                            this,
                            operaButtonMapList,
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
                                    // 改变顶部对应TextView值
                                    if(arg2>=0){ //分类型跳到不同的页面
                                        int phoneButtonType= (int) operaButtonMapList.get(arg2).get("phoneButtonType");
                                        Map<String, Object> map_info=operaButtonMapList.get(arg2);
                                        map_info.put("info_tableId",Integer.parseInt(tableId));
                                        map_info.put("info_mainId", mainId);
                                        map_info.put("info_pageId", pageId);

                                        switch (phoneButtonType){
                                            case 12://修改页面
                                                if(operaButtonMapList.get(arg2).get("addStyle")!=3){
                                                Intent mIntent = new Intent(InfoActivity.this, EditActivity.class);
                                                mBundle.putSerializable("info_data", (Serializable) map_info);
                                                mIntent.putExtras(mBundle);
                                                startActivity(mIntent);
                                                }else{
                                                    Toast.makeText(InfoActivity.this, "定制化开发", Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                            case 13://单项删除操作
                                                operaBtnTurnUrl=operaButtonMapList.get(arg2).get("operaBtnTurnUrl")+"";
                                                deleteButtonType= (int) operaButtonMapList.get(arg2).get("phoneButtonType");
                                                deleteDialog();
                                                break;
                                            case 18://添加类型操作
                                                Toast.makeText(InfoActivity.this, "定制化开发", Toast.LENGTH_SHORT).show();
                                                break;
                                        }
                                        // 隐藏弹出窗口
                                        if (popupWindow != null && popupWindow.isShowing()) {
                                            popupWindow.dismiss();
                                        }
                                    }}
                            });
                    // 创建弹出窗口
                    // 窗口内容为layoutLeft，里面包含一个ListView
                    // 窗口宽度跟tvLeft一样
                    popupWindow = new PopupWindow(toolLayout, item_button_list.getWidth(),
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    ColorDrawable cd = new ColorDrawable(0b1);
                    popupWindow.setBackgroundDrawable(cd);
                    popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
                    //设置半透明
                    WindowManager.LayoutParams params=getWindow().getAttributes();
                    params.alpha=0.7f;
                    getWindow().setAttributes(params);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            WindowManager.LayoutParams params=getWindow().getAttributes();
                            params.alpha=1f;
                            getWindow().setAttributes(params);
                        }
                    });
                    popupWindow.update();
                    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popupWindow.setTouchable(true); // 设置popupwindow可点击
                    popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
                    popupWindow.setFocusable(true); // 获取焦点
                    popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                    popupWindow.showAtLocation(toolLayout,Gravity.BOTTOM,0,0);

                    // 设置popupwindow的位置（相对tvLeft的位置）
                    int topBarHeight = rlTopBar.getBottom();
                    popupWindow.showAsDropDown(item_button_list, 0,
                            (topBarHeight - item_button_list.getHeight()) / 2);

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

                }}catch (Exception e){
                    Toast.makeText(InfoActivity.this, "按钮数据未下载，检查网络！", Toast.LENGTH_SHORT).show();
                }
                break;

        }



    }
}
