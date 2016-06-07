package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MultiValueItemActivity extends AppCompatActivity {

    @Bind(R.id.lv_multi_value_item2)
    ListView lvMultiValueItem2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_value_item2);
        ButterKnife.bind(this);


        getIntentData();


    }

    private void getIntentData() {

        Intent intent = getIntent();
        String itemDataStr = intent.getStringExtra("itemDataStr");
        List<Map<String, Object>> multiItemListMap = JSON.parseObject(itemDataStr,
                new TypeReference<List<Map<String, Object>>>() {
                });
        SimpleAdapter adapter = new SimpleAdapter(this, multiItemListMap,
                R.layout.activity_info_item, new String[]{"fieldCnName", "fieldCnName2"},
                new int[]{R.id.tv_name,
                        R.id.tv_entity_name});
        lvMultiValueItem2.setAdapter(adapter);


    }
}
