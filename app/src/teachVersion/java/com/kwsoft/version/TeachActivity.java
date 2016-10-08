package com.kwsoft.version;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.login.ProjectSelectActivity;

public class TeachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach);

        Intent intent = new Intent();
        intent.setClass(this, ProjectSelectActivity.class);
        startActivity(intent);

    }
}
