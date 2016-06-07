package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.AddStuBtnTurnBaseAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/11/27 0027.
 */
public class AddActivity extends BaseActivity implements View.OnClickListener {
    private ListView mListView;
    private String tableId;
    private String buttonCommitUrl;//第二层请求
    private int startTurnPage;//链接字段
    private AddStuBtnTurnBaseAdapter adapter;
    private Map<String, String> paramsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initView();
       Map<String,Object> pageData= (Map<String, Object>) getIntent().getSerializableExtra("phoneButtonAdd");
        String buttonTurnUrl = (String) pageData.get("buttonTurnUrl");
        startTurnPage =Integer.parseInt((String) pageData.get("startTurnPage"));
        String commitUrl = (String) pageData.get("buttonCommitUrl");
        buttonCommitUrl=commitUrl.replaceFirst("10.252.46.80","182.92.108.162");
        tableId = getIntent().getStringExtra("tableId");

        paramsMap=new HashMap<>();
        paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);//"15535211113"
        paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);//"111111"
        paramsMap.put(Constant.tableId, tableId + "");
        paramsMap.put(Constant.pageId, startTurnPage + "");
        requestAddData(buttonTurnUrl.replaceFirst("10.252.46.80","182.92.108.162"));
    }

    public void requestAddData(String volleyUrl) {
        String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储
                        Log.e("TAG", jsonData);
                        analysisAddData(jsonData);//解析并转至字段数据请求
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {//失败后读取本地
                VolleySingleton.onErrorResponseMessege(AddActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = paramsMap;
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    /**
     * 初始化布局
     */
    public void initView() {
        mListView = (ListView) findViewById(R.id.lv_stu_info);
        ImageView mCancel = (ImageView) findViewById(R.id.IV_back_list_add);
        ImageView mCommit = (ImageView) findViewById(R.id.tv_commit_add);
        mCancel.setOnClickListener(this);
        mCommit.setOnClickListener(this);

    }
    public void analysisAddData(String jsonData) {
        Log.e("TAG", "获取添加页面数据成功"+jsonData);

        Map mapData= JSON.parseObject(jsonData, Map.class);
        List<Map<String, Object>> mapSet = (List<Map<String, Object>>) mapData.get("phoneAddFieldSet");

        //Log.e("TAG", "获取添加页面数据成功"+mapSet.get(8).toString());
        try {
            JSONObject object = new JSONObject(jsonData);
            JSONArray pafsArray = object.getJSONArray("phoneAddFieldSet");
            List<Map<String, String>> phoneAddFieldSets = new ArrayList<Map<String, String>>();
            Log.e("TAG", "断点1");
            if (pafsArray.length() > 0) {
                for (int i = 0; i < pafsArray.length()-1; i++) {
                    JSONObject object1 = pafsArray.getJSONObject(i);
                    String fieldRole = object1.getString("fieldRole");
                    Map<String, String> map = new HashMap<>();
                    //21 为内部多值对象
//                    if (("21".equals(fieldRole)) || ("20".equals(fieldRole))) {
                    if ("21".equals(fieldRole)) {
                        JSONObject dialogDataList = new JSONObject(object1.getString("dialogDataList"));
                        String dialogDataListStr = dialogDataList.toString();
                        map.put("dialogDataList", dialogDataListStr);
                        JSONArray dialogFieldSetarr = object1.getJSONArray("dialogFieldSet");
                        String dialogFieldSet = dialogFieldSetarr.toString();
                        JSONArray dialogSearchSetarr = object1.getJSONArray("dialogSearchSet");
                        String dialogSearchSet = dialogSearchSetarr.toString();
                        map.put("dialogFieldSet", dialogFieldSet);
                        map.put("dialogField", object1.getString("dialogField"));
                        map.put("relationTableId", object1.getString("relationTableId"));
                        map.put("showFieldArr", object1.getString("showFieldArr"));
                        map.put("dialogSearchSet", dialogSearchSet);
                        map.put("dialogSearchUrl", object1.getString("dialogSearchUrl"));
                    } else if ("16".equals(fieldRole)) {
                        String dicOptions = object1.getString("dicOptions");
                        map.put("dicOptions", dicOptions);
                    }
                    Log.e("TAG", "断点4");
                    map.put("fieldRole", fieldRole);
                    map.put("fieldCnName", object1.getString("fieldCnName"));
                    map.put("fieldId", object1.getString("fieldId"));
                    map.put("ifMust", object1.getString("ifMust"));
                    map.put("fieldId", object1.getString("fieldId"));

                    phoneAddFieldSets.add(map);
                    Log.e("TAG", "添加页面配置数据获取"+pafsArray.length()+" "+i+" "+phoneAddFieldSets.toString());
                }
                Log.e("TAG", "断点2");
                adapter = new AddStuBtnTurnBaseAdapter(AddActivity.this, AddActivity.this, phoneAddFieldSets);
                mListView.setAdapter(adapter);
                Log.e("TAG", "断点3");
                Log.e("TAG", "添加页面配置数据获取"+phoneAddFieldSets.toString());
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String fieldrole = adapter.getItem(i).get("fieldRole");

                        int position = i;
                        if ("21".equals(fieldrole)) {
                            TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
                            String content = tvContent.getText().toString();

                            Intent intent = new Intent(AddActivity.this, MultiSelectActivity.class);
                            intent.putExtra("dialogDataList", adapter.getItem(i).get("dialogDataList"));
                            intent.putExtra("dialogFieldSet", adapter.getItem(i).get("dialogFieldSet"));
                            intent.putExtra("dialogSearchSet", adapter.getItem(i).get("dialogSearchSet"));
                            intent.putExtra("dialogSearchUrl", adapter.getItem(i).get("dialogSearchUrl"));
                            intent.putExtra("content", content);
                            intent.putExtra("position", position);
                            startActivityForResult(intent, 1);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data        主要用于返回内部对象多值类字段中选择的数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data != null) {
                    String sb = data.getStringExtra("sb");
                    String sbid = data.getStringExtra("sbid");
                    int position = data.getIntExtra("position", -1);
                    if (position >= 0) {
                        View view = mListView.getChildAt(position);
                        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
                        TextView name_id = (TextView) view.findViewById(R.id.name_id);
                        tv_content.setText(sb);
                        name_id.setText(sbid);
                        Map<String, String> maps = adapter.maps;
                        maps.put(position + "", 5 + "/" + position + "/" + sbid);

                    }
                }
                break;
            default:

                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_back_list_add:
                finish();
                break;
            case R.id.tv_commit_add:
                //提交功能  弹出框 提问
                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setMessage("确认提交吗？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        commitMethod();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            default:
                break;
        }
    }

    /**
     * 提交方法
     */
    public void commitMethod() {
        Map<String, String> maps = adapter.maps;
        //Log.e("maps===========", maps.toString() + "");
        final List<String> listUrl = new ArrayList<>();
        List<String> mustList = new ArrayList<>();
        if (maps.size() > 0) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Map<String, String> mapAdapter1 = adapter.getItem(i);
                String ifMust = mapAdapter1.get("ifMust").trim();
                if ("1".equals(ifMust)) {
                    mustList.add(i + "");
                }
            }

            Set<String> keySet = maps.keySet();
            if (keySet.size() >= mustList.size()) {
                //遍历key集合，获取value
                for (String key : keySet) {
                    String restrval = getValue(maps, mustList, key);
                    if ("1".equals(restrval)) {
                        Toast.makeText(AddActivity.this, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("2".equals(restrval)) {
                        Toast.makeText(AddActivity.this, "身份证格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("3".equals(restrval)) {
                        Toast.makeText(AddActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("4".equals(restrval)) {
                        Toast.makeText(AddActivity.this, "姓名中不能包含特殊字符或长度太长", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listUrl.add(restrval);
                }

                if ((mustList.size() != 0) || listUrl.contains("0")) {
                    Toast.makeText(AddActivity.this, "必填数据必须填写", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    threadCom(listUrl);
                }
            } else {
                Toast.makeText(AddActivity.this, "必填选项必须填写！！！", Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(AddActivity.this, "您还没有填写数据...", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param listUrl 连接网络，提交数据
     *                replaceFirst("10.252.46.80","182.92.108.162")
     */
    public void threadCom(final List<String> listUrl) {
        new Thread() {
            public void run() {
                String[] mParamsName = {Constant.USER_NAME, Constant.PASSWORD, Constant.tableId, Constant.pageId};
                String[] mParamValue = {Constant.USERNAME_ALL, Constant.PASSWORD_ALL, tableId, startTurnPage+""};
                String result = getUrlConnection(mParamsName,
                        mParamValue, listUrl);
                if ("1".equals(result)) {
                    Constant.threadToast(AddActivity.this, "添加成功！");
                } else {
                    Constant.toastMeth(AddActivity.this, "未提交成功！！");
                }
            }
        }.start();
    }

    public String getValue(Map<String, String> maps, List<String> mustList, String key) {
        String valall;
        String type;
        String positionstr;
        String value;
        String result;
        valall = maps.get(key);
        //if (valall.length() > 0) {
        String valuearr[] = valall.split("/");
        type = valuearr[0].trim();
        positionstr = valuearr[1].trim();
        value = valuearr[2].trim();
        int position = Integer.parseInt(positionstr);
        if (mustList.contains(positionstr)) {
            mustList.remove(positionstr);
        }
        int typeint = Integer.parseInt(type);
        Map<String, String> mapAdapter = adapter.getItem(position);
        String fieldId = mapAdapter.get("fieldId");
        String ifMust = mapAdapter.get("ifMust");
        String fieldRole = mapAdapter.get("fieldRole");
        switch (fieldRole) {
            case "6":
                if (!Constant.isMobileNO(value)) {
                    result = "1";
                } else {
                    result = obtainVal(value, typeint, mapAdapter, fieldId, ifMust);
                }
                break;
            case "3":
                if (!Constant.isIdentityID(value)) {
                    result = "2";
                } else {
                    result = obtainVal(value, typeint, mapAdapter, fieldId, ifMust);
                }
                break;
            case "4":
                if (!Constant.isEmail(value)) {
                    result = "3";
                } else {
                    result = obtainVal(value, typeint, mapAdapter, fieldId, ifMust);
                }
                break;
            case "9":
                if (!Constant.isName(value.trim())) {
                    result = "4";
                } else {
                    result = obtainVal(value, typeint, mapAdapter, fieldId, ifMust);
                }
                break;
            default:
                result = obtainVal(value, typeint, mapAdapter, fieldId, ifMust);
                break;
        }
        return result;
    }


    public String obtainVal(String value, int typeint, Map<String, String> mapAdapter, String fieldId, String ifMust) {
        String retStr;
        if (typeint == 5) {
            String dialogField = mapAdapter.get("dialogField");
            String relationTableId = mapAdapter.get("relationTableId");
            String showFieldArr = mapAdapter.get("showFieldArr");

            if ("0".equals(ifMust)) {
                if (TextUtils.isEmpty(value)) {
                    retStr = "0";
                } else {
                    String valarr[] = value.split(",");
                    int valsize = valarr.length;

                    String retStr1 = "t0_au_" + relationTableId + "_" + showFieldArr + "_" + fieldId + "_dz" + "=" + valsize;
                    retStr = retStr1;
                    for (int i = 0; i < valsize; i++) {
                        String retStr2 = "&" + "t1_au_" + relationTableId + "_" + showFieldArr + "_" + dialogField + "=" + valarr[i];
                        retStr = retStr + retStr2;
                    }
                }
            } else {
                if (TextUtils.isEmpty(value)) {
                    retStr = "";
                } else {
                    String valarr[] = value.split(",");
                    int valsize = valarr.length;

                    String retStr1 = "t0_au_" + relationTableId + "_" + showFieldArr + "_" + fieldId + "_dz" + "=" + valsize;
                    retStr = retStr1;
                    for (int i = 0; i < valsize; i++) {
                        String retStr2 = "&" + "t1_au_" + relationTableId + "_" + showFieldArr + "_" + dialogField + "=" + valarr[i];
                        retStr = retStr + retStr2;
                    }

                }
            }
        } else {
            retStr = getStrValue(fieldId, ifMust, value);
        }
        return retStr;
    }

    public String getStrValue(String fieldId, String ifMust, String value) {
        String retStr;
        if ("0".equals(ifMust)) {
            if (TextUtils.isEmpty(value)) {
                retStr = "0";
            } else {
                retStr = "t0_au_" + tableId + "_" + startTurnPage + "_" + fieldId + "=" + value;
            }
        } else {
//                    if (TextUtils.isEmpty(value)) {
//                        retStr = "";
//                    } else {
            retStr = "t0_au_" + tableId + "_" + startTurnPage + "_" + fieldId + "=" + value;
//                    }
        }
        return retStr;
    }

    private String getUrlConnection(String[] mParamsName, String[] mParamValue, List<String> listUrl) {
        StringBuffer sb = null;
        // String response = "";
        try {
            // String urlPath = urlString + userName + "&stuPassword=" + pwd;
            // 建立连接
            URL url = new URL(buttonCommitUrl);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
            String data1 = mParamsName[0] + "=" + URLEncoder.encode(mParamValue[0], "UTF-8") + "&" + mParamsName[1] + "=" + URLEncoder.encode(mParamValue[1], "UTF-8") + "&" + mParamsName[2] + "=" + URLEncoder.encode(mParamValue[2], "UTF-8") + "&" + mParamsName[3] + "=" + URLEncoder.encode(mParamValue[3], "UTF-8");
            String data = data1;
            for (int i = 0; i < listUrl.size(); i++) {
                Log.e("listUrl.get(i)=====", listUrl.get(i));
                // String parse = "&" + URLEncoder.encode(listUrl.get(i), "UTF-8");
                String parse = "&" + listUrl.get(i);
                data = data + parse;
            }
            Log.e("data===========", data);
            // 设置请求的头
            urlConnection.setRequestProperty("Connection", "keep-alive");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求的头
            urlConnection.setRequestProperty("Content-Length", String.valueOf(data.getBytes().length));
            // 设置请求的头
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");

            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
            // setDoInput的默认值就是true
            // 获取输出流
            OutputStream os = urlConnection.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                sb = new StringBuffer(result);

            } else {
                Constant.toastMeth(AddActivity.this, "提交失败.........");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Constant.toastMeth(AddActivity.this, "网络出现错误");
        }
        return sb.toString();
    }

}
