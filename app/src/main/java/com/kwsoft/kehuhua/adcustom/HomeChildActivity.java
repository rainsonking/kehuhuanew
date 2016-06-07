package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.BusiMenuAdapter;
import com.kwsoft.kehuhua.utils.CloseActivityClass;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
public class HomeChildActivity extends Activity implements View.OnClickListener {
    private ListView mListView;
    public BusiMenuAdapter adapter;
    public Context context;
    public ImageView imageView, iv_back;
    public TextView father_name, tv_cancel;
    public String menuStr;
    public String timeInterface,secondMenuName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_child);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
        getListData();
    }

    private void initView() {
        context = this;
        mListView = (ListView) findViewById(R.id.listview);
        father_name = (TextView) findViewById(R.id.father_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_cancel = (TextView) findViewById(R.id.tv_cancle);
        iv_back.setOnClickListener(this);
        //ListView item点击事件,传递json数据
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent(HomeChildActivity.this, ListActivity.class);
                mIntent.putExtra("menu", menuStr);
                startActivity(mIntent);
            }
        });
    }

    //获得列表名称数据并解析json
    public void getListData() {
        Intent intent = getIntent();
        menuStr = intent.getStringExtra("menu");
        secondMenuName=intent.getStringExtra("secondMenuName");
        father_name.setText(secondMenuName);
        timeInterface = intent.getStringExtra("timeInterface");
        //还原解析成 List<Map>格式的数据timeInterface
        final List<Map<String, Object>> listMap = JSON.parseObject(menuStr,
                new TypeReference<List<Map<String, Object>>>() {
                });
        if (listMap.size() != 0) {
            mListView.setAdapter(new BusiMenuAdapter(HomeChildActivity.this, listMap));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent = new Intent(HomeChildActivity.this, ListActivity.class);
                    Log.e("TAG", "子菜单传递给列表页面的位置（页面）" + position + "");
                    intent.putExtra("timeInterface", timeInterface);
                    intent.putExtra("tableId", String.valueOf(listMap.get(position).get("tableId")));
                    intent.putExtra("phonePageId", String.valueOf(listMap.get(position).get("phonePageId")));
                    intent.putExtra("titleName", listMap.get(position).get("phoneMenuName")+"");

                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            default:
                break;
        }
    }
}