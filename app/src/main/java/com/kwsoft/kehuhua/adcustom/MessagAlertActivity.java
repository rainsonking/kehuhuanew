package com.kwsoft.kehuhua.adcustom;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.config.Url;
import com.kwsoft.kehuhua.fragments.FragmentTabAdapter;
import com.kwsoft.kehuhua.fragments.GetFragment;
import com.kwsoft.kehuhua.fragments.UnGetFragment;
import com.kwsoft.kehuhua.login.LoginActivity;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class MessagAlertActivity extends BaseActivity {
    public static boolean isForeground = false;

    private String lancherActivityClassName=LoginActivity.class.getName();

    private static final String TAG = "JPush";

    private Fragment getFragment;
    private Fragment ungetFragment;
    private List<Fragment> mFragments;// 每个部分的fragment实体
    FragmentTabAdapter tabAdapter;
    public RadioGroup radioGroup;// 无线电广播组？按钮集中营
    private ImageView IV_back_list_item_tadd;
    private TextView tv_delete,tv_cancle,tv_ok;
    private LinearLayout layout_buttom,layout_select_bar;
    private RelativeLayout layout_not_read,layout_read,add_item_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messag_alert);
//        getSupportActionBar().hide();

        radioGroup=(RadioGroup) findViewById(R.id.book_radioGroup);
        layout_select_bar=(LinearLayout) findViewById(R.id.layout_select_bar);
        IV_back_list_item_tadd=(ImageView) findViewById(R.id.IV_back_list_item_tadd);
        layout_not_read=(RelativeLayout) findViewById(R.id.layout_not_read);
        add_item_title=(RelativeLayout) findViewById(R.id.add_item_title);
        add_item_title.setBackgroundColor(getResources().getColor(topBarColor));
        layout_read=(RelativeLayout) findViewById(R.id.layout_read);
        IV_back_list_item_tadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        layout_buttom=(LinearLayout) findViewById(R.id.layout_buttom);

        tv_delete=(TextView) findViewById(R.id.tv_delete);
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getVisibility() == View.VISIBLE) {
                    radioGroup.setVisibility(View.GONE);
                    layout_select_bar.setVisibility(View.GONE);
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                    layout_select_bar.setVisibility(View.VISIBLE);
                }
                if (layout_buttom.getVisibility() == View.VISIBLE) {
                    tv_delete.setText("删除");
                    layout_buttom.setVisibility(View.GONE);
                } else {
                    tv_delete.setText("取消");
                    layout_buttom.setVisibility(View.VISIBLE);
                }

                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_un_get) {
                    if (layout_buttom.getVisibility() == View.VISIBLE) {
                        ((UnGetFragment) ungetFragment).setShowAndHide(true);
                    } else {
                        ((UnGetFragment) ungetFragment).setShowAndHide(false);
                    }
                } else {
                    if (layout_buttom.getVisibility() == View.VISIBLE) {
                        ((GetFragment) getFragment).setShowAndHide(true);
                    } else {
                        ((GetFragment) getFragment).setShowAndHide(false);
                    }
                }
            }
        });

        tv_cancle=(TextView) findViewById(R.id.tv_cancle);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_buttom.setVisibility(View.GONE);
                radioGroup.setVisibility(View.VISIBLE);
                layout_select_bar.setVisibility(View.VISIBLE);
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_un_get) {
                    ((UnGetFragment) ungetFragment).setShowAndHide(false);
                } else {
                    ((GetFragment) getFragment).setShowAndHide(false);
                }
                tv_delete.setText("删除");
            }
        });

        tv_ok=(TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_un_get) {
                    String ids=((UnGetFragment) ungetFragment).getIds();
                    if (!TextUtils.isEmpty(ids) && !ids.equals("null")&&ids.length()>0) {
                        deleteShowDailog(ids);
                    } else {
                        Toast.makeText(MessagAlertActivity.this,"请选择消息！",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String ids=((GetFragment) getFragment).getIds();
                    if (!TextUtils.isEmpty(ids) && !ids.equals("null")&&ids.length()>0) {
                        deleteShowDailog(ids);
                    } else {
                        Toast.makeText(MessagAlertActivity.this,"请选择消息！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ((RadioButton) radioGroup.findViewById(R.id.rb_un_get)).setChecked(true);// 设置radiogroup的机制
        ungetFragment=new UnGetFragment();
//        ((UnGetFragment)ungetFragment).setInterface(this);
        getFragment=new GetFragment();
        mFragments=new ArrayList<>();
        mFragments.add(ungetFragment);
        mFragments.add(getFragment);
        tabAdapter = new FragmentTabAdapter(this, mFragments, R.id.book_content, radioGroup);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener()
        {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index)
            {
                // TODO Auto-generated method stub
                super.OnRgsExtraCheckedChanged(radioGroup, checkedId, index);
                switch (checkedId)
                {
                    case R.id.rb_get:
                        layout_read.setVisibility(View.VISIBLE);
                        layout_not_read.setVisibility(View.INVISIBLE);
                        break;

                    case R.id.rb_un_get:
                        layout_not_read.setVisibility(View.VISIBLE);
                        layout_read.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });

        registerMessageReceiver();

    }

    //确认删除弹出框
    public void deleteShowDailog(final String ids) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除选中消息？");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteMsgData(Url.baseUrl+Url.deleteMsg,ids);
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void deleteMsgData(String volleyUrl, final String ids) {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }

        getProgressDialog().show();

        //参数
        Map<String,String> paramsMap=new HashMap<>();
        paramsMap.put("delIds",ids);
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new EdusStringCallback(MessagAlertActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext,e);
                        getProgressDialog().dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        setStore(response);
                    }
                });
    }

    private void setStore(String jsonData) {
        getProgressDialog().dismiss();
        Log.i("123","jsonData===>"+jsonData);
        if (!TextUtils.isEmpty(jsonData)) {
            layout_buttom.setVisibility(View.GONE);
            radioGroup.setVisibility(View.VISIBLE);
            layout_select_bar.setVisibility(View.VISIBLE);
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_un_get) {
                ((UnGetFragment) ungetFragment).setShowAndHide(false);
                ((UnGetFragment) ungetFragment).refreshData();
                int count=getLoginUserSharedPre().getInt("count",0);
                if (count >= Constant.deleteNum) {
                    int countNew=count-Constant.deleteNum;
                    getLoginUserSharedPre().edit().putInt("count",countNew).commit();
                    BadgeUtil.sendBadgeNumber(MessagAlertActivity.this, countNew);
                }
            } else {
                ((GetFragment) getFragment).setShowAndHide(false);
                ((GetFragment) getFragment).refreshData();
            }
            tv_delete.setText("删除");
            Toast.makeText(MessagAlertActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MessagAlertActivity.this,"删除失败！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void initView() {

    }


    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Log.i("123","test===>"+showMsg);
            }
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // 处理返回逻辑
//                finish();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

}
