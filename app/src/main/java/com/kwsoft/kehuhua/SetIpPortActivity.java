package com.kwsoft.kehuhua;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.widget.CommonToolbar;

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
    private CommonToolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip_port);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle("重设IP");
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.drawable.edit_commit1));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip= String.valueOf(sysIpEt.getText()).replace(" ", "");
                String port= String.valueOf(sysPortEt.getText()).replace(" ", "");
                String project= String.valueOf(sysProjectEt.getText()).replace(" ", "");


                if (!ip.equals("")&&!port.equals("")&&!project.equals("")) {

                    sysUrl="http://"+ip+":"+port+"/"+project+"/";
                    AlertDialog.Builder builder = new AlertDialog.Builder(SetIpPortActivity.this);
                    builder.setMessage("确定修改项目地址为："+sysUrl+"？");
                    builder.setTitle("");
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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


                }else{

                    Snackbar.make(view, "请将这三项填写完整（空格将自动过滤）", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }




}
