package com.kwsoft.kehuhua.adapter;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.EditActivity;
import com.kwsoft.kehuhua.adcustom.R;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/9 0009.
 */
public class ChangeStuInfoAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, Object>> mListValues;

    public ChangeStuInfoAdapter(List<Map<String, Object>> mListValues, Context mContext) {
        this.mContext = mContext;
        this.mListValues = mListValues;
    }

    @Override
    public int getCount() {
        return mListValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mListValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("TAG","mMap值+++++++++++++++++++++++");



            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_edit_item, null);
            TextView mTextViewField = (TextView) convertView.findViewById(R.id.tv_stu_field);
            final TextView mTextViewFieldName = (TextView) convertView.findViewById(R.id.tv_stu_field_name);

        Map<String, Object> mMap = mListValues.get(position);
        Log.e("TAG","mMap值+++++++++++++++++++++++"+ mMap.toString());
        mTextViewField.setText(mMap.get("leftData") + ":  ");

        
        int fieldRole = Integer.parseInt(String.valueOf(mListValues.get(position).get("fieldRole")));
        if (fieldRole == 14) {

            if(mMap.get("rightData")!=null&&!mMap.get("rightData").equals("")){
            String dateTime= mMap.get("rightData") + "";
                Log.e("TAG", "fieldRole=" + fieldRole + "   日期填入前：" + mMap.get("rightData") + "");
            String[] dt=dateTime.split(" ");
            if(dt.length>1){

            mTextViewFieldName.setText(dt[0]);
            EditActivity.mapCommit.put(mListValues.get(position).get("jiChuKey") + "", dt[0]);

            }else{
               mTextViewFieldName.setText(mMap.get("rightData") + "");
                EditActivity.mapCommit.put(mListValues.get(position).get("jiChuKey") + "",mMap.get("rightData") + "");
            }}

        mTextViewFieldName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker dp, int year, int mounth, int day) {
                                String dateTime=year + "-" + (mounth+1) + "-" + day;
                               mTextViewFieldName.setText(dateTime);
                                mListValues.get(position).put("rightData", dateTime);
                                EditActivity.mapCommit.put(mListValues.get(position).get("jiChuKey") + "", mListValues.get(position).get("rightData") + "");
                            Log.e("TAG","提交的日期："+mListValues.get(position).get("rightData") + "");

                            }
                        },
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH)).show();




            }
        });
        }else{
            mTextViewFieldName.setText(mMap.get("rightData") + "");
        }
        return convertView;
    }

}
