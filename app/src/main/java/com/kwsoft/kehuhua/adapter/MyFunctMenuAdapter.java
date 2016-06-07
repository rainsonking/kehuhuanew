package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.bean.FunctionMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/11/26 0026.
 */
public class MyFunctMenuAdapter extends BaseAdapter {
    private Context context;
    public LayoutInflater inflater;
    private List<FunctionMenuBean> lists = new ArrayList<>();

    public MyFunctMenuAdapter(Context context, List<FunctionMenuBean> lists) {
        super();
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_home_item, null);
            viewHolder.iv_item = (ImageView) view.findViewById(R.id.iv_item);
            viewHolder.tv_item = (TextView) view.findViewById(R.id.tv_item);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        FunctionMenuBean functionMenuBean = lists.get(i);
        viewHolder.iv_item.setBackgroundResource(functionMenuBean.getImg());
        viewHolder.tv_item.setText(functionMenuBean.getTvName());

        return view;
    }

    class ViewHolder {
        public ImageView iv_item;
        public TextView tv_item;
    }

}
