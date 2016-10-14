package com.kwsoft.version.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.CourseActivity;
import com.kwsoft.kehuhua.adcustom.ListActivity2;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.version.StuPra;
import com.kwsoft.version.TextAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/10 0010.
 *
 */
public class MenuFragment extends Fragment {
    private ListView regionListView;
    private GridView nextMenu;
    //    private TextView rightView;
//    private ArrayList<String> groups = new ArrayList<String>();
    private TextAdapter earaListViewAdapter;
    private List<Map<String, Object>> parentList;
    private List<Map<String, Object>> childList = new ArrayList<>();
    private SimpleAdapter nextAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        getIntentData();
        initView(view);

        regionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                earaListViewAdapter.setSelectPos(position);
                earaListViewAdapter.notifyDataSetChanged();
               int childNum= getChildMap(position);
                if (childNum==0) {
                    toItem(parentList.get(position));
                }else{
                    nextAdapter.notifyDataSetChanged();
                }

            }
        });

        nextMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toItem(childList.get(position));
            }
        });
        return view;
    }

    private void getIntentData() {
        Bundle menuBundle = getArguments();

        String menuStr = menuBundle.getString("menuDataMap");
        if (menuStr != null) {
            List<Map<String, Object>> menuListMap = JSON.parseObject(menuStr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            if (menuListMap.size() > 0) {
                parentList = DataProcess.noPhoneList(menuListMap);

            } else {
                Toast.makeText(getActivity(), "无菜单数据", Toast.LENGTH_SHORT).show();
            }
            Log.e("TAG", "获得学员端菜单数据：" + menuStr);
        }
    }


    private void initView(View view) {
        //开始设置左侧内容，初始化控件
        regionListView = (ListView) view.findViewById(R.id.listView);
        //初始化父级菜单list
        nextMenu = (GridView) view.findViewById(R.id.next_menu);
//        rightView = (TextView) view.findViewById(R.id.tv);
//        for (int i = 0; i < parentList.size(); i++) {
//            groups.add(String.valueOf(parentList.get(i).get("menuName")));
//        }




        earaListViewAdapter = new TextAdapter(getActivity(), parentList);
        regionListView.setAdapter(earaListViewAdapter);

        //开始设置第一个父级菜单要显示的二层菜单
        getChildMap(0);

        Log.e("TAG", "教师端菜单childList：" + childList.toString());
        nextAdapter = new SimpleAdapter(getActivity(), childList,
                R.layout.stu_next_menu_item, new String[]{"images","menuName"},
                new int[]{R.id.next_menu_image,R.id.next_menu_name});
        nextMenu.setAdapter(nextAdapter);
//        rightView.setText(groups.get(0));
    }


    public int getChildMap(int position) {
        childList.clear();
        if (parentList.get(position).get("meunColl")!=null) {
            childList.addAll((List<Map<String, Object>>)parentList.get(position).get("meunColl"));
            for (int i = 0; i < childList.size(); i++) {
                String newMenuName = String.valueOf(childList.get(i).get("menuName"));
                childList.get(i).put("menuName", newMenuName.replace("手机端", ""));
                childList.get(i).put("images", StuPra.images[i]);
            }
        }
        return childList.size();
    }

    public void toItem(Map<String, Object> itemData) {
        if (itemData.get("menuPageUrl") == null) {
            try {
                String itemDataString = JSONArray.toJSONString(itemData);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ListActivity2.class);
                Log.e("TAG", "itemData" + itemDataString);
                intent.putExtra("itemData", itemDataString);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String itemDataString = JSONArray.toJSONString(itemData);
            Intent intent = new Intent();
            intent.setClass(getActivity(), CourseActivity.class);

            intent.putExtra("itemData", itemDataString);
            startActivity(intent);
        }
    }
}
