package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.treeViewUtils.FileBean;
import com.kwsoft.kehuhua.treeViewUtils.Node;
import com.kwsoft.kehuhua.treeViewUtils.OrgBean;
import com.kwsoft.kehuhua.treeViewUtils.SimpleTreeListViewAdapter;
import com.kwsoft.kehuhua.treeViewUtils.TreeListViewAdapter;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class TreeViewActivity extends BaseActivity {
    Map<String, Object> dataMap;
    Map<String, String> paramsMap = new HashMap<>();
    String paramsStr, tableId, pageId;
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
        initView();
        getDataIntent();//获取数据


        requestData();

    }
    private CommonToolbar mToolbar;
    @Override
    public void initView() {

        mTree = (ListView) findViewById(R.id.list_tree);
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.edit_commit1));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump2Activity();
            }
        });
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
        String viewName=intent.getStringExtra("viewName");

        mToolbar.setTitle(viewName);
        String  needFilterListStr = String.valueOf(intent.getStringExtra("needFilterListStr"));

        List<Map<String,String>> needFilterList=JSON.parseObject(needFilterListStr,
                new TypeReference<List<Map<String, String>>>() {
                });

        Log.e("TAG", "传递到下拉树多选activity中多值的Id" + idArrStr);

        try {
            String[] idArr = idArrStr.split(",");
            Integer[] idArrInt = new Integer[idArr.length];

            for (int i = 0; i < idArr.length; i++) {
                idArrInt[i] = Integer.valueOf(idArr[i]);
            }

            Collections.addAll(idArrList, idArrInt);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        if (needFilterList!=null&&needFilterList.size()>0) {
            for (int i=0;i<needFilterList.size();i++) {
                paramsMap.putAll(needFilterList.get(i));
            }
        }
        paramsStr = JSON.toJSONString(paramsMap);

    }

    private static final String TAG = "TreeViewActivity";
    
    public void requestData() {
        final String volleyUrl = Constant.sysUrl + Constant.requestTreeDialog;

        Log.e("TAG", "网络获取内多dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取下拉树参数 " + paramsMap.toString());

        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(TreeViewActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }


    private void setStore(String data) {

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
        if(Constant.jumpNum1==4){
            intentTree.setClass(TreeViewActivity.this, AddTemplateDataActivity.class);
        }else if(Constant.jumpNum==1){
            intentTree.setClass(TreeViewActivity.this, OperateDataActivity.class);
        }

        Bundle bundle = new Bundle();
        bundle.putString("myValue", myValue);

        intentTree.putExtra("bundle", bundle);
        setResult(2, intentTree);
        this.finish();
    }
}