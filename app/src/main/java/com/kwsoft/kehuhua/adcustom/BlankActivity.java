package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kwsoft.kehuhua.utils.CloseActivityClass;

public class BlankActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        ImageView  IV_back_list= (ImageView) findViewById(R.id.personal_center_back_home);
        assert IV_back_list != null;
        IV_back_list.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
          switch(v.getId()){

              case R.id.personal_center_back_home:

                 this.finish();
                  break;


          }




    }
}
