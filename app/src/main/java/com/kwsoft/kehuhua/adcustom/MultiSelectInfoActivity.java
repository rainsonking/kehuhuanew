package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kwsoft.kehuhua.bean.AddStuSchBean;
import com.kwsoft.kehuhua.utils.CloseActivityClass;

/**
 * Created by Administrator on 2015/12/8 0008.
 */
public class MultiSelectInfoActivity extends Activity implements View.OnClickListener {
    private TextView tv_address, tv_state;
    private TextView textView2;
  //  private TextView tv_cancle;
    private ImageView iv_back, imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_cbdialog_schoolinfo);
        CloseActivityClass.activityList.add(this);
        initView();

        AddStuSchBean rows = (AddStuSchBean) getIntent().getSerializableExtra("rows");

        tv_address.setText(rows.getName());
        tv_state.setText(rows.getDicafm());
    }

    private void initView() {
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_state = (TextView) findViewById(R.id.tv_state);
        textView2 = (TextView) findViewById(R.id.textView2);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        imageView = (ImageView) findViewById(R.id.imageView);

        textView2.setText("校区详情");
//        tv_cancle = (TextView) findViewById(R.id.tv_cancle);
//        tv_cancle.setVisibility(View.VISIBLE);
        iv_back.setOnClickListener(this);
        iv_back.setImageResource(R.mipmap.iv_back_2);
        iv_back.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
