package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adapter.RowsBaseAdapter;
import com.kwsoft.kehuhua.bean.AddStuSchBean;
import com.kwsoft.kehuhua.bean.DialogDataList;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.model.OnDataListener;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.view.ExpandTabView;
import com.kwsoft.kehuhua.view.ViewRight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class MultiSelectActivity extends Activity implements View.OnClickListener, AbsListView.OnScrollListener, OnDataListener {
    private ListView listView;
    private List<DialogDataList> lists = new ArrayList<>();
    List<String> listItem = new ArrayList<>();
    List<String> listItemid = new ArrayList<>();
    private List<AddStuSchBean> listMaps = null;
    RowsBaseAdapter adapter;
    int position;
    List<String> contentlist = new ArrayList<String>();
    private ExpandTabView expandTabView;
    String idStr = "";
    private ViewRight viewRight;
    private ArrayList<View> mViewArray = new ArrayList<View>();
    private List<Map<String, String>> items = new ArrayList<Map<String, String>>();//显示字段
    String dialogSearchUrl = "", dialogDataList = "";
    //private final String[] itemsVaule = new String[] { "1", "2", "3", "4", "5", "6" };//隐藏id
    private View loadMoreView;
    private Button loadMoreButton;
    private int visibleLastIndex = 0;   //最后的可视项索引
    private int visibleItemCount;       // 当前窗口可见项总数
    private int datasize = 38;          //模拟数据集的条数
    JSONArray dialogSearchSetArr = null;
    int limit = 10, start = 10;
    JSONArray rows = new JSONArray();
    private int totalLegth = 0;
    private int filterLength = 0;
    private String searchUrl = "";//筛选后获取到的url
    private int searchStart = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_cbdialog_school);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initView();
        init();
    }


    private void init() {
        Intent intent = getIntent();
        dialogDataList = intent.getStringExtra("dialogDataList");
        String dialogFieldSet = intent.getStringExtra("dialogFieldSet");
        String content = intent.getStringExtra("content");
        String dialogSearchSet = intent.getStringExtra("dialogSearchSet");
        dialogSearchUrl = intent.getStringExtra("dialogSearchUrl");
        filterLength = 0;
        if (!TextUtils.isEmpty(content)) {
            String[] contentArr = content.split(",");
            contentlist = java.util.Arrays.asList(contentArr);
        }

        position = intent.getIntExtra("position", -1);
        try {
            JSONArray dialogSearchSetArr = new JSONArray(dialogSearchSet);
            Log.e("dialogSearchSet==", dialogSearchSet);

            for (int j = 0; j < dialogSearchSetArr.length(); j++) {
                JSONObject dialogSearchSetObj = (JSONObject) dialogSearchSetArr.get(j);
                String fieldCnName = dialogSearchSetObj.getString("fieldCnName");
                String fieldRole = dialogSearchSetObj.getString("fieldRole");
                Map<String, String> map = new HashMap<String, String>();
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
            initVaule();
            initListener();

//            JSONObject object = new JSONObject("dialogDataList");
            JSONArray array = new JSONArray(dialogFieldSet);
            JSONObject object1 = (JSONObject) array.get(0);
            String id = object1.getString("tableId");
            idStr = "T_" + id + "_0";

            JSONObject object = new JSONObject(dialogDataList);
            rows = object.getJSONArray("rows");
            totalLegth = rows.length();

            initListDate(idStr, rows);
            initAdapter();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent2 = new Intent(MultiSelectActivity.this, MultiSelectInfoActivity.class);
                    intent2.putExtra("rows", listMaps.get(i));
                    startActivity(intent2);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListDate(String idStr, JSONArray rows) throws JSONException {
        listMaps = new ArrayList<AddStuSchBean>();
        int lengthRow = 0;
        if (rows.length() < 10) {
            lengthRow = rows.length();
            start = rows.length();
            start = 0;
        } else {
            lengthRow = 10;
            start = 0;
        }
        getListMap(idStr, rows, lengthRow);
    }

    private void getListMap(String idStr, JSONArray rows, int lengthRow) throws JSONException {
        for (int i = 0; i < lengthRow; i++) {
            JSONObject object2 = rows.getJSONObject(i);
            AddStuSchBean addStuSchBean = new AddStuSchBean();
            String name = object2.get("AFM_1").toString();
            addStuSchBean.setName(name);
            addStuSchBean.setId(object2.getString(idStr));
            if (object2.has("DIC_AFM_2")) {
                addStuSchBean.setDicafm(object2.getString("DIC_AFM_2"));
            } else {
                addStuSchBean.setDicafm(null);
            }
            if (!contentlist.isEmpty() && contentlist.contains(name)) {
                addStuSchBean.setIsCheck(true);
            } else {
                addStuSchBean.setIsCheck(false);
            }

            listMaps.add(addStuSchBean);
        }
    }

    private void initAdapter() {
        adapter = new RowsBaseAdapter(this, listMaps);
//        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list);
        ImageView tv_cancle = (ImageView) findViewById(R.id.IV_back_list_add);
        ImageView add_commit = (ImageView) findViewById(R.id.add_commit);
        add_commit.setOnClickListener(this);
        tv_cancle.setOnClickListener(this);

        loadMoreView = getLayoutInflater().inflate(R.layout.listview_loadmore, null);
        loadMoreButton = (Button) loadMoreView.findViewById(R.id.loadMoreButton);
        listView.addFooterView(loadMoreView);
        listView.setOnScrollListener(this);

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
                            if (start < rows.length()) {
                                startstr = "start=" + start;
                                String urlPage = startstr + "&" + limitstr;
                                String data = getUrlConnection(dialogSearchUrl, urlPage);
                                int lenghmore = 0;
                                Log.e("urlPage===", data + "/" + urlPage);
                                try {
                                    JSONObject object = new JSONObject(data);
                                    JSONArray datarows = object.getJSONArray("rows");
                                    if (datarows.length() > 10) {
                                        lenghmore = 10;
                                        getListMap(idStr, datarows, lenghmore);
                                        message.what = 2;
                                        handler.sendMessage(message);
                                    } else {
                                        lenghmore = datarows.length();
                                        getListMap(idStr, datarows, lenghmore);
                                        message.what = 3;
                                        handler.sendMessage(message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
                            if (searchStart < rows.length()) {
                                startstr = "start=" + searchStart;
                                String urlPage = startstr + "&" + limitstr + searchUrl;
                                String data = getUrlConnection(dialogSearchUrl, urlPage);
                                int lenghmore = 0;
                                Log.e("urlPage===", data + "/" + urlPage);
                                try {
                                    JSONObject object = new JSONObject(data);
                                    JSONArray datarows = object.getJSONArray("rows");
                                    if (datarows.length() > 10) {
                                        lenghmore = 10;
                                        getListMap(idStr, datarows, lenghmore);
                                        message.what = 2;
                                        handler.sendMessage(message);
                                    } else {
                                        lenghmore = datarows.length();
                                        getListMap(idStr, datarows, lenghmore);
                                        message.what = 3;
                                        handler.sendMessage(message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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

    private void initVaule() {
        expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
        viewRight = new ViewRight(this, items);

        mViewArray.add(viewRight);
        ArrayList<String> mTextArray = new ArrayList<String>();
        mTextArray.add("");
        expandTabView.setValue(mTextArray, mViewArray);
        expandTabView.setTitle("", 0);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_commit:
                listItem.clear();
                listItemid.clear();
                StringBuilder sb = null;
                StringBuilder sbid = null;
                int count = adapter.getCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        AddStuSchBean addStuSchBean = adapter.getItem(i);
                        if (addStuSchBean.getIsCheck()) {
                            String name = addStuSchBean.getName();
                            String id = addStuSchBean.getId();
                            listItemid.add(id);
                            listItem.add(name);
                        }
                    }
                    Log.e("listitem===", listItem.size() + "");
                    if (listItemid.size() == 0) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MultiSelectActivity.this);
                        builder1.setTitle("提示");
                        builder1.setMessage("没有选中任何记录");
                        builder1.show();
                    } else {
                        sb = new StringBuilder();
                        sbid = new StringBuilder();
                        for (int i = 0; i < listItemid.size(); i++) {

                            if (i == (listItemid.size() - 1)) {
                                sb.append(listItem.get(i));
                                sbid.append(listItemid.get(i));
                            } else {
                                sb.append(listItem.get(i) + ",");
                                sbid.append(listItemid.get(i) + ",");
                            }
                        }

                        Intent intent = new Intent();
                        intent.putExtra("sb", sb.toString());// 放入返回值
                        intent.putExtra("sbid", sbid.toString());
                        intent.putExtra("position", position);

                        setResult(0, intent);// 放入回传的值,并添加一个Code,方便区分返回的数据

                        finish();
                    }
                }
                break;
            case R.id.tv_cancle:
                finish();
                break;

//            case R.id.searchButton_add:
//                initVaule();
//                break;

            default:
                break;
        }
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
                        JSONObject object = new JSONObject(dialogDataList);
                        JSONArray rows = object.getJSONArray("rows");
                        loadMoreButton.setText("查看更多...");
                        filterLength = 0;
                        //start = 0;
                        initListDate(idStr, rows);
                        initAdapter();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                initAdapter();
                loadMoreButton.setText("数据已经加载完成！");
                Log.e("ListMap==", listMaps.size() + "");
            } else if (msg.what == 1) {
                initAdapter();
                //loadMoreButton.setText("查看更多...");
            } else if (msg.what == 2) {
                initAdapter();
                //  adapter.notifyDataSetChanged();
                loadMoreButton.setText("查看更多...");
            } else if (msg.what == 3) {
                initAdapter();
                loadMoreButton.setText("数据已经加载完成！");

            } else if (msg.what == 4) {
                //listView.removeFooterView(loadMoreView);
                loadMoreButton.setText("数据已经加载完成！");
                Toast.makeText(MultiSelectActivity.this, "数据已经加载完成！", Toast.LENGTH_LONG).show();
            }

        }
    };

    private void onRefresh(final String url) {

        expandTabView.onPressBack();
        String pathUrl = dialogSearchUrl + "&" + url;
        searchUrl = url;
        Log.e("onRefresh=pathUrl=", pathUrl);
        new Thread() {
            public void run() {
                String data = getUrlConnection(dialogSearchUrl, url);
                try {
                    JSONObject object = new JSONObject(data);
                    Log.e("Date+++", data);
                    JSONArray rows = object.getJSONArray("rows");
                    Message message = new Message();
                    if (rows.length() > 0 && rows.length() <= 10) {
                        initListDate(idStr, rows);
                        filterLength = 1;
                        message.what = 0;
                        handler.sendMessage(message);
                    } else if (rows.length() == 0) {
                        message.what = 1;
                        filterLength = 1;
                        handler.sendMessage(message);
                    } else if ((rows.length() > 10) && (rows.length() < totalLegth)) {
                        listMaps = new ArrayList<AddStuSchBean>();
                        getListMap(idStr, rows, 10);

                        message.what = 2;
                        filterLength = 2;
                        searchStart = 0;
                        handler.sendMessage(message);
                    } else if (rows.length() == totalLegth) {
                        initListDate(idStr, rows);
                        message.what = 2;
                        filterLength = 0;
                      //  start = 0;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }


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
                Constant.toastMeth(MultiSelectActivity.this, "查询失败.........");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Constant.toastMeth(MultiSelectActivity.this, "网络出现错误");
        }
        return sb.toString();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        int itemsLastIndex = adapter.getCount() - 1;  //数据集最后一项的索引
        int lastIndex = itemsLastIndex + 1;
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && visibleLastIndex == lastIndex) {
            // 如果是自动加载,可以在这里放置异步加载数据的代码
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.visibleItemCount = visibleItemCount;
        visibleLastIndex = firstVisibleItem + visibleItemCount - 1;

        Log.e("firstVisibleItem = ", firstVisibleItem + "");
        Log.e("visibleItemCount = ", visibleItemCount + "");
        Log.e("totalItemCount = ", totalItemCount + "");

        //如果所有的记录选项等于数据集的条数，则移除列表底部视图
        if (totalItemCount == datasize + 1) {
            //listView.removeFooterView(loadMoreView);
            loadMoreButton.setText("数据已经加载完成！");
            Toast.makeText(this, "数据全部加载完!", Toast.LENGTH_LONG).show();
            Log.e("onscroll==", "listviewFooter=====");
        }
    }

    @Override
    public void onGetDataSuccess(String jsonData) {

    }

    @Override
    public void onGetDataError() {

    }

    @Override
    public void onLoading(long total, long current) {

    }
}
