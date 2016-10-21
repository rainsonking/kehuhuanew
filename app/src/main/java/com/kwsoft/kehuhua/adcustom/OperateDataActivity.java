package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class OperateDataActivity extends BaseActivity {

    @Bind(R.id.lv_operate_item)
    ListView lvAddItem;
    private CommonToolbar mToolbar;
    private String buttonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);
        ButterKnife.bind(this);

        getIntentData();
        initView();
    }


    //获取参数
    private void getIntentData() {
//        Intent intent = getIntent();
//        String buttonSetItemStr = intent.getStringExtra("buttonSetItemStr");
//
//        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);
//
//        buttonName = String.valueOf(buttonSetItem.get("buttonName"));
////        tvAddItemTitle.setText(buttonName);
//        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
//
//        dataId = String.valueOf(buttonSetItem.get("dataId"));
//        tableId = String.valueOf(buttonSetItem.get("tableId"));
//        Constant.tempTableId = tableId;
//        Constant.tempPageId = pageId;
//
//        paramsMap = new HashMap<>();
//        paramsMap.put(tableId, tableId);
//        paramsMap.put(pageId, pageId);
    }

    @Override
    public void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle(buttonName);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.often_more));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toCommit();
            }
        });
    }




    private void toCommit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(buttonName+"？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                requestAddCommit();
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
