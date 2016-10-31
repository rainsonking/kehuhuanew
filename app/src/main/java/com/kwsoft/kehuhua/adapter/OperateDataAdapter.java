package com.kwsoft.kehuhua.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.AddTemplateDataActivity;
import com.kwsoft.kehuhua.adcustom.MultiValueActivity;
import com.kwsoft.kehuhua.adcustom.OperateDataActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.TreeViewActivity;
import com.kwsoft.kehuhua.adcustom.UnlimitedAddActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.datetimeselect.SelectDateDialog;
import com.kwsoft.kehuhua.datetimeselect.SelectTimeDialog;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.kehuhua.wechatPicture.SelectPictureActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.kwsoft.kehuhua.config.Constant.itemValue;
import static java.lang.String.valueOf;


public class OperateDataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private List<Map<String, Object>> mDatas;

    private Context mContext;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private static final int VIEW_TYPE = 1;
    private Activity mActivity;
    private String tableId, dataId, pageId;

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

    public OperateDataAdapter(List<Map<String, Object>> mDatas, Map<String, String> paramsMap) {


        this.mDatas = mDatas;
        tableId = paramsMap.get(Constant.tableId);
        pageId = paramsMap.get(Constant.pageId);
        dataId = Constant.mainIdValue;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        mContext = parent.getContext();
        this.mActivity = (Activity) mContext;
        Log.e(TAG, "onCreateViewHolder: 适配器创建");
        if(mActivity instanceof OperateDataActivity) {
            Constant.jumpNum = 1;
        } else if (mActivity instanceof AddTemplateDataActivity) {
            Constant.jumpNum1 = 4;
        }
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        if (VIEW_TYPE == viewType) {
            view = mInflater.inflate(R.layout.empty_view, parent, false);

            return new EmptyViewHolder(view);
        }
        view = mInflater.inflate(R.layout.activity_add_item, null);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        return new OperateHolder(view);
    }

    private static final String TAG = "OperateDataAdapter";
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder thisHolder, final int position) {
        if (thisHolder instanceof OperateHolder) {
            final OperateHolder holder = (OperateHolder) thisHolder;
            Log.e(TAG, "position "+position+"  onBindViewHolder: mDatas.get(position)  "+mDatas.get(position));
            //item操作
                final int fieldRole = Integer.valueOf(valueOf(mDatas.get(position).get("fieldRole")));
//初始化必填字段标志
                int ifMust = 0;
                if (mDatas.get(position).get("ifMust") != null) {
                    ifMust = Integer.valueOf(valueOf(mDatas.get(position).get("ifMust")));
                }
//初始化是否可修改标志
                int ifUpdate = 0;
                if (mDatas.get(position).get("ifUpdate") != null) {
                    ifUpdate = Integer.valueOf(valueOf(mDatas.get(position).get("ifUpdate")));
                }
//初始化左侧名称
                String fieldCnName = valueOf(mDatas.get(position).get("fieldCnName"));
                if (fieldCnName.equals("") || fieldCnName.equals("null")) {
                    fieldCnName = "";
                }
                holder.textView.setText(fieldCnName);

//初始化必填标志
                if (ifMust == 1) {
                    holder.textViewIfMust.setVisibility(View.VISIBLE);
                }
//设置控件不可点击
                if (ifUpdate == 0) {
                    holder.list_item_cover.setVisibility(View.VISIBLE);
                    holder.list_item_cover2.setVisibility(View.VISIBLE);
                }
//初始化item项参数，供传递到多选和树形选择用
                final Map<String, Object> childPra = mDatas.get(position);

/**
 * 默认值选取，取key为：true_defaultShowValName
 *
 */

//默认值选择,不包含20、21的情况，如果存在赋值，不存在为空串
                String defaultName;
                Object itemObj = mDatas.get(position).get(Constant.itemName);
                if (itemObj != null) {
                    defaultName = valueOf(itemObj);
                } else {
                    defaultName = "";
                }


//chooseType判断
                int chooseType = -1;
                if (mDatas.get(position).get("chooseType") != null) {
                    chooseType = Integer.valueOf(valueOf(mDatas.get(position).get("chooseType")));
                }
                final int finalChooseType = chooseType;

//jsWhereStr判断   "jsWhereStr": "#{19:180} == 6"，通过判断来决定校区选择或者考点选择模块等等是否显示
                boolean isShow = isShow(position, holder.textView, holder.textViewIfMust);
//1、普通编辑框

                if (fieldRole == -1 || fieldRole == 1 || fieldRole == 2 || fieldRole == 10 ||
                        fieldRole == 3 || fieldRole == 4 || fieldRole == 5 ||
                        fieldRole == 6 || fieldRole == 7 || fieldRole == 11 ||
                        fieldRole == 12 || fieldRole == 13 || fieldRole == 8 ||
                        fieldRole == 9 || fieldRole == 24 || fieldRole == 29) {

                    if (fieldRole == 24) {//密码格式
                        holder.add_edit_text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    if (fieldRole == 6) {//手机号
                        holder.add_edit_text.setInputType(InputType.TYPE_CLASS_PHONE);
                        InputFilter[] filters = {new InputFilter.LengthFilter(11)};
                        holder.add_edit_text.setFilters(filters);
                        // add_edit_text.setError("请输入正确的手机号");
                    }
                    if (fieldRole == 13) {//金额，带小数点
                        holder.add_edit_text.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                    if (fieldRole == 5) {//网址
                        holder.add_edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                        holder.add_edit_text.setError("请输入正确的网址");
                    }
                    if (fieldRole == 11) {//网址
                        holder.add_edit_text.setInputType(InputType.TYPE_TEXT_VARIATION_PHONETIC);
                        holder.add_edit_text.setError("请输入正确的拼音");
                    }
                    if (fieldRole == 2) {//富文本
                        holder.add_edit_text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    }
                    if (fieldRole == 3) {//身份证号
                        holder.add_edit_text.setInputType(InputType.TYPE_CLASS_PHONE);
                        InputFilter[] filters = {new InputFilter.LengthFilter(18)};
                        holder.add_edit_text.setFilters(filters);
                        //add_edit_text.
                    }
                    if (fieldRole == 12) {//身份证号
                        holder.add_edit_text.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                    if (fieldRole == 8) {//订单编号
                        Constant.tmpFieldId = String.valueOf(mDatas.get(position).get("tmpFieldId"));
                    }
                    if (isShow) {
                        holder.add_edit_text.setVisibility(View.VISIBLE);
                    }
                    if (!defaultName.equals("")) {
                        holder.add_edit_text.setText(defaultName);

                    }
                    mDatas.get(position).put(itemValue, defaultName);

                    holder.add_edit_text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            mDatas.get(position).put(Constant.itemName, editable.toString());
                            mDatas.get(position).put(Constant.itemValue, editable.toString());
                        }
                    });

//2、单值选择项&星期
                } else if (fieldRole == 16 || fieldRole == 23) {
                    if (isShow) {
                        holder.add_spinner.setVisibility(View.VISIBLE);
                    }
                    //删除无用字典值数据
                    List<Map<String, Object>> dicList = getNewDicList(position);
                    //设置默认选中值以及byId的位置
                    int byId = -1;//
                    int dicDefaultSelectInt;
                    String dicDefaultSelect;
                    //有值的情况

                    if (!defaultName.equals("")&&!defaultName.equals("null")&&Utils.isNum(defaultName)) {
                        byId = getById(dicList, byId, Integer.valueOf(defaultName));
                        //无值、有默认值的情况
                    } else if (!valueOf(mDatas.get(position).get("dicDefaultSelect")).equals("")) {
                        dicDefaultSelect = valueOf(mDatas.get(position).get("dicDefaultSelect"));
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
                        dataset.add(valueOf(dicList.get(i).get("DIC_NAME")));
                    }
                    mDatas.get(position).put(Constant.itemValue, valueOf(dicList.get(byId).get("DIC_ID")));
                    mDatas.get(position).put(Constant.itemName, valueOf(dicList.get(byId).get("DIC_ID")));
                    String dicName=String.valueOf(dicList.get(byId).get("DIC_NAME"));


                    if (!dicName.equals("")&&!dicName.equals("null")) {
                        holder.add_spinner.setText(dicName);
                    }


                    final List<Map<String, Object>> finalDicList = dicList;
                    final int size = dataset.size();
                    final String[] arrs = dataset.toArray(new String[size]);
                    holder.add_spinner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.activity_adapter_radio_item, R.id.text1, arrs);
                            AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("").
                                    setAdapter(adapter, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String DIC_ID = valueOf(finalDicList.get(which).get("DIC_ID"));
                                            mDatas.get(position).put(itemValue, DIC_ID);
                                            mDatas.get(position).put(Constant.itemName, DIC_ID);
                                            holder.add_spinner.setText(arrs[which]);
                                            dialog.dismiss();
                                        }
                                    }).create();
                            dialog.show();
                        }
                    });
//3、日期

                } else if (fieldRole == 14 || fieldRole == 26 || fieldRole == 28) {
                    if (isShow) {
                        holder.addGeneral.setVisibility(View.VISIBLE);
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
                    holder.addGeneral.setText(defaultName);
                    if (mDatas.get(position).get(itemValue) == null) {
                        mDatas.get(position).put(itemValue, defaultName);
                        mDatas.get(position).put(Constant.itemName, defaultName);
                    }


                   holder.addGeneral.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           SelectDateDialog mSelectDateDialog = new SelectDateDialog(mContext);
                           mSelectDateDialog.setOnClickListener(new SelectDateDialog.OnClickListener() {
                               @Override
                               public boolean onSure(int mYear, int mMonth, int mDay, long time) {
                                   SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                   final String date=dateFormat.format(time);
                                   //选择时间
                                   SelectTimeDialog mSelectTimeDialog=new SelectTimeDialog(mContext, new SelectTimeDialog.OnClickListener() {
                                       @Override
                                       public boolean onSure(int hour, int minute, int setTimeType) {
                                           String result = String.format("%02d:%02d:%02d", hour, minute,0);
                                           String dateAndTime=date+" "+result;
                                           holder.addGeneral.setText(dateAndTime);
                                           Log.e(TAG, "onSure: dateAndTime  "+dateAndTime);
                                           mDatas.get(position).put(itemValue, dateAndTime);
                                           mDatas.get(position).put(Constant.itemName, dateAndTime);
                                           return false;
                                       }

                                       @Override
                                       public boolean onCancel() {
                                           return false;
                                       }
                                   });
                                   Calendar c = Calendar.getInstance();
                                   c.setTimeInMillis(System.currentTimeMillis());
                                   mSelectTimeDialog.show(c.get(Calendar.HOUR_OF_DAY),
                                           c.get(Calendar.MINUTE),
                                           c.get(Calendar.SECOND));
                                   return false;
                               }

                               @Override
                               public boolean onCancel() {
                                   return false;
                               }
                           });
                           Calendar c = Calendar.getInstance();
                           c.setTimeInMillis(System.currentTimeMillis());
                           mSelectDateDialog.show(c.get(Calendar.YEAR),
                                   c.get(Calendar.MONTH),
                                   c.get(Calendar.DAY_OF_MONTH));
                       }
                   });

//4、时间

                } else if (fieldRole == 15) {
                    if (isShow) {
                        holder.addGeneral.setVisibility(View.VISIBLE);

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
                    holder.addGeneral.setText(defaultName);
                    if (mDatas.get(position).get(itemValue) == null) {
                        mDatas.get(position).put(itemValue, defaultName);
                        mDatas.get(position).put(Constant.itemName, defaultName);
                    }

//                    final String finalDateType = dateType;
holder.addGeneral.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        SelectTimeDialog mSelectTimeDialog=new SelectTimeDialog(mContext, new SelectTimeDialog.OnClickListener() {
            @Override
            public boolean onSure(int hour, int minute, int setTimeType) {
                String result = String.format("%02d:%02d:%02d", hour, minute,0);
                holder.addGeneral.setText(result);
                Log.e(TAG, "onSure: result"+result);
                mDatas.get(position).put(itemValue, result);
                mDatas.get(position).put(Constant.itemName, result);
                return false;
            }

            @Override
            public boolean onCancel() {
                return false;
            }
        });
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        mSelectTimeDialog.show(c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND));
    }
});

                } else if (fieldRole == 19) {
/**
 *
 * 添加作业附件
 *
 *
 */
                    if (isShow && ifUpdate == 1) {
                        holder.image_upload_layout.setVisibility(View.VISIBLE);
                        holder.image_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(mActivity, SelectPictureActivity.class);
                                intent.putExtra("position", position + "");
                                mActivity.startActivityForResult(intent, 2);
                            }
                        });

                        //判断选择了x张图片

                        String numPic = valueOf(mDatas.get(position).get(itemValue));

                        if (!numPic.equals("null") && !numPic.equals("")) {

                            String[] numPicArray = numPic.split(",");
                            int picLength = numPicArray.length;
                            String picContent = "已选" + picLength + "张图片";
                            holder.picNumber.setText(picContent);
                        } else {
                            holder.picNumber.setText("尚无附件");
                        }


                    } else {
                        holder.addGeneral.setVisibility(View.VISIBLE);
                        holder.addGeneral.setText(defaultName);
                    }
//5、内部对象单值
                } else if (fieldRole == 20 || fieldRole == 22) {
                    if (isShow) {
                        holder.addGeneral.setVisibility(View.VISIBLE);
//                addGeneral.setHint("请选择");
                    } else {
                        holder.addGeneral.setVisibility(View.GONE);
                    }

                    String itemName;
                    if (mDatas.get(position).get(Constant.itemName) != null) {
                        itemName = valueOf(mDatas.get(position).get(Constant.itemName));
                    } else {
                        itemName = "";
                    }

                    if (!itemName.equals("")) {
                        holder.addGeneral.setText(itemName);
                    }
                    final String finalFieldCnName1 = fieldCnName;
                    holder.addGeneral.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            List<Map<String, String>> needFilterList = getNeedFilter(position);
                            String idArrs = "";
                            try {
                                idArrs = valueOf(mDatas.get(position).get(itemValue));
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

                    String addStyle = valueOf(mDatas.get(position).get("addStyle"));
                    if (addStyle.equals("1") || addStyle.equals("2")) {

                        if (isShow) {
                            holder.addGeneral.setVisibility(View.VISIBLE);
//                    addGeneral.setHint("请选择");
                        }
                        String itemName;
                        if (mDatas.get(position).get(Constant.itemName) != null) {
                            itemName = valueOf(mDatas.get(position).get(Constant.itemName));
                        } else {
                            itemName = "";
                        }

                        if (!itemName.equals("")) {
                            holder.addGeneral.setText(itemName);
                        }

                        final String finalFieldCnName = fieldCnName;
                        holder.addGeneral.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                List<Map<String, String>> needFilterList = getNeedFilter(position);
                                String idArrs = "";
                                if (mDatas.get(position).get(itemValue) != null) {
                                    idArrs = valueOf(mDatas.get(position).get(itemValue));
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
                            holder.add_unlimited.setVisibility(View.VISIBLE);
                        }

                        String unlimitedAddValue = "";
                        if (mDatas.get(position).get("tempListValue") != null) {
                            unlimitedAddValue = valueOf(mDatas.get(position).get("tempListValue"));

                        }
/**
 * 无限添加按钮
 */
////获取参数
                        final String showFieldArr = valueOf(mDatas.get(position).get("showFieldArr"));
                        final String fieldSetStr = JSON.toJSONString(mDatas);
////异步请求模板数据
                        final String finalUnlimitedAddValue = unlimitedAddValue;
                        final String finalFieldCnName2 = fieldCnName;
                        holder.add_unlimited.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                        requestData(showFieldArr);
                                try {
                                    Intent intent = new Intent();
                                    intent.setClass(mContext, UnlimitedAddActivity.class);
                                    intent.putExtra("fieldSetStr", fieldSetStr);
                                    intent.putExtra("showFieldArr", showFieldArr);
                                    intent.putExtra("viewName", finalFieldCnName2);
                                    intent.putExtra("unlimitedAddValue", finalUnlimitedAddValue);
                                    intent.putExtra("position", position + "");
                                    intent.putExtra("tableId", tableId);
                                    intent.putExtra("relationTableId", valueOf(mDatas.get(position).get("relationTableId")));
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
                        holder.addGeneral.setVisibility(View.VISIBLE);
                    }

                    if (!defaultName.equals("")) {
                        holder.addGeneral.setHint(defaultName);
                        mDatas.get(position).put(itemValue, defaultName);
                    }
                }
            holder.itemView.setTag(mDatas.get(position));
        }

    }

    /**
     * 获取单项数据
     */

    private Map<String, Object> getData(int position) {

        return mDatas.get(position);
    }

    /**
     * 获取全部数据
     */
    public List<Map<String, Object>> getDatas() {

        return mDatas;
    }

    /**
     * 清除数据
     */
    public void clearData() {
        mDatas.clear();
        notifyItemRangeRemoved(0, mDatas.size());
        notifyDataSetChanged();
    }

    /**
     * 下拉刷新更新数据
     */
    public void addData(List<Map<String, Object>> datas) {
        Log.e(TAG, "addData: datas"+datas.toString());
        addData(0, datas);
        notifyDataSetChanged();
    }

    /**
     * 上拉加载添加数据的方法
     */
    public void addData(int position, List<Map<String, Object>> datas) {

        if (datas != null && datas.size() > 0) {

            mDatas.addAll(datas);
            //下一步最容易出问题

            notifyItemRangeChanged(position, mDatas.size());

            for (int i=position;i<mDatas.size();i++) {

                notifyItemChanged(i);
            }
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


    private boolean isShow(int position, TextView textView, TextView textViewIfMust) {
        boolean isShow = true;
        if (mDatas.get(position).get("jsWhereStr") != null) {
            String jsWhereStr = valueOf(mDatas.get(position).get("jsWhereStr")).replace(" ", "").replace("==", "");
            String jsWhereStrFieldId = jsWhereStr.substring(jsWhereStr.indexOf(":") + 1, jsWhereStr.indexOf("}"));
            String jsWhereStrValue = jsWhereStr.substring(jsWhereStr.indexOf("}") + 1, jsWhereStr.length());
            int fieldIdNeed = Integer.valueOf(jsWhereStrFieldId);
            int dicNeed = Integer.valueOf(jsWhereStrValue);
            int countNum = 0;
            for (int i = 0; i < mDatas.size(); i++) {
                countNum++;
                int fieldIdTemp = Integer.valueOf(valueOf(mDatas.get(i).get("fieldId")));
                if (fieldIdNeed == fieldIdTemp) {
                    //获得字典值
                    if (mDatas.get(i).get("true_defaultShowVal") != null) {
                        int dicTemp = Integer.valueOf(valueOf(mDatas.get(i).get("true_defaultShowVal")));
                        if (dicNeed == dicTemp) {
                            break;
                        }
                    }
                }
            }
            if (countNum == mDatas.size()) {
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
                if (Integer.parseInt(valueOf(dicList.get(i).get("DIC_ID"))) == dicDefaultSelectInt) {
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
                (List<Map<String, Object>>) mDatas.get(position).get("dicList");


        String dicChildShow = valueOf(mDatas.get(position).get("dicChildShow"));
        List<Integer> dicChildShowList = new ArrayList<>();
        if (dicChildShow != null && !dicChildShow.equals("") && !dicChildShow.equals("null")) {
            //将字符串类型数组转换为int型集合
            String[] dicChildShowStrArr = dicChildShow.split(",");
            for (String aDicChildShowStrArr : dicChildShowStrArr) {
                dicChildShowList.add(Integer.parseInt(aDicChildShowStrArr));
            }
            //将字典列表遍历的过程中比较，并删除不存在的id项
            List<Map<String, Object>> dicListNew = new ArrayList<>();
            if (dicList.size() > 0) {
                for (int i = 0; i < dicList.size(); i++) {
                    int dicIdTemp = Integer.valueOf(valueOf(dicList.get(i).get("DIC_ID")));
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
            String isSession = valueOf(mDatas.get(position).get("needFilter"));
            //判断是否为无限添加的item进入选项
            if (mActivity instanceof AddTemplateDataActivity && Constant.fieldSetStr != null) {
                List<Map<String, Object>> fieldSet2 = JSON.parseObject(Constant.fieldSetStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                needFilterList = getIdvalue(fieldSet2, isSession, position);
            } else {
                //查找目标数据源的fieldId
                needFilterList = getIdvalue(mDatas, isSession, position);
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
                    if (Integer.valueOf(valueOf(fieldSetTemp.get(l).get("fieldRole"))) == 21) {
                        if ((valueOf(fieldSetTemp.get(l).get("fieldId"))
                                .equals(needFilterStr[1]))) {
//                            Log.e("TAG", "fieldSetTemp.get(l)21" + fieldSetTemp.get(l).get(itemValue));
                            if (fieldSetTemp.get(l).get(itemValue) != null) {
                                idValues = valueOf(fieldSetTemp.get(l).get(itemValue));
                            } else {
                                Toast.makeText(mContext, "您需要填写" + valueOf(fieldSetTemp.get(l).get("fieldCnName")), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if ((valueOf(fieldSetTemp.get(l).get("fieldId"))
                                .equals(needFilterStr[1]))) {
//                            Log.e("TAG", "fieldSetTemp.get(l)20" + fieldSetTemp.get(l).get(itemValue));
                            if (fieldSetTemp.get(l).get(itemValue) != null) {
                                idValues = valueOf(fieldSetTemp.get(l).get(itemValue));
                            } else {
                                Toast.makeText(mContext, "您需要填写" + valueOf(fieldSetTemp.get(l).get("fieldCnName")), Toast.LENGTH_SHORT).show();
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
            intent.setClass(mContext, TreeViewActivity.class);
            String childPraStr = JSON.toJSONString(childPra);
            String needFilterListStr = JSON.toJSONString(needFilterList);
            intent.putExtra("treePraStr", childPraStr);
            intent.putExtra("viewName", viewName);
            intent.putExtra("idArrs", idArrs);
            intent.putExtra("isMulti", aTrue);
            intent.putExtra("position", valueOf(position));
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
            intent.setClass(mContext, MultiValueActivity.class);
            String childPraStr = JSON.toJSONString(childPra);
            String needFilterListStr = JSON.toJSONString(needFilterList);
            intent.putExtra("multiValueData", childPraStr);
            intent.putExtra("viewName", viewName);
            intent.putExtra("needFilterListStr", needFilterListStr);
            intent.putExtra("idArrs", idArrs);
            intent.putExtra("position", valueOf(position));
            intent.putExtra("isMulti", isMulti);
            mActivity.startActivityForResult(intent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}