package com.kwsoft.version.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/19 0019.
 *
 */

public class StuInfoAdapter extends BaseAdapter {

    public List<Map<String, String>> stuInfo = new ArrayList<>();
    public Context mcontext;
    public LayoutInflater mInflater;

    public StuInfoAdapter(List<Map<String, String>> stuInfo, Context mcontext) {
        this.stuInfo = stuInfo;
        this.mcontext = mcontext;
        this.mInflater = LayoutInflater.from(mcontext);
    }

    @Override
    public int getCount() {
        return stuInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return stuInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = mInflater.inflate(R.layout.activity_info_item, null);

            viewHolder = new ViewHolder();
            viewHolder.tvLeftName = (TextView)convertView.findViewById(
                    R.id.tv_name);
            viewHolder.tvRightName = (TextView)convertView.findViewById(
                    R.id.tv_entity_name);
//            viewHolder.tvRightName.setMovementMethod(ScrollingMovementMethod.getInstance());
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Map<String, String> map=stuInfo.get(position);
        viewHolder.tvLeftName.setText(map.get("fieldCnName"));
        viewHolder.tvRightName.setText(map.get("fieldCnName2"));
        return convertView;
    }

    public class ViewHolder{
        public TextView tvLeftName,tvRightName;
    }
}
