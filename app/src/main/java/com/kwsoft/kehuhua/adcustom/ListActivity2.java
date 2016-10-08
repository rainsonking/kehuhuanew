package com.kwsoft.kehuhua.adcustom;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.ListAdapter2;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.config.Url;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.view.LoadMoreRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListActivity2 extends BaseActivity {
    public static boolean isForeground = false;
    private List<List<Map<String, String>>> list;
    private LoadMoreRecyclerView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListAdapter2 listAdapter2;
    private int limit=20,start=0;
    private boolean isLoadMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_avtivity2);
        listView=(LoadMoreRecyclerView)findViewById(R.id.lv);
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

        requestMsgData(Url.baseUrl+ Url.getMsgUrl);
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
//                        getProgressDialog().dismiss();
                        Log.i("123","jsonData===>"+jsonData);
                        swipeRefreshLayout.setRefreshing(false);
                        Map<String,Object> map= null;
                        try {
                            map = JSON.parseObject(jsonData,
                                    new TypeReference<Map<String, Object>>() {
                                    });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (map!=null) {

                            Map<String, Object> pageSet = (Map<String, Object>) map.get("pageSet");




                            list=(List<List<Map<String, String>>>) map.get("pageSet");




                            String countStr=map.get("dataCount")+"";
                            if (!countStr.equals("null")) {
                                int count=Integer.parseInt(countStr);
                                BadgeUtil.sendBadgeNumber(ListActivity2.this,count);
                                getLoginUserSharedPre().edit().putInt("count",count).commit();
                            }
                        }
                        if (list != null && list.size() > 0) {
                            if (listAdapter2 != null) {
                                if (isLoadMore) {
                                    listAdapter2.addDatas(list);
                                } else {
                                    listAdapter2.setData(list);
                                }

                            } else {
//                                listAdapter2 = new ListAdapter2(ListActivity2.this, list);
//                                listView.setAdapter(listAdapter2);
                            }
                            listView.notifyMoreFinish(true);
                        } else {
                            listView.notifyMoreFinish(false);
                        }
                        Log.e("123", "单独获取的获取列表数据" + jsonData);
                        isLoadMore=false;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoadMore=false;
                listView.notifyMoreFinish(true);
                //refreshListView.hideFooterView();
//                getProgressDialog().dismiss();
                swipeRefreshLayout.setRefreshing(false);
                Log.i("123", "请求失败！");
                Toast.makeText(ListActivity2.this, "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
                VolleySingleton.onErrorResponseMessege(ListActivity2.this, volleyError);

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
//                String localCookie=getLoginUserSharedPre().getString("Cookie",null);
                String localCookie= Constant.localCookie;
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

}
