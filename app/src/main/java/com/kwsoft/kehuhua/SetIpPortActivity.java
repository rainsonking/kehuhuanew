package com.kwsoft.kehuhua;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.config.Constant.sysUrl;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class SetIpPortActivity extends AppCompatActivity {

    @Bind(R.id.sys_ip_et)
    EditText sysIpEt;
    @Bind(R.id.sys_port_et)
    EditText sysPortEt;
    @Bind(R.id.sys_project_et)
    EditText sysProjectEt;
    @Bind(R.id.now_sys_url)
    TextView nowSysUrl;
    private CommonToolbar mToolbar;
    private SharedPreferences sPreferences;
    private static final String TAG = "SetIpPortActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip_port);
        ButterKnife.bind(this);

        initView();

    }

    @SuppressWarnings("unchecked")
    private void initView() {
        nowSysUrl.setText(sysUrl);
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle("地址设置");
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.drawable.edit_commit1));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sPreferences = getSharedPreferences("edusIpPortProjectName", MODE_PRIVATE);
        Map<String, String> map = (Map<String, String>) sPreferences.getAll();

        if (map.size() > 0) {//如果存在储存值则设置，否则为text中默认填写值
            sysIpEt.setText(sPreferences.getString("edus_ip", ""));
            sysPortEt.setText(sPreferences.getString("edus_port", ""));
            sysProjectEt.setText(sPreferences.getString("edus_project", ""));
        }

        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ip = String.valueOf(sysIpEt.getText()).replace(" ", "");
                final String port = String.valueOf(sysPortEt.getText()).replace(" ", "");
                final String project = String.valueOf(sysProjectEt.getText()).replace(" ", "");


                if (!ip.equals("") && !project.equals("")) {
                    String url;
                    if (port.equals("")) {
                        url =ip + "/" + project + "/";
                    } else {
                        url =ip + ":" + port + "/" + project + "/";
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(SetIpPortActivity.this);
                    builder.setMessage("确定修改项目地址为：" + url + "？");
                    builder.setTitle("");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initIpData(ip, port, project);
                            finish();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();


                } else {

                    Snackbar.make(view, "请至少将第1、3填写完整（空格将自动过滤）", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initIpData(String ip, String port, String project) {
        if (port.equals("")) {
            sysUrl =ip + "/" + project + "/";
        } else {
            sysUrl =ip + ":" + port + "/" + project + "/";
        }
        sPreferences.edit().putString("edus_project", project).apply();
        sPreferences.edit().putString("edus_ip", ip).apply();
        sPreferences.edit().putString("edus_port", port).apply();

    }


}
