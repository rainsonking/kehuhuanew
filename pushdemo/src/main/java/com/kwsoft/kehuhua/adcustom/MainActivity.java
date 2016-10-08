package com.kwsoft.kehuhua.adcustom;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adcustom.adapter.MyItemRecyclerViewAdapter;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.adcustom.config.Constant;
import com.kwsoft.kehuhua.adcustom.config.Url;
import com.kwsoft.kehuhua.adcustom.utils.BadgeUtil;
import com.kwsoft.kehuhua.adcustom.utils.VolleySingleton;
import com.kwsoft.kehuhua.adcustom.view.LoadMoreRecyclerView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity {

    public static boolean isForeground = false;

    private EditText msgText;
    private String lancherActivityClassName=LoginActivity.class.getName();

    private static final String TAG = "JPush";
    private List<Map<String,Object>> list;
    private LoadMoreRecyclerView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    private int limit=20,start=0;
    private boolean isLoadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView=(LoadMoreRecyclerView)findViewById(R.id.lv);
        msgText = (EditText)findViewById(R.id.msg_rec);
        String userId=getLoginUserSharedPre().getString("USERID", null);
        if (!TextUtils.isEmpty(userId)) {
            setAlias(userId);
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                start=0;
                requestMsgData(Url.baseUrl+Url.getMsgUrl);
            }
        });
        listView.setHasFixedSize(true);
        listView.setAutoLoadMoreEnable(true);
        listView.setLoadMoreListener(new LoadMoreRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                isLoadMore=true;
                start+=limit;
                requestMsgData(Url.baseUrl+Url.getMsgUrl);
            }
        });

        requestMsgData(Url.baseUrl+Url.getMsgUrl);

        registerMessageReceiver();

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(MainActivity.this,TestActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,list.get(position).get("title")+"");
//                bundle.putString(JPushInterface.EXTRA_ALERT,list.get(position).get("content")+"");
//                bundle.putString("mainId",list.get(position).get("mainId")+"");
//                bundle.putString("if_seeCn",list.get(position).get("if_seeCn")+"");
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

//        TableIconShowMsg.setBadgeCount(this,218);
//        BadgeUtil.sendBadgeNumber(this,156);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
//        builder.setContentTitle("您有"+90+"未读消息");
//        builder.setTicker("您有"+90+"未读消息");
//        builder.setAutoCancel(true);
//        builder.setSmallIcon(R.drawable.login);
//        builder.setDefaults(Notification.DEFAULT_LIGHTS);
//        Notification notification = builder.build();
//        BadgeUtil.setBadgeCount(notification,this,90);
    }

    @Override
    public void initView() {

    }

    private void requestMsgData(String url) {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isLoadMore) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
//        getProgressDialog().show();
//        RequestCall call = OkHttpUtils.getInstance().post().url(url).build();
//        call.execute(new Callback() {
//            @Override
//            public Object parseNetworkResponse(Response response) throws Exception {
//                return response.body().string();
//            }
//
//            @Override
//            public void onError(Call call, Exception e) {
//                getProgressDialog().dismiss();
//                Toast.makeText(MainActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onResponse(Object response) {
//                getProgressDialog().dismiss();
//                Log.i("123",response.toString());
//            }
//        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
//                        getProgressDialog().dismiss();
                        swipeRefreshLayout.setRefreshing(false);
                        Map<String,Object> map= null;
                        try {
                            map = JSON.parseObject(jsonData,Map.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (map!=null) {
                            list=(List<Map<String,Object>>) map.get("msgList");
                            Log.i("123","list===>"+list);
//                            String countStr=map.get("msgCount")+"";
//                            if (!countStr.equals("null")) {
//                                int count=Integer.parseInt(countStr);
//                                BadgeUtil.sendBadgeNumber(MainActivity.this,count);
//                            }
                        }
                        if (list != null && list.size() > 0) {
                            if (myItemRecyclerViewAdapter != null) {
                                if (isLoadMore) {
                                    myItemRecyclerViewAdapter.addDatas(list);
                                } else {
                                    myItemRecyclerViewAdapter.setData(list);
                                }

                            } else {
                                myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(MainActivity.this, list);
                                listView.setAdapter(myItemRecyclerViewAdapter);
                            }

                            if (list.size() <20) {
                                listView.notifyMoreFinish(false);
                            } else {
                                listView.notifyMoreFinish(true);
                            }
                        } else {
                            listView.notifyMoreFinish(false);
                        }
                        Log.e("123", "单独获取的获取列表数据" + jsonData);
                        isLoadMore=false;
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoadMore=false;
                listView.notifyMoreFinish(true);
                //refreshListView.hideFooterView();
//                getProgressDialog().dismiss();

                swipeRefreshLayout.setRefreshing(false);
                Log.i("123", "请求失败！");
                Toast.makeText(MainActivity.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
                VolleySingleton.onErrorResponseMessege(MainActivity.this, volleyError);

            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<>();
                map.put("limit",limit+"");
                map.put("start",start+"");
                return map;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String localCookie=getLoginUserSharedPre().getString("Cookie",null);
//                String localCookie= Constant.cookie;
                if (localCookie != null && localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Cookie", localCookie);
                    Log.i("123", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                stringRequest);
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Log.i("123","test===>"+showMsg);
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private void setCostomMsg(String msg){
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void setTag(String tag){

        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(MainActivity.this,R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
                Toast.makeText(MainActivity.this,R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            tagSet.add(sTagItme);
        }

        //调用JPush API设置Tag
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TAGS, tagSet));

    }

    private void setAlias(String alias){
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(MainActivity.this,R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(MainActivity.this,R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

//            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

//            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    // 双击退出程序
//    private long firstTime = 0;

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) { // 如果两次按键时间间隔大于2秒，则不退出
                Toast.makeText(this, "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;// 更新firstTime
                return true;
            } else {
                // finish();// 两次按键小于2秒时，退出应用
                System.exit(0);
//                ExampleApplication myApplication = (ExampleApplication) getApplication();
//                myApplication.exitAppAll(MainActivity.this);
                //MyApplication.getInstance().exitAppAll(this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/


    private void sendBadgeNumber() {
        String number = "37";
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }

        String manufacturer=Build.MANUFACTURER;
        Log.i("123","manufacturer====>"+manufacturer);
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(number);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSamsumg(number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSony(number);
        } else {
            Toast.makeText(this, "Not Support", Toast.LENGTH_LONG).show();
        }
    }

    private void sendToXiaoMi(String number) {
        int count=Integer.parseInt(number);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = null;
        boolean isMiUIV6 = true;
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentTitle("您有"+count+"未读消息");
            builder.setTicker("您有"+count+"未读消息");
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.login);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            notification = builder.build();
            //以上代码为notification的初始化信息，在实际应用中，可以单独使用

            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);

            field.set(miuiNotification, count);// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);

            field.set(notification, miuiNotification);
            Toast.makeText(this, "Xiaomi=>isSendOk=>1", Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            e.printStackTrace();
            //miui 6之前的版本
            isMiUIV6 = false;
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name",getPackageName() + "/"+ lancherActivityClassName);
            localIntent.putExtra("android.intent.extra.update_application_message_text",count);
            sendBroadcast(localIntent);
        }
        finally
        {
            if(notification!=null && isMiUIV6 )
            {
                //miui6以上版本需要使用通知发送
                nm.notify(101010, notification);
            }
        }

    }

    private void sendToSony(String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        int count=Integer.parseInt(number);
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",lancherActivityClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",getPackageName());//包名
        sendBroadcast(localIntent);

        Toast.makeText(this, "Sony," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    private void sendToSamsumg(String number)
    {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        int count=Integer.parseInt(number);
        localIntent.putExtra("badge_count", count);//数字
        localIntent.putExtra("badge_count_package_name", getPackageName());//包名
        localIntent.putExtra("badge_count_class_name",lancherActivityClassName); //启动页
        sendBroadcast(localIntent);
        Toast.makeText(this, "Samsumg," + "isSendOk", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // 处理返回逻辑
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
