package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends Activity implements View.OnClickListener{

    private String searchSetUrl;
    private Map<String,String> searchParameter;
    private DiskLruCacheHelper DLCH;
    private String searchParameterString;
    private List<Map<String, Object>> mList;
    private ListView mListView;
    private HashMap<Integer, String> hashMap = new HashMap<>();
    private HashMap<Integer, String> hashMap1 = new HashMap<>();
    private int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        try {
            DLCH = new DiskLruCacheHelper(SearchActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getDataIntent();
        initView();
        requestSearchData(searchSetUrl);
    }

    public void initView() {
        mListView= (ListView) findViewById(R.id.lv_search);
        TextView cancel = (TextView) findViewById(R.id.tv_search_cancel);
        TextView commit = (TextView) findViewById(R.id.tv_commit_search);
        commit.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }
    public void getDataIntent() {
        Intent intent = getIntent();
        searchSetUrl = intent.getStringExtra("searchSetUrl");
        searchParameterString = intent.getStringExtra("searchParameter");
        searchParameter= JSON.parseObject(searchParameterString,Map.class);
    }
//获取请求数据
private void requestSearchData(String volleyUrl) {
    Log.e("TAG","是否能运行到此3");
    final String volleyUrl1= volleyUrl.replaceFirst("10.252.46.80","182.92.108.162");
    StringRequest mStringRequest = new StringRequest(Request.Method.POST, volleyUrl1,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String jsonData) {
                    DLCH.put(volleyUrl1+searchParameterString,jsonData);
                    Log.i("jsonData", jsonData);
                    //解析筛选条件jsonData数据
                    parseJsonData(jsonData);
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            String diskData=DLCH.getAsString(volleyUrl1+searchParameterString);
            parseJsonData(diskData);
        }
    }
    ) {
        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return searchParameter;
        }
    };
    VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(mStringRequest);
}

    private void parseJsonData(String jsonData) {

        Map<String, Object> mMap = JSON.parseObject(jsonData, Map.class);
        mList= (List<Map<String, Object>>) mMap.get("phoneSearchSet");
        Log.e("TAG","搜索返回配置数据"+mList.toString());
        if(mList!=null){
            for(int i=0;i<mList.size();i++){
                mList.get(i).put("tempValue","");
                int fieldRole= (int) mList.get(i).get("fieldRole");
                int fieldType= (int) mList.get(i).get("fieldType");

                if(fieldRole==16){
                    //拼接第1个参数（逻辑与），写死的

                    String key1=mList.get(i).get("fieldSearchName")+"_d"+"_andOr";
                    mList.get(i).put("isAndOrKey",key1);
                    //拼接第2个参数（包含关系），也是写死的
                    String tempW=mList.get(i).get("fieldSearchName")+"";
                    String[] data= tempW.split("__");
                    if(data.length>1){
                        mList.get(i).put("valueKey",data[1]+"strCond_dic");}else{
                        mList.get(i).put("valueKey","");
                    }


                }else if(fieldType==2){//字符串
                    //拼接第1个参数（逻辑与），写死的

                    String key1=mList.get(i).get("fieldSearchName")+"_"+mList.get(i).get("fieldType")+"_andOr";
                    //拼接第2个参数（包含关系），也是写死的
                    String key2=mList.get(i).get("fieldSearchName")+"_strCond_pld";
                    //拼接第3个Key名称，也是写死的
                    String key3=mList.get(i).get("fieldSearchName")+"_strVal_pld";
                    mList.get(i).put("isAndOrKey",key1);
                    mList.get(i).put("logicContainKey",key2);
                    mList.get(i).put("valueKey",key3);
                }else if(fieldType==1){//数字
                    //拼接第1个参数（逻辑与），写死的
                    String key1=mList.get(i).get("fieldSearchName")+"_"+mList.get(i).get("fieldType")+"_andOr";
                    //拼接第2个参数（包含关系），也是写死的
                    String key2=mList.get(i).get("fieldSearchName")+"_numCondOne_pld";
                    //拼接第3个Key名称，也是写死的
                    String key3=mList.get(i).get("fieldSearchName")+"_numValOne_pld";
                    mList.get(i).put("isAndOrKey",key1);
                    mList.get(i).put("logicContainKey",key2);
                    mList.get(i).put("valueKey",key3);

                }else if(fieldType==3) {//日期
                    //拼接第1个参数（逻辑与），写死的
                    String key1=mList.get(i).get("fieldSearchName")+"_"+mList.get(i).get("fieldType")+"_andOr";
                    //拼接第2个参数（包含关系），也是写死的
                    String key2=mList.get(i).get("fieldSearchName")+"_startDates_pld";
                    //拼接第3个Key名称，也是写死的
                    String key3=mList.get(i).get("fieldSearchName")+"_endDates_pld";
                    mList.get(i).put("isAndOrKey",key1);
                    mList.get(i).put("logicContainKey",key2);
                    mList.get(i).put("valueKey",key3);
                    mList.get(i).put("tempValue2","");
                }
            }}

        if (mList != null) {
            SearchAdapter searchAdapter = new SearchAdapter(SearchActivity.this);
            mListView.setAdapter(searchAdapter);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_commit_search:
                searchCommit(Constant.searchCommitUrl);
                break;
            case R.id.tv_search_cancel:
                this.finish();
                break;
        }
    }


    /**
     * popupWindow适配器adapter
     *
     *
     */

    public class SearchAdapter extends BaseAdapter {
        private Context mContext;

        public SearchAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //Spinner searchSpinner= (Spinner) convertView.findViewById(R.id.chooseDic);
            //判断右侧展示的属性数据是否为字典或者时间选择
            if((int)mList.get(position).get("fieldRole")==16){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_search_item_date, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_name_dic);
                tv.setText(mList.get(position).get("fieldCnName") + ":");
                //设置右侧默认值
                final TextView tv2= (TextView) convertView.findViewById(R.id.tv_view_dic);
                String s1= (String) mList.get(position).get("dicOptions");
                com.alibaba.fastjson.JSONArray ja = JSON.parseArray(s1);
                final List<Map<String, Object>> dicMap = new ArrayList<>();
                List<String> dicArrayList=new ArrayList();

                for (int k = 0; ja.size() > k; k++) {
                    String str = ja.get(k).toString();
                    Map<String, Object> mapDicItem = JSON.parseObject(str, Map.class);
                    dicArrayList.add((String) mapDicItem.get("DIC_NAME"));
                    dicMap.add(mapDicItem);
                }
                tv2.setText("请选择");
                tv2.setTextColor(getResources().getColor(R.color.gray));
                //mList.get(position).put("tempValue", "" + dicMap.get(0).get("DIC_NAME"));
                Log.e("TAG", "右侧完毕：" + s1);
                final String[] dicIdOptions = dicArrayList.toArray(new String[dicArrayList.size()]);
                //Log.e("TAG", "词典值" + dicIdOptions[0] + dicIdOptions[1] + dicIdOptions[2]);
                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);

                        builder.setSingleChoiceItems(dicIdOptions, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv2.setText(dicMap.get(which).get("DIC_NAME") + "");
                                mList.get(position).put("tempValue", dicMap.get(which).get("DIC_ID") + "");
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });
            }else if((int)mList.get(position).get("fieldType")==3){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_search_item_date, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_name_date);
                tv.setText(mList.get(position).get("fieldCnName") + ":");
                final TextView date3=(TextView) convertView.findViewById(R.id.tv_date_start);
                final TextView date4=(TextView) convertView.findViewById(R.id.tv_date_end);
                date3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                                               Date date = new Date();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
//                        DatePickDialogUtil timeWindow = new DatePickDialogUtil(ListActivity.this, sdf.format(date));
//                        timeWindow.dateTimePicKDialog(date3,position,mList,"tempValue");
                        Calendar c = Calendar.getInstance();
                        new DatePickerDialog(SearchActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker dp, int year, int mounth, int day) {
                                        String dateTime=year + "-" + (mounth+1) + "-" + day;
                                        date3.setText(dateTime);
                                        mList.get(position).put("tempValue", dateTime);
                                    }
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });

                date4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Date date = new Date();
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
//                        DatePickDialogUtil timeWindow = new DatePickDialogUtil(ListActivity.this, sdf.format(date));
//                        timeWindow.dateTimePicKDialog(date4,position,mList,"tempValue2");
                        Calendar c = Calendar.getInstance();
                        new DatePickerDialog(SearchActivity.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker dp, int year, int mounth, int day) {
                                        String dateTime=year + "-" + (mounth+1) + "-" + day;
                                        date4.setText(dateTime);
                                        mList.get(position).put("tempValue2", dateTime);
                                    }
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            }else if((int)mList.get(position).get("fieldType")==2){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_search_item_edit, null);
                TextView tv = (TextView) convertView.findViewById(R.id.tv_name_edit);
                tv.setText(mList.get(position).get("fieldCnName") + ":");
                EditText editText = (EditText) convertView.findViewById(R.id.ed_input);
                editText.setHint("请输入"+mList.get(position).get("fieldCnName"));
                //为editText设置TextChangedListener，每次改变的值设置到hashMap
                //我们要拿到里面的值根据position拿值
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index = position;
                        }
                        return false;
                    }
                });

                editText.clearFocus();
                if (index != -1 && index == position) {
                    // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                    editText.requestFocus();
                }
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //将editText中改变的值设置的HashMap中
                        hashMap.put(position, s.toString());
                        mList.get(position).put("tempValue", s.toString());
                    }
                });

                //如果hashMap不为空，就设置的editText
                if(hashMap.get(position) != null){
                    editText.setText(hashMap.get(position));
                }

                editText.setSelection(editText.getText().length());


            }else if((int)mList.get(position).get("fieldType")==1){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_screen_item, null);
                Log.e("TAG", "editText");
                TextView tv = (TextView) convertView.findViewById(R.id.tv_term_name);
                tv.setText(mList.get(position).get("fieldCnName") + ":");
                EditText editText = (EditText) convertView.findViewById(R.id.ed_input_term);
                //为editText设置TextChangedListener，每次改变的值设置到hashMap
                //我们要拿到里面的值根据position拿值
                editText.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index = position;
                        }
                        return false;
                    }
                });

                editText.clearFocus();
                if (index != -1 && index == position) {
                    // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                    editText.requestFocus();
                }
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //将editText中改变的值设置的HashMap中
                        hashMap1.put(position, s.toString());
                        mList.get(position).put("tempValue", s.toString());
                    }
                });

                //如果hashMap不为空，就设置的editText
                if(hashMap1.get(position) != null){
                    editText.setText(hashMap1.get(position));
                }
                editText.setSelection(editText.getText().length());
            }
            return convertView;
        }
    }
    private void searchCommit(String volleyUrl){

        Log.e("TAG", "搜索配置数据" + mList.toString());

        Map<String,String> mapSearchCommit=new HashMap<>();
        mapSearchCommit.putAll(searchParameter);
        for(int i=0;i<mList.size();i++){
            if(mList.get(i).get("tempValue")!=null&&!mList.get(i).get("tempValue").equals("")){
                int fieldRole= (int) mList.get(i).get("fieldRole");
                int fieldType= (int) mList.get(i).get("fieldType");
                if(fieldRole==16){//字典
                    mapSearchCommit.put(""+mList.get(i).get("isAndOrKey"),0+"");

                    Log.e("TAG", "字典数据：" + "第一个值"+mList.get(i).get("valueKey")+"第二个值"+mList.get(i).get("tempValue"));
                    mapSearchCommit.put(""+mList.get(i).get("valueKey"),""+mList.get(i).get("tempValue"));

                }else if(fieldType==2||fieldType==1){//字符串+数字

                    mapSearchCommit.put(""+mList.get(i).get("isAndOrKey"),0+"");
                    mapSearchCommit.put(""+mList.get(i).get("logicContainKey"),0+"");
                    mapSearchCommit.put(""+mList.get(i).get("valueKey"),""+mList.get(i).get("tempValue"));
                }else if(fieldType==3){//日期
                    mapSearchCommit.put(""+mList.get(i).get("isAndOrKey"),0+"");
                    mapSearchCommit.put(""+mList.get(i).get("logicContainKey"),""+mList.get(i).get("tempValue"));
                    mapSearchCommit.put(""+mList.get(i).get("valueKey"),""+mList.get(i).get("tempValue2"));
                    SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
                    String date1=""+mList.get(i).get("tempValue");
                    String date2=""+mList.get(i).get("tempValue2");
                    Date firstDate;
                    Date secondDate;
                    try {
                        firstDate = sdf.parse(date1);
                        secondDate = sdf.parse(date2);
                        boolean flag = firstDate.before(secondDate);
                        if(!flag){
                            Toast.makeText(SearchActivity.this,"起始日期必须要小于截止日期",Toast.LENGTH_SHORT).show();
                            return;
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }}

        Log.e("TAG", "搜索输入结果数据" + hashMap.toString());
        final Map<String,String> map=mapSearchCommit;
        Log.e("TAG", "搜索所有请求参数" + mapSearchCommit.toString());
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        backList(jsonData);
                        Log.e("TAG", "搜索请求返回结果打印" + jsonData);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(SearchActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
    }

    final int RESULT_CODE=101;

    public void backList(String result_search) {
        Intent intent=new Intent(this,ListActivity.class);
        intent.putExtra("second", result_search);
        intent.putExtra("isSearch", true);
        setResult(RESULT_CODE, intent);
        finish();
    }
}
