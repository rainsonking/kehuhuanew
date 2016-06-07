package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kwsoft.kehuhua.bean.LoginError;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.utils.VolleySingleton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rainson on 2015/12/5 0005.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private TextView btnLogin;
    private EditText mUserName, mPassword;
    private String nameValue, pwdValue;
    private CheckBox checkBox;
    private SharedPreferences sPreferences;
    private Map<String, String> map;
    private ListView listView;
    private ImageView select;
    private PopupWindow pw;
    private int width, i;
    private LinearLayout parent, option;
    private DiskLruCacheHelper DLCH;
    private long exitTime=0;// 退出时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloseActivityClass.activityList.add(this);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 透明底部导航栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        try {
            DLCH = new DiskLruCacheHelper(LoginActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initView();
    }

    //初始化用户名输入框、密码输入框、登陆按钮、取消按钮
    private void initView() {
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        mUserName = (EditText) findViewById(R.id.mUserName);
        mPassword = (EditText) findViewById(R.id.mPassword);
        parent = (LinearLayout) findViewById(R.id.llayout);
        select = (ImageView) findViewById(R.id.select);
        select.setOnClickListener(this);
        btnLogin.setOnClickListener(this);


        // 读取已经记住的用户名与密码
        sPreferences = getSharedPreferences("session", MODE_PRIVATE);
        map = (Map<String, String>) sPreferences.getAll();
        //设置默认用户名密码
        int k = map.size() / 2;
        if (k > 0) {//如果存在账户
            //取出用户名和密码并直接跳转至登录页面
            mUserName.setText(sPreferences.getString("name" + (k - 1), ""));
            mPassword.setText(sPreferences.getString("pwd" + (k - 1), ""));
        }

        List<String> list = new ArrayList<String>();

        for (int i = 0; i < (map.size() / 2); i++) {
            String name = sPreferences.getString("name" + i, "");
            list.add(name);
        }

        // 用4个参数的指定，哪个listview中的textview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.items, R.id.item, list);
        option = (LinearLayout) getLayoutInflater().inflate(R.layout.option, null);
        // 要在这个linearLayout里面找listView......
        listView = (ListView) option.findViewById(R.id.op);
        listView.setAdapter(adapter);

        // 获取屏幕的宽度并设置popupwindow的宽度为width,我这里是根据布局控件所占的权重
        WindowManager wManager = (WindowManager) getSystemService(this.WINDOW_SERVICE);
        width = wManager.getDefaultDisplay().getWidth() * 2 / 5;

        // 实例化一个popupwindow对象
        pw = new PopupWindow(option, width, RadioGroup.LayoutParams.WRAP_CONTENT, true);
        ColorDrawable dw = new ColorDrawable(00000);
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

    }

    //按钮事件控制
    @Override
    public void onClick(View view) {
        int i = 0;
        switch (view.getId()) {
            case R.id.btnLogin:
                postLogin();
                break;
            case R.id.select:
                //选择输入过的账户和密码
                pw.showAsDropDown(select, 15, -4);
                break;
           default:
                break;
        }
    }

    /**
     * 根据用户输入的用户名和密码，
     * 通过网络地址获取JSON数据，
     * 返回后直接传递给主页面
     **/
    public void postLogin() {
        nameValue = mUserName.getText().toString();//trim去掉首尾空格
        pwdValue = mPassword.getText().toString();
        Log.e("TAG","用户名密码已经输入");
        if (!nameValue.equals("") && !pwdValue.equals("")) {//判断用户名密码非空
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, Constant.baseUrl+Constant.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String menuData) {
                         DLCH.put(Constant.baseUrl + Constant.LOGIN_URL + Constant.USERNAME_ALL, menuData);
                            Log.e("TAG","数据已经获取");
                            check(menuData);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    VolleySingleton.onErrorResponseMessege(LoginActivity.this, volleyError);
                    String diskData = DLCH.getAsString(Constant.baseUrl + Constant.LOGIN_URL + Constant.USERNAME_ALL);

                    check(diskData);
                }
            }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(Constant.USER_NAME, nameValue);//"15535211113"
                    map.put(Constant.PASSWORD, pwdValue);//"111111"
                    return map;
                }

            };
            VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(loginInterfaceData);
        } else {

            Toast.makeText(LoginActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
        }
    }

    //此方法传递菜单JSON数据
    private void mainPage(String menuStr) {
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, NavActivity.class);
        intent.putExtra("menu", menuStr);

        startActivity(intent);
        finish();
    }

    //解析获得的data数据中的erro值，如果它为1
    // 则提示用户名密码输入问题，sp中并不存储
    // 新密码，为0则跳转，sp存储新密码

    private void check(String menuData) {
        if(menuData!=null) {
            //获取error的值，判断
            Log.e("TAG", "menuData" + menuData);
            LoginError loginError = JSON.parseObject(menuData, LoginError.class);
            if (loginError.getError() != 0) {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
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
                mainPage(menuData);//保存完用户名和密码，跳转到主页面
            }
        }else{
            Toast.makeText(LoginActivity.this, "服务器超时", Toast.LENGTH_SHORT).show();

        }
    }


    //返回桌面
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO 按两次返回键退出应用程序
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                CloseActivityClass.exitClient(LoginActivity.this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
