package com.kwsoft.kehuhua.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.kwsoft.kehuhua.adcustom.AddItemsActivity;
import com.kwsoft.kehuhua.adcustom.AddTemplateDataActivity;
import com.kwsoft.kehuhua.adcustom.MultiValueActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.RowsAddActivity;
import com.kwsoft.kehuhua.adcustom.RowsEditActivity;
import com.kwsoft.kehuhua.adcustom.TreeViewActivity;
import com.kwsoft.kehuhua.adcustom.UnlimitedAddActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.NoDoubleClickListener;
import com.kwsoft.kehuhua.wechatPicture.SelectPictureActivity;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwsoft.kehuhua.config.Constant.itemValue;

/**
 * Created by Administrator on 2016/6/7 0007.
 *
 */
public class Add_EditAdapter extends BaseAdapter {
    private LayoutInflater inflater = null;
    private List<Map<String, Object>> fieldSet;
    private Context context;
    private static final String DATEPICKER_TAG = "datepicker";
    private static final String TIMEPICKER_TAG = "timepicker";
    private HashMap<Integer, String> hashMap;

    {
        hashMap = new HashMap<>();
    }

    private Activity mActivity;
    private Map<String, String> paramsMap = new HashMap<>();
    private String tableId, dataId, pageId;
    private android.support.v4.app.FragmentManager fm;


    public Add_EditAdapter(Context context, List<Map<String, Object>> fieldSet,
                           Map<String, String> paramsMap) {
        this.mActivity = (Activity) context;
        Log.e("TAG", "适配器初始化开始");
        this.mActivity = (Activity) context;
        if (mActivity instanceof AddItemsActivity) {
            Constant.jumpNum = 1;
            fm = ((AddItemsActivity) context).getSupportFragmentManager();
        } else if (mActivity instanceof RowsEditActivity) {
            Constant.jumpNum = 2;
            fm = ((RowsEditActivity) context).getSupportFragmentManager();
        } else if (mActivity instanceof RowsAddActivity) {
            Constant.jumpNum = 3;
            fm = ((RowsAddActivity) context).getSupportFragmentManager();
        } else if (mActivity instanceof AddTemplateDataActivity) {
            Constant.jumpNum1 = 4;
            fm = ((AddTemplateDataActivity) context).getSupportFragmentManager();
        }


        this.paramsMap = paramsMap;
        this.context = context;
        this.fieldSet = fieldSet;
        inflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        tableId = paramsMap.get(Constant.tableId);
        pageId = paramsMap.get(Constant.pageId);
        dataId = Constant.mainIdValue;
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

        final int fieldRole = Integer.valueOf(String.valueOf(fieldSet.get(position).get("fieldRole")));
        Log.e("TAG", "所在位置 position：" + position + "  fieldRole:" + fieldRole);
//初始化必填字段标志
        int ifMust = 0;
        if (fieldSet.get(position).get("ifMust") != null) {
            ifMust = Integer.valueOf(String.valueOf(fieldSet.get(position).get("ifMust")));
        }
//初始化是否可修改标志
        int ifUpdate = 0;
        if (fieldSet.get(position).get("ifUpdate") != null) {
            ifUpdate = Integer.valueOf(String.valueOf(fieldSet.get(position).get("ifUpdate")));
        }

        convertView = inflater.inflate(R.layout.activity_add_item, null);
//初始化左侧名称
        TextView textView = (TextView) convertView.findViewById(R.id.add_item_name);
        String fieldCnName = String.valueOf(fieldSet.get(position).get("fieldCnName"));
        if (fieldCnName.equals("") || fieldCnName.equals("null")) {
            fieldCnName = "";
        }
        textView.setText(fieldCnName);

//初始化必填标志
        TextView textViewIfMust = (TextView) convertView.findViewById(R.id.tv_if_must);
        if (ifMust == 1) {
            textViewIfMust.setVisibility(View.VISIBLE);
        }
        RelativeLayout list_item_cover = (RelativeLayout) convertView.findViewById(R.id.list_item_cover);
        RelativeLayout list_item_cover2 = (RelativeLayout) convertView.findViewById(R.id.list_item_cover2);
//设置控件不可点击
        if (ifUpdate == 0) {
            list_item_cover.setVisibility(View.VISIBLE);
            list_item_cover2.setVisibility(View.VISIBLE);
//            list_item_cover.setBackgroundColor(context.getResources().getColor(R.color.no_edit_tv));
//            list_item_cover.setClickable(true);
        }
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
//初始化item项参数，供传递到多选和树形选择用
        final Map<String, Object> childPra = fieldSet.get(position);

/**
 * 默认值选取，取key为：true_defaultShowValName
 *
 */

//默认值选择,不包含20、21的情况，如果存在赋值，不存在为空串
        String defaultName;
        Object itemObj = fieldSet.get(position).get(Constant.itemName);
        if (itemObj != null) {
            defaultName = String.valueOf(itemObj);
        } else {
            defaultName = "";
        }


//chooseType判断
        int chooseType = -1;
        if (fieldSet.get(position).get("chooseType") != null) {
            chooseType = Integer.valueOf(String.valueOf(fieldSet.get(position).get("chooseType")));
        }
        final int finalChooseType = chooseType;

//jsWhereStr判断   "jsWhereStr": "#{19:180} == 6"，通过判断来决定校区选择或者考点选择模块等等是否显示
        boolean isShow = isShow(position, textView, textViewIfMust);
//1、普通编辑框

        if (fieldRole == -1 || fieldRole == 1 || fieldRole == 2 || fieldRole == 10 ||
                fieldRole == 3 || fieldRole == 4 || fieldRole == 5 ||
                fieldRole == 6 || fieldRole == 7 || fieldRole == 11 ||
                fieldRole == 12 || fieldRole == 13 || fieldRole == 8 ||
                fieldRole == 9 || fieldRole == 24 || fieldRole == 29) {
//            add_edit_text.setHint("请填写");

            if (fieldRole == 24) {//密码格式
                add_edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            if (fieldRole == 6) {//手机号
                add_edit_text.setInputType(InputType.TYPE_CLASS_PHONE);
                InputFilter[] filters = {new InputFilter.LengthFilter(11)};
                add_edit_text.setFilters(filters);
                // add_edit_text.setError("请输入正确的手机号");
            }
            if (fieldRole == 4) {//邮箱
                add_edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                add_edit_text.setError("请输入正确的邮箱");
            }
            if (fieldRole == 13) {//金额，带小数点
                add_edit_text.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            if (fieldRole == 5) {//网址
                add_edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                add_edit_text.setError("请输入正确的网址");
            }
            if (fieldRole == 11) {//网址
                add_edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);
                add_edit_text.setError("请输入正确的拼音");
            }
            if (fieldRole == 2) {//富文本
                add_edit_text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
            if (fieldRole == 3) {//身份证号
                add_edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);
                //add_edit_text.
            }
            if (fieldRole == 12) {//身份证号
                add_edit_text.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }
            if (fieldRole == 8) {//订单编号
                Constant.tmpFieldId = String.valueOf(fieldSet.get(position).get("tmpFieldId"));
                Log.e("TAG", "Constant.tmpFieldId " + Constant.tmpFieldId);
                Log.e("TAG", "Constant.tmpFieldId " + Constant.tmpFieldId);
                Log.e("TAG", "Constant.tmpFieldId " + Constant.tmpFieldId);
            }
            if (isShow) {
                add_edit_text.setVisibility(View.VISIBLE);
            }
            if (!defaultName.equals("")) {
                add_edit_text.setText(defaultName);

            }
            fieldSet.get(position).put(itemValue, defaultName);
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
                    fieldSet.get(position).put(Constant.itemName, s.toString());
                    fieldSet.get(position).put(itemValue, s.toString());
                }
            });

            add_edit_text.setSelection(add_edit_text.getText().length());


//2、单值选择项&星期
        } else if (fieldRole == 16 || fieldRole == 23) {
            Log.e("TAG", "字典适配开始 ");
            Log.e("TAG", "字典适配开始 ");
            if (isShow) {
                add_spinner.setVisibility(View.VISIBLE);
            }
            //删除无用字典值数据
            List<Map<String, Object>> dicList = getNewDicList(position);
            //设置默认选中值以及byId的位置
            int byId = -1;//
            int dicDefaultSelectInt;
            String dicDefaultSelect;
            //有值的情况
            if (!defaultName.equals("")) {
                byId = getById(dicList, byId, Integer.valueOf(defaultName));
                //无值、有默认值的情况
            } else if (!String.valueOf(fieldSet.get(position).get("dicDefaultSelect")).equals("")) {
                dicDefaultSelect = String.valueOf(fieldSet.get(position).get("dicDefaultSelect"));
                //获得默认选中值
                dicDefaultSelectInt = Integer.valueOf(dicDefaultSelect);
                //如果有默认选中值，将byId确定
                byId = getById(dicList, byId, dicDefaultSelectInt);
            } else {
                byId = 0;
            }
            //字典按钮点击选择Arrays.asList("One", "Two", "Three", "Four", "Five")
            List<String> dataset = new LinkedList<>();

            for (int i = 0; i < dicList.size(); i++) {
                dataset.add(String.valueOf(dicList.get(i).get("DIC_NAME")));
            }
            add_spinner.attachDataSource(dataset);
            add_spinner.setSelectedIndex(byId);
            add_spinner.setTextColor(Color.BLACK);
           // add_spinner.invalidateDrawable(context.getResources().getDrawable(R.mipmap.ic_launcher));
            fieldSet.get(position).put(itemValue, String.valueOf(dicList.get(byId).get("DIC_ID")));
            fieldSet.get(position).put(Constant.itemName, String.valueOf(dicList.get(byId).get("DIC_ID")));

            final List<Map<String, Object>> finalDicList = dicList;
            final String oldDicId = String.valueOf(fieldSet.get(position).get("true_defaultShowVal"));
            Log.e("TAG", "oldDicId " + oldDicId);
            add_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionDic, long id) {
                    String DIC_ID = String.valueOf(finalDicList.get(positionDic).get("DIC_ID"));

                    fieldSet.get(position).put(itemValue, DIC_ID);
                    fieldSet.get(position).put(Constant.itemName, DIC_ID);

                    if (!oldDicId.equals(DIC_ID)) {
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            Log.e("TAG", "字典适配完毕 ");
//3、日期

        } else if (fieldRole == 14 || fieldRole == 26 || fieldRole == 28) {
            if (isShow) {
                addGeneral.setVisibility(View.VISIBLE);
            }
            //将long型时间改为约定的时间格式
            String dateType = "yyyy-MM-dd HH:mm:ss";
            //判断 如果defaultName是格林尼治时间字符串

            //转换long为日期
            //如果是则转换为时间类型字符串
            if (defaultName.matches("[0-9]+")) {
                long defaultNameLong = Long.valueOf(defaultName);
                //转换long为日期
                Log.e("TAG", "defaultNameLong " + defaultNameLong);
                Date date = new Date(defaultNameLong);
                defaultName = new SimpleDateFormat(dateType).format(date);
            }
            addGeneral.setText(defaultName);
            if (fieldSet.get(position).get(itemValue) == null) {
                fieldSet.get(position).put(itemValue, defaultName);
                fieldSet.get(position).put(Constant.itemName, defaultName);
            }


            final String finalDateType = dateType;
            addGeneral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    //默认选中当前时间
                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                                    //获得年月日
                                    int monthNew = month + 1;
                                    final String dateTime2 = year + "-" + monthNew + "-" + day;
                                    TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                                            (new TimePickerDialog.OnTimeSetListener() {
                                                @Override
                                                public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
                                                    //获得时分并与日期加在一起，后缀加上秒数
                                                    String sDt = dateTime2 + " " + hour + ":" + minute + ":00";
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                    try {
                                                        Date dt2 = sdf.parse(sDt);
                                                        SimpleDateFormat sdf2 = new SimpleDateFormat(finalDateType);
                                                        String dateStr = sdf2.format(dt2);
                                                        addGeneral.setText(dateStr);
                                                        fieldSet.get(position).put(itemValue, dateStr);
                                                        fieldSet.get(position).put(Constant.itemName, dateStr);
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                                            false, false);
                                    timePickerDialog.setVibrate(true);
                                    timePickerDialog.setCloseOnSingleTapMinute(false);
                                    timePickerDialog.show(fm, TIMEPICKER_TAG);
                                }
                            }),
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH),
                            true);
                    datePickerDialog.setVibrate(true);
                    datePickerDialog.setYearRange(1983, 2030);
                    datePickerDialog.setCloseOnSingleTapDay(false);
                    datePickerDialog.show(fm, DATEPICKER_TAG);
                }
            });

//4、时间

        } else if (fieldRole == 15) {
            if (isShow) {
                addGeneral.setVisibility(View.VISIBLE);

            }
//将long型时间改为约定的时间格式
            String dateType = "HH:mm:ss";
            Log.e("TAG", "defaultName " + defaultName);
//判断是否为纯数字

            //存储defaultName


            //如果是则转换为时间类型字符串
            if (defaultName.matches("[0-9]+")) {
                long defaultNameLong = Long.valueOf(defaultName);
                //转换long为日期
                Log.e("TAG", "defaultNameLong " + defaultNameLong);
                Date date = new Date(defaultNameLong);
                defaultName = new SimpleDateFormat(dateType).format(date);
            }
            addGeneral.setText(defaultName);
            if (fieldSet.get(position).get(itemValue) == null) {
                fieldSet.get(position).put(itemValue, defaultName);
                fieldSet.get(position).put(Constant.itemName, defaultName);
            }

            final String finalDateType = dateType;
            addGeneral.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(System.currentTimeMillis());
                    //默认选中当前时间
                    TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                            (new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(RadialPickerLayout view, int hour, int minute) {
                                    String sDt = hour + ":" + minute + ":00";
                                    SimpleDateFormat sdf = new SimpleDateFormat(finalDateType);
                                    try {
                                        Date dt2 = sdf.parse(sDt);
                                        SimpleDateFormat sdf2 = new SimpleDateFormat(finalDateType);
                                        String dateStr = sdf2.format(dt2);
                                        addGeneral.setText(dateStr);
                                        fieldSet.get(position).put(itemValue, dateStr);
                                        fieldSet.get(position).put(Constant.itemName, dateStr);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                            false, false);
                    timePickerDialog.setVibrate(true);
                    timePickerDialog.setCloseOnSingleTapMinute(false);
                    timePickerDialog.show(fm, TIMEPICKER_TAG);

                }//onclick完毕
            });
        } else if (fieldRole == 19) {
/**
 *
 * 添加作业附件
 *
 *
 */
            if (isShow && ifUpdate == 1) {
                image_upload_layout.setVisibility(View.VISIBLE);
                image_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mActivity, SelectPictureActivity.class);
                        intent.putExtra("position", position + "");
                        mActivity.startActivityForResult(intent, 2);
                    }
                });

                //判断选择了x张图片

                String numPic = String.valueOf(fieldSet.get(position).get(itemValue));

                if (!numPic.equals("null") && !numPic.equals("")) {

                    String[] numPicArray = numPic.split(",");
                    int picLength = numPicArray.length;
                    String picContent = "已选" + picLength + "张图片";
                    picNumber.setText(picContent);
                } else {
                    picNumber.setText("尚无附件");
                }


            } else {
                addGeneral.setVisibility(View.VISIBLE);
                addGeneral.setText(defaultName);
            }
//5、内部对象单值
        } else if (fieldRole == 20 || fieldRole == 22) {
            if (isShow) {
                addGeneral.setVisibility(View.VISIBLE);
//                addGeneral.setHint("请选择");
            } else {
                addGeneral.setVisibility(View.GONE);
            }

            String itemName;
            if (fieldSet.get(position).get(Constant.itemName) != null) {
                itemName = String.valueOf(fieldSet.get(position).get(Constant.itemName));
            } else {
                itemName = "";
            }

            if (!itemName.equals("")) {
                addGeneral.setText(itemName);
            }
            final String finalFieldCnName1 = fieldCnName;
            addGeneral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Map<String, String>> needFilterList = getNeedFilter(position);
                    String idArrs = "";
                    try {
                        idArrs = String.valueOf(fieldSet.get(position).get(itemValue));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (finalChooseType != 1) {
                        toMultiValueActivity(finalFieldCnName1, "false", idArrs, childPra, needFilterList, position);


                    } else {
                        Log.e("TAG", "跳转到下拉树");
                        toTreeView(finalFieldCnName1, "false", idArrs, childPra, needFilterList, position);
                    }

                }
            });
        }
//6、内部对象多值
        else if (fieldRole == 21) {

            String addStyle = String.valueOf(fieldSet.get(position).get("addStyle"));
            if (addStyle.equals("1") || addStyle.equals("2")) {

                if (isShow) {
                    addGeneral.setVisibility(View.VISIBLE);
//                    addGeneral.setHint("请选择");
                }
                String itemName;
                if (fieldSet.get(position).get(Constant.itemName) != null) {
                    itemName = String.valueOf(fieldSet.get(position).get(Constant.itemName));
                } else {
                    itemName = "";
                }

                if (!itemName.equals("")) {
                    addGeneral.setText(itemName);
                }

                final String finalFieldCnName = fieldCnName;
                addGeneral.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<Map<String, String>> needFilterList = getNeedFilter(position);
                        String idArrs = "";
                        if (fieldSet.get(position).get(itemValue) != null) {
                            idArrs = String.valueOf(fieldSet.get(position).get(itemValue));
                        }
                        if (finalChooseType != 1) {
                            toMultiValueActivity(finalFieldCnName, "true", idArrs, childPra, needFilterList, position);
                        } else {//跳转到下拉树选择
                            toTreeView(finalFieldCnName, "true", idArrs, childPra, needFilterList, position);
                        }
                    }
                });
            } else if (addStyle.equals("3")) {
                if (isShow) {
                    add_unlimited.setVisibility(View.VISIBLE);
                }

                String unlimitedAddValue = "";
                if (fieldSet.get(position).get("tempListValue") != null) {
                    unlimitedAddValue = String.valueOf(fieldSet.get(position).get("tempListValue"));

                }
/**
 * 无限添加按钮
 */
////获取参数
                final String showFieldArr = String.valueOf(fieldSet.get(position).get("showFieldArr"));
                final String fieldSetStr = JSON.toJSONString(fieldSet);
////异步请求模板数据
                final String finalUnlimitedAddValue = unlimitedAddValue;
                final String finalFieldCnName2 = fieldCnName;
                add_unlimited.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        requestData(showFieldArr);
                        try {
                            Intent intent = new Intent();
                            intent.setClass(context, UnlimitedAddActivity.class);
                            intent.putExtra("fieldSetStr", fieldSetStr);
                            intent.putExtra("showFieldArr", showFieldArr);
                            intent.putExtra("viewName", finalFieldCnName2);
                            intent.putExtra("unlimitedAddValue", finalUnlimitedAddValue);
                            intent.putExtra("position", position + "");
                            intent.putExtra("tableId", tableId);
                            intent.putExtra("relationTableId", String.valueOf(fieldSet.get(position).get("relationTableId")));
                            intent.putExtra("pageId", pageId);
                            mActivity.startActivityForResult(intent, 2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } else {
            if (isShow) {
                addGeneral.setVisibility(View.VISIBLE);
            }

            if (!defaultName.equals("")) {
                addGeneral.setHint(defaultName);
                fieldSet.get(position).put(itemValue, defaultName);
            }


        }

        return convertView;

    }

    private boolean isShow(int position, TextView textView, TextView textViewIfMust) {
        boolean isShow = true;
        if (fieldSet.get(position).get("jsWhereStr") != null) {
            String jsWhereStr = String.valueOf(fieldSet.get(position).get("jsWhereStr")).replace(" ", "").replace("==", "");
            String jsWhereStrFieldId = jsWhereStr.substring(jsWhereStr.indexOf(":") + 1, jsWhereStr.indexOf("}"));
            String jsWhereStrValue = jsWhereStr.substring(jsWhereStr.indexOf("}") + 1, jsWhereStr.length());
            int fieldIdNeed = Integer.valueOf(jsWhereStrFieldId);
            int dicNeed = Integer.valueOf(jsWhereStrValue);
            int countNum = 0;
            for (int i = 0; i < fieldSet.size(); i++) {
                countNum++;
                int fieldIdTemp = Integer.valueOf(String.valueOf(fieldSet.get(i).get("fieldId")));
                if (fieldIdNeed == fieldIdTemp) {
                    //获得字典值
                    if (fieldSet.get(i).get("true_defaultShowVal") != null) {
                        int dicTemp = Integer.valueOf(String.valueOf(fieldSet.get(i).get("true_defaultShowVal")));
                        if (dicNeed == dicTemp) {
                            break;
                        }
                    }
                }
            }
            if (countNum == fieldSet.size()) {
                isShow = false;
                textView.setVisibility(View.GONE);
                textViewIfMust.setVisibility(View.GONE);
            }
        }
        return isShow;
    }

    /**
     */
    private int getById(List<Map<String, Object>> dicList, int byId, int dicDefaultSelectInt) {
        //纠正显示值错误
        if (dicList.size() == 1) {
            byId = 0;

        } else {
            //正常循环
            for (int i = 0; i < dicList.size(); i++) {
                if (Integer.parseInt(String.valueOf(dicList.get(i).get("DIC_ID"))) == dicDefaultSelectInt) {
                    byId = i;
                    break;
                }
            }
        }
        return byId;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private List<Map<String, Object>> getNewDicList(int position) {
        List<Map<String, Object>> dicList =
                (List<Map<String, Object>>) fieldSet.get(position).get("dicList");


        String dicChildShow = String.valueOf(fieldSet.get(position).get("dicChildShow"));
        List<Integer> dicChildShowList = new ArrayList<>();
        if (dicChildShow != null && !dicChildShow.equals("")) {
            //将字符串类型数组转换为int型集合
            String[] dicChildShowStrArr = dicChildShow.split(",");
            for (String aDicChildShowStrArr : dicChildShowStrArr) {
                dicChildShowList.add(Integer.parseInt(aDicChildShowStrArr));
            }
            //将字典列表遍历的过程中比较，并删除不存在的id项
            List<Map<String, Object>> dicListNew = new ArrayList<>();
            if (dicList.size() > 0) {
                for (int i = 0; i < dicList.size(); i++) {
                    int dicIdTemp = Integer.valueOf(String.valueOf(dicList.get(i).get("DIC_ID")));
                    if (dicChildShowList.contains(dicIdTemp)) {
                        dicListNew.add(dicList.get(i));
                    }
                }
            }
            dicList = dicListNew;
        }
        return dicList;
    }


    //获得needFilter
    private List<Map<String, String>> getNeedFilter(int position) {
        List<Map<String, String>> needFilterList = new ArrayList<>();
        try {
            //当前表position位置的needFilter
            String isSession = String.valueOf(fieldSet.get(position).get("needFilter"));
            //判断是否为无限添加的item进入选项
            if (mActivity instanceof AddTemplateDataActivity && Constant.fieldSetStr != null) {
                List<Map<String, Object>> fieldSet2 = JSON.parseObject(Constant.fieldSetStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                needFilterList = getIdvalue(fieldSet2, isSession, position);
            } else {
                //查找目标数据源的fieldId
                needFilterList = getIdvalue(fieldSet, isSession, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return needFilterList;
    }

    private List<Map<String, String>> getIdvalue(List<Map<String, Object>> fieldSetTemp, String isSession, int position) {

        List<Map<String, String>> needFilterList = new ArrayList<>();
        if (isSession.contains(":") && !isSession.contains("SESSION")) {
            String[] needFilterArray = isSession.split(",");
            for (String aNeedFilterArray : needFilterArray) {
                Map<String, String> needFilterMap = new HashMap<>();
                //将拆分好的两对数分别存入一个字符串
                //再将该字符串用等号拆分成一个数组
                String[] needFilterStr = aNeedFilterArray.split(":");
                //拆分好后需要将右边的值赋值为空串或者值
                //遍历查找整个fieldSet中的"tempValue"
                //如果有值赋值，没值为空串
                String idValues = "";
                for (int l = 0; l < fieldSetTemp.size(); l++) {
                    if (Integer.valueOf(String.valueOf(fieldSetTemp.get(l).get("fieldRole"))) == 21) {
                        if ((String.valueOf(fieldSetTemp.get(l).get("fieldId"))
                                .equals(needFilterStr[1]))) {
//                            Log.e("TAG", "fieldSetTemp.get(l)21" + fieldSetTemp.get(l).get(itemValue));
                            if (fieldSetTemp.get(l).get(itemValue) != null) {
                                idValues = String.valueOf(fieldSetTemp.get(l).get(itemValue));
                            } else {
                                Toast.makeText(context, "您需要填写" + String.valueOf(fieldSetTemp.get(l).get("fieldCnName")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if ((String.valueOf(fieldSetTemp.get(l).get("fieldId"))
                                .equals(needFilterStr[1]))) {
//                            Log.e("TAG", "fieldSetTemp.get(l)20" + fieldSetTemp.get(l).get(itemValue));
                            if (fieldSetTemp.get(l).get(itemValue) != null) {
                                idValues = String.valueOf(fieldSetTemp.get(l).get(itemValue));
                            } else {
                                Toast.makeText(context, "您需要填写" + String.valueOf(fieldSetTemp.get(l).get("fieldCnName")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                if (idValues.equals("") && Constant.relationFieldId != null
                        && Constant.relationFieldId.equals(needFilterStr[1])) {
                    idValues = dataId;
                    Constant.relationField = position;
                }
                needFilterMap.put(needFilterStr[0], idValues);

                needFilterList.add(needFilterMap);
            }
        }
        return needFilterList;
    }

    private void toTreeView(String viewName, String aTrue, String idArrs, Map<String, Object> childPra, List<Map<String, String>> needFilterList, int position) {

        try {
            Intent intent = new Intent();
            intent.setClass(context, TreeViewActivity.class);
            String childPraStr = JSON.toJSONString(childPra);
            String needFilterListStr = JSON.toJSONString(needFilterList);
            intent.putExtra("treePraStr", childPraStr);
            intent.putExtra("viewName", viewName);
            intent.putExtra("idArrs", idArrs);
            intent.putExtra("isMulti", aTrue);
            intent.putExtra("position", String.valueOf(position));
            intent.putExtra("needFilterListStr", needFilterListStr);
            Log.e("TAG", "向下拉树传递needFilterListStr" + needFilterListStr);
            mActivity.startActivityForResult(intent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void toMultiValueActivity(String viewName, String isMulti, String idArrs, Map<String, Object> childPra, List<Map<String, String>> needFilterList, int position) {
        try {

            Intent intent = new Intent();
            intent.setClass(context, MultiValueActivity.class);
            String childPraStr = JSON.toJSONString(childPra);
            String needFilterListStr = JSON.toJSONString(needFilterList);
            intent.putExtra("multiValueData", childPraStr);
            intent.putExtra("viewName", viewName);
            intent.putExtra("needFilterListStr", needFilterListStr);
            intent.putExtra("idArrs", idArrs);
            intent.putExtra("position", String.valueOf(position));
            intent.putExtra("isMulti", isMulti);
            mActivity.startActivityForResult(intent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


















