package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/2 0002.
 */
public class BusiMenuAdapter extends BaseAdapter {
    private Context context;
    public LayoutInflater inflater;
    private List<Map<String, Object>> mList;


    public BusiMenuAdapter(Context context,List<Map<String, Object>> mList) {
        super();
        this.context = context;
        this.mList = mList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("TAG", "子菜单传递给列表页面的位置(适配器)" + i + "");
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_home_child_item, null);
            viewHolder.tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.tv_item_name.setText((String) mList.get(i).get("phoneMenuName"));

        return view;
    }

    class ViewHolder {
        public TextView tv_item_name;
    }
}
