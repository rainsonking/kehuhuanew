package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TabActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;


/**
 * Created by Administrator on 2015/12/1 0001.
 *
 */
public class ListAdapter extends ArrayAdapter<List<Map<String, String>>> {
    private int resourceId;
    public boolean flag = false;
    private Context mContext;
    private List<List<Map<String, String>>> listData;
    private List<Map<String, Object>> childTab=new ArrayList<>();
    private static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况

    public ListAdapter(Context context, int textViewResourceId,
                       List<List<Map<String, String>>> objects,List<Map<String, Object>> childTabs) {
        super(context, textViewResourceId, objects);
        mContext=context;
        resourceId = textViewResourceId;
        listData = objects;
        isSelected =new HashMap<>();

//        Log.e("TAG", "list适配器初次获得tab列表的数据："+childTabs.toString());
        if (childTabs!=null) {
            childTab=childTabs;
        }

        // 初始化数据
        initDate();
//        Log.e("TAG", "list适配器构造方法完成");
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < listData.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    private static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    private static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListAdapter.isSelected = isSelected;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
//        Log.e("TAG", "list适配器getView");
        List<Map<String, String>> student = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
        TextView list_item_vertical_line = (TextView) convertView.findViewById(R.id.list_item_vertical_line);
        list_item_vertical_line.setBackgroundColor(mContext.getResources().getColor(topBarColor));
        TextView studentName = (TextView) convertView.findViewById(R.id.stu_name);
        TextView left1 = (TextView) convertView.findViewById(R.id.left1);
        TextView left2 = (TextView) convertView.findViewById(R.id.left2);
        TextView left3 = (TextView) convertView.findViewById(R.id.left3);
        TextView left4 = (TextView) convertView.findViewById(R.id.left4);
        TextView left5 = (TextView) convertView.findViewById(R.id.left5);
        TextView left6 = (TextView) convertView.findViewById(R.id.left6);

        TextView right1 = (TextView) convertView.findViewById(R.id.right1);
        TextView right2 = (TextView) convertView.findViewById(R.id.right2);
        TextView right3 = (TextView) convertView.findViewById(R.id.right3);
        TextView right4 = (TextView) convertView.findViewById(R.id.right4);
        TextView right5 = (TextView) convertView.findViewById(R.id.right5);
        TextView right6 = (TextView) convertView.findViewById(R.id.right6);
        
        RelativeLayout click_open= (RelativeLayout) convertView.findViewById(R.id.click_open);
        TextView click_open_btn= (TextView) convertView.findViewById(R.id.click_open_btn);
        CheckBox checkboxOperateData = (CheckBox) convertView.findViewById(R.id.checkbox_operate_data);


        if(flag){
            checkboxOperateData.setVisibility(View.VISIBLE);
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
            checkboxOperateData.setChecked(isSelected.get(position));
        }

//        Log.e("TAG", "适配List开始  ");
        assert student != null;

        try {
            final String title=student.get(0).get("fieldCnName2");
            studentName.setText(!title.equals("null")?title:"");
            studentName.setVisibility(View.VISIBLE);
//左1
            String left1Title=student.get(1).get("fieldCnName");
            left1.setText(!left1Title.equals("null")?left1Title:"");
            left1.setVisibility(View.VISIBLE);
//右1
            String right1Title=student.get(1).get("fieldCnName2");
            right1.setText(!right1Title.equals("null")?right1Title:"");
            right1.setVisibility(View.VISIBLE);
//左2
            String left2Title=student.get(2).get("fieldCnName");
            left2.setText(!left2Title.equals("null")?left2Title:"");
            left2.setVisibility(View.VISIBLE);

            String right2Title=student.get(2).get("fieldCnName2");
            right2.setText(!right2Title.equals("null")?right2Title:"");
            right2.setVisibility(View.VISIBLE);
//左3
            String left3Title=student.get(3).get("fieldCnName");
            left3.setText(!left3Title.equals("null")?left3Title:"");
            left3.setVisibility(View.VISIBLE);

            String right3Title=student.get(3).get("fieldCnName2");
            right3.setText(!right3Title.equals("null")?right3Title:"");
            right3.setVisibility(View.VISIBLE);
//左4
            String left4Title=student.get(4).get("fieldCnName");
            left4.setText(!left4Title.equals("null")?left4Title:"");
            left4.setVisibility(View.VISIBLE);

            String right4Title=student.get(4).get("fieldCnName2");
            right4.setText(!right4Title.equals("null")?right4Title:"");
            right4.setVisibility(View.VISIBLE);
//左5
            String left5Title=student.get(5).get("fieldCnName");
            left5.setText(!left5Title.equals("null")?left5Title:"");
            left5.setVisibility(View.VISIBLE);

            String right5Title=student.get(5).get("fieldCnName2");
            right5.setText(!right5Title.equals("null")?right5Title:"");
            right5.setVisibility(View.VISIBLE);
//左6
            String left6Title=student.get(6).get("fieldCnName");
            left6.setText(!left6Title.equals("null")?left6Title:"");
            left6.setVisibility(View.VISIBLE);

            String right6Title=student.get(6).get("fieldCnName2");
            right6.setText(!right6Title.equals("null")?right6Title:"");
            right6.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TAG", "适配List完毕  ");

final String titleName= student.get(0).get("fieldCnName2");
        final String mainId= student.get(0).get("mainId");
//        Log.e("TAG", "list的元素整体打印  "+student.toString());
        Log.e("TAG", "childTab  "+childTab.toString());

        if (childTab.size()>0) {

            click_open.setVisibility(View.VISIBLE);
            click_open_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, TabActivity.class);
                    intent.putExtra("mainId", mainId);
                    intent.putExtra("childTab", JSON.toJSONString(childTab));
                    intent.putExtra("titleName",titleName);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }




}
