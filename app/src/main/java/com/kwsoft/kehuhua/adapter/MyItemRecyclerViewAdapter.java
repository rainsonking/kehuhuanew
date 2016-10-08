package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TestActivity;
import com.kwsoft.kehuhua.utils.BadgeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;


public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String,Object>> mValues;
    private Context context;
    private View.OnClickListener onClickListener;
    private boolean isShowCheck;

    public MyItemRecyclerViewAdapter(Context context, List<Map<String,Object>> items, View.OnClickListener onClickListener) {
        mValues = items;
        for (Map<String, Object> map : mValues) {
            map.put("isCheck",false);
        }
        Log.i("123","mValues===>"+mValues);
        this.context=context;
        this.onClickListener=onClickListener;
    }

    public void setData(List<Map<String,Object>> datas) {
        for (Map<String, Object> map : datas) {
            map.put("isCheck",false);
        }
        mValues.clear();
        mValues.addAll(datas);
    }

    public void addDatas(List<Map<String,Object>> datas) {
        for (Map<String, Object> map : datas) {
            map.put("isCheck",false);
        }
        mValues.addAll(datas);
    }

    public List<Map<String,Object>> getList() {
        return mValues;
    }

    public void setShowCheck(boolean isShowCheck) {
        this.isShowCheck=isShowCheck;
//        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_un_get_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder mHolder = (ViewHolder) holder;
        mHolder.mItem = mValues.get(position);
        String title=mValues.get(position).get("title")+"";
        String content=mValues.get(position).get("content")+"";
        final String readState=mValues.get(position).get("if_seeCn")+"";
        String notice_name=mValues.get(position).get("notice_name")+"";
        long time=(long)mValues.get(position).get("create_date");
        final boolean isCheck=(boolean)mValues.get(position).get("isCheck");
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = sdf.format(date);
        mHolder.tv_title.setText(!title.equals("null")?title:"");
        mHolder.tv_content.setText(!content.equals("null")?content:"");
        mHolder.tv_persion.setText(!notice_name.equals("null")?notice_name:"");
        mHolder.date.setText(!dateStr.equals("null")?dateStr:"");
        if (isShowCheck) {
            mHolder.cb.setVisibility(View.VISIBLE);
            mHolder.cb.setChecked(isCheck);
        } else {
            mHolder.cb.setVisibility(View.GONE);
            mValues.get(position).put("isCheck",false);
        }
        if (!readState.equals("null")) {
            if (readState.equals("否")) {
                mHolder.iv_img.setText("未读");
                mHolder.iv_img.setBackgroundResource(R.drawable.no_read);
            } else {
                mHolder.iv_img.setText("已读");
                mHolder.iv_img.setBackgroundResource(R.drawable.read);
            }
        }
        View view=mHolder.mView;
        view.setTag(position);
        view.setOnClickListener(onClickListener);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mHolder.cb.getVisibility() == View.GONE) {
//
//                    Intent intent = new Intent(context, TestActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, mValues.get(position).get("title") + "");
//                    bundle.putString(JPushInterface.EXTRA_ALERT, mValues.get(position).get("content") + "");
//
//                    bundle.putString("dateStr", mValues.get(position).get("dateStr") + "");
//                    bundle.putString("notice_name", mValues.get(position).get("notice_name") + "");
//                    bundle.putString("", mValues.get(position).get("mainId") + "");
//                    bundle.putString("mainId", mValues.get(position).get("mainId") + "");
//                    bundle.putString("if_seeCn", mValues.get(position).get("if_seeCn") + "");
//                    intent.putExtras(bundle);
//
//                    if (readState.equals("否")) {
//                        mHolder.iv_img.setBackgroundResource(R.drawable.read);
//                        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
//                        int count = sp.getInt("count", 0);
//                        if (count > 0) {
//                            count--;
//                            sp.edit().putInt("count", count).commit();
//                            BadgeUtil.sendBadgeNumber(context, count);
//                        }
//                    }
//                    context.startActivity(intent);
//                } else {
//                    mHolder.cb.setChecked(!isCheck);
//                    mValues.get(position).put("isCheck",!isCheck);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_title;
        public final TextView tv_content;
        public final TextView tv_persion;
        public final TextView date;
        public final TextView iv_img;
        public final CheckBox cb;
        public Map<String,Object> mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_persion = (TextView) view.findViewById(R.id.tv_persion);
            date = (TextView) view.findViewById(R.id.date);
            iv_img = (TextView) view.findViewById(R.id.iv_img);
            cb = (CheckBox) view.findViewById(R.id.cb);
        }

    }
}
