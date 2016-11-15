package com.kwsoft.kehuhua.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TabActivity;
import com.kwsoft.version.StuPra;

import java.util.List;
import java.util.Map;


public class ListAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<List<Map<String, String>>> mDatas;
    private List<Map<String, Object>> childTab;
    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private static final int VIEW_TYPE = 1;

    /**
     * 获取条目 View填充的类型
     * 默认返回0
     * 将lists为空返回 1
     */
    public int getItemViewType(int position) {
        if (mDatas.size() <= 0) {
            return VIEW_TYPE;
        }
        return super.getItemViewType(position);
    }


    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String data);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public ListAdapter2(List<List<Map<String, String>>> mDatas, List<Map<String, Object>> childTab) {
        this.mDatas = mDatas;
        this.childTab = childTab;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        Log.e("TAG", "viewType:" + viewType);
        if (VIEW_TYPE == viewType) {
            view = mInflater.inflate(R.layout.empty_view, parent, false);

            return new EmptyViewHolder(view);
        }

        if (!StuPra.studentProId.equals("5704e45c7cf6c0b2d9873da6")) {
            view = mInflater.inflate(R.layout.activity_list_item, null);
        }else{
            view = mInflater.inflate(R.layout.activity_list_item_teach, null);
        }

        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder thisHolder, int position) {
        if (thisHolder instanceof ListViewHolder) {
            final ListViewHolder holder = (ListViewHolder) thisHolder;
            List<Map<String, String>> item = getData(position);
            try {
                final String title = item.get(0).get("fieldCnName2");
                holder.studentName.setText(!title.equals("null") ? title : "");
                holder.studentName.setVisibility(View.VISIBLE);
//左1
                String left1Title = item.get(1).get("fieldCnName");
                holder.left1.setText(!left1Title.equals("null") ? left1Title : "");
                holder.left1.setVisibility(View.VISIBLE);
//右1
                String right1Title = item.get(1).get("fieldCnName2");
                holder.right1.setText(!right1Title.equals("null") ? right1Title : "");
                holder.right1.setVisibility(View.VISIBLE);
//左2
                String left2Title = item.get(2).get("fieldCnName");
                holder.left2.setText(!left2Title.equals("null") ? left2Title : "");
                holder.left2.setVisibility(View.VISIBLE);

                String right2Title = item.get(2).get("fieldCnName2");
                holder.right2.setText(!right2Title.equals("null") ? right2Title : "");
                holder.right2.setVisibility(View.VISIBLE);
//左3
                String left3Title = item.get(3).get("fieldCnName");
                holder.left3.setText(!left3Title.equals("null") ? left3Title : "");
                holder.left3.setVisibility(View.VISIBLE);

                String right3Title = item.get(3).get("fieldCnName2");
                holder.right3.setText(!right3Title.equals("null") ? right3Title : "");
                holder.right3.setVisibility(View.VISIBLE);
//左4
                String left4Title = item.get(4).get("fieldCnName");
                holder.left4.setText(!left4Title.equals("null") ? left4Title : "");
                holder.left4.setVisibility(View.VISIBLE);

                String right4Title = item.get(4).get("fieldCnName2");
                holder.right4.setText(!right4Title.equals("null") ? right4Title : "");
                holder.right4.setVisibility(View.VISIBLE);
//左5
                String left5Title = item.get(5).get("fieldCnName");
                holder.left5.setText(!left5Title.equals("null") ? left5Title : "");
                holder.left5.setVisibility(View.VISIBLE);

                String right5Title = item.get(5).get("fieldCnName2");
                holder.right5.setText(!right5Title.equals("null") ? right5Title : "");
                holder.right5.setVisibility(View.VISIBLE);
//左6
                String left6Title = item.get(6).get("fieldCnName");
                holder.left6.setText(!left6Title.equals("null") ? left6Title : "");
                holder.left6.setVisibility(View.VISIBLE);

                String right6Title = item.get(6).get("fieldCnName2");
                holder.right6.setText(!right6Title.equals("null") ? right6Title : "");
                holder.right6.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }


//判断跳转子表格
            final String titleName = item.get(0).get("fieldCnName2");
            final String mainId = item.get(0).get("mainId");
            Log.e(TAG, "onBindViewHolder: childTab "+childTab.toString());
            if (childTab.size() > 0) {
                holder.dash_ll.setVisibility(View.VISIBLE);
                holder.click_open.setVisibility(View.VISIBLE);
                holder.click_open_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "onClick: childTab "+childTab.toString());
                        Intent intent = new Intent();
                        intent.setClass(mContext, TabActivity.class);
                        intent.putExtra("mainId", mainId);
                        intent.putExtra("childTab", JSON.toJSONString(childTab));
                        intent.putExtra("titleName", titleName);
                        mContext.startActivity(intent);
//                        holder.click_open_btn.setAlpha(new Float(0.75));

                    }
                });
            }else{
                holder.dash_ll.setVisibility(View.GONE);
                holder.click_open.setVisibility(View.GONE);
            }

            holder.itemView.setTag(item);
        }

    }

    private static final String TAG = "ListAdapter2";
    /**
     * 获取单项数据
     */

    private List<Map<String, String>> getData(int position) {

        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     */
    public List<List<Map<String, String>>> getDatas() {

        return mDatas;
    }

    /**
     * 清除数据
     */
    public void clearData() {

        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
    }


    /**
     * 下拉刷新更新数据
     */
    public void addData(List<List<Map<String, String>>> datas, List<Map<String, Object>> childTab) {
        this.childTab=childTab;
        addData(0, datas);

    }

    /**
     * 上拉加载添加数据的方法
     */
    public void addData(int position, List<List<Map<String, String>>> datas) {

        if (datas != null && datas.size() > 0) {

            mDatas.addAll(datas);
            notifyItemRangeChanged(position, mDatas.size());
        }

    }

    @Override
    public int getItemCount() {

        return mDatas.size() > 0 ? mDatas.size() : 1;

    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(view, JSON.toJSONString(view.getTag()));
        }
    }
}