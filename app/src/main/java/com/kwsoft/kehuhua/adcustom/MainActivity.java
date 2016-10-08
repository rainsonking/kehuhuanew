package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.os.Bundle;

import com.kwsoft.kehuhua.utils.CloseActivityClass;

public class MainActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CloseActivityClass.activityList.add(this);
        setContentView(R.layout.activity_main);

    }

}
