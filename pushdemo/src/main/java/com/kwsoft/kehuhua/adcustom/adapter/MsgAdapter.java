package com.kwsoft.kehuhua.adcustom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.kwsoft.kehuhua.adcustom.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/10 0010.
 */
public class MsgAdapter extends BaseAdapter {
    private List<Map<String, Object>> mList;
    private Context mContext;


    public MsgAdapter(Context mContext, List<Map<String, Object>> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(
                R.layout.fragment_un_get_item, null);

        TextView textView=(TextView)convertView.findViewById(R.id.tv_title);
        TextView textView1=(TextView)convertView.findViewById(R.id.tv_content);
        TextView textReadState=(TextView)convertView.findViewById(R.id.tv_read_state_value);
        TextView textDate=(TextView)convertView.findViewById(R.id.date);
        String title=mList.get(position).get("title")+"";
        String content=mList.get(position).get("content")+"";
        String readState=mList.get(position).get("if_seeCn")+"";
        long time=(long)mList.get(position).get("create_date");
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dateStr = sdf.format(date);
        textView.setText(!title.equals("null")?title:"");
        textView1.setText(!content.equals("null")?content:"");
        textReadState.setText(!readState.equals("null")?readState:"");
        textDate.setText(!dateStr.equals("null")?dateStr:"");
        return convertView;
    }
}
