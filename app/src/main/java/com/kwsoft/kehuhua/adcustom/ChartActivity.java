package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class ChartActivity extends BaseActivity{
    private CommonToolbar mToolbar;
    String titleName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        CloseActivityClass.activityList.add(this);
        getIntentDatas();
        initView();

    }
    public void getIntentDatas() {
        Intent mIntent = this.getIntent();
        try {
            titleName = mIntent.getStringExtra("titleName");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public void initView() {
        WebView wb= (WebView) findViewById(R.id.wb);
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

        wb.getSettings().setAllowFileAccess(true);
        //开启脚本支持
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl("file:///android_asset/echart/myechart.html");
//        wb.loadUrl("javascript:createChart('bar',[89,78,77]);");
//        btn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

}
