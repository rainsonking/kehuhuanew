package com.kwsoft.kehuhua.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TabActivity;

import java.util.List;
import java.util.Map;


public class ListAdapter2 extends RecyclerView.Adapter<ListAdapter2.ViewHolder> {

    private List<List<Map<String, String>>> mDatas;
    private List<Map<String, Object>> childTab;
    private Context mContext;

    public ListAdapter2(List<List<Map<String, String>>> mDatas,List<Map<String, Object>> childTab){
        this.mDatas = mDatas;
        this.childTab = childTab;
    }





//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//
//        final ViewHolder mHolder =holder;
//        mHolder.mItem = mValues.get(position);
//        if(flag){
//            mHolder.checkboxOperateData.setVisibility(View.VISIBLE);
//            mHolder.checkboxOperateData.setOnClickListener(new View.OnClickListener() {
//
//                public void onClick(View v) {
//                    if (isSelected.get(position)) {
//                        isSelected.put(position, false);
//                        setIsSelected(isSelected);
//                        mValues.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
//                    } else {
//                        isSelected.put(position, true);
//                        setIsSelected(isSelected);
//                        mValues.get(position).get(0).put("isCheck", String.valueOf(isSelected.get(position)));
//                    }
//                }
//            });
//            mHolder.checkboxOperateData.setChecked(isSelected.get(position));
//        }
//        final String title=mHolder.mItem.get(0).get("fieldCnName2");
//        mHolder.studentName.setText(!title.equals("null")?title:"");
//
//        String left1Title=mHolder.mItem.get(1).get("fieldCnName");
//        mHolder.left1.setText(!left1Title.equals("null")?left1Title:"");
//
//        String right1Title=mHolder.mItem.get(1).get("fieldCnName2");
//        mHolder.right1.setText(!right1Title.equals("null")?right1Title:"");
//
//        String left2Title=mHolder.mItem.get(2).get("fieldCnName");
//        mHolder.left2.setText(!left2Title.equals("null")?left2Title:"");
//
//        String right2Title=mHolder.mItem.get(2).get("fieldCnName2");
//        mHolder.right2.setText(!right2Title.equals("null")?right2Title:"");
//
//        String left3Title=mHolder.mItem.get(3).get("fieldCnName");
//        mHolder.left3.setText(!left3Title.equals("null")?left3Title:"");
//
//        String right3Title=mHolder.mItem.get(3).get("fieldCnName2");
//        mHolder.right3.setText(!right3Title.equals("null")?right3Title:"");
//
//        String left4Title=mHolder.mItem.get(4).get("fieldCnName");
//        mHolder.left4.setText(!left4Title.equals("null")?left4Title:"");
//
//        String right4Title=mHolder.mItem.get(4).get("fieldCnName2");
//        mHolder.right4.setText(!right4Title.equals("null")?right4Title:"");
//
//        String right5Title=mHolder.mItem.get(5).get("fieldCnName2");
//        mHolder.right5.setText(!right5Title.equals("null")?right5Title:"");
//
//        String right6Title=mHolder.mItem.get(6).get("fieldCnName2");
//        mHolder.right6.setText(!right6Title.equals("null")?right6Title:"");
//
//
//
//
//
//        Log.e("TAG", "titleName  "+title);
//        if (childTab.size()>0) {
//
//            mHolder.click_open.setVisibility(View.VISIBLE);
//            mHolder.click_open_btn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent();
//                    intent.setClass(context, TabActivity.class);
//
//                    intent.putExtra("childTab", JSON.toJSONString(childTab));
//                    intent.putExtra("titleName",title);
//                    context.startActivity(intent);
//                }
//            });
//        }
//        View view=mHolder.mView;
//        view.setTag(position);
//
//    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View view = mInflater.inflate(R.layout.activity_list_item,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        List<Map<String, String>> item = getData(position);
        try {
            final String title=item.get(0).get("fieldCnName2");
            holder.studentName.setText(!title.equals("null")?title:"");
            holder.studentName.setVisibility(View.VISIBLE);
//左1
            String left1Title=item.get(1).get("fieldCnName");
            holder.left1.setText(!left1Title.equals("null")?left1Title:"");
            holder.left1.setVisibility(View.VISIBLE);
//右1
            String right1Title=item.get(1).get("fieldCnName2");
            holder.right1.setText(!right1Title.equals("null")?right1Title:"");
            holder.right1.setVisibility(View.VISIBLE);
//左2
            String left2Title=item.get(2).get("fieldCnName");
            holder.left2.setText(!left2Title.equals("null")?left2Title:"");
            holder.left2.setVisibility(View.VISIBLE);

            String right2Title=item.get(2).get("fieldCnName2");
            holder.right2.setText(!right2Title.equals("null")?right2Title:"");
            holder.right2.setVisibility(View.VISIBLE);
//左3
            String left3Title=item.get(3).get("fieldCnName");
            holder.left3.setText(!left3Title.equals("null")?left3Title:"");
            holder.left3.setVisibility(View.VISIBLE);

            String right3Title=item.get(3).get("fieldCnName2");
            holder.right3.setText(!right3Title.equals("null")?right3Title:"");
            holder.right3.setVisibility(View.VISIBLE);
//左4
            String left4Title=item.get(4).get("fieldCnName");
            holder.left4.setText(!left4Title.equals("null")?left4Title:"");
            holder.left4.setVisibility(View.VISIBLE);

            String right4Title=item.get(4).get("fieldCnName2");
            holder.right4.setText(!right4Title.equals("null")?right4Title:"");
            holder.right4.setVisibility(View.VISIBLE);
//左5
            String left5Title=item.get(5).get("fieldCnName");
            holder.left5.setText(!left5Title.equals("null")?left5Title:"");
            holder.left5.setVisibility(View.VISIBLE);

            String right5Title=item.get(5).get("fieldCnName2");
            holder.right5.setText(!right5Title.equals("null")?right5Title:"");
            holder.right5.setVisibility(View.VISIBLE);
//左6
            String left6Title=item.get(6).get("fieldCnName");
            holder.left6.setText(!left6Title.equals("null")?left6Title:"");
            holder.left6.setVisibility(View.VISIBLE);

            String right6Title=item.get(6).get("fieldCnName2");
            holder.right6.setText(!right6Title.equals("null")?right6Title:"");
            holder.right6.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TAG", "适配List完毕  ");

//判断跳转子表格
        final String titleName= item.get(0).get("fieldCnName2");
        final String mainId= item.get(0).get("mainId");
        if (childTab.size()>0) {

            holder.click_open.setVisibility(View.VISIBLE);
            holder.click_open_btn.setOnClickListener(new View.OnClickListener() {
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
    }
    /**
     * 获取单项数据
     */

    private List<Map<String, String>> getData(int position){

        return mDatas.get(position);
    }
    /**
     * 获取全部数据
     */
    public List<List<Map<String, String>>> getDatas(){

        return  mDatas;
    }

    /**
     * 清除数据
     */
    public void clearData(){

        mDatas.clear();
        notifyItemRangeRemoved(0,mDatas.size());
    }
    /**
     *
     *下拉刷新更新数据
     */
    public void addData(List<List<Map<String, String>>> datas){

        addData(0,datas);
    }
    /**
     *
     * 上拉加载添加数据的方法
     */
    public void addData(int position,List<List<Map<String, String>>> datas){

        if(datas !=null && datas.size()>0) {

            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }
    @Override
    public int getItemCount() {
        if(mDatas!=null && mDatas.size()>0)
            return mDatas.size();
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView studentName,
                left1,left2,left3,left4,left5,left6,
                right1,right2,right3,right4,right5,right6,
                click_open_btn;
        RelativeLayout click_open;

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
        }

    }
}