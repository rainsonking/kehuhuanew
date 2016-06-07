package com.kwsoft.kehuhua.adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class SchselAdapter extends BaseAdapter {
    public Context context;
    public List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
    private LayoutInflater inflater;

    public Map<String, String> maps = new HashMap<String, String>();
    public Map<String, String> mapOpts = new HashMap<String, String>();

    public SchselAdapter(Context context, List<Map<String, String>> lists) {
        super();
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflater.inflate(R.layout.activity_schinfo_title_two_item, viewGroup, false);
        TextView mTextView = (TextView) view.findViewById(R.id.tv_name);
        EditText mEdtitText = (EditText) view.findViewById(R.id.et_edit);
        final TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        LinearLayout layout_time = (LinearLayout) view.findViewById(R.id.layout_time);
        final TextView tv_statime = (TextView) view.findViewById(R.id.tv_statime);
        final TextView tv_endtime = (TextView) view.findViewById(R.id.tv_endtime);
        final TextView id_name = (TextView) view.findViewById(R.id.id_name);

        final Map<String, String> map = lists.get(i);
        final String fieldRoleStr = map.get("fieldRole");
        int fieldRole = Integer.parseInt(fieldRoleStr);
        final int position = i;
        String name = map.get("fieldCnName");
        final String fieldType = map.get("fieldType");
        final String fieldSearchName = map.get("fieldSearchName");

        mTextView.setText(name);


        switch (Constant.getViewType(fieldRole)) {
            case 2:
                final EditText editText = mEdtitText;
                String str = maps.get(position + "");
                if (!TextUtils.isEmpty(str)) {
                    String arr[] = str.split("/");
                    if (arr.length > 4) {
                        String value = arr[4];
                        mEdtitText.setText(value);
                    }
                }
                mEdtitText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        String edit = editText.getText().toString().trim();
                        Log.d("SchselAdapter===", fieldSearchName + "===" + fieldType + "===" + edit);
                        maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + edit);

                    }
                });

                break;
            case 1:
                break;
            case 3:
                mEdtitText.setVisibility(View.GONE);
                tv_content.setVisibility(View.VISIBLE);
                String str2 = mapOpts.get(position + "");
                if (!TextUtils.isEmpty(str2)) {

                    tv_content.setText(str2);

                }
                JSONArray dicOptArr = null;
                List<String> list2 = new ArrayList<String>();
                final List<String> list3 = new ArrayList<String>();
                try {
                    dicOptArr = new JSONArray(map.get("dicOptions"));

                    for (int j = 0; j < dicOptArr.length(); j++) {
                        JSONObject object2 = dicOptArr.getJSONObject(j);
                        list2.add(object2.getString("DIC_NAME"));
                        list3.add(object2.getInt("DIC_ID") + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String[] dicOptions = list2.toArray(new String[list2.size()]);

                tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setTitle("请选择");
                        //   设置一个单项选择下拉框
//         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
//         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
//         * 第三个参数给每一个单选项绑定一个监听器

                        tv_content.setText(dicOptions[1]);
                        id_name.setText(list3.get(1));

                        //  maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + id_name.getText().toString());
                        //mapOpts.put(position + "", dicOptions[1]);
                        builder.setSingleChoiceItems(dicOptions, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("dialog1", which + "");
                                tv_content.setText(dicOptions[which]);
                                id_name.setText(list3.get(which));

                                maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + id_name.getText().toString());
                                mapOpts.put(position + "", dicOptions[which]);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                });

                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                mEdtitText.setVisibility(View.GONE);
                layout_time.setVisibility(View.VISIBLE);

                String str6 = maps.get(position + "");
                if (!TextUtils.isEmpty(str6)) {
                    String arr[] = str6.split("/");
                    if (arr.length > 4) {
                        String value = arr[4];
                        String[] date ={"",""};
                        if (value != null && value.length() > 0) {
                             date[0] = value.substring(0, 10);
                             date[1] = value.substring(11);
                        }
                        tv_statime.setText(date[0]);
                        tv_endtime.setText(date[1]);
                    }
                }
                // maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + tv_statime.getText().toString());
                tv_statime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog = new DatePickerDialog(
                                context,
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                        String monthfirStr = getMonStr(month);
                                        String daystr = getDayStr(dayOfMonth);
                                        String statime = year + "-" + monthfirStr + "-" + daystr;
                                        tv_statime.setText(statime);
                                        maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + tv_statime.getText().toString() + "到" + tv_endtime.getText().toString());

                                    }
                                },
                                c.get(Calendar.YEAR), // 传入年份
                                c.get(Calendar.MONTH), // 传入月份
                                c.get(Calendar.DAY_OF_MONTH) // 传入天数
                        );
                        dialog.show();

                    }
                });

                tv_endtime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog = new DatePickerDialog(
                                context,
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                        String monthfirStr = getMonStr(month);
                                        String daystr = getDayStr(dayOfMonth);
                                        String endTime = year + "-" + monthfirStr + "-" + daystr;
                                        tv_endtime.setText(endTime);
                                        maps.put(position + "", position + "/" + fieldType + "/" + fieldSearchName + "/" + fieldRoleStr + "/" + tv_statime.getText().toString() + "T" + tv_endtime.getText().toString());

                                    }
                                },
                                c.get(Calendar.YEAR), // 传入年份
                                c.get(Calendar.MONTH), // 传入月份
                                c.get(Calendar.DAY_OF_MONTH) // 传入天数
                        );
                        dialog.show();
                    }
                });
                break;
            case 7:


                break;
            default:
                break;


        }

        return view;
    }

    @NonNull
    public String getMonStr(int month) {
        String monthfirStr;
        if ((month + 1) < 10) {
            monthfirStr = "0" + (month + 1);

        } else {
            monthfirStr = (month + 1) + "";
        }
        return monthfirStr;
    }

    public String getDayStr(int day) {
        String dayfirStr;
        if (day < 10) {
            dayfirStr = "0" + day;
        } else {
            dayfirStr = day + "";
        }
        return dayfirStr;
    }

}
