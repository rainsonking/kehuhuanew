package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.NoDoubleClickListener;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddItemsActivity extends AppCompatActivity {

    @Bind(R.id.IV_back_list_item_tadd)
    ImageView IVBackListItemTadd;
    @Bind(R.id.tv_commit_item_tadd)
    ImageView tvCommitItemTadd;
    @Bind(R.id.tv_add_item_title)
    TextView tvAddItemTitle;
    @Bind(R.id.add_item_title)
    RelativeLayout addItemTitle;
    @Bind(R.id.lv_add_item)
    PullToRefreshListView lvAddItem;

    private String tableId,pageId,buttonName;
    private Map<String, String> paramsMap;
    private List<Map<String, Object>> fieldSet = new ArrayList<>();
    private String alterTime = "100";
    private int pos;
    private Map<Integer, String> idArrMap = new HashMap<>();
    private AddAdapter addAdapter;
    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";


    private Map<String, String> defaultValArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_items);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getIntentData();
        init();
        requestAdd();
    }

    private void init() {
        paramsMap = new HashMap<>();
        paramsMap.put(Constant.tableId, tableId);
        paramsMap.put(Constant.pageId, pageId);
        //paramsMap.put(Constant.timeName, Constant.dataTime);
        //String paramsStr = paramsMap.toString();


    }

    //获取参数
    private void getIntentData() {
        Intent intent = getIntent();
        String buttonSetItemStr = intent.getStringExtra("buttonSetItemStr");
        Map<String, Object> buttonSetItem = JSON.parseObject(buttonSetItemStr);
        buttonName = String.valueOf(buttonSetItem.get("buttonName"));
        pageId = String.valueOf(buttonSetItem.get("startTurnPage"));
        tableId = String.valueOf(buttonSetItem.get("tableId"));
        Constant.tempTableId = tableId;
        Constant.tempPageId = pageId;
    }

    //请求
    public void requestAdd() {
        String volleyUrl = Constant.sysUrl + Constant.requestAdd;
        Log.e("TAG", "网络获取添加dataUrl " + volleyUrl);
        Log.e("TAG", "网络获取添加table " + paramsMap.get("tableId"));
        Log.e("TAG", "网络获取添加page " + paramsMap.get("pageId"));
        Log.e("TAG", "网络获取添加" + Constant.timeName + " " + alterTime);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "网络获取添加按钮数据" + jsonData);
                        //DLCH.put(volleyUrl + paramsStr, jsonData);
                        setStore(jsonData);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(AddItemsActivity.this, volleyError);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paramsMap;
            }

            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }


//解析展示

    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        Log.e("TAG", "解析添加数据1");
        Map<String, Object> buttonSet = JSON.parseObject(jsonData);
        try{
//获取alterTime
            alterTime = String.valueOf(buttonSet.get("alterTime"));
//获取默认值
            defaultValArr = (Map<String, String>) buttonSet.get("defaultValArr");
//获取fieldSet
            Map<String, Object> pageSet = (Map<String, Object>) buttonSet.get("pageSet");
            fieldSet = (List<Map<String, Object>>) pageSet.get("fieldSet");
        }catch (Exception e){
            e.printStackTrace();
        }
//展示数据
        if (fieldSet != null && fieldSet.size() > 0) {
            addAdapter = new AddAdapter();
            lvAddItem.setAdapter(addAdapter);
        }

    }

    @OnClick({R.id.IV_back_list_item_tadd, R.id.tv_commit_item_tadd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.IV_back_list_item_tadd:

                this.finish();
                break;
            case R.id.tv_commit_item_tadd:
                commitAdd();
                break;
        }
    }

    private void commitAdd() {

        int numNull = 0;
        for (int i = 0; i < fieldSet.size(); i++) {
            int ifMust = Integer.valueOf(String.valueOf(fieldSet.get(i).get("ifMust")));
            String tempValue = String.valueOf(fieldSet.get(i).get("tempValue"));
            if (ifMust == 1 && (tempValue.equals("")||tempValue.equals("null"))) {
                numNull++;
                break;
            }
        }

        if (numNull == 0) {

            Map<String, Object> commitMap1 = new HashMap<>();
            //拼接网址
            for (int i = 0; i < fieldSet.size(); i++) {
                String key = String.valueOf(fieldSet.get(i).get("tempKey"));
                String value;
                if (fieldSet.get(i).get("tempValue") != null) {
                    value = String.valueOf(fieldSet.get(i).get("tempValue"));
                } else {
                    value = "";
                }
                commitMap1.put(key, value);

            }

            String pinJie1="";
            for(Map.Entry entry:commitMap1.entrySet()){
                pinJie1+=entry.getKey()+"="+entry.getValue()+"&";
            }
            Log.e("TAG", "pinJie1" + pinJie1);
            for (int i = 0; i < fieldSet.size(); i++) {
                if (fieldSet.get(i).get("idArr") != null) {
                    String[] ids = String.valueOf(fieldSet.get(i).get("idArr")).split(",");
                    String keyChild = String.valueOf(fieldSet.get(i).get("tempKeyIdArr"));
                    String pinJie2 = "";
                    for (String id : ids) {
                        pinJie2 += keyChild + "=" + id + "&";
                    }
                    pinJie1 += pinJie2;
                }
            }
            String chooseData2 = pinJie1.substring(0, pinJie1.length() - 1);
            String chooseData1 = "?" + Constant.tableId + "=" + tableId + "&" + Constant.pageId + "=" + pageId + "&";
            String commitUrl = Constant.sysUrl + Constant.commitAdd + chooseData1 + chooseData2;
            //请求网络提交
            Log.e("TAG", "pinJie1+2" + pinJie1);
            Log.e("TAG", "添加合成数据" + commitUrl);
            requestAddCommit(commitUrl);

            //判断是否提交成功，如果成功返回并刷新列表，如果失败，提示用户失败，不返回
        } else {
            Toast.makeText(AddItemsActivity.this, "必填字段不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestAddCommit(String volleyUrl) {
        StringRequest loginInterfaceData = new StringRequest(Request.Method.GET, volleyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonData) {//磁盘存储后转至处理
                        Log.e("TAG", "获得添加结果" + jsonData);
                        int isCommitSuccess = Integer.valueOf(jsonData);
                        if (isCommitSuccess == 1) {
                            toListActivity();
                        } else {
                            Toast.makeText(AddItemsActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleySingleton.onErrorResponseMessege(AddItemsActivity.this, volleyError);
            }
        }
        ) {
            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(
                loginInterfaceData);
    }
    public class AddAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private HashMap<Integer, String> hashMap = new HashMap<>();
        public AddAdapter() {
            Log.e("TAG", "添加适配器监测点1");
            mInflater = LayoutInflater.from(AddItemsActivity.this);
        }
        @Override
        public int getCount() {
            return fieldSet.size();
        }
        @Override
        public Object getItem(int position) {
            return fieldSet.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        private Integer index = -1;
        @Override
        @SuppressWarnings("unchecked")
        public View getView(final int position, View convertView, ViewGroup parent) {
            Log.e("TAG", "添加适配器监测点2");
            final int fieldRole = Integer.valueOf(String.valueOf(fieldSet.get(position).get("fieldRole")));
            int ifMust=0;
            if(fieldSet.get(position).get("ifMust")!=null){
                ifMust = Integer.valueOf(String.valueOf(fieldSet.get(position).get("ifMust")));
            }

            String fieldCnName = String.valueOf(fieldSet.get(position).get("fieldCnName"));

            convertView = mInflater.inflate(R.layout.activity_add_item, null);
//左侧名称

            TextView textView = (TextView) convertView.findViewById(R.id.add_item_name);
            textView.setText(fieldCnName);

//必填标志
            TextView textViewIfMust = (TextView) convertView.findViewById(R.id.tv_if_must);
            if (ifMust == 1) {
                textViewIfMust.setVisibility(View.VISIBLE);
            }
//初始化编辑框
            EditText add_edit_text = (EditText) convertView.findViewById(R.id.add_edit_text);
//日期选、时间、内部对象多值选择器
            final TextView addGeneral = (TextView) convertView.findViewById(R.id.add_general);
//字典选择器单值选择项
            Spinner add_spinner = (Spinner) convertView.findViewById(R.id.add_spinner);
//默认值选择
            String fieldAliasList="";
            if (fieldSet.get(position).get("fieldId")!=null) {
                fieldAliasList ="au_"+tableId + "_" + pageId + "_" +fieldSet.get(position).get("fieldId");
            }
            String defaultName="";
            if (defaultValArr!=null&&defaultValArr.get(fieldAliasList)!=null) {
                defaultName = defaultValArr.get(fieldAliasList);
            }
//1、普通编辑框
            if (fieldRole == 1 ||fieldRole == -1 || fieldRole == 2 || fieldRole == 10 ||
                    fieldRole == 3 || fieldRole == 4 || fieldRole == 5 ||
                    fieldRole == 6 || fieldRole == 7 || fieldRole == 11 ||
                    fieldRole == 12 || fieldRole == 13 ||
                    fieldRole == 9 || fieldRole == 24 || fieldRole == 29) {
                if (fieldRole == 2) {
                    fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId")+"_" + "editor");
                }else{
                    fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                }
                fieldSet.get(position).put("tempValue", defaultName);
                add_edit_text.setVisibility(View.VISIBLE);
                //为editText设置TextChangedListener，每次改变的值设置到hashMap
                //我们要拿到里面的值根据position拿值
                add_edit_text.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            index = position;
                        }
                        return false;
                    }
                });
                add_edit_text.clearFocus();
                if (index != -1 && index == position) {
                    // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                    add_edit_text.requestFocus();
                }
                add_edit_text.addTextChangedListener(new TextWatcher() {
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
                        fieldSet.get(position).put("tempValue", s.toString());
                    }
                });
                //如果hashMap不为空，就设置的editText
                if (hashMap.get(position) != null) {
                    add_edit_text.setText(hashMap.get(position));
                } else {
                    add_edit_text.setText(defaultName);
//                    fieldSet.get(position).put("tempValue", defaultName);
                }
                add_edit_text.setSelection(add_edit_text.getText().length());
//2、单值选择项&星期
                Log.e("TAG", "添加适配器监测点5");
            } else if (fieldRole == 16 || fieldRole == 23) {

                fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                //fieldSet.get(position).put("tempValue","0");
                add_spinner.setVisibility(View.VISIBLE);
                final List<Map<String, Object>> dicList =
                        (List<Map<String, Object>>) fieldSet.get(position).get("dicList");
                SimpleAdapter simpleAdapter = new SimpleAdapter(AddItemsActivity.this,
                        dicList, R.layout.spinner_item,
                        new String[]{"DIC_NAME"}, new int[]{R.id.spinner_item_name});
                int byId = 0;
                if (!defaultName.equals("")) {
                    int defaultNum = Integer.valueOf(defaultName);
                    for (int i = 0; i < dicList.size(); i++) {
                        if (dicList.get(i).get("DIC_ID") == defaultNum) {
                            byId = i;
                            break;
                        }
                    }
                }
                add_spinner.setAdapter(simpleAdapter);
                add_spinner.setSelection(byId, true);
                fieldSet.get(position).put("tempValue", defaultName);
                add_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int positionDic, long id) {
                        String DIC_ID = String.valueOf(dicList.get(positionDic).get("DIC_ID"));
                        fieldSet.get(position).put("tempValue", DIC_ID);
                        Log.e("TAG","DIC_ID"+DIC_ID+" position "+position);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
//3、日期
                Log.e("TAG", "添加适配器监测点6");
            } else if (fieldRole == 14 || fieldRole == 26 || fieldRole == 28) {
                fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                fieldSet.get(position).put("tempValue", defaultName);
                Log.e("TAG", "添加适配器监测点7默认日期"+defaultName);
                addGeneral.setHint(defaultName);
                addGeneral.setVisibility(View.VISIBLE);
                addGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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

                                        addGeneral.setText(dateTime);
                                        fieldSet.get(position).put("tempValue",dateTime);
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
//4、时间

            } else if (fieldRole == 15) {
                fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                addGeneral.setHint(defaultName);
                fieldSet.get(position).put("tempValue", defaultName);
                addGeneral.setVisibility(View.VISIBLE);
                addGeneral.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        final Calendar c = Calendar.getInstance();
                        c.setTimeInMillis(System.currentTimeMillis());
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
                                        String dateData = hourNew + ":" + minuteNew;
                                        addGeneral.setText(dateData);
                                        fieldSet.get(position).put("tempValue",dateData);
                                    }
                                }), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                false, false);
                        timePickerDialog.setVibrate(true);
                        timePickerDialog.setCloseOnSingleTapMinute(false);
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
                    }
                });
//5、内部对象单值
                Log.e("TAG", "添加适配器监测点8");
            } else if (fieldRole == 20||fieldRole == 22) {
                //获得chooseType
                Log.e("TAG", "pos被赋予的position"+position);
                fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                addGeneral.setHint("请选择");
                addGeneral.setVisibility(View.VISIBLE);
                if (fieldSet.get(position).get("tempValueName") != null) {
                    addGeneral.setText(String.valueOf(fieldSet.get(position).get("tempValueName")));
                }
                final String multiValueData = JSON.toJSONString(fieldSet.get(position));
                addGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        String idArrs = "";
                        if (fieldSet.get(position).get("tempValue")!= null) {
                            idArrs = String.valueOf(fieldSet.get(position).get("tempValue"));
                        }
                        toMultiValueActivity("false", idArrs, multiValueData);
                    }
                });
                Log.e("TAG", "添加适配器监测点9");
            }
//6、内部对象多值
            else if (fieldRole == 21) {
           fieldSet.get(position).put("tempKey", "t0_au_" + fieldSet.get(position).get("relationTableId") + "_" +
                        fieldSet.get(position).get("showFieldArr") +
                        "_" + fieldSet.get(position).get("fieldId") + "_dz");
                String defaultName1 = "请选择";
                String idsPre1 = "";
                addGeneral.setHint(defaultName1);
                addGeneral.setVisibility(View.VISIBLE);
                if (fieldSet.get(position).get("tempValueName") != null) {
                    addGeneral.setText(String.valueOf(fieldSet.get(position).get("tempValueName")));
                }
                final String multiValueData = JSON.toJSONString(fieldSet.get(position));
                addGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pos = position;
                        String idArrs = "";
                        if (idArrMap.get(position) != null) {
                            idArrs = idArrMap.get(position);
                        }
                        toMultiValueActivity("true", idArrs, multiValueData);
                    }
                });
            } else {
                addGeneral.setVisibility(View.VISIBLE);
                fieldSet.get(position).put("tempKey", "t0_au_" + tableId + "_" + pageId + "_" + fieldSet.get(position).get("fieldId"));
                fieldSet.get(position).put("tempValue", defaultName);
                addGeneral.setHint(defaultName);
            }
            return convertView;
        }
        /**
         * 跳转至子菜单列表
         */

        public void toMultiValueActivity(String isMulti, String idArrs, String multiValueData) {
            Intent intent = new Intent();
            Log.e("TAG", "内部对象多值检查点3");
            intent.setClass(AddItemsActivity.this, MultiValueActivity.class);
            Constant.jumpNum=1;
            Log.e("TAG", "内部对象多值检查点4");
            intent.putExtra("multiValueData", multiValueData);
            intent.putExtra("idArrs", idArrs);
            intent.putExtra("isMulti", isMulti);
            Log.e("TAG", "内部对象多值检查点5" + idArrs);
            startActivityForResult(intent, 2);
        }
    }

    /**
     * 跳转至子菜单列表
     */
    public void toListActivity() {
        Toast.makeText(AddItemsActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
        this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == requestCode) {
            if (2 == resultCode) {
                Constant.jumpNum=0;
                Bundle bundle = data.getBundleExtra("bundle");
                String strFromAct2 = bundle.getString("myValue");
                //txtFromAct2.setText(strFromAct2);
                Map<String, Object> dataMap = JSON.parseObject(strFromAct2,
                        new TypeReference<Map<String, Object>>() {
                        });

                assert dataMap != null;
                String num = String.valueOf(dataMap.get("num"));
                String ids = String.valueOf(dataMap.get("ids"));
                String names = String.valueOf(dataMap.get("names"));
                String isMulti = String.valueOf(dataMap.get("isMulti"));

                if (isMulti.equals("true")) {
                    fieldSet.get(pos).put("tempValueName", names);
                    fieldSet.get(pos).put("tempValue", num);
                    //num肯定大于0
                    fieldSet.get(pos).put("tempKeyIdArr", "t1_au_" + fieldSet.get(pos).get("relationTableId") + "_" +
                            fieldSet.get(pos).get("showFieldArr") +
                            "_" + fieldSet.get(pos).get("pageDialog"));
                    fieldSet.get(pos).put("idArr", ids);
                    Log.e("TAG","多选idArr推进结果"+ids);
                    idArrMap.put(pos, ids);
                    addAdapter.notifyDataSetChanged();
                }else {

                    fieldSet.get(pos).put("tempValue", ids);
                    fieldSet.get(pos).put("tempValueName", names);
                    Log.e("TAG","pos值"+pos);
                    addAdapter.notifyDataSetChanged();


                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

