package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/12/1 0001.
 *
 */
public class ListAdapter extends ArrayAdapter<List<Map<String, String>>> {
    private int resourceId;
    public boolean flag = false;
    List<List<Map<String, String>>> listData;
    private static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况

    public ListAdapter(Context context, int textViewResourceId,
                       List<List<Map<String, String>>> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        listData = objects;
        isSelected = new HashMap<>();
        // 初始化数据
        initDate();

    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < listData.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListAdapter.isSelected = isSelected;
    }

    List<Map<String, String>> student;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("TAG", "list适配器监测点1");
        student = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView studentName = (TextView) convertView.findViewById(R.id.stu_name);
        TextView left1 = (TextView) convertView.findViewById(R.id.left1);
        TextView left2 = (TextView) convertView.findViewById(R.id.left2);
        TextView left3 = (TextView) convertView.findViewById(R.id.left3);
        TextView left4 = (TextView) convertView.findViewById(R.id.left4);
        TextView right1 = (TextView) convertView.findViewById(R.id.right1);
        TextView right2 = (TextView) convertView.findViewById(R.id.right2);
        TextView right3 = (TextView) convertView.findViewById(R.id.right3);
        TextView right4 = (TextView) convertView.findViewById(R.id.right4);
        CheckBox checkboxOperateData = (CheckBox) convertView.findViewById(R.id.checkbox_operate_data);
        Log.e("TAG", "list适配器监测点2");
        if(flag){
            checkboxOperateData.setVisibility(View.VISIBLE);
            Log.e("TAG", "list适配器监测点2.0");
            checkboxOperateData.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position, false);
                        setIsSelected(isSelected);
                        listData.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
                    } else {
                        isSelected.put(position, true);
                        setIsSelected(isSelected);
                        listData.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
                    }
                }
            });
            Log.e("TAG", "list适配器监测点2.1");
            checkboxOperateData.setChecked(isSelected.get(position));
        }

        Log.e("TAG", "list适配器监测点3");
        if (student.size() >= 5) {
            studentName.setText(student.get(0).get("fieldCnName2"));
            left1.setText(student.get(1).get("fieldCnName"));
            right1.setText(student.get(1).get("fieldCnName2"));
            left2.setText(student.get(2).get("fieldCnName"));
            right2.setText(student.get(2).get("fieldCnName2"));
            left3.setText(student.get(3).get("fieldCnName"));
            right3.setText(student.get(3).get("fieldCnName2"));
            left4.setText(student.get(4).get("fieldCnName"));
            right4.setText(student.get(4).get("fieldCnName2"));
        } else if (student.size() == 4) {
            studentName.setText(student.get(0).get("fieldCnName2"));
            left1.setText(student.get(1).get("fieldCnName"));
            right1.setText(student.get(1).get("fieldCnName2"));
            left2.setText(student.get(2).get("fieldCnName"));
            right2.setText(student.get(2).get("fieldCnName2"));
            left3.setText(student.get(3).get("fieldCnName"));
            right3.setText(student.get(3).get("fieldCnName2"));

        } else if (student.size() == 3) {
            studentName.setText(student.get(0).get("fieldCnName2"));
            left1.setText(student.get(1).get("fieldCnName"));
            right1.setText(student.get(1).get("fieldCnName2"));
            left2.setText(student.get(2).get("fieldCnName"));
            right2.setText(student.get(2).get("fieldCnName2"));
        } else if (student.size() == 2) {
            studentName.setText(student.get(0).get("fieldCnName2"));
            left1.setText(student.get(1).get("fieldCnName"));
            right1.setText(student.get(1).get("fieldCnName2"));
        } else if (student.size() == 1) {
            studentName.setText(student.get(0).get("fieldCnName2"));
            left1.setText(student.get(1).get("fieldCnName"));
            right1.setText(student.get(1).get("fieldCnName2"));
        }
        Log.e("TAG", "list适配器监测点4");
        return convertView;
    }




}
