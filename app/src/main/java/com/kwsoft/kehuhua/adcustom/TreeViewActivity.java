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
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.treeViewUtils.FileBean;
import com.kwsoft.kehuhua.treeViewUtils.Node;
import com.kwsoft.kehuhua.treeViewUtils.OrgBean;
import com.kwsoft.kehuhua.treeViewUtils.SimpleTreeListViewAdapter;
import com.kwsoft.kehuhua.treeViewUtils.TreeListViewAdapter;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TreeViewActivity extends AppCompatActivity {
    Map<String, Object> dataMap;
    Map<String, String> paramsMap = new HashMap<>();
    String paramsStr, tableId, pageId;
    @Bind(R.id.tree_back_edit)
    ImageView treeBackEdit;
    @Bind(R.id.tree_list_commint)
    ImageView treeListCommint;
    @Bind(R.id.tree_textViewTitle)
    TextView treeTextViewTitle;
    private String isMulti;
    private ListView mTree;
    private SimpleTreeListViewAdapter<OrgBean> mAdapter;
    private List<FileBean> mDatas;
    private List<OrgBean> mDatas2;
    private List<Map<String, Object>> dataInit = new ArrayList<>();
    private List<Integer> idArrList = new ArrayList<>();
    private String position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_view);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getDataIntent();//获取数据

        mTree = (ListView) findViewById(R.id.list_tree);
        requestData();

    }

    private void getDataIntent() {

        Intent intent = getIntent();
        String multiValueData = intent.getStringExtra("treePraStr");
        dataMap = JSON.parseObject(multiValueData,
                new TypeReference<Map<String, Object>>() {
                });

        tableId = String.valueOf(dataMap.get("tableId"));
        pageId = String.valueOf(dataMap.get("pageDialog"));
        String idArrStr = intent.getStringExtra("idArrs");
        isMulti = String.valueOf(intent.getStringExtra("isMulti"));
        position= String.valueOf(intent.getStringExtra("position"));
        Log.e("TAG", "传递到多选activity中多值的Id" + idArrStr);
        if (!idArrStr.equals("")) {
            String[] idArr = idArrStr.split(",");
            Integer[] idArrInt = new Integer[idArr.length];

            for (int i = 0; i < idArr.length; i++) {
                idArrInt[i] = Integer.valueOf(idArr[i]);
            }

            Collections.addAll(idArrList, idArrInt);
        }

        Log.e("TAG", "传递到多选activity中多值后转换的Id" + idArrList);

        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        paramsStr = JSON.toJSONString(paramsMap);
    }


    private void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestTreeDialog;

        Log.e("TAG", "网络获取内多dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取内多table " + paramsMap.get("tableId"));
        Log.e("TAG", "网络获取内多page " + paramsMap.get("pageId"));
        //Log.e("TAG", "获取" + Constant.timeName + " " +dataTime);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取下拉树" + jsonData);
                        dataStore(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //refreshListView.hideFooterView();
                VolleySingleton.onErrorResponseMessege(TreeViewActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);

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
            mDatas = new ArrayList<FileBean>();
            mDatas2 = new ArrayList<OrgBean>();
            for (int i = 0; i < dataInit.size(); i++) {
                //获取id
                int id = Integer.valueOf(String.valueOf(dataInit.get(i).get("id")));
                //获取name
                String label = String.valueOf(dataInit.get(i).get("name"));
                //获取父亲的Id
                int pId = Integer.valueOf(String.valueOf(dataInit.get(i).get("pId")));
                FileBean bean = new FileBean(id, pId, label);
                mDatas.add(bean);
                OrgBean bean2 = new OrgBean(id, pId, label);
                mDatas2.add(bean2);
            }
            try {
                mAdapter = new SimpleTreeListViewAdapter<OrgBean>(mTree, this,
                        mDatas2, 0, isMulti, idArrList);
                mTree.setAdapter(mAdapter);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            initEvent();


        } else {
            Toast.makeText(TreeViewActivity.this, "无下拉数据",
                    Toast.LENGTH_SHORT).show();
        }


        try {
            mAdapter = new SimpleTreeListViewAdapter<OrgBean>(mTree, this,
                    mDatas2, 0, isMulti, idArrList);
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
                    Toast.makeText(TreeViewActivity.this, node.getName(),
                        Toast.LENGTH_SHORT).show();
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


    private void jump2Activity() {
        Map<String, Object> map = new HashMap<>();
        String ids = "";
        String names = "";
        int num = Constant.idArrList.size();
        for (int i = 0; i < Constant.idArrList.size(); i++) {
            ids += String.valueOf(Constant.idArrList.get(i)) + ",";
        }
        for (int i = 0; i < dataInit.size(); i++) {
            if (Constant.idArrList.contains(
                    Integer.valueOf(String.valueOf(dataInit.get(i).get("id")))
            )) {
                names += String.valueOf(dataInit.get(i).get("name")) + " ";
            }

        }

        map.put("num", num);
        map.put("isMulti", isMulti);
        map.put("position", position);
        if (!ids.equals("")) {
            map.put("ids", ids.substring(0, ids.length() - 1));
            map.put("names", names.substring(0, names.length() - 1));
        } else {
            map.put("ids", ids);
            map.put("names", names);
        }

        Log.e("TAG", "选中后的值 ： " + ids);

        String myValue = JSON.toJSONString(map);
        Intent intentTree = new Intent();
        if (Constant.jumpNum == 1) {
            intentTree.setClass(TreeViewActivity.this, AddItemsActivity.class);
        } else if (Constant.jumpNum == 2) {
            intentTree.setClass(TreeViewActivity.this, RowsEditActivity.class);
        }

        Bundle bundle = new Bundle();
        bundle.putString("myValue", myValue);

        intentTree.putExtra("bundle", bundle);
        setResult(2, intentTree);
        this.finish();
    }

    @OnClick({R.id.tree_back_edit, R.id.tree_list_commint})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tree_back_edit:
                this.finish();
                break;
            case R.id.tree_list_commint:
                jump2Activity();
                break;
        }
    }
}