package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.app.ListActivity;
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
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.kwsoft.kehuhua.utils.CloseActivityClass;

import java.util.List;
import java.util.Map;

public class HomeChildActivity extends Activity implements View.OnClickListener {
    private ListView child_menu_list;
    public Context context;
    public ImageView iv_back;
    public TextView father_name, tv_cancel;
    public String menuStr;

    public List<Map<String, Object>> childList;

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
        child_menu_list = (ListView) findViewById(R.id.child_menu_list);
        father_name = (TextView) findViewById(R.id.father_name);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_cancel = (TextView) findViewById(R.id.tv_cancle);
        iv_back.setOnClickListener(this);
        //ListView item点击事件,传递json数据
        child_menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent(HomeChildActivity.this, ListActivity2.class);
                mIntent.putExtra("menu", menuStr);
                startActivity(mIntent);
            }
        });
    }

    //获得列表名称数据并解析json
    public void getListData() {
        Intent intent = getIntent();
        menuStr = intent.getStringExtra("childList");
        childList= (List<Map<String, Object>>) JSONArray.parse(menuStr);
        String fatherName=childList.get(0).get("parent_menuName")+"";
        father_name.setText(fatherName);
        //去掉手机端三个字
        deleteWord();
        SimpleAdapter adapter = new SimpleAdapter(this, childList,
                R.layout.activity_home_child_item, new String[] {"menuName"},
                new int[] {R.id.tv_item_name});
        child_menu_list.setAdapter(adapter);
        child_menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> itemData=childList.get(position);
                toItem(itemData);
            }
        });
    }

    public void deleteWord(){
        for (int i=0;i<childList.size();i++) {

           String newMenuName= String.valueOf(childList.get(i).get("menuName")).replace("手机端","");
            childList.get(i).put("menuName",newMenuName);

        }




    }

    public void toItem(Map<String, Object> itemData) {


        if (itemData.get("menuPageUrl")==null) {
            String itemDataString= JSONArray.toJSONString(itemData);
            Intent intent = new Intent();
            intent.setClass(this, ListActivity.class);
            Log.e("TAG","itemData"+itemDataString);
            intent.putExtra("itemData",itemDataString);
            startActivity(intent);
        } else {
            String itemDataString= JSONArray.toJSONString(itemData);
            Intent intent = new Intent();
            intent.setClass(this, CourseActivity.class);

            intent.putExtra("itemData",itemDataString);
            startActivity(intent);
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