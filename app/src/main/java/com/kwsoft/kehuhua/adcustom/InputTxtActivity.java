package com.kwsoft.kehuhua.adcustom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class InputTxtActivity extends Activity {
    private EditText mInputValues;
    private Intent intent;
    private Bundle mBundle;
    private  String values;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_txt);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        initView();
    }

    private void initView() {
        intent=getIntent();
        mBundle=intent.getExtras();
        String rightValue=mBundle.getString("value");
        final int fieldId=Integer.parseInt(mBundle.getString("fieldId"));
        final int ifMust=Integer.parseInt(mBundle.getString("ifMust"));


        mInputValues= (EditText) findViewById(R.id.ed_change_values);
        mInputValues.setText(rightValue);
        ImageView mImageViewBack = (ImageView) findViewById(R.id.IV_back_info);
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ImageView mTextChange = (ImageView) findViewById(R.id.IV_edit_text_commit);

        mTextChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                values=mInputValues.getText().toString();
                if (!values.equals("")) {
                        //判断是否满足正则表达式
                        switch(fieldId){
                            case 3973:

                                if(Constant.isIdentityID(values)){
                                    Log.e("TAG","身份证号修改");
                                    successInput(values);
                                }else{
                                    Toast.makeText(InputTxtActivity.this, "请输入正确的身份证号", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3972:

                                if(Constant.isMobileNO(values)){
                                    Log.e("TAG","手机号修改");
                                    successInput(values);
                                }else{

                                    Toast.makeText(InputTxtActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3971:

                                if(Constant.isName(values)){
                                    Log.e("TAG","姓名修改");
                                    successInput(values);
                                }else{
                                    Toast.makeText(InputTxtActivity.this, "姓名中只能包含中文、字母、空格", Toast.LENGTH_SHORT).show();

                                }
                                break;
                            default:
                                successInput(values);
                                break;
                        }

                    } else if (ifMust != 0) {
                        Toast.makeText(InputTxtActivity.this, "必填属性不能为空", Toast.LENGTH_SHORT).show();


                    }else{

                    successInput("");
                }

            }


        });
    }


//    public boolean regExContent(String regEx){
//        Pattern pattern = Pattern.compile(regEx);
//        Matcher matcher = pattern.matcher(values);
//        return matcher.matches();
//    }
    public void successInput(String valuesResult){

        int position = mBundle.getInt("position");
        intent.putExtra("Values", valuesResult);
        intent.putExtra("position", position);
        setResult(position, intent);
        finish();


    }
}
