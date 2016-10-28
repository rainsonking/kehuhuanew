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
import com.kwsoft.kehuhua.adapter.MyItemRecyclerViewAdapter;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TestActivity;
import com.kwsoft.kehuhua.config.Url;
import com.kwsoft.kehuhua.login.LoginActivity;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.view.LoadMoreRecyclerView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class GetFragment extends Fragment implements View.OnClickListener {

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

    private void requestMsgData(String volleyUrl) {
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
//参数
        Map<String,String> paramsMap=new HashMap<>();
        paramsMap.put("limit",limit+"");
        paramsMap.put("start",start+"");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(getActivity()) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        isLoadMore=false;
                        listView.notifyMoreFinish(true);
                        swipeRefreshLayout.setRefreshing(false);
                        ErrorToast.errorToast(mContext,e);
                        Log.i("123", "请求失败！");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }

    public void setStore(String jsonData){

        Log.i("123","jsonData===>"+jsonData);
        swipeRefreshLayout.setRefreshing(false);
        Map<String,Object> map= null;
        try {
            map = JSON.parseObject(jsonData,Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map!=null) {
            list=(List<Map<String,Object>>) map.get("readMsgList");
        }
        if (list != null && list.size() > 0) {
            if (myItemRecyclerViewAdapter != null) {
                if (isLoadMore) {
                    myItemRecyclerViewAdapter.addDatas(list);
                } else {
                    myItemRecyclerViewAdapter.setData(list);
                }

            } else {
                myItemRecyclerViewAdapter = new MyItemRecyclerViewAdapter(getActivity(), list,GetFragment.this);
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
        String ids="";
        if (myItemRecyclerViewAdapter!=null) {
            List<Map<String,Object>> mValues=myItemRecyclerViewAdapter.getList();
            for (Map<String, Object> map : mValues) {
                boolean isCheck=(boolean)map.get("isCheck");
                if (isCheck) {
                    String id=map.get("mainId")+",";
                    ids+=id;
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
                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", getActivity().MODE_WORLD_READABLE);
                int count = sp.getInt("count", 0);
                if (count > 0) {
                    count--;
                    sp.edit().putInt("count", count).commit();
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
