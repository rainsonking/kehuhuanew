package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TabActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<List<Map<String, String>>> mValues;
    private Context context;
    private List<Map<String, Object>> childTab;
    public boolean flag = false;
    private static HashMap<Integer, Boolean> isSelected; // 用来控制CheckBox的选中状况






    public ListAdapter2(Context context, List<List<Map<String, String>>> items,List<Map<String, Object>> childTabs) {
        mValues = items;
        Log.e("TAG","mValues===>"+mValues);
        this.context=context;
        this.childTab=childTabs;
        isSelected = new HashMap<>();
        // 初始化数据
        initDate();
    }
    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < mValues.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ListAdapter2.isSelected = isSelected;
    }

    public void setData(List<List<Map<String, String>>> datas) {
        mValues.clear();
        mValues.addAll(datas);
    }

    public void addDatas(List<List<Map<String, String>>> datas) {
        mValues.addAll(datas);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final ViewHolder mHolder = (ViewHolder) holder;
        mHolder.mItem = mValues.get(position);
        if(flag){
            mHolder.checkboxOperateData.setVisibility(View.VISIBLE);
            mHolder.checkboxOperateData.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position, false);
                        setIsSelected(isSelected);
                        mValues.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
                    } else {
                        isSelected.put(position, true);
                        setIsSelected(isSelected);
                        mValues.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
                    }
                }
            });
            mHolder.checkboxOperateData.setChecked(isSelected.get(position));
        }
        final String title=mHolder.mItem.get(0).get("fieldCnName2");
        mHolder.studentName.setText(!title.equals("null")?title:"");

        String left1Title=mHolder.mItem.get(1).get("fieldCnName");
        mHolder.left1.setText(!left1Title.equals("null")?left1Title:"");

        String right1Title=mHolder.mItem.get(1).get("fieldCnName2");
        mHolder.right1.setText(!right1Title.equals("null")?right1Title:"");

        String left2Title=mHolder.mItem.get(2).get("fieldCnName");
        mHolder.left2.setText(!left2Title.equals("null")?left2Title:"");

        String right2Title=mHolder.mItem.get(2).get("fieldCnName2");
        mHolder.right2.setText(!right2Title.equals("null")?right2Title:"");

        String left3Title=mHolder.mItem.get(3).get("fieldCnName");
        mHolder.left3.setText(!left3Title.equals("null")?left3Title:"");

        String right3Title=mHolder.mItem.get(3).get("fieldCnName2");
        mHolder.right3.setText(!right3Title.equals("null")?right3Title:"");

        String left4Title=mHolder.mItem.get(4).get("fieldCnName");
        mHolder.left4.setText(!left4Title.equals("null")?left4Title:"");

        String right4Title=mHolder.mItem.get(4).get("fieldCnName2");
        mHolder.right4.setText(!right4Title.equals("null")?right4Title:"");

        String right5Title=mHolder.mItem.get(5).get("fieldCnName2");
        mHolder.right5.setText(!right5Title.equals("null")?right5Title:"");

        String right6Title=mHolder.mItem.get(6).get("fieldCnName2");
        mHolder.right6.setText(!right6Title.equals("null")?right6Title:"");





        Log.e("TAG", "titleName  "+title);
        if (childTab.size()>0) {

            mHolder.click_open.setVisibility(View.VISIBLE);
            mHolder.click_open_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(context, TabActivity.class);

                    intent.putExtra("childTab", JSON.toJSONString(childTab));
                    intent.putExtra("titleName",title);
                    context.startActivity(intent);
                }
            });
        }
        View view=mHolder.mView;
        view.setTag(position);

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView studentName,left1,left2,left3,left4,left5,left6,right1,right2,right3,right4,right5,right6,click_open_btn;
        RelativeLayout click_open;
        CheckBox checkboxOperateData;
        List<Map<String, String>> mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            studentName = (TextView) view.findViewById(R.id.stu_name);
            left1 = (TextView) view.findViewById(R.id.left1);
            left2 = (TextView) view.findViewById(R.id.left2);
            left3 = (TextView) view.findViewById(R.id.left3);
            left4 = (TextView) view.findViewById(R.id.left4);
            left5 = (TextView) view.findViewById(R.id.left5);
            left6 = (TextView) view.findViewById(R.id.left6);

            right1 = (TextView) view.findViewById(R.id.right1);
            right2 = (TextView) view.findViewById(R.id.right2);
            right3 = (TextView) view.findViewById(R.id.right3);
            right4 = (TextView) view.findViewById(R.id.right4);
            right5 = (TextView) view.findViewById(R.id.right5);
            right6 = (TextView) view.findViewById(R.id.right6);


            click_open= (RelativeLayout) view.findViewById(R.id.click_open);
            click_open_btn= (TextView) view.findViewById(R.id.click_open_btn);
            checkboxOperateData = (CheckBox) view.findViewById(R.id.checkbox_operate_data);
        }

    }
}


//        if (!readState.equals("null")) {
//            if (readState.equals("否")) {
//                mHolder.iv_img.setText("未读");
//                mHolder.iv_img.setBackgroundResource(R.drawable.no_read);
//            } else {
//                mHolder.iv_img.setText("已读");
//                mHolder.iv_img.setBackgroundResource(R.drawable.read);
//            }
//        }
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,TestActivity.class);
//                Bundle bundle=new Bundle();
//                bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE,mValues.get(position).get("title")+"");
//                bundle.putString(JPushInterface.EXTRA_ALERT,mValues.get(position).get("content")+"");
//
//                bundle.putString("dateStr",mValues.get(position).get("dateStr")+"");
//                bundle.putString("notice_name",mValues.get(position).get("notice_name")+"");
//                bundle.putString("",mValues.get(position).get("mainId")+"");
//                bundle.putString("mainId",mValues.get(position).get("mainId")+"");
//                bundle.putString("if_seeCn",mValues.get(position).get("if_seeCn")+"");
//                intent.putExtras(bundle);
//
//                if (readState.equals("否")) {
//                    mHolder.iv_img.setBackgroundResource(R.drawable.read);
//                    SharedPreferences sp=context.getSharedPreferences("userInfo",Context.MODE_WORLD_READABLE);
//                    int count=sp.getInt("count",0);
//                    if (count > 0) {
//                        count--;
//                        sp.edit().putInt("count",count).commit();
//                        BadgeUtil.sendBadgeNumber(context,count);
//                    }
//                }
//                context.startActivity(intent);
//            }
//        });
