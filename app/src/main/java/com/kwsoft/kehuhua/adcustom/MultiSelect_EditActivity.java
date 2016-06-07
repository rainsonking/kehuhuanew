package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.AddSchoolAndMechanismAdapter;
import com.kwsoft.kehuhua.bean.ListMapBean;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.view.ExpandTabView;
import com.kwsoft.kehuhua.view.ViewRight;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class MultiSelect_EditActivity extends Activity implements AbsListView.OnScrollListener {
    private ListView mListView;
    private ImageView mImageViewBack;
    private ImageView mTextChange;
    private List<List<Map<String, Object>>> mListData;
    private List<List<Map<String, Object>>> mTotalData;
    private ViewRight viewRight;
    private ArrayList<View> mViewArray = new ArrayList<View>();
    private ExpandTabView expandTabView;
    private List<Map<String, String>> items = new ArrayList<Map<String, String>>();//显示字段
    private String dialogSearchSet, dialogSearchUrl;
    private String dialogFieldSet;
    //private List<List<Map<String, Object>>> mListSearch = new ArrayList<>();
    AddSchoolAndMechanismAdapter adapter;
    private View loadMoreView;
    private Button loadMoreButton;
    int limit = 10, start = 0;
    private int totalLegth = 0;
    private int filterLength = 0;
    private String searchUrl = "";//筛选后获取到的url
    private int searchStart = 0;
    private ListMapBean listMapBean;
    private int filterDataLeg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_school_mechanism);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        intView();
        getData();
    }

    private void intView() {
        mListView = (ListView) findViewById(R.id.lv_add);
        mImageViewBack = (ImageView) findViewById(R.id.IV_back_list_multi);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTextChange = (ImageView) findViewById(R.id.multi_commit);
        mTextChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> mList = new ArrayList<>();
                List<String> mListKey = new ArrayList<>();
                for (int i = 0; i < mListData.size(); i++) {
                    if ((boolean) mListData.get(i).get(0).get("isCheck")) {
                        mList.add(mListData.get(i).get(0).get("rightData") + "");
                        mListKey.add(mListData.get(i).get(0).get("idValue") + "");
                    }
                }
                Log.i("mList+++++++++++++", mList + "");
                Intent intent = getIntent();
                Bundle mBundle = intent.getExtras();
                int position = mBundle.getInt("position");
                intent.putExtra("Values", (Serializable) mList);
                intent.putExtra("ValuesKey", (Serializable) mListKey);
                intent.putExtra("position", position);
                setResult(position, intent);
                finish();
            }
        });


        loadMoreView = getLayoutInflater().inflate(R.layout.listview_loadmore, null);
        loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);
        mListView.addFooterView(loadMoreView);
        mListView.setOnScrollListener(this);

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreButton.setText("正在加载中...");   //设置按钮文字
                new Thread() {
                    @Override
                    public void run() {
                        String startstr = "", limitstr = "limit=" + limit;
                        Message message = new Message();
                        if (filterLength == 0) {
                            start = start + 10;
                            if (start < totalLegth) {
                                Log.e("mtotalData==", totalLegth + "");
                                startstr = "start=" + start;
                                String urlPage = startstr + "&" + limitstr;
                                String data = getUrlConnection(dialogSearchUrl, urlPage);
                                Log.e("urlPage===", data + "/" + urlPage);
                                List<List<Map<String, Object>>> searchListData = analisisData(data);
                                for (int i = 0; i < searchListData.size(); i++) {
                                    mListData.add(searchListData.get(i));
                                }
                                if ((start + 10) < totalLegth) {
                                    message.what = 2;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = 3;
                                    handler.sendMessage(message);
                                }

                            } else {
                                message.what = 4;
                                handler.sendMessage(message);
                            }
                        } else if (filterLength == 1) {
                            message.what = 4;
                            handler.sendMessage(message);
                        } else if (filterLength == 2) {
                            searchStart = searchStart + 10;
                            if (searchStart < filterDataLeg) {
                                startstr = "start=" + searchStart;
                                String urlPage = startstr + "&" + limitstr + searchUrl;
                                String data = getUrlConnection(dialogSearchUrl, urlPage);
                                Log.e("urlPage===", data + "/" + urlPage);
                                List<List<Map<String, Object>>> searchListData = analisisData(data);
                                for (int i = 0; i < searchListData.size(); i++) {
                                    mListData.add(searchListData.get(i));
                                }
                                if ((searchStart + 10) < filterDataLeg) {
                                    message.what = 2;
                                    handler.sendMessage(message);
                                } else {
                                    message.what = 3;
                                    handler.sendMessage(message);
                                }
                            } else {
                                message.what = 4;
                                handler.sendMessage(message);
                            }
                        }
                    }
                }.start();
            }
        });
    }

    public void getData() {
        Intent mIntent = this.getIntent();
        Bundle bundle = mIntent.getExtras();
        listMapBean = (ListMapBean) bundle.getSerializable("listMap");
        mTotalData = listMapBean.getMapList();
        totalLegth = mTotalData.size();
        getAllData();

        filterLength = 0;
        dialogFieldSet = mIntent.getStringExtra("dialogFieldSet");
        dialogSearchUrl = mIntent.getStringExtra("dialogSearchUrl");
        dialogSearchSet = mIntent.getStringExtra("dialogSearchSet");
        Log.e("dialogSearchUrl==", dialogSearchUrl);
        Log.e("dialogFieldSet==", dialogFieldSet);
        Log.e("dialogSearchSet==", dialogSearchSet);
        Log.e("mListData==", mListData.toString());


        try {
            JSONArray dialogSearchSetArr = new JSONArray(dialogSearchSet);
            Log.e("dialogSearchSet==", dialogSearchSet);

            for (int j = 0; j < dialogSearchSetArr.length(); j++) {
                JSONObject dialogSearchSetObj = (JSONObject) dialogSearchSetArr.get(j);
                String fieldCnName = dialogSearchSetObj.getString("fieldCnName");
                String fieldRole = dialogSearchSetObj.getString("fieldRole");
                Map<String, String> map = new HashMap<>();
                map.put("fieldCnName", fieldCnName);
                map.put("fieldType", dialogSearchSetObj.getString("fieldType"));
                map.put("fieldSearchName", dialogSearchSetObj.getString("fieldSearchName"));
                map.put("fieldRole", fieldRole);
                if ("16".equals(fieldRole)) {
                    String dicOptions = dialogSearchSetObj.getString("dicOptions");
                    map.put("dicOptions", dicOptions);
                }
                items.add(map);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initVaule();
        initListener();
        initAdapter(mListData);
    }

    public void getAllData() {

        Log.e("mTotalData==", mTotalData.size() + "");
        start = 0;
        filterLength = 0;
        if (mTotalData.size() > 10) {
            mListData = listMapBean.getMapList().subList(0, 10);
            loadMoreButton.setText("查看更多...");
        } else {
            mListData = listMapBean.getMapList();
            loadMoreButton.setText("数据加载完成！");
        }
    }

    private void initAdapter(List<List<Map<String, Object>>> mListData) {
        if (mListData != null) {
            adapter = new AddSchoolAndMechanismAdapter(MultiSelect_EditActivity.this, mListData);
            mListView.setAdapter(adapter);
        }
    }

    private void initVaule() {
        expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
        viewRight = new ViewRight(this, items);

        mViewArray.add(viewRight);
        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("");
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setTitle("", 0);

    }

    private void initListener() {

        viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {

            @Override
            public void getValue(String url) {
                Log.e("Url===", url);
                if (url.length() > 0) {
                    onRefresh(url);
                } else {
                    expandTabView.onPressBack();
                    try {
                        getAllData();
                        initAdapter(mListData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void onRefresh(final String url) {

        expandTabView.onPressBack();
        String pathUrl = dialogSearchUrl + "&" + url;
        searchUrl = url;
        Log.e("onRefresh=pathUrl=", pathUrl);
        new Thread() {
            public void run() {
                String data = getUrlConnection(dialogSearchUrl, url);
                try {
                    mListData = analisisData(data);
                    Log.e("Date+++", mListData.toString());
                    Message message = new Message();
                    if (mListData.size() > 0 && mListData.size() <= 10) {
                        filterLength = 1;
                        message.what = 0;
                        handler.sendMessage(message);
                    } else if (mListData.size() == 0) {
                        message.what = 1;
                        filterLength = 1;
                        handler.sendMessage(message);
                    } else if ((mListData.size() > 10) && (mListData.size() < totalLegth)) {
                        mListData = mListData.subList(0, 10);
                        message.what = 2;
                        filterLength = 2;
                        filterDataLeg = data.length();
                        searchStart = 0;
                        handler.sendMessage(message);
                    } else if (mListData.size() == totalLegth) {
                        Log.e("total==length=", totalLegth + "=" + mListData.size());
                        start = 0;
                        filterLength = 0;
                        if (totalLegth > 10) {
                            mListData = mListData.subList(0, 10);
                            Log.e("total==length=", 1 + "=" + 1);
                            message.what = 2;
                        } else {
                            Log.e("total==length=", 2 + "=" + 2);
                            message.what = 0;
                        }
                        handler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                initAdapter(mListData);
                loadMoreButton.setText("数据已经加载完成！");
                Log.e("handler=", 0 + "=" + 0);
            } else if (msg.what == 1) {
                Toast.makeText(MultiSelect_EditActivity.this, "没有您搜索的数据！", Toast.LENGTH_LONG).show();
                // loadMoreButton.setText("查看更多...");
                Log.e("handler=", 1 + "=" + 1);
            } else if (msg.what == 2) {
                loadMoreButton.setText("查看更多...");
                initAdapter(mListData);

                Log.e("handler=", 2 + "=" + 2);
            } else if (msg.what == 3) {
                initAdapter(mListData);
                loadMoreButton.setText("数据已经加载完成！");
                Log.e("handler=", 3 + "=" + 3);
            } else if (msg.what == 4) {
                loadMoreButton.setText("数据已经加载完成！");
                Toast.makeText(MultiSelect_EditActivity.this, "数据已经加载完成！", Toast.LENGTH_LONG).show();
                Log.e("handler=", 4 + "=" + 4);
            }
        }
    };

    private String getUrlConnection(String dialogSearchUrl, String data) {
        StringBuffer sb = null;
        // String response = "";
        try {
            // String urlPath = urlString + userName + "&stuPassword=" + pwd;
            // 建立连接
            URL url2 = new URL(dialogSearchUrl);
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
            // 设置请求的方式
            urlConnection.setRequestMethod("POST");
            // 设置请求的超时时间
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);
            // 传递的数据
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
                Constant.toastMeth(MultiSelect_EditActivity.this, "查询失败.........");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Constant.toastMeth(MultiSelect_EditActivity.this, "网络出现错误");
        }
        return sb.toString();
    }

    private List<List<Map<String, Object>>> analisisData(String SearchData) {

        Map<String, Object> dataMap = JSON.parseObject(SearchData, Map.class);
        List<Map<String, Object>> itemData = (List<Map<String, Object>>) dataMap.get("rows");
        List<List<Map<String, Object>>> data = new ArrayList<>();
        List<Map<String, Object>> dialogFieldSetMap = JSON.parseObject(dialogFieldSet,
                new TypeReference<List<Map<String, Object>>>() {
                });
        Log.e("dialogFieldSetMap", dialogFieldSetMap.toString());
        for (int i = 0; i < itemData.size(); i++) {//数目决定了列表项的条数
            List<Map<String, Object>> dataChildList = new ArrayList<>();//数目决定了属性的条数
            for (int j = 0; j < dialogFieldSetMap.size(); j++) {
                String idValue = "";
                Map<String, Object> mapData = new HashMap<>();//将数据放进map
                String fieldCnName = dialogFieldSetMap.get(j).get("fieldCnName") + "";
                String fieldAliasName;
                if (dialogFieldSetMap.get(j).get("dicParId") != null) {
                    fieldAliasName = itemData.get(i).get("DIC_" + dialogFieldSetMap.get(j).get("fieldAliasName")) + "";
                } else {
                    fieldAliasName = itemData.get(i).get(dialogFieldSetMap.get(j).get("fieldAliasName") + "") + "";

                }
                if (itemData.get(i).get("T_" + dialogFieldSetMap.get(j).get("tableId") + "_0") != null) {
                    idValue = "" + itemData.get(i).get("T_" + dialogFieldSetMap.get(j).get("tableId") + "_0");
                }
                // String value=""+ itemData.get(i).get("T_"+dialogFieldSetMap.get(j).get("tableId")+"_0");


                mapData.put("fieldCnName", fieldCnName);
                mapData.put("rightData", fieldAliasName);
                mapData.put("idValue", idValue);
                mapData.put("isCheck", false);
                dataChildList.add(mapData);
            }
            data.add(dataChildList);
        }
        Log.e("TAG", "rightData" + data.toString());
        return data;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }
}
