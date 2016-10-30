package com.kwsoft.kehuhua.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/10/21 0021.
 *
 */

/**
 * //初始化左侧名称
 TextView textView = (TextView) convertView.findViewById(R.id.add_item_name);

 //初始化必填标志
 TextView textViewIfMust = (TextView) convertView.findViewById(R.id.tv_if_must);
 if (ifMust == 1) {
 textViewIfMust.setVisibility(View.VISIBLE);
 }
 RelativeLayout list_item_cover = (RelativeLayout) convertView.findViewById(R.id.list_item_cover);
 RelativeLayout list_item_cover2 = (RelativeLayout) convertView.findViewById(R.id.list_item_cover2);

 //初始化上传图片框
 RelativeLayout image_upload_layout = (RelativeLayout) convertView.findViewById(R.id.image_upload_layout);

 TextView picNumber = (TextView) convertView.findViewById(R.id.pic_number);
 Button image_upload = (Button) convertView.findViewById(R.id.image_upload);

 //初始化编辑框
 EditText add_edit_text = (EditText) convertView.findViewById(R.id.add_edit_text);
 //初始化日期选、时间、内部对象多值选择器
 final TextView addGeneral = (TextView) convertView.findViewById(R.id.add_general);
 //初始化字典选择器单值选择项
 NiceSpinner add_spinner = (NiceSpinner) convertView.findViewById(R.id.add_spinner);
 //初始化无限添加按钮
 Button add_unlimited = (Button) convertView.findViewById(R.id.add_unlimited);
 */
public class OperateHolder extends RecyclerView.ViewHolder {
    final View mView;
    TextView textView,textViewIfMust,picNumber;
    RelativeLayout list_item_cover,list_item_cover2,image_upload_layout;
    Button image_upload,add_unlimited;
    EditText add_edit_text;
    final TextView addGeneral;
    TextView add_spinner;


    OperateHolder(View view) {
        super(view);
        mView = view;
        textView = (TextView) view.findViewById(R.id.add_item_name);
        textViewIfMust = (TextView) view.findViewById(R.id.tv_if_must);
        list_item_cover = (RelativeLayout) view.findViewById(R.id.list_item_cover);
        list_item_cover2 = (RelativeLayout) view.findViewById(R.id.list_item_cover2);
        image_upload_layout = (RelativeLayout) view.findViewById(R.id.image_upload_layout);
        picNumber = (TextView) view.findViewById(R.id.pic_number);
        image_upload = (Button) view.findViewById(R.id.image_upload);
        add_edit_text = (EditText) view.findViewById(R.id.add_edit_text);
        addGeneral = (TextView) view.findViewById(R.id.add_general);
        add_spinner = (TextView) view.findViewById(R.id.add_spinner);
        add_unlimited = (Button) view.findViewById(R.id.add_unlimited);

    }
}
