package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.ChangeStuInfoAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.ListMapBean;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.TMap;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
public class EditActivity extends BaseActivity {
    private ListView mListView;
    private String changeUrl;
    private String changePageId;
    private String commitUrl;
    private String tableId;
    private List<Map<String, Object>> mListValues = new ArrayList<>();
    private ChangeStuInfoAdapter mChangeStuInfoAdapter;
    private int fieldRole;
    private String mainId;
    private int selectedFruitIndex = 0;
    private RequestQueue mRequestQueue;//定义网络请求队列
    private Map<String, Object> mapData = new HashMap<>();//定义存储data数据的集合
    private Map<String, Object> mapSet = new HashMap<>();//定义存储配置数据的集合
    public static Map<String, String> mapCommit = new HashMap<>();//存储提交的数据
    private String strUrl;//请求修改页面权限的网址

    public TMap<String, String> tMap = new TMap<>();

    private Map<String, String> paramsMap=new HashMap<>();
    private String paramsString;

    /**
     *
     */
    private DiskLruCacheHelper DLCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloseActivityClass.activityList.add(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_edit);
        try {
            DLCH = new DiskLruCacheHelper(EditActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取传递过来的数据
        getBundleData();
        mapCommit.put(Constant.USER_NAME, Constant.USERNAME_ALL);
        mapCommit.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
        mapCommit.put(Constant.tableId, tableId);
        mapCommit.put(Constant.pageId, changePageId);
        mapCommit.put("t0_au_" + tableId + "_" + changePageId, mainId + "");
        mapCommit.put("mainId", mainId + "");
        //请求要修改的学员字段

        paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);//"15535211113"
        paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);//"111111"
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, changePageId);
        paramsString=paramsMap.toString();



        requestField1(changeUrl.replaceFirst("10.252.46.80","182.92.108.162"));
        initView();
    }


    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_stu);
        ImageView mCancel = (ImageView) findViewById(R.id.tv_cancel_edit);
        ImageView mCommit = (ImageView) findViewById(R.id.tv_commit_edit);

        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitStuInfo();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //根据fieldRole,判断字段类型
                fieldRole = Integer.parseInt(String.valueOf(mListValues.get(position).get("fieldRole")));
                int viewType = Constant.getViewType(fieldRole);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                if (viewType == 1 || viewType == 2) {
                    //跳转到Txt格式修改页面
                    intent.setClass(EditActivity.this, InputTxtActivity.class);
                    bundle.putInt("position", position);
                    bundle.putString("value", (String) mListValues.get(position).get("rightData"));
                    bundle.putString("fieldId", (String) mListValues.get(position).get("fieldId"));
                    bundle.putString("ifMust", (String) mListValues.get(position).get("ifMust"));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 2);
                } else if (viewType == 3) {
                    List<Map<String, Object>> listMap = (List<Map<String, Object>>) mListValues.get(position).get("mapDic");
                    List<String> strList = new ArrayList<>();
                    List<String> strIdList = new ArrayList<>();
                    String strName;
                    String strId;
                    for (int i = 0; listMap.size() > i; i++) {
                        strName = (String) listMap.get(i).get("DIC_NAME");

                        strId = "" + listMap.get(i).get("DIC_ID");
                        strList.add(strName);
                        strIdList.add(strId);

                    }
                    final String[] dicOptions = strList.toArray(new String[strList.size()]);
                    final String[] dicIdOptions = strIdList.toArray(new String[strIdList.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
//                    builder.setIcon(R.mipmap.ic_launcher);
//                    builder.setTitle("请选择");
                    builder.setSingleChoiceItems(dicOptions, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedFruitIndex = which;
                            mListValues.get(position).put("rightData", dicOptions[selectedFruitIndex]);
                            mapCommit.put(mListValues.get(position).get("jiChuKey") + "", dicIdOptions[selectedFruitIndex] + "");
                            mChangeStuInfoAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                            selectedFruitIndex = 0;
                        }
                    });
                    builder.show();
                } else if (fieldRole == 21 || fieldRole == 22) {
                    Intent intent2 = new Intent(EditActivity.this, MultiSelect_EditActivity.class);
                    intent2.putExtra("position", position);
                    List<List<Map<String, Object>>> listMap = (List<List<Map<String, Object>>>) mListValues.get(position).get("listListData");
                    ListMapBean listMapBean = new ListMapBean();
                    listMapBean.setMapList(listMap);
                    bundle.putSerializable("listMap", listMapBean);

                    List<Map<String, Objects>> searchData = (List<Map<String, Objects>>) mapSet.get("phoneUpdateFieldSet");

                    String dialogSearchUrl = searchData.get(position).get("dialogSearchUrl") + "";
                    String dialogSearchSet = searchData.get(position).get("dialogSearchSet") + "";
                    intent2.putExtra("dialogFieldSet", searchData.get(position).get("dialogFieldSet") + "");
                    intent2.putExtras(bundle);
                    intent2.putExtra("dialogSearchUrl", dialogSearchUrl);
                    intent2.putExtra("dialogSearchSet", dialogSearchSet);
                    startActivityForResult(intent2, 1);
                }

            }
        });
    }

    //提交修改

    /**
     * 需要在点提交按钮的时候检查
     * 1、必填字段是否为空
     * 2、学员姓名是否含有特殊字符（中文 英文 空格）
     * 3、学员手机号是否为11位数字
     */
    public void commitStuInfo() {
        getProgressDialog().setMessage("正在修改中。。");
        getProgressDialog().show();
        Log.e("TAG", "进入修改数据提交"+ mListValues.get(mListValues.size()-1).get("rightData") + "");
        String jiChuZhi = commitUrl + "?";
        Log.e("TAG", "jiChuZhi" + jiChuZhi);
        String diYiCeng = "";
        for (String key : mapCommit.keySet()) {
            String valuesResult1=mapCommit.get(key);
            diYiCeng += key + "=" + valuesResult1 + "&";
        }
        Log.e("TAG", "diYiCeng" + diYiCeng);
        String diErCengTemp = "";
        String diErCeng = "";
        if (tMap.size() > 0) {
            for (String key : tMap.keySet()) {
                List<String> newStr = tMap.get(key);
                for (int i = 0; i < newStr.size(); i++) {
                    diErCengTemp = diErCengTemp + key + "=" + newStr.get(i) + "&";
                }
            }

            if (diErCengTemp != null) {
                diErCeng = diErCengTemp.substring(0, diErCengTemp.length() - 1);
            }
        }


        Log.e("TAG", "diErCeng" + diErCeng);
        String url = jiChuZhi + diYiCeng + diErCeng;
        Log.e("TAG", "URL" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url.replaceFirst("10.252.46.80","182.92.108.162"), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("TAG", "返回正确的成功码" + s);
                getProgressDialog().dismiss();
                if (s.equals("1")) {
                    Toast.makeText(EditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(EditActivity.this, ListActivity.class);
                startActivity(intent);
                //finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                getProgressDialog().dismiss();

                Log.e("TAG", "修改失败返回的错误码" + volleyError.toString());
            }
        });
        mRequestQueue.add(stringRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 2) {
                String values = data.getStringExtra("Values");
                mListValues.get(resultCode).put("rightData", values);
                if(values!=null){
                        Log.e("TAG",""+values);
                    try {
                        String a= URLEncoder.encode(values, "utf-8");
                        mapCommit.put(mListValues.get(resultCode).get("jiChuKey") + "", URLEncoder.encode(values, "utf-8"));
                        Log.e("TAG", "values" + values);
                        Log.e("TAG", "a" + a);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else{
                    mapCommit.put(mListValues.get(resultCode).get("jiChuKey") + "", "");

                }

                Log.e("TAG", "mListData传过来的基础类型的值" + values);
                mChangeStuInfoAdapter.notifyDataSetChanged();
            } else if (requestCode == 1) {

                List<String> mListData = (List<String>) data.getSerializableExtra("Values");
                List<String> mListKey = (List<String>) data.getSerializableExtra("ValuesKey");
                Log.e("TAG", "mListData传过来的值" + mListData);
                mListValues.get(resultCode).put("rightData", mListData.toString());
                Log.e("TAG", "data.getStringExtra(\"Values\")" + data.getStringExtra("Values") + "");
                //将其他基础数据添加进map

                mapCommit.put(mListValues.get(resultCode).get("jiChuKey") + "", mListData.size() + "");
                tMap.remove(mListValues.get(resultCode).get("childKey"));
                for (int z = 0; z < mListData.size(); z++) {
                    tMap.putValue(mListValues.get(resultCode).get("childKey") + "", mListKey.get(z) + "");
                }

                mChangeStuInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getBundleData() {
        Intent mIntent = this.getIntent();
        Map<String, Object> map_info= (Map<String, Object>) mIntent.getSerializableExtra("info_data");

        commitUrl =map_info.get("operaBtnCommitUrl")+"";
        changeUrl = map_info.get("operaBtnTurnUrl")+"";
        strUrl = map_info.get("oprBtnPageDataUrl")+"";
        Log.e("TAG","oprBtnPageDataUrl:"+strUrl);
        changePageId =map_info.get("startTurnPage")+"";
        tableId =map_info.get("info_tableId")+"";
        mainId = map_info.get("info_mainId")+"";
    }

    /**
     * 2、通过网络获取左侧的数据
     */
    public void requestField1(final String volleyUrl) {
        Log.e("TAG", "修改界面左侧地址" + volleyUrl);
        mRequestQueue = VolleySingleton.getVolleySingleton(this.getApplicationContext()).getRequestQueue();
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        DLCH.put(volleyUrl+paramsString, jsonData);
                        Log.e("TAG", "获取修改界面-左侧数据" + jsonData);
                        stuFieldAnalysis(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//读取本地后也转至处理
                VolleySingleton.onErrorResponseMessege(EditActivity.this, volleyError);
                String diskData = DLCH.getAsString(volleyUrl+paramsString);
                stuFieldAnalysis(diskData);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    /**
     * 3、解析请求到的左侧数据
     */
    public void stuFieldAnalysis(String jsonData) {
        mapSet = JSON.parseObject(jsonData, Map.class);
        requestStuData(strUrl.replaceFirst("10.252.46.80","182.92.108.162"));
    }

    /**
     * 4、通过网络获取左侧的数据+url requestStuField()
     * 这里面包含数据展示的判定样式：跳转编辑框、弹窗选择类似校区、弹窗选择类似性别
     */
    public void requestStuData(final String volleyUrl) {
        Log.e("TAG", "获取修改界面-右侧数据" + volleyUrl);
        Log.e("TAG", "tableId" + tableId);
        Log.e("TAG", "changePageId" + changePageId);
        Log.e("TAG", "mainId" + mainId);
        Log.e("TAG", "mainTableId" + tableId);
        mRequestQueue = VolleySingleton.getVolleySingleton(this.getApplicationContext()).getRequestQueue();
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        DLCH.put(volleyUrl+paramsString, jsonData);
                        Log.e("TAG", "获取修改界面-右侧数据" + jsonData);
                        stuDataAnalysis(jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//读取本地后也转至处理
                VolleySingleton.onErrorResponseMessege(EditActivity.this, volleyError);
                Log.e("TAG", "获取修改界面-右侧数据失败");
                String diskData = DLCH.getAsString(volleyUrl+paramsString);
                stuDataAnalysis(diskData);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =paramsMap;
                map.put(Constant.MAINID, mainId);
                map.put(Constant.MAINTABLEID, tableId);
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    /**
     * 5、
     * A：分开顺序展示先展示左侧，再根据左侧展示右侧
     * 右侧点击的时候跳到不同的页面
     * <p/>
     * B：直接展示data数据到修改页面然后再修改左侧的值
     */
    //解析data数据并展示
    public void stuDataAnalysis(String jsonData) {
        mapData = JSON.parseObject(jsonData, Map.class);
        if(mapSet!=null&&mapData!=null){
        unionVision();}
    }

    public void unionVision() {
        //1、摘取最后一层实际数据：包括配置和数据
        List<Map<String, Object>> mapSet1 =
                (List<Map<String, Object>>) mapSet.get("phoneUpdateFieldSet");
        List<Map<String, Object>> mapData1 =
                (List<Map<String, Object>>) mapData.get("rows");
        //2、外循环，循环次数以配置数据为准
        for (int i = 0; i < mapSet1.size(); i++) {
            Map<String, Object> mapEdit = new HashMap<>();//重新定义修改数据
            int fieldRole = (int) mapSet1.get(i).get("fieldRole");//获取fieldRole
            String ifMust = mapSet1.get(i).get("ifMust") + "";//获取是否必填
            String fieldId = mapSet1.get(i).get("fieldId") + "";//获取fieldId
        //3、依据fieldRole判断
            if (fieldRole == 21) {
                Log.e("TAG", "已经进入21");
                String dataLeftValue = (String) mapSet1.get(i).get("fieldCnName");
                //获取右侧内容
                //1、学员机构汉字key即为上一步左侧中文名，由key获取data中的列表
                Map<String, Object> dataInnerMap = (Map<String, Object>) mapData1.get(0).get(dataLeftValue);
                List<Map<String, Object>> mapDataInnerList = (List<Map<String, Object>>) dataInnerMap.get("rows");
                //设置临时存储的空串
                String contentList = "";
                //拼接Key
                List<Map<String, Object>> mapTemp = (List<Map<String, Object>>) mapSet1.get(i).get("dialogFieldSet");
                Map<String, Object> mapSetTemp = (Map<String, Object>) mapSet1.get(i).get("dialogDataList");
                List<Map<String, Object>> mapInnerSet = (List<Map<String, Object>>) mapSetTemp.get("rows");
                String keyTemp1 = "" + mapTemp.get(0).get("tableId");
                String keyTemp2 = (String) mapTemp.get(0).get("fieldAliasName");
                String keyTemp = "F_" + keyTemp1 + "_" + keyTemp2;
                //遍历上边获得的列表
                List<Integer> valueId = new ArrayList<>();
                String keyId = "T_" + mapTemp.get(0).get("tableId") + "_0";
                for (int j = 0; j < mapDataInnerList.size(); j++) {
                    contentList = contentList + mapDataInnerList.get(j).get(keyTemp) + " ";
                    valueId.add((Integer) mapDataInnerList.get(j).get(keyId));
                }
                //将字典发给map列表
                //获取字典的设置
                List<List<Map<String, Object>>> listListData = new ArrayList<>();
                //遍历"dialogFieldSet"，创建新属性

                for (int n = 0; n < mapInnerSet.size(); n++) {//右侧的数据遍历
                    List<Map<String, Object>> mapArray = new ArrayList<>();
                    for (int m = 0; m < mapTemp.size(); m++) {//左侧的配置循环
                        Map<String, Object> singleMap = new HashMap<>();
                        //学校名称的左侧中文名
                        String leftData = (String) mapTemp.get(m).get("fieldCnName");
                        //学校名称的key:分两种情况：是否含有"dicParId"
                        String fieldAliasName;
                        if (mapTemp.get(m).containsKey("dicParId")) {
                            fieldAliasName = "DIC_" + mapTemp.get(m).get("fieldAliasName");
                        } else {
                            fieldAliasName = (String) mapTemp.get(m).get("fieldAliasName");

                        }
                        //找到拼接的key
                        String pinJieTableId = "T_" + mapTemp.get(m).get("tableId") + "_0";
                        singleMap.put("leftData", leftData);
                        singleMap.put("rightData", ""+mapInnerSet.get(n).get(fieldAliasName));
                        singleMap.put("pinJieTableId", pinJieTableId);
                        singleMap.put("idValue", mapInnerSet.get(n).get(pinJieTableId));
                        int a = (int) mapInnerSet.get(n).get(pinJieTableId);
                        if (valueId.indexOf(a) >= 0) {
                            singleMap.put("isCheck", true);
                        } else {
                            singleMap.put("isCheck", false);
                        }
                        //至此收集到了一个字段的所有信息
                        mapArray.add(singleMap);//这是一个校区的所有信息
                    }
                    listListData.add(mapArray);
                }
                String jiChuKey = "t0_au_" + mapSet1.get(i).get("relationTableId") + "_" +
                        mapSet1.get(i).get("showFieldArr") +
                        "_" + mapSet1.get(i).get("fieldId") + "_dz";
                String childKey = "t1_au_" + mapSet1.get(i).get("relationTableId") + "_" +
                        mapSet1.get(i).get("showFieldArr") +
                        "_" + mapSet1.get(i).get("dialogField");
                String dialogFieldSet=JSON.toJSONString(dataInnerMap);
                mapEdit.put("leftData", dataLeftValue);
                mapEdit.put("rightData", contentList);
                mapEdit.put("ifMust", ifMust);
                mapEdit.put("fieldId", fieldId);
                mapEdit.put("fieldRole", fieldRole);
                mapEdit.put("jiChuKey", jiChuKey);
                mapEdit.put("childKey", childKey);
                mapEdit.put("listListData", listListData);
                mapEdit.put("dialogFieldSet", dialogFieldSet);
                mapCommit.put(jiChuKey, mapDataInnerList.size() + "");
                for (int j = 0; j < mapDataInnerList.size(); j++) {
                    tMap.putValue(childKey, listListData.get(j).get(0).get("idValue") + "");
                }
                mListValues.add(mapEdit);
            }else if(fieldRole == 16 || fieldRole == 17 || fieldRole == 23){
                String dataLeftValue = (String) mapSet1.get(i).get("fieldCnName");
                String dataKey = "DIC_" + mapSet1.get(i).get("fieldAliasName");

                String rightData="";
                if(mapData1.get(0).get(dataKey)!=null){

                    rightData=mapData1.get(0).get(dataKey)+"";}
                //解析词典
                String s = (String) mapSet1.get(i).get("dicOptions");
                String s1 = s.replace("\\", "");
                com.alibaba.fastjson.JSONArray ja = JSON.parseArray(s1);
                List<Map<String, Object>> mapDic = new ArrayList<>();
                String id = "";
                for (int k = 0; ja.size() > k; k++) {
                    String str = ja.get(k).toString();

                    Map<String, Object> mapDicItem = JSON.parseObject(str, Map.class);
                    mapDic.add(mapDicItem);
                    if (mapDicItem.get("DIC_NAME").equals(rightData)) {

                        id = ""+mapDicItem.get("DIC_ID");
                    }
                }
                Log.e("TAG", "listMapToString" + mapDic.toString());
                String jiChuKey = "t0_au_" + tableId + "_" + changePageId + "_" + mapSet1.get(i).get("fieldId");
                mapEdit.put("leftData", dataLeftValue);
                mapEdit.put("rightData", rightData);
                mapEdit.put("ifMust", ifMust);
                mapEdit.put("fieldId", fieldId);
                mapEdit.put("fieldRole", fieldRole);
                mapEdit.put("mapDic", mapDic);
                mapEdit.put("jiChuKey", "" + jiChuKey);
                mapCommit.put(jiChuKey, id);
                mListValues.add(mapEdit);
            }else{
                String dataLeftValue = (String) mapSet1.get(i).get("fieldCnName");
                String dataKey = (String) mapSet1.get(i).get("fieldAliasName");
                String jiChuKey = "t0_au_" + tableId + "_" + changePageId + "_" + mapSet1.get(i).get("fieldId");
                String rightData="";
                if(mapData1.get(0).get(dataKey)!=null){
                    rightData=mapData1.get(0).get(dataKey)+"";}
                mapEdit.put("leftData", dataLeftValue);
                mapEdit.put("rightData", rightData);
                mapEdit.put("ifMust", ifMust);
                mapEdit.put("fieldId", fieldId);
                mapEdit.put("fieldRole", fieldRole);
                mapEdit.put("jiChuKey", jiChuKey);
                if(mapData1.get(0).get(dataKey)!=null){

                    String a= null;
                    boolean b= Utils.isChineseChar(mapData1.get(0).get(dataKey) + "");
                    if(b){
                        try {
                            a = URLEncoder.encode(mapData1.get(0).get(dataKey) + "", "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        mapCommit.put(jiChuKey, a+"");}else{
                        mapCommit.put(jiChuKey, mapData1.get(0).get(dataKey) + "");

                    }

                }
                else{
                    mapCommit.put(jiChuKey,"");
                }
                mListValues.add(mapEdit);


            }
        }

        if (mListValues != null) {
            Log.e("TAG","打印数据源："+mListValues.toString());
            mChangeStuInfoAdapter = new ChangeStuInfoAdapter(mListValues, EditActivity.this);
            mListView.setAdapter(mChangeStuInfoAdapter);
        }

    }
}
