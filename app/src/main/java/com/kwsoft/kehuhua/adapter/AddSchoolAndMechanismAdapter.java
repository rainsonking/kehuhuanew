package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class AddSchoolAndMechanismAdapter extends BaseAdapter {
    private Context mContext;
    private CheckBox mCheckBox;
    private List<List<Map<String, Object>>> mListData;

    public AddSchoolAndMechanismAdapter(Context mContext, List<List<Map<String, Object>>> mListData) {
        this.mContext = mContext;
        this.mListData = mListData;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.add_school_mechanism_item, null);
        mCheckBox = (CheckBox) convertView.findViewById(R.id.cb_name);
        final Map<String, Object> mMap = mListData.get(position).get(0);
        Log.e("Tag",mMap.toString());
        mCheckBox.setText(mMap.get("rightData") + "");
        mCheckBox.setChecked((Boolean) mMap.get("isCheck"));
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mMap.put("isCheck", true);
                }
            }
        });
        return convertView;
    }
}
