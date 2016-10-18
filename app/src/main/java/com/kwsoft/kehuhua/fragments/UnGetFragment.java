package com.kwsoft.kehuhua.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.adapter.MyItemRecyclerViewAdapter;
import com.kwsoft.kehuhua.login.LoginActivity;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TestActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.config.Url;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.view.LoadMoreRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class UnGetFragment extends Fragment implements View.OnClickListener{

    public static boolean isForeground = false;

    private EditText msgText;
    private String lancherActivityClassName=LoginActivity.class.getName();

    private List<Map<String,Object>> list;
    private LoadMoreRecyclerView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MyItemRecyclerViewAdapter myItemRecyclerViewAdapter;
    private int limit=20,start=0;
    private boolean isLoadMore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_unget, null);

        listView=(LoadMoreRecyclerView)view.findViewById(R.id.lv);
        msgText = (EditText)view.findViewById(R.id.msg_rec);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
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

        requestMsgData(Url.baseUrl+ Url.getMsgUrl);

        return view;
    }

    private void requestMsgData(String url) {
        if (!((MessagAlertActivity)getActivity()).hasInternetConnected()) {
            Toast.makeText(getActivity(), "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
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
                            map = JSON.parseObject(jsonData,Map.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (map!=null) {
                            list=(List<Map<String,Object>>) map.get("notMsgList");
//                            String countStr=map.get("notMsgCount")+"";
//                            if (!countStr.equals("null")) {
//                                int count=Integer.parseInt(countStr);
//                                BadgeUtil.sendBadgeNumber(getActivity(),count);
//                                ((MessagAlertActivity)getActivity()).getLoginUserSharedPre().edit().putInt("count",count).commit();
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
                                myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(getActivity(), list,UnGetFragment.this);
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
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                isLoadMore=false;
                listView.notifyMoreFinish(true);
                //refreshListView.hideFooterView();
//                getProgressDialog().dismiss();
                swipeRefreshLayout.setRefreshing(false);
                Log.i("123", "请求失败！");
                try {
                    Toast.makeText(getActivity(), "系统正在维护中,请稍后再试...", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                VolleySingleton.onErrorResponseMessege(getActivity(), volleyError);

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
        VolleySingleton.getVolleySingleton(getActivity()).addToRequestQueue(
                stringRequest);
    }

    //显示或隐藏列表项选择按钮
    public void setShowAndHide(boolean isShow) {
        if (myItemRecyclerViewAdapter != null) {
            myItemRecyclerViewAdapter.setShowCheck(isShow);
        }
        if (list != null && list.size() < 20) {
            listView.notifyMoreFinish(false);
        } else {
            listView.notifyMoreFinish(true);
        }
    }

    //刷新数据
    public void refreshData() {
        requestMsgData(Url.baseUrl+ Url.getMsgUrl);
    }

    //获取删除列表项的id
    public String getIds() {
        Constant.deleteNum=0;
        String ids="";
        if (myItemRecyclerViewAdapter!=null) {
            List<Map<String,Object>> mValues=myItemRecyclerViewAdapter.getList();
            for (Map<String, Object> map : mValues) {
                boolean isCheck=(boolean)map.get("isCheck");
                if (isCheck) {
                    String id=map.get("mainId")+",";
                    ids+=id;
                    Constant.deleteNum++;
                }
            }
            if (ids.length()>0) {
                ids=ids.substring(0,ids.lastIndexOf(","));
            }
        }
        return ids;
    }

    @Override
    public void onClick(View v) {
        Log.i("123","click====>");
        int position=(Integer) v.getTag();
        List<Map<String,Object>> mValues=myItemRecyclerViewAdapter.getList();
        String readState=mValues.get(position).get("if_seeCn")+"";
        boolean isCheck=(boolean)mValues.get(position).get("isCheck");
        CheckBox cb=(CheckBox) v.findViewById(R.id.cb);
        TextView iv_img=(TextView) v.findViewById(R.id.iv_img);

        if (cb.getVisibility() == View.GONE) {

            Intent intent = new Intent(getActivity(), TestActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, mValues.get(position).get("title") + "");
            bundle.putString(JPushInterface.EXTRA_ALERT, mValues.get(position).get("content") + "");

            bundle.putString("dateStr", mValues.get(position).get("dateStr") + "");
            bundle.putString("notice_name", mValues.get(position).get("notice_name") + "");
            bundle.putString("", mValues.get(position).get("mainId") + "");
            bundle.putString("mainId", mValues.get(position).get("mainId") + "");
            bundle.putString("if_seeCn", mValues.get(position).get("if_seeCn") + "");
            intent.putExtras(bundle);

            if (readState.equals("否")) {
                iv_img.setBackgroundResource(R.drawable.read);
                iv_img.setText("已读");
                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_WORLD_READABLE);
                int count = sp.getInt("count", 0);
                if (count > 0) {
                    count--;
                    sp.edit().putInt("count", count).apply();
                    BadgeUtil.sendBadgeNumber(getActivity(), count);
                }
            }
            getActivity().startActivity(intent);
        } else {
            cb.setChecked(!isCheck);
            mValues.get(position).put("isCheck",!isCheck);
        }
    }
}
