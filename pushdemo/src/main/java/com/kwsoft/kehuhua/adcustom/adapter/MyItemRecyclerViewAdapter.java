package com.kwsoft.kehuhua.adcustom.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TestActivity;
import com.kwsoft.kehuhua.adcustom.utils.BadgeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Map<String,Object>> mValues;
    private Context context;

    public MyItemRecyclerViewAdapter(Context context,List<Map<String,Object>> items) {
        mValues = items;
        Log.i("123","mValues===>"+mValues);
        this.context=context;
    }

    public void setData(List<Map<String,Object>> datas) {
        mValues.clear();
        mValues.addAll(datas);
    }

    public void addDatas(List<Map<String,Object>> datas) {
        mValues.addAll(datas);
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
        long time=(long)mValues.get(position).get("create_date");
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = sdf.format(date);
        mHolder.tv_title.setText(!title.equals("null")?title:"");
        mHolder.tv_content.setText(!content.equals("null")?content:"");
        mHolder.tv_read_state_value.setText(!readState.equals("null")?readState:"");
        mHolder.date.setText(!dateStr.equals("null")?dateStr:"");
        if (!readState.equals("null")) {
            if (readState.equals("否")) {
                mHolder.iv_img.setVisibility(View.VISIBLE);
            } else {
                mHolder.iv_img.setVisibility(View.INVISIBLE);
            }
        }
        View view=mHolder.mView;
        view.setTag(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,TestActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,mValues.get(position).get("title")+"");
                bundle.putString(JPushInterface.EXTRA_ALERT,mValues.get(position).get("content")+"");
                bundle.putString("mainId",mValues.get(position).get("mainId")+"");
                bundle.putString("if_seeCn",mValues.get(position).get("if_seeCn")+"");
                intent.putExtras(bundle);

                if (readState.equals("否")) {
                    mHolder.iv_img.setVisibility(View.INVISIBLE);
                    mValues.get(position).put("if_seeCn","是");
                    SharedPreferences sp=context.getSharedPreferences("userInfo", context.MODE_WORLD_READABLE);
                    int count=sp.getInt("count",0);
                    if (count > 0) {
                        count--;
                        sp.edit().putInt("count",count).commit();
                        BadgeUtil.sendBadgeNumber(context,count);
                    }
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tv_title;
        public final TextView tv_content;
        public final TextView tv_read_state_value;
        public final TextView date;
        public final TextView iv_img;
        public Map<String,Object> mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_read_state_value = (TextView) view.findViewById(R.id.tv_read_state_value);
            date = (TextView) view.findViewById(R.id.date);
            iv_img = (TextView) view.findViewById(R.id.iv_img);
        }

    }
}
