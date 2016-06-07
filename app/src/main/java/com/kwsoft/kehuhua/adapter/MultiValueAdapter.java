package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/25 0025.
 *
 */
public class MultiValueAdapter extends BaseAdapter {

    private Context mContext;
    private List<List<Map<String, Object>>> mListData;
    private LayoutInflater mInflater;
    private static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况
    private String mIsMulti;

    public MultiValueAdapter(Context context, List<List<Map<String, Object>>> listData, String isMulti) {
        this.mContext = context;
        this.mListData = listData;
        this.mIsMulti = isMulti;
        mInflater = LayoutInflater.from(mContext);
        isSelected = new HashMap<>();
        // 初始化数据
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < mListData.size(); i++) {
            getIsSelected().put(i, Boolean.valueOf(String.valueOf(mListData.get(i).get(0).get("isCheck"))));
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MultiValueAdapter.isSelected = isSelected;
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
        Map<String, Object> mapName = mListData.get(position).get(0);
        convertView = mInflater.inflate(R.layout.activity_multi_value_item, null);
        TextView multi_item_text = (TextView) convertView.findViewById(R.id.multi_item_text);
        CheckBox multi_item_cb = (CheckBox) convertView.findViewById(R.id.multi_item_cb);
        multi_item_cb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mIsMulti.equals("true")) {
                if (isSelected.get(position)) {
                    isSelected.put(position, false);
                    setIsSelected(isSelected);
                    mListData.get(position).get(0).put("isCheck", isSelected.get(position));
                } else{
                    isSelected.put(position, true);
                    setIsSelected(isSelected);
                    mListData.get(position).get(0).put("isCheck", isSelected.get(position));
                }
                }else if (mIsMulti.equals("false")){
                    boolean cu = !isSelected.get(position);
                    // 先将所有的置为FALSE
                    for(Integer p : isSelected.keySet()) {
                        isSelected.put(p, false);
                    }
                    // 再将当前选择CB的实际状态
                    isSelected.put(position, cu);
                    MultiValueAdapter.this.notifyDataSetChanged();

                    for (int i=0;i<mListData.size();i++) {
                        mListData.get(i).get(0).put("isCheck", false);
                    }
                    //beSelectedData.clear();
                    if(cu) mListData.get(position).get(0).put("isCheck", isSelected.get(position));
                    Log.e("TAG","适配器单选"+String.valueOf(mListData.get(position).get(0).get("isCheck")));
                }
            }
        });
        //读取名称
        multi_item_text.setText(String.valueOf(mapName.get("fieldCnName2")));
        multi_item_cb.setChecked(isSelected.get(position));
        return convertView;
    }
}
