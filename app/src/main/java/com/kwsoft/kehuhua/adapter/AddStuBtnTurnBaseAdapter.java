package com.kwsoft.kehuhua.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/5 0005.
 */
public class AddStuBtnTurnBaseAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String, String>> lists = new ArrayList<Map<String, String>>();
    private LayoutInflater inflater;
    private Activity activity;
    final int TYPE_1 = 1;
    final int TYPE_2 = 2;
    final int TYPE_3 = 3;
    final int TYPE_4 = 4;
    final int TYPE_5 = 5;
    final int TYPE_6 = 6;
    final int TYPE_7 = 7;
    public Map<String, String> maps = new HashMap<String, String>();


    public AddStuBtnTurnBaseAdapter(Context context, Activity activity, List<Map<String, String>> lists) {
        super();
        this.activity = activity;
        this.context = context;
        this.lists = lists;
        inflater = LayoutInflater.from(context);
        // maps.clear();
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Map<String, String> getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, final ViewGroup viewGroup) {
        final Map<String, String> phoneAddFieldSet = lists.get(i);
        final ViewHolder viewHolder;
        final int position = i;
        int type = getItemViewType(i);

        viewHolder = new ViewHolder();
        convertView = inflater.inflate(R.layout.activity_add_item_cbdialog, null);
        viewHolder.tv_cbd = (TextView) convertView.findViewById(R.id.tv_cbd);
        viewHolder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
        viewHolder.et_edit = (EditText) convertView.findViewById(R.id.et_edit);
        viewHolder.fieldRole = (TextView) convertView.findViewById(R.id.fieldRole);
        viewHolder.name_id = (TextView) convertView.findViewById(R.id.name_id);
        viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout);

        switch (type) {
            case TYPE_2:
                viewHolder.et_edit.setVisibility(View.VISIBLE);
                viewHolder.tv_content.setVisibility(View.GONE);
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                final String ifmust = phoneAddFieldSet.get("ifMust");
                if ("1".equals(ifmust)) {
                    viewHolder.et_edit.setHint("必填");
                    viewHolder.fieldRole.setText(TYPE_2 + "");
                }
                switch (phoneAddFieldSet.get("fieldRole")) {
                    case "3":
                    case "6":
                    case "7":
                        viewHolder.et_edit.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        break;
                    default:
                        break;
                }

//                String str = maps.get(position + "");
//                if (!TextUtils.isEmpty(str)) {
//                    String value = str.substring(2, str.length());
//                    viewHolder.et_edit.setText(value);
//                }
                final ViewHolder finalViewHolder2 = viewHolder;
                viewHolder.et_edit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        String str = finalViewHolder2.et_edit.getText().toString();

                        if ((str != null) && (str.length() > 0)) {
                            Log.e("aftertTextChanged==", str);
                            maps.put(position + "", TYPE_2 + "/" + position + "/" + str);
                        }
                    }
                });
                break;
            case TYPE_1:
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                viewHolder.fieldRole.setText(TYPE_1 + "");
                break;
            case TYPE_6://日期
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                final ViewHolder finalViewHolder6 = viewHolder;
                viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog = new DatePickerDialog(
                                context,
                                new DatePickerDialog.OnDateSetListener() {
                                    public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
                                        finalViewHolder6.tv_content.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                                        maps.put(position + "", TYPE_6 + "/" + position + "/" + finalViewHolder6.tv_content.getText().toString());

                                    }
                                },
                                c.get(Calendar.YEAR), // 传入年份
                                c.get(Calendar.MONTH), // 传入月份
                                c.get(Calendar.DAY_OF_MONTH) // 传入天数
                        );
                        dialog.show();

                    }
                });
                viewHolder.fieldRole.setText(TYPE_6 + "");
                break;
            case TYPE_7://时间
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                final ViewHolder finalViewHolder7 = viewHolder;

                viewHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar c = Calendar.getInstance();
                        Dialog dialog1 = new TimePickerDialog(
                                context,
                                new TimePickerDialog.OnTimeSetListener() {
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        finalViewHolder7.tv_content.setText("您选择了：" + hourOfDay + "时" + minute + "分");
                                        maps.put(position + "", TYPE_7 + "/" + position + "/" + finalViewHolder7.tv_content.getText().toString());

                                    }
                                },
                                c.get(Calendar.HOUR_OF_DAY),
                                c.get(Calendar.MINUTE),
                                false
                        );
                        dialog1.show();
                    }
                });
                viewHolder.fieldRole.setText(TYPE_7 + "");
                break;
            case TYPE_3:
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                viewHolder.fieldRole.setText(TYPE_3 + "");
                if ("1".equals(phoneAddFieldSet.get("ifMust"))) {
                    viewHolder.tv_content.setHint("必填");
                }
                JSONArray dicOptArr = null;
                List<String> list2 = new ArrayList<String>();
                final List<String> list3 = new ArrayList<String>();
                try {
                    dicOptArr = new JSONArray(phoneAddFieldSet.get("dicOptions"));

                    for (int j = 0; j < dicOptArr.length(); j++) {
                        JSONObject object2 = dicOptArr.getJSONObject(j);
                        list2.add(object2.getString("DIC_NAME"));
                        list3.add(object2.getInt("DIC_ID") + "");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String[] dicOptions = list2.toArray(new String[list2.size()]);


                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setTitle("请选择");
                        //    设置一个单项选择下拉框
//         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
//         * 第二个参数代表索引，指定默认哪一个单选框被勾选上，1表示默认'女' 会被勾选上
//         * 第三个参数给每一个单选项绑定一个监听器

                        finalViewHolder.tv_content.setText(dicOptions[1]);
                        finalViewHolder.name_id.setText(list3.get(1));
                        maps.put(position + "", TYPE_3 + "/" + position + "/" + finalViewHolder.name_id.getText().toString());
                        builder.setSingleChoiceItems(dicOptions, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("dialog1", which + "");
                                finalViewHolder.tv_content.setText(dicOptions[which]);
                                finalViewHolder.name_id.setText(list3.get(which));
                                maps.put(position + "", TYPE_3 + "/" + position + "/" + finalViewHolder.name_id.getText().toString());
                                dialog.dismiss();
                            }
                        });
//                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                Log.e("dialog", which + "");
//                                Log.e("dicOptions", dicOptions.length + "");
//
//                                dialog.dismiss();
//
//                            }
//                        });
//                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//
//                            }
//                        });
                        builder.show();
                    }
                });

                break;
            case TYPE_4:
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                viewHolder.fieldRole.setText(TYPE_4 + "");

                final ViewHolder finalViewHolder4 = viewHolder;
                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Dialog dialog = null;
                        final boolean[] flags = new boolean[]{false, false, false};
                        final String[] hobbys = new String[]{"红", "黄", "蓝"};
                        final AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        //设置对话框的图标
                        builder.setIcon(R.mipmap.ic_launcher);
                        //设置对话框的标题
                        builder.setTitle("复选框对话框");
                        builder.setMultiChoiceItems(hobbys, flags, new DialogInterface.OnMultiChoiceClickListener() {
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                flags[which] = isChecked;
                                String result = "您选择了：";
                                for (int i = 0; i < flags.length; i++) {
                                    if (flags[i]) {
                                        result = result + hobbys[i] + "、";
                                    }
                                }
                                finalViewHolder4.tv_content.setText(result.substring(0, result.length() - 1));
                                maps.put(position + "", TYPE_4 + "/" + position + "/" + finalViewHolder4.tv_content.getText().toString());

                            }
                        });

                        //添加一个确定按钮
                        builder.setPositiveButton(" 确 定 ", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        //创建一个复选框对话框
                        dialog = builder.show();
                    }
                });


                break;
            case TYPE_5:
                viewHolder.tv_cbd.setText(phoneAddFieldSet.get("fieldCnName"));
                if ("1".equals(phoneAddFieldSet.get("ifMust"))) {
                    viewHolder.tv_content.setHint("必填");
                }
                viewHolder.fieldRole.setText(TYPE_5 + "");
                break;
            default:
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, String> phoneAddFieldSet = lists.get(position);
        int fieldRole = Integer.parseInt(phoneAddFieldSet.get("fieldRole"));
        int viewType = Constant.getViewType(fieldRole);
        return viewType;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    class ViewHolder {
        public TextView tv_cbd;
        public TextView tv_content;
        public EditText et_edit;
        public TextView fieldRole;
        public TextView name_id;
        public LinearLayout layout;
    }


}
