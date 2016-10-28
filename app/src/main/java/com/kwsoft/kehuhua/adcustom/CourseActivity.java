package com.kwsoft.kehuhua.adcustom;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.model.OnDataListener;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.view.CourseView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import noman.weekcalendar.WeekCalendar;
import noman.weekcalendar.WeekDateChaListener;
import noman.weekcalendar.listener.OnDateClickListener;
import okhttp3.Call;

public class CourseActivity extends AppCompatActivity implements OnDataListener,WeekDateChaListener {
    private WeekCalendar weekCalendar;
    private List<Map<String,Object>> list=new ArrayList<>();
    private List<Map<String,Object>> listQingjia;
    //    private CourseGridView courseGridView;
//    private CourseAdapter courseAdapter;
    private TextView tv_date;
    private ImageView IV_back_list_item_tadd;
    private RelativeLayout layout_date;
    private DateTime currentDateTime;
    private SharedPreferences sharedPreferences;

    //本周第一天
    private String thisWeekFirstDate;
    //本周最后一天
    private String thisWeekLastDate;
    //左侧显示时间的textview的宽度像素
    private float leftTextViewWidth;
    //左侧显示时间的textview的高度像素
    private float leftTextViewHeight;
    //每节课所占的宽度
    private float courseInfoWidth;
    //每分钟所对应的屏幕高度像素
    private float oneMinWidth;
    //课程表日期的布局
    private RelativeLayout rl_top;
    //选中日期时添加的view
    private View selectView,selectViewButtom;
    //添加课程信息的布局
    private RelativeLayout courseInfoLayout;
    //课程信息的背景布局
    private RelativeLayout layout_course_bg;
    //左侧textview
    private TextView leftTextView;
    //每条横线的高度像素
    private float lineHeight;
    //    int dataIndexMon=0;//记录数据当前周周一的下标
//    int dataIndexSun=0;//记录数据当前周周日的下标
    private int leaveIndex;//请假的下标
    private String tableId="";

    private Map<String,String> paramsMap=new HashMap<>();
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course2);
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getIntentData();
        init();
        url=Constant.sysUrl+"dataPlAdd_interfaceShowDateCourse.do";
        //?mainId=73&tableId=19&minDate=2016-05-26&maxDate=2016-06-26

        requestCourseData(url);
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            String buttonSetItemStr = intent.getStringExtra("itemData");
            Map<String, Object> itemData = JSON.parseObject(buttonSetItemStr);
            String urlHalf= String.valueOf(itemData.get("menuPageUrl"));
            // "dataPlAdd_toShowPage.do?tableId=19&ifAjax=1",
            String jieguo = urlHalf.substring(urlHalf.indexOf("?")+1,urlHalf.length()-1);
            String[] jieGuo1= urlHalf.split("tableId=");
            String[] jieGuo2=jieGuo1[1].split("&");
            tableId= jieGuo2[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void init(){
        Log.e("TAG","课程表监测点paramsMap"+paramsMap.toString());
        rl_top=(RelativeLayout)findViewById(R.id.rl_top);
        layout_course_bg=(RelativeLayout) findViewById(R.id.layout_course_bg);
        courseInfoLayout=(RelativeLayout)findViewById(R.id.layout_course_info);
        leftTextView=(TextView)findViewById(R.id.tv_left);
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        leftTextView.measure(w, h);
        leftTextViewWidth =leftTextView.getMeasuredWidth();
        leftTextViewHeight =dip2px(this,60f);
        lineHeight=dip2px(this,0.5f);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int layoutWidth = metric.widthPixels;     // 屏幕宽度（像素）
        courseInfoWidth=(layoutWidth-leftTextViewWidth-lineHeight*2)/7;
        oneMinWidth=leftTextViewHeight/30;
        sharedPreferences=getSharedPreferences("userInfo",MODE_PRIVATE);
        tv_date=(TextView)findViewById(R.id.tv_date);
        IV_back_list_item_tadd=(ImageView) findViewById(R.id.IV_back_list_item_tadd);
        IV_back_list_item_tadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_date=(RelativeLayout)findViewById(R.id.layout_date);
        currentDateTime=new DateTime();
        Log.e("TAG","课程表监测点2");
        tv_date.setText(currentDateTime.getMonthOfYear()+"月");
        layout_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());
                //默认选中当前时间
                com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog = com.fourmob.datetimepicker.date.DatePickerDialog.newInstance((new com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog, int year, int month, int day) {

                                String monthNew = String.valueOf(month + 1);
                                String dayNew = String.valueOf(day);
                                if (month + 1 < 10) {
                                    monthNew = "0" + String.valueOf(month + 1);
                                }
                                if (day < 10) {
                                    dayNew = "0" + String.valueOf(day);
                                }


                                tv_date.setText(monthNew);
//                                addGeneral.setText(year + "-" + monthNew + "-" + dayNew);
                                Log.i("123","date===>"+year+"-"+monthNew+"-"+dayNew);
                                String dateStr=year+"-"+monthNew+"-"+dayNew;
                                DateTime testDate=DateTime.parse(dateStr);
                                weekCalendar.setSelectedDate(testDate);
                            }
                        }),
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH),
                        true);
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1983, 2030);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), "datepicker");
            }
        });
        weekCalendar = (WeekCalendar)findViewById(R.id.weekCalendar);
        weekCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(DateTime dateTime) {
//                Toast.makeText(this(), "You Selected " + dateTime.toString(), Toast
//                        .LENGTH_SHORT).show();
                String date=dateTime.toString("yyyy-MM-dd HH:mm:ss");
                int week=week(date);
                //左边距
                float marginLeft=((week-1)*courseInfoWidth)+leftTextViewWidth+lineHeight*2;
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams((int)courseInfoWidth,dip2px(CourseActivity.this,2));
                layoutParams.setMargins((int)marginLeft,dip2px(CourseActivity.this,64),0,0);

                RelativeLayout.LayoutParams lpButtom=new RelativeLayout.LayoutParams((int)courseInfoWidth, ViewGroup.LayoutParams.MATCH_PARENT);
                lpButtom.setMargins((int)marginLeft,0,0,0);
                if (selectView == null) {
                    selectView = new View(CourseActivity.this);
                    selectView.setBackgroundResource(R.color.selectColor);
                } else {
                    rl_top.removeView(selectView);
                }
                selectView.setLayoutParams(layoutParams);
                rl_top.addView(selectView);

                if (selectViewButtom == null) {
                    selectViewButtom = new View(CourseActivity.this);
                    selectViewButtom.setBackgroundResource(R.color.selectColorButtom);
                } else {
                    layout_course_bg.removeView(selectViewButtom);
                }
                selectViewButtom.setLayoutParams(lpButtom);
                layout_course_bg.addView(selectViewButtom);
            }

        });
        Log.e("TAG","课程表监测点3");
        weekCalendar.setWeekDateChaListener(this);

        DateTime midDate=currentDateTime.withDayOfWeek(DateTimeConstants.THURSDAY);
        DateTime startDate=midDate.plusDays(-3);
        DateTime endDate=midDate.plusDays(3);
        thisWeekFirstDate=startDate.toString("yyyy-MM-dd");
        thisWeekLastDate=endDate.toString("yyyy-MM-dd");

        paramsMap.put("mainId",Constant.USERID);
        paramsMap.put("tableId",tableId);
        paramsMap.put("minDate",thisWeekFirstDate);//thisWeekFirstDate
        paramsMap.put("maxDate",thisWeekLastDate);//thisWeekLastDate
    }

    private static final String TAG = "CourseActivity";

    //请求课程表数据
    private void requestCourseData(String volleyUrl){
        //startAnim();
        Log.e("TAG","请求课表数据");

        //参数
        paramsMap.put("tNumber", "0");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(CourseActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }


    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {


        Map<String,Object>  dataMap = null;
        try {
            dataMap = JSON.parseObject(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("TAG", "转换课程表数据");

        list= (List<Map<String, Object>>) dataMap.get("dataInfo");

        if (list.size()>0) {
            setCourseDataInTable();
        }else{
            // stopAnim();
            Toast.makeText(this, "无课表数据", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onGetDataSuccess(String jsonData) {

    }

    @Override
    public void onGetDataError() {

    }

    @Override
    public void onLoading(long total, long current) {

    }

    //将课程信息填写到课表中
    private void setCourseDataInTable(){
        Log.e("TAG", "处理课程表数据");
        SimpleDateFormat long2Date=new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        boolean firstData=false;//是否已经知道当前周第一节课
        for (int i=0;i<list.size();i++){
            Log.e("TAG", "开始循环课程表数据");
            long startTime=Long.valueOf(String.valueOf(list.get(i).get("START_TIME")));
            long endTime=Long.valueOf(String.valueOf(list.get(i).get("END_TIME")));
            Date dateS=new Date(startTime);
            Date dateE=new Date(endTime);
            final String sTimeStr=long2Date.format(dateS);
            final String eTimeStr=long2Date.format(dateE);
            Log.e("TAG","开始时间："+sTimeStr);
            Log.e("TAG","结束时间："+eTimeStr);

            String leftTime=sTimeStr.substring(11,sTimeStr.length());
            String rightTime=eTimeStr.substring(11,eTimeStr.length());

            try {
                String date=eTimeStr.substring(0,10);
                Log.e("TAG","结束日期："+date);
                String[] splitLeft = leftTime.split(":");
                String stringHour = splitLeft[0];
                String stringMin = splitLeft[1];
                int left=Integer.parseInt(stringHour)*60;
                left+=Integer.parseInt(stringMin);
                Log.e("TAG","课程表检测1");
                String[] splitRight = rightTime.split(":");
                String stringHour1 = splitRight[0];
                String stringMin1 = splitRight[1];
                int right=Integer.parseInt(stringHour1)*60;
                right+=Integer.parseInt(stringMin1);
                Log.e("TAG","课程表检测2");
                int week=week(date);
                //左边距
                float marginLeft=((week-1)*courseInfoWidth)+leftTextViewWidth+lineHeight*2;
                //上边距跨过横线的条数总和的高度像素
                float topLineSumHeight=((left-480)/30)*lineHeight;
                //上边距
                float marginTop=(left-480)*oneMinWidth+topLineSumHeight;
                CourseView courseView=new CourseView(this);
                //课程高度跨过的横线条数总和的高度像素
                float chLineSumHeight=((right-left)/30)*lineHeight;
                //课程高度
                Log.e("TAG","课程表检测3");
                float courseHeight=(right-left)*oneMinWidth+chLineSumHeight;
                RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams((int)courseInfoWidth,(int)courseHeight);
                layoutParams.setMargins((int)marginLeft,(int)marginTop,0,0);
//                courseView.getTopRightIv().setAdjustViewBounds(true);
//                courseView.getTopRightIv().setMaxWidth((int)(courseInfoWidth/2));
//                courseView.getTopRightIv().setMaxHeight((int)(courseInfoWidth/2));
                courseView.setLayoutParams(layoutParams);
                Log.e("TAG","课程表检测3。1");
                String courseName="";
                if (list.get(i).get("SHOW_CONTENT")!=null) {
                    courseName=String.valueOf(list.get(i).get("SHOW_CONTENT"));
                }
                Log.e("TAG","课程表检测3。2");
                courseView.setBackgroundResource(R.drawable.couse_bg);
                courseView.setCourseNameText(courseName);
                courseInfoLayout.addView(courseView);

                final String content=list.get(i).get("SHOW_CONTENT")+"";
                courseView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(CourseActivity.this,CourseDetailActivity.class);
                        if (!content.equals("null")) {
                            intent.putExtra("content",content);
                            intent.putExtra("sTimeStr",sTimeStr);
                            intent.putExtra("eTimeStr",eTimeStr);
                        }
                        startActivity(intent);
                    }
                });
                Log.e("TAG","课程表检测4");
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        // stopAnim();
    }

    //获得星期几
    private int week(String date){
        if (date.equals("null")) {
            return 8;
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar=Calendar.getInstance();
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int week=calendar.get(Calendar.DAY_OF_WEEK);
        if (week==1){
            return 7;
        }else if (week==2){
            return 1;
        }else if (week==3){
            return 2;
        }else if (week==4){
            return 3;
        }else if (week==5){
            return 4;
        }else if (week==6){
            return 5;
        }else if (week==7){
            return 6;
        }else {
            return 8;
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void getDate(DateTime dateTime) {
        currentDateTime=dateTime;
        tv_date.setText(currentDateTime.getMonthOfYear()+"月");
        DateTime midDate=dateTime.withDayOfWeek(DateTimeConstants.THURSDAY);
        DateTime startDate=midDate.plusDays(-3);
        DateTime endDate=midDate.plusDays(3);
        thisWeekFirstDate=startDate.toString("yyyy-MM-dd");
        thisWeekLastDate=endDate.toString("yyyy-MM-dd");
        courseInfoLayout.removeAllViews();
        paramsMap.put("minDate",thisWeekFirstDate);//thisWeekFirstDate
        paramsMap.put("maxDate",thisWeekLastDate);//thisWeekLastDate
        requestCourseData(url);

//        courseAdapter.startAndEndDate(startDate.toString("yyyy-MM-dd"),endDate.toString("yyyy-MM-dd"));
//        courseAdapter.notifyDataSetChanged();
    }

//    void startAnim() {
//        findViewById(R.id.avloadingIndicatorViewLayoutPro).setVisibility(View.VISIBLE);
//    }
//
//    void stopAnim() {
//        findViewById(R.id.avloadingIndicatorViewLayoutPro).setVisibility(View.GONE);
//    }

}
