package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.treeViewUtils.FileBean;
import com.kwsoft.kehuhua.treeViewUtils.Node;
import com.kwsoft.kehuhua.treeViewUtils.OrgBean;
import com.kwsoft.kehuhua.treeViewUtils.SimpleTreeListViewAdapter_noCHeckbox;
import com.kwsoft.kehuhua.treeViewUtils.TreeListViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity {

    @Bind(R.id.tree_back_menu)
    ImageView treeBackMenu;
    @Bind(R.id.list_tree_menu)
    ListView mTree;


    private SimpleTreeListViewAdapter_noCHeckbox<OrgBean> mAdapter;
    private List<FileBean> mDatas;
    private List<OrgBean> mDatas2;
    private List<Map<String, Object>> dataInit = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String menuData = intent.getStringExtra("menuData");
        dataStore(menuData);
    }


    @OnClick(R.id.tree_back_menu)
    public void onClick() {
        finish();
    }


    private void dataStore(String data) {

//解析
        try {
            dataInit = JSON.parseObject(data,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        } catch (Exception e) {

            e.printStackTrace();
        }

        Log.e("TAG", "网络获取下拉树后解析1" + dataInit.toString());
//判断大小
        if (dataInit != null && dataInit.size() > 0) {
            mDatas = new ArrayList<>();
            mDatas2 = new ArrayList<>();
            for (int i = 0; i < dataInit.size(); i++) {
                //获取id
                int id = Integer.valueOf(String.valueOf(dataInit.get(i).get("menuId")));
                //获取name
                String label = String.valueOf(dataInit.get(i).get("menuName"));
                //获取父亲的Id
                int pId = Integer.valueOf(String.valueOf(dataInit.get(i).get("parent_menuId")));
                FileBean bean = new FileBean(id, pId, label);
                mDatas.add(bean);
                OrgBean bean2 = new OrgBean(id, pId, label);
                mDatas2.add(bean2);
            }
            try {
                mAdapter = new SimpleTreeListViewAdapter_noCHeckbox<>(mTree, this,
                        mDatas2, 0);
                mTree.setAdapter(mAdapter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            initEvent();
        } else {
            Toast.makeText(MenuActivity.this, "无菜单数据",
                    Toast.LENGTH_SHORT).show();
        }


        try {
            mAdapter = new SimpleTreeListViewAdapter_noCHeckbox<>(mTree, this,
                    mDatas2, 0);
            mTree.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        initEvent();
    }

    private void initEvent() {
        mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
                if (node.isLeaf())
                {

                    Map<String, Object> itemData=dataInit.get(position);
                    toItem(itemData);

//                    Toast.makeText(MenuActivity.this, node.getName(),
//                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id)
            {
                // DialogFragment
//                final EditText et = new EditText(TreeViewActivity.this);
//                new AlertDialog.Builder(TreeViewActivity.this).setTitle("Add Node")
//                        .setView(et)
//                        .setPositiveButton("Sure", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which)
//                            {
//
//                                if (TextUtils.isEmpty(et.getText().toString()))
//                                    return;
//                                mAdapter.addExtraNode(position, et.getText()
//                                        .toString());
//                            }
//                        }).setNegativeButton("Cancel", null).show();

                return true;
            }
        });
    }


    public void toItem(Map<String, Object> itemData) {
        String itemDataString= JSONArray.toJSONString(itemData);
        Intent intent = new Intent();
        intent.setClass(this, ListActivity.class);
        intent.putExtra("itemData",itemDataString);
        startActivity(intent);
    }
}
