package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.bean.AddStuSchBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class RowsBaseAdapter extends BaseAdapter {
    private Context context;
    public LayoutInflater inflater;
    public List<AddStuSchBean> lists = new ArrayList<AddStuSchBean>();
    //public List<Boolean> mChecked;
    // public HashMap<Integer, View> map = new HashMap<Integer, View>();

    public RowsBaseAdapter(Context context, List<AddStuSchBean> lists) {
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
    public AddStuSchBean getItem(int i) {
        return lists.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_add_item_cbdialog_school_item, null);
            viewHolder.cb = (CheckBox) view.findViewById(R.id.cb);

            view.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) view.getTag();
        }
        final AddStuSchBean addStuSchBean = lists.get(i);
        viewHolder.cb.setText(addStuSchBean.getName());
        viewHolder.cb.setChecked(addStuSchBean.getIsCheck());


        viewHolder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cb.isChecked()) {

                    addStuSchBean.setIsCheck(true);
                } else {
                    addStuSchBean.setIsCheck(false);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        public CheckBox cb;
        //public TextView textView;
    }
}
