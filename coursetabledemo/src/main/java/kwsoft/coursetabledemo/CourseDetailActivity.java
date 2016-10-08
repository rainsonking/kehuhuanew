package kwsoft.coursetabledemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CourseDetailActivity extends AppCompatActivity {

    private ImageView IV_back_list_item_tadd;
    private TextView tv_start_time,tv_end_time,tv_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        IV_back_list_item_tadd=(ImageView)findViewById(R.id.IV_back_list_item_tadd);
        IV_back_list_item_tadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_start_time=(TextView) findViewById(R.id.tv_start_time);
        tv_end_time=(TextView) findViewById(R.id.tv_end_time);
        tv_remark=(TextView) findViewById(R.id.tv_remark);
        getSupportActionBar().hide();
        Intent intent=getIntent();
        if (intent!=null) {
            String sTimeStr=intent.getStringExtra("sTimeStr");
            String eTimeStr=intent.getStringExtra("eTimeStr");
            String content=intent.getStringExtra("content");
            tv_start_time.setText(sTimeStr);
            tv_end_time.setText(eTimeStr);
            tv_remark.setText(content);
        }
    }
}
