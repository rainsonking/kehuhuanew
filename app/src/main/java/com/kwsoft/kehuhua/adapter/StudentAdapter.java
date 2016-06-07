package com.kwsoft.kehuhua.adapter;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2015/12/1 0001.
 */
public class StudentAdapter extends ArrayAdapter<List<Map<String, String>>> {
    private int resourceId;
    private boolean isMulChoice = false; //是否多选
//    private PhoneFieldSet phoneFieldSet;

    public StudentAdapter(Context context, int textViewResourceId,
                          List<List<Map<String, String>>> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
//        this.phoneFieldSet=phoneFieldSet;
    }

    public void setIsMulChoice(boolean isMulChoice){
        this.isMulChoice=isMulChoice;
    }

    CursorAdapter ca;


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        List<Map<String, String>> student = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_nextpage = (ImageView) view.findViewById(R.id.iv_nextpage);
            viewHolder.studentName = (TextView) view.findViewById(R.id.stu_name);
            viewHolder.left1= (TextView) view.findViewById(R.id.left1);
            viewHolder.left2= (TextView) view.findViewById(R.id.left2);
            viewHolder.left3= (TextView) view.findViewById(R.id.left3);
            viewHolder.left4= (TextView) view.findViewById(R.id.left4);
            viewHolder.right1= (TextView) view.findViewById(R.id.right1);
            viewHolder.right2= (TextView) view.findViewById(R.id.right2);
            viewHolder.right3= (TextView) view.findViewById(R.id.right3);
            viewHolder.right4= (TextView) view.findViewById(R.id.right4);


            viewHolder.cb = (CheckBox) view.findViewById(R.id.cb);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            String stuName = URLDecoder.decode(student.get(1).get("fieldCnName2") + "", "utf-8");
            viewHolder.studentName.setText(stuName);

            String left1 = URLDecoder.decode(student.get(2).get("fieldCnName") + "", "utf-8");
            String right1 = URLDecoder.decode(student.get(2).get("fieldCnName2") + "", "utf-8");
            viewHolder.left1.setText(left1);
            viewHolder.right1.setText(right1);

            String left2 = URLDecoder.decode(student.get(3).get("fieldCnName") + "", "utf-8");
            String right2 = URLDecoder.decode(student.get(3).get("fieldCnName2") + "", "utf-8");
            viewHolder.left2.setText(left2);
            viewHolder.right2.setText(right2);

            String left3 = URLDecoder.decode(student.get(4).get("fieldCnName") + "", "utf-8");
            String right3 = URLDecoder.decode(student.get(4).get("fieldCnName2") + "", "utf-8");
            viewHolder.left3.setText(left3);
            viewHolder.right3.setText(right3);

            String left4 = URLDecoder.decode(student.get(5).get("fieldCnName") + "", "utf-8");
            String right4 = URLDecoder.decode(student.get(5).get("fieldCnName2") + "", "utf-8");
            viewHolder.left4.setText(left4);
            viewHolder.right4.setText(right4);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if(isMulChoice){
            viewHolder.iv_nextpage.setVisibility(View.GONE);
            viewHolder.cb.setVisibility(View.VISIBLE);
            if (student.get(0).get("check").equals("true")){
                viewHolder.cb.setChecked(true);
            }else {
                viewHolder.cb.setChecked(false);
            }
        }else {
            viewHolder.iv_nextpage.setVisibility(View.VISIBLE);
            viewHolder.cb.setVisibility(View.GONE);
            if (student.get(0).get("check").equals("true")){
                viewHolder.cb.setChecked(true);
            }else {
                viewHolder.cb.setChecked(false);
            }
        }
        return view;
    }

    public class ViewHolder {

        ImageView iv_nextpage;

        TextView studentName;
        TextView left1;
        TextView left2;
        TextView left3;
        TextView left4;

        TextView right1;
        TextView right2;
        TextView right3;
        TextView right4;


        public CheckBox cb;

    }

}
