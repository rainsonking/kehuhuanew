package com.kwsoft.kehuhua.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;

/**
 * Created by Administrator on 2016/6/7 0007.
 *
 */
public class CourseView extends RelativeLayout {
    TextView tv_order_state,tv_course_name,tv_course_type;
    RelativeLayout relativeLayout;
    ImageView imageView;

    public CourseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view=LayoutInflater.from(context).inflate(R.layout.course_view, this);
//        tv_order_state=(TextView)view.findViewById(R.id.tv_order_state);
        tv_course_name=(TextView)view.findViewById(R.id.tv_course_name);
//        tv_course_type=(TextView)view.findViewById(R.id.tv_course_type);
//        imageView=(ImageView) view.findViewById(R.id.iv_top_right);
//        relativeLayout=(RelativeLayout)view.findViewById(R.id.layout_course_view);
    }

    public CourseView(Context context) {
        super(context);
        View view=LayoutInflater.from(context).inflate(R.layout.course_view, this);
//        tv_order_state=(TextView)view.findViewById(R.id.tv_order_state);
        tv_course_name=(TextView)view.findViewById(R.id.tv_course_name);
//        tv_course_type=(TextView)view.findViewById(R.id.tv_course_type);
//        imageView=(ImageView) view.findViewById(R.id.iv_top_right);
//        relativeLayout=(RelativeLayout)view.findViewById(R.id.layout_course_view);
    }

    public ImageView getTopRightIv(){
        return imageView;
    }

    public void setOrderSateText(String text) {
        tv_order_state.setText(text);
    }

    public void setCourseTypeTextColor(int color) {
        tv_course_type.setTextColor(color);
    }

    public void setCourseNameText(String text) {
        tv_course_name.setText(text);
    }

    public void setCourseTypeText(String text) {
        tv_course_type.setText(text);
    }

}
