package com.kwsoft.kehuhua.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.ExampleUtil;
import com.kwsoft.kehuhua.adcustom.NavActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.BadgeUtil;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.Call;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.headline)
    TextView headline;
    private EditText mUserName, mPassword;
    private String nameValue, pwdValue;
    private CheckBox checkBox;
    private SharedPreferences sPreferences;
    private Map<String, String> map;
    //private ImageView select;
    private PopupWindow pw;
    private int i;
    //private Spinner projectSelectSpinner;
    private DiskLruCacheHelper DLCH;

    //private long exitTime = 0;// 退出时间
    //private List<Map<String, Object>> projectListMap=new ArrayList<>();

    {
        //主项目设置成顶栏红色
              Constant.topBarColor=R.color.prim_topBarColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        CloseActivityClass.activityList.add(this);
        // 透明底部导航栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        try {
            DLCH = new DiskLruCacheHelper(LoginActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //CloseActivityClass.exitClient(this);
        //getData();
        initView();

//        projectSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int positionDic, long id) {
//                Constant.proId = "" + projectListMap.get(positionDic).get("proId");
//                Constant.sysUrl="http://"+String.valueOf(projectListMap.get(positionDic).get("programa_url"))+":"+
//                        String.valueOf(projectListMap.get(positionDic).get("pro_port"))+"/"+String.valueOf(projectListMap.get(positionDic).get("pro_en_name"))+"/";
//                Log.e("TAG", "项目主地址 "+Constant.sysUrl);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

//    private void getData() {
//        requestProjectList();
//    }

    /**
     * 第一步，获取项目列表
     */
//    public void requestProjectList() {
//        startAnim();
//        final String volleyUrl = Constant.sysUrl + Constant.sysLoginUrl;
//        Log.e("TAG", "请求地址" + volleyUrl);
//        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, volleyUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String sysData) {
//                        DLCH.put(volleyUrl, sysData);
//                        analysisData(sysData);
//                        Log.e("TAG", "项目列表：" + sysData);
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                VolleySingleton.onErrorResponseMessege(LoginActivity.this, volleyError);
//                stopAnim();
//
//            }
//        }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("source", "1");
//                return map;
//            }
//
//        };
//        VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
//
//    }


//    public void analysisData(String jsonData) {
//        try {
//            projectListMap = JSON.parseObject(jsonData,
//                    new TypeReference<List<Map<String, Object>>>() {
//                    });
//            SimpleAdapter simpleAdapter = new SimpleAdapter(LoginActivity.this,
//                    projectListMap, R.layout.login_spinner_item,
//                    new String[]{"programa_name"}, new int[]{R.id.login_spinner_item});
//            projectSelectSpinner.setAdapter(simpleAdapter);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(LoginActivity.this, "项目信息错误", Toast.LENGTH_SHORT).show();
//        }
//        if (projectListMap.size()>0) {
//            projectSelectSpinner.setSelection(0, true);
//        }
//        stopAnim();
//    }


    //初始化用户名输入框、密码输入框、登陆按钮、取消按钮
    @SuppressWarnings("unchecked")
    public void initView() {
        TextView btnLogin = (TextView) findViewById(R.id.btnLogin);
        mUserName = (EditText) findViewById(R.id.mUserName);
        mPassword = (EditText) findViewById(R.id.mPassword);
//        mPassword.setKeyListener(DialerKeyListener.getInstance());
        headline.setText(Constant.proName);
        //projectSelectSpinner= (Spinner) findViewById(R.id.project_select);
        //LinearLayout parent = (LinearLayout) findViewById(R.id.llayout);
        //select = (ImageView) findViewById(R.id.select);
        //select.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        // 读取已经记住的用户名与密码
        sPreferences = getSharedPreferences(Constant.proId, MODE_PRIVATE);
        map = (Map<String, String>) sPreferences.getAll();
        //设置默认用户名密码
        int k = map.size() / 2;
        if (k > 0) {//如果存在账户
            //取出用户名和密码并直接跳转至登录页面
            mUserName.setText(sPreferences.getString("name" + (k - 1), ""));
            mPassword.setText(sPreferences.getString("pwd" + (k - 1), ""));
        }
        List<String> list = new ArrayList<>();
        for (int i = 0; i < (map.size() / 2); i++) {
            String name = sPreferences.getString("name" + i, "");
            list.add(name);
        }

        // 用4个参数的指定，哪个listview中的textview
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.items, R.id.item, list);
        LinearLayout option = (LinearLayout) getLayoutInflater().inflate(R.layout.option, null);
        // 要在这个linearLayout里面找listView......
        ListView listView = (ListView) option.findViewById(R.id.op);
        listView.setAdapter(adapter);
        // 获取屏幕的宽度并设置popupwindow的宽度为width,我这里是根据布局控件所占的权重
        WindowManager wManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = wManager.getDefaultDisplay().getWidth() * 2 / 5;
        // 实例化一个popupwindow对象
        pw = new PopupWindow(option, width, RadioGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable dw = new ColorDrawable(0);
        pw.setBackgroundDrawable(dw);
        pw.setOutsideTouchable(true);
        pw.setFocusable(true);//这里必须设置为true才能点击区域外或者消失
        pw.setTouchable(true);//这个控制PopupWindow内部控件的点击事件
        pw.setOutsideTouchable(true);
        pw.update();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // 获取选中项内容及从sharePreferences中获取对应的密码
                String username = adapterView.getItemAtPosition(position).toString();
                String pwd = sPreferences.getString("pwd" + position, "");
                mUserName.setText(username);
                mPassword.setText(pwd);
                pw.dismiss(); // 选择后，popupwindow自动消失
            }
        });


        if (DLCH.getAsString(Constant.sysUrl + Constant.projectLoginUrl + Constant.USERNAME_ALL + Constant.proId + "menuAlterTime") != null) {
            Constant.menuAlterTime = DLCH.getAsString(Constant.sysUrl + Constant.projectLoginUrl + Constant.USERNAME_ALL + Constant.proId + "menuAlterTime");

        }


    }

    //按钮事件控制
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                stopAnim();
                startAnim();
                postLogin();
                break;
//            case R.id.select:
//                //选择输入过的账户和密码
//                pw.showAsDropDown(select, 15, -4);
//                break;
            default:
                break;
        }
    }

    private static final String TAG = "LoginActivity";
    /**
     * 根据用户输入的用户名和密码，
     * 通过网络地址获取JSON数据，
     * 返回后直接传递给主页面
     **/
    public void postLogin() {
        if (!hasInternetConnected()) {
            Toast.makeText(this, "当前网络不可用，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        nameValue = mUserName.getText().toString();//trim去掉首尾空格
        pwdValue = mPassword.getText().toString();
        if (!nameValue.equals("") && !pwdValue.equals("")) {//判断用户名密码非空
            final String volleyUrl = Constant.sysUrl + Constant.projectLoginUrl;
            Log.e("TAG", "准备登陆volleyUrl " + Constant.sysUrl + Constant.projectLoginUrl);

            //参数
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(Constant.USER_NAME, nameValue);
            paramsMap.put(Constant.PASSWORD, pwdValue);
            paramsMap.put(Constant.proIdName, Constant.proId);
            paramsMap.put(Constant.timeName, Constant.menuAlterTime);
            paramsMap.put(Constant.sourceName, Constant.sourceInt);
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            stopAnim();
                            Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: "+"  id  "+id);
                            check(response);
                        }
                    });
        } else {
            stopAnim();
            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    //此方法传递菜单JSON数据
    private void mainPage() {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, NavActivity.class);
        Log.e("TAG", "跳转到主页面");
        stopAnim();
        startActivity(intent);
        finish();
    }

    //解析获得的data数据中的error值，如果它为1
    // 则提示用户名密码输入问题，sp中并不存储
    // 新密码，为0则跳转，sp存储新密码

    private void check(String menuData) {
        if (menuData != null) {
            //获取error的值，判断
            LoginError loginError = JSON.parseObject(menuData, LoginError.class);
            if (loginError.getError() != 0) {
                stopAnim();
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("TAG", "登陆成功后的menu数据" + menuData);
                //当成功登陆后存储正确的用户名和密码,
                Constant.USERNAME_ALL = nameValue;
                Constant.PASSWORD_ALL = pwdValue;
                //保存菜单数据
                DLCH.put(Constant.USERNAME_ALL + Constant.PREURL, menuData);
                //跳转至主页面并传递菜单数据
                if (i == 0) {
                    i = map.size() / 2;
                }
                sPreferences.edit().putString("name" + i, nameValue).putString("pwd" + i, pwdValue).apply();
                i++;
                getLoginName(menuData);
                if (Constant.menuIsAlter == 1) {
                    DLCH.put(Constant.sysUrl + Constant.projectLoginUrl + Constant.USERNAME_ALL + Constant.proId, menuData);
                    menuData = menuData;

                } else {

                    String menuDataDisk = "";
                    try {
                        menuDataDisk = DLCH.getAsString(Constant.sysUrl + Constant.projectLoginUrl + Constant.USERNAME_ALL + Constant.proId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("TAG", "本地存储的菜单数据" + menuDataDisk);
                    menuData = menuDataDisk;
                }
                mainPage();//保存完用户名和密码，跳转到主页面
            }
        } else {
            Toast.makeText(LoginActivity.this, "服务器超时", Toast.LENGTH_SHORT).show();

        }
    }

    @SuppressWarnings("unchecked")
    public void getLoginName(String menuData) {

        Map<String, Object> menuMap = JSON.parseObject(menuData,
                new TypeReference<Map<String, Object>>() {
                });
        String countStr = String.valueOf(menuMap.get("notMsgCount"));
        if (!TextUtils.isEmpty(countStr) && !countStr.equals("null")) {
            int count = Integer.parseInt(countStr);
            getLoginUserSharedPre().edit().putInt("count", count).commit();
            BadgeUtil.sendBadgeNumber(LoginActivity.this, count);
        } else {
            BadgeUtil.sendBadgeNumber(LoginActivity.this, 0);
        }
        if (menuMap.get("loginInfo") != null) {

            try {
                Map<String, Object> loginInfo = (Map<String, Object>) menuMap.get("loginInfo");
                if (loginInfo.get("USERNAME") != null) {
                    Log.e("TAG", "USERNAME" + loginInfo.get("USERNAME"));
                    Constant.loginName = String.valueOf(loginInfo.get("USERNAME"));
                    Toast.makeText(LoginActivity.this, "欢迎登陆：" + Constant.loginName, Toast.LENGTH_SHORT).show();
                    Constant.USERID = String.valueOf(loginInfo.get("USERID"));
                    //xiebubiao修改
                    setAlias(Constant.USERID);
                    Constant.menuAlterTime = "";
                    DLCH.put(Constant.sysUrl + Constant.projectLoginUrl + Constant.USERNAME_ALL + Constant.proId + "menuAlterTime", Constant.menuAlterTime);
                    Constant.menuIsAlter = Integer.valueOf(String.valueOf(menuMap.get("isAlter")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void startAnim() {
        findViewById(R.id.avloadingIndicatorViewLayout).setVisibility(View.VISIBLE);
    }

    void stopAnim() {
        findViewById(R.id.avloadingIndicatorViewLayout).setVisibility(View.GONE);
    }


    /**
     * xiebubiao修改
     */
    private void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
            Toast.makeText(LoginActivity.this, R.string.error_alias_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ExampleUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(LoginActivity.this, R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private static final int MSG_SET_ALIAS = 1001;
    private static final int MSG_SET_TAGS = 1002;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d("123", "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;

                case MSG_SET_TAGS:
                    Log.d("123", "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;

                default:
                    Log.i("123", "Unhandled msg - " + msg.what);
            }
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i("123", logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("123", logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i("123", "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("123", logs);
            }

//            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i("123", logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i("123", logs);
                    if (ExampleUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i("123", "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e("123", logs);
            }

//            ExampleUtil.showToast(logs, getApplicationContext());
        }

    };


}
