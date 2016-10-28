package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.NoDoubleClickListener;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class SearchActivity extends FragmentActivity {


    @Bind(R.id.lv_search)
    ListView lvSearch;

    private List<Map<String, Object>> searchSet;
    private CommonToolbar mToolbar;
    private DiskLruCacheHelper DLCH;
    private Map<String, String> paramsMap;
    private Map<String, String> paramsMapNew = new HashMap<>();
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        try {
            DLCH = new DiskLruCacheHelper(SearchActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Constant.commitPra = new HashMap<>();
        getDataIntent();
        //parseSearchSet();
        mySearch();
    }


    public void getDataIntent() {
        Bundle  bundle = this.getIntent().getExtras();
        String searchSetStr = bundle.getString("searchSet");
        searchSet = JSON.parseObject(searchSetStr,
                new TypeReference<List<Map<String, Object>>>() {
                });
        String paramsStr = bundle.getString("paramsStr");

        paramsMap = JSON.parseObject(paramsStr,
                new TypeReference<Map<String, String>>() {
                });

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);

        mToolbar.setTitle("条件筛选");
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));

        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

            mToolbar.showRightImageButton();
            //右侧下拉按钮
            mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchCommit();
                }
            });

    }


    //展示搜索数据
    public void mySearch() {
        Log.e("TAG", "展示搜索数据" + searchSet);
        SearchAdapter addAdapter = new SearchAdapter();
        lvSearch.setAdapter(addAdapter);
    }

    private void searchCommit() {
        Log.e("TAG", "筛选提交参数列表" + Constant.commitPra.toString());
        paramsMapNew.putAll(paramsMap);
        paramsMapNew.putAll(Constant.commitPra);
        paramsMapNew.put("mainTableId","");
        paramsMapNew.put("mainPageId","");
        paramsMapNew.put("mainId","");
        paramsMapNew.put("defaultSearchSetSelect","0");
        paramsMapNew.remove("alterTime");
        requestSearch();


    }

    private static final String TAG = "SearchActivity";
    private void requestSearch() {
        String volleyUrl = Constant.sysUrl + Constant.requestListSet;

        //请求
        OkHttpUtils
                .post()
                .params(paramsMapNew)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(SearchActivity.this) {
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


    public class SearchAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private HashMap<Integer, String> hashMap = new HashMap<>();
        private Integer index = -1;
        private HashMap<Integer, String> hashMap1 = new HashMap<>();
        private Integer index1 = -1;
        private HashMap<Integer, String> hashMap2 = new HashMap<>();
        private Integer index2 = -1;


        public SearchAdapter() {
            mInflater = LayoutInflater.from(SearchActivity.this);
        }

        @Override
        public int getCount() {
            return searchSet.size();
        }

        @Override
        public Object getItem(int position) {
            return searchSet.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        @SuppressWarnings("unchecked")
        public View getView(final int position, View convertView, ViewGroup parent) {

            final int fieldType = Integer.valueOf(String.valueOf(searchSet.get(position).get("fieldType")));
            String fieldCnName = String.valueOf(searchSet.get(position).get("fieldCnName"));
            final String fieldAliasName = String.valueOf(searchSet.get(position).get("fieldAliasName"));

            convertView = mInflater.inflate(R.layout.activity_search_item, null);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_left_name);
            textView.setText(fieldCnName);
//开始判断fieldType
            if (fieldType == 2) {
                LinearLayout is_contain_layout = (LinearLayout) convertView.findViewById(R.id.is_contain_layout);
                Spinner is_contain_spinner = (Spinner) convertView.findViewById(R.id.is_contain_spinner);
                EditText is_contain_edit = (EditText) convertView.findViewById(R.id.is_contain_edit);
///拼接key，每项提交均有三个参数  1、参数类型  2、参数所属下拉ID  3、参数值

//1、 5项选择+输入框
                Constant.commitPra.put(fieldAliasName + "_" + fieldType + "_andOr", "0");


                is_contain_layout.setVisibility(View.VISIBLE);
                is_contain_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Constant.commitPra.put(fieldAliasName + "_strCond_pld", String.valueOf(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Constant.commitPra.put(fieldAliasName + "_strCond_pld", "0");
                    }
                });

                //为editText设置TextChangedListener，每次改变的值设置到hashMap
                //我们要拿到里面的值根据position拿值
                is_contain_edit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index = position;
                        }
                        return false;
                    }
                });

                is_contain_edit.clearFocus();
                if (index != -1 && index == position) {
                    // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                    is_contain_edit.requestFocus();
                }
                is_contain_edit.addTextChangedListener(new TextWatcher() {
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
                    }
                });

                //如果hashMap不为空，就设置的editText
                if (hashMap.get(position) != null) {
                    is_contain_edit.setText(hashMap.get(position));
                    Constant.commitPra.put(fieldAliasName + "_strVal_pld",hashMap.get(position));
                } else {
                    Constant.commitPra.put(fieldAliasName + "_strVal_pld", "");
                }
                is_contain_edit.setSelection(is_contain_edit.getText().length());

//日期时间类
            } else if (fieldType == 3) {
                LinearLayout linearLayoutDate = (LinearLayout) convertView.findViewById(R.id.is_date_layout);
                final TextView date1 = (TextView) convertView.findViewById(R.id.is_date1);
                final TextView date2 = (TextView) convertView.findViewById(R.id.is_date2);
                Constant.commitPra.put(fieldAliasName + "_" + fieldType + "_andOr", "0");

                if(Constant.commitPra.get(fieldAliasName + "_endDates_pld")==null){
                    Log.e("TAG", "检查点日期赋值为空e");
                    Constant.commitPra.put(fieldAliasName + "_endDates_pld", "");
                }
                if(Constant.commitPra.get(fieldAliasName + "_startDates_pld")==null){
                    Log.e("TAG", "检查点日期赋值为空s");
                    Constant.commitPra.put(fieldAliasName + "_startDates_pld", "");
                }


                int fieldRole = Integer.valueOf(String.valueOf(searchSet.get(position).get("fieldRole")));
                if (fieldRole == 14 || fieldRole == 26 || fieldRole == 28) {
//日期类
                    linearLayoutDate.setVisibility(View.VISIBLE);
                    date1.setHint("开始日期");
                    date2.setHint("结束日期");
                    Constant.commitPra.put(fieldAliasName + "_dateType", "yyyy-MM-dd");

                    date1.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {

                            final Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            //默认选中当前时间
                            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                            String monthNew=String.valueOf(month);
                                            String dayNew=String.valueOf(day);
                                            if (month + 1<10) {
                                                monthNew="0"+String.valueOf(month + 1);
                                            }
                                            if (day<10) {
                                                dayNew="0"+String.valueOf(day);
                                            }
                                            String dateTime = year + "-" + monthNew + "-" + dayNew;
                                            date1.setText(dateTime);
                                            Constant.commitPra.put(fieldAliasName + "_startDates_pld", dateTime);
                                            Log.e("TAG", "检查点日期赋值startDates："+dateTime);
                                        }
                                    }),
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH),
                                    true);
                            datePickerDialog.setVibrate(true);
                            datePickerDialog.setYearRange(1983, 2030);
                            datePickerDialog.setCloseOnSingleTapDay(false);
                            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                        }
                    });

                    date2.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {


                            final Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            //默认选中当前时间
                            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                             String monthNew=String.valueOf(month);
                                             String dayNew=String.valueOf(day);
                                            if (month + 1<10) {
                                                monthNew="0"+String.valueOf(month + 1);
                                            }
                                            if (day<10) {
                                                dayNew="0"+String.valueOf(day);
                                            }
                                            String dateTime = year + "-" + monthNew + "-" + dayNew;
                                            date2.setText(dateTime);
                                            Constant.commitPra.put(fieldAliasName + "_endDates_pld", dateTime);
                                            Log.e("TAG", "检查点日期赋值endDates："+dateTime);
                                        }
                                    }),
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH),
                                    true);
                            datePickerDialog.setVibrate(true);
                            datePickerDialog.setYearRange(1983, 2030);
                            datePickerDialog.setCloseOnSingleTapDay(false);
                            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                        }
                    });
//时间类
                } else {
                    Constant.commitPra.put(fieldAliasName + "_dateType", "yyyy-MM-dd" + " " + "HH:mm");

                    if(Constant.commitPra.get(fieldAliasName + "_endDates_pld")==null){
                        Constant.commitPra.put(fieldAliasName + "_endDates_pld", "");
                    }
                    if(Constant.commitPra.get(fieldAliasName + "_startDates_pld")==null){
                        Constant.commitPra.put(fieldAliasName + "_startDates_pld", "");
                    }

                    linearLayoutDate.setVisibility(View.VISIBLE);
                    date1.setHint("开始时间");
                    date2.setHint("结束时间");
                    date1.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            final Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            //默认选中当前时间
                            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                            String monthNew=String.valueOf(month);
                                            String dayNew=String.valueOf(day);
                                            if (month + 1<10) {
                                                monthNew="0"+String.valueOf(month + 1);
                                            }
                                            if (day<10) {
                                                dayNew="0"+String.valueOf(day);
                                            }
                                            String dateTime = year + "-" + monthNew + "-" + dayNew;
                                            date1.setText(dateTime);
                                            final String dateData =dateTime;
                                            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                                                    (new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
                                                            String hourNew=String.valueOf(hour);
                                                            String minuteNew=String.valueOf(minute);
                                                            if (hour<10) {
                                                                hourNew="0"+String.valueOf(hour);
                                                            }
                                                            if (minute<10) {
                                                                minuteNew="0"+String.valueOf(minute);
                                                            }
                                                            String dateData2 = dateData + " " + hourNew + ":" + minuteNew;
                                                            date1.setText(dateData2);
                                                            Constant.commitPra.put(fieldAliasName + "_startDates_pld", dateData2);
                                                        }
                                                    }), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                                    false, false);
                                            timePickerDialog.setVibrate(true);
                                            timePickerDialog.setCloseOnSingleTapMinute(false);
                                            timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                                        }
                                    }),
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH),
                                    true);
                            datePickerDialog.setVibrate(true);
                            datePickerDialog.setYearRange(1983, 2030);
                            datePickerDialog.setCloseOnSingleTapDay(false);
                            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                        }
                    });

                    date2.setOnClickListener(new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View v) {
                            final Calendar c = Calendar.getInstance();
                            c.setTimeInMillis(System.currentTimeMillis());
                            //默认选中当前时间
                            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((new DatePickerDialog.OnDateSetListener() {
                                        @Override
                                        public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

                                            String monthNew=String.valueOf(month);
                                            String dayNew=String.valueOf(day);
                                            if (month + 1<10) {
                                                monthNew="0"+String.valueOf(month + 1);
                                            }
                                            if (day<10) {
                                                dayNew="0"+String.valueOf(day);
                                            }
                                            final String dateTime = year + "-" + monthNew + "-" + dayNew;

                                            date2.setText(dateTime);
                                            final String dateTime2=year + "-" + monthNew + "-" + dayNew;

                                            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                                                    (new TimePickerDialog.OnTimeSetListener() {
                                                        @Override
                                                        public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
                                                            String hourNew=String.valueOf(hour);
                                                            String minuteNew=String.valueOf(minute);
                                                            if (hour<10) {
                                                                hourNew="0"+String.valueOf(hour);
                                                            }
                                                            if (minute<10) {
                                                                minuteNew="0"+String.valueOf(minute);
                                                            }
                                                            String dateData2 = dateTime2 + " " + hourNew + ":" + minuteNew;
                                                            date2.setText(dateData2);
                                                            Constant.commitPra.put(fieldAliasName + "_endDates_pld", dateData2);
                                                        }
                                                    }), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                                    false, false);
                                            timePickerDialog.setVibrate(true);
                                            timePickerDialog.setCloseOnSingleTapMinute(false);
                                            timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                                        }
                                    }),
                                    c.get(Calendar.YEAR),
                                    c.get(Calendar.MONTH),
                                    c.get(Calendar.DAY_OF_MONTH),
                                    true);
                            datePickerDialog.setVibrate(true);
                            datePickerDialog.setYearRange(1983, 2030);
                            datePickerDialog.setCloseOnSingleTapDay(false);
                            datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
                        }
                    });
                }
            } else if (fieldType == 1) {
                Constant.commitPra.put(fieldAliasName + "_"+fieldType+"_andOr", "0");
                Constant.commitPra.put(fieldAliasName + "_numCondOne_pld", "2");
                Constant.commitPra.put(fieldAliasName + "_numCondTwo_pld", "2");
                Constant.commitPra.put(fieldAliasName + "_numValOne_pld", "");
                Constant.commitPra.put(fieldAliasName + "_numValTwo_pld", "");
                RelativeLayout relativeLayoutNum = (RelativeLayout) convertView.findViewById(R.id.is_num_layout);
                Spinner is_num_spinner = (Spinner) convertView.findViewById(R.id.is_num_spinner);
                Spinner is_num_spinner2 = (Spinner) convertView.findViewById(R.id.is_num_spinner2);
                EditText is_num_edit = (EditText) convertView.findViewById(R.id.is_num_edit);
                EditText is_num_edit2 = (EditText) convertView.findViewById(R.id.is_num_edit2);
                relativeLayoutNum.setVisibility(View.VISIBLE);
                is_num_spinner.setSelection(0);
                is_num_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Constant.commitPra.put(fieldAliasName + "_numCondOne_pld", String.valueOf(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                is_num_spinner2.setSelection(0);
                is_num_spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Constant.commitPra.put(fieldAliasName + "_numCondTwo_pld", String.valueOf(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

//为editText设置TextChangedListener，每次改变的值设置到hashMap1
                //我们要拿到里面的值根据position拿值
                is_num_edit.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index1 = position;
                        }
                        return false;
                    }
                });

                is_num_edit.clearFocus();
                if (index1 != -1 && index1 == position) {
                    // 如果当前的行下标和点击事件中保存的index1一致，手动为EditText设置焦点。
                    is_num_edit.requestFocus();
                }
                is_num_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //将editText中改变的值设置的hashMap1中
                        hashMap1.put(position, s.toString());
                    }
                });

                //如果hashMap1不为空，就设置的editText
                if (hashMap1.get(position) != null) {
                    is_num_edit.setText(hashMap1.get(position));
                    Constant.commitPra.put(fieldAliasName + "_numValOne_pld", hashMap1.get(position));

                } else {
                    Constant.commitPra.put(fieldAliasName + "_numValOne_pld", "");
                }
                is_num_edit.setSelection(is_num_edit.getText().length());

//为editText设置TextChangedListener，每次改变的值设置到hashMap2
                //我们要拿到里面的值根据position拿值
                is_num_edit2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index2 = position;
                        }
                        return false;
                    }
                });

                is_num_edit2.clearFocus();
                if (index2 != -1 && index2 == position) {
                    // 如果当前的行下标和点击事件中保存的index2一致，手动为EditText设置焦点。
                    is_num_edit2.requestFocus();
                }
                is_num_edit2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //将editText中改变的值设置的hashMap2中
                        hashMap2.put(position, s.toString());
                    }
                });

                //如果hashMap2不为空，就设置的editText
                if (hashMap2.get(position) != null) {
                    is_num_edit2.setText(hashMap2.get(position));

                    Constant.commitPra.put(fieldAliasName + "_numValTwo_pld", hashMap2.get(position));
                } else {
                    Constant.commitPra.put(fieldAliasName + "_numValTwo_pld", "");
                }
                is_num_edit2.setSelection(is_num_edit2.getText().length());


//是否包含+多值类
            }
//            else if (fieldType == 4) {
//                LinearLayout is_dic_layout = (LinearLayout) convertView.findViewById(R.id.is_dic_layout);
//                Spinner is_dic_spinner = (Spinner) convertView.findViewById(R.id.is_dic_spinner);
//                MultiSpinner is_dic_adapter = (MultiSpinner) convertView.findViewById(R.id.is_dic_adapter);
//                is_dic_layout.setVisibility(View.VISIBLE);
//                //选择包含与否
//                Constant.commitPra.put(fieldAliasName + "_d_andOr", "0");
//                Constant.commitPra.put(fieldAliasName + "_d_dicCond_pld", "0");
//                Constant.commitPra.put(fieldAliasName + "strCond_dic", "");
//
//                is_dic_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        Constant.commitPra.put(fieldAliasName + "_d_dicCond_pld", String.valueOf(position));
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                    }
//                });
//
//                //选择数据
//                List<Map<String, Object>> dicList=new ArrayList<>();
//                try{
//                    dicList =
//                            (List<Map<String, Object>>) searchSet.get(position).get("dicList");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                //删除无用字典值数据
//
//                    String dicChildShow= String.valueOf(searchSet.get(position).get("dicChildShow"));
//                    List<Integer> dicChildShowList=new ArrayList<>();
//
//                if (dicChildShow!=null&&!dicChildShow.equals("")) {
//                //将字符串类型数组转换为int型集合
//                    String[] dicChildShowStrArr=dicChildShow.split(",");
//                    for (String aDicChildShowStrArr : dicChildShowStrArr) {
//                        dicChildShowList.add(Integer.parseInt(aDicChildShowStrArr));
//                    }
//                }
//                   //将字典列表遍历的过程中比较，并删除不存在的id项
//                if (dicList.size()>0) {
//                    for (int i=0;i<dicList.size();i++) {
//                       int dicIdTemp=Integer.valueOf(String.valueOf(dicList.get(i).get("DIC_ID")));
//                        if (!dicChildShowList.contains(dicIdTemp)) {
//                            dicList.remove(i);
//                        }
//                    }
//
//                    is_dic_adapter.setTitle(" ");
//                    is_dic_adapter.setPosition(fieldAliasName + "strCond_dic");
//                    ArrayList multiSpinnerList=new ArrayList();//数据装入目标
//                    for(int i=0;i<dicList.size();i++){//装入数据个数
//                        SimpleSpinnerOption option=new SimpleSpinnerOption();//每个数据分为2个，分别放入名称和标识
//                        if (dicList.get(i).get("DIC_NAME")!=null&&dicList.get(i).get("DIC_ID")!=null) {
//                            option.setName(String.valueOf(dicList.get(i).get("DIC_NAME")));
//                            option.setValue(Integer.valueOf(String.valueOf(dicList.get(i).get("DIC_ID"))));
//                            multiSpinnerList.add(option);
//                        }
//                    }
//                    is_dic_adapter.setDataList(multiSpinnerList);
//
//                }else{
//                    Toast.makeText(SearchActivity.this, "无数据", Toast.LENGTH_SHORT).show();
//                }
//            }
            return convertView;
        }
    }
    public void setStore(String result_search) {
        String paramsNext= JSON.toJSONString(paramsMapNew);
        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra("result_search", result_search);
        intent.putExtra("paramsNext", paramsNext);

        startActivity(intent);
        finish();
    }
}