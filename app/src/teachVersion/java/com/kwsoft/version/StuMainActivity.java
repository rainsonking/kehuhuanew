package com.kwsoft.version;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.kwsoft.kehuhua.zxing.CaptureActivity;
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.fragment.MeFragment;
import com.kwsoft.version.fragment.MenuFragment;
import com.kwsoft.version.fragment.StuFragmentTabAdapter;
import com.kwsoft.version.fragment.StudyFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 学员端看板界面
 * wyl
 */
public class StuMainActivity extends BaseActivity implements View.OnClickListener {
    StuFragmentTabAdapter stutabAdapter;
    private RadioGroup radioGroup;
    private RadioButton radio3;
    private String arrStr;
    private String menuDataMap;//看板数据、课程表数据、主菜单数据
    private CommonToolbar mToolbar;
    private String hideMenuList;//获取我的界面中的tableid pageid

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);
//        getSupportActionBar().hide();
        CloseActivityClass.activityList.add(this);
        initView();
        initFragment();
         initDialog();
    }

    public void initDialog() {

        CustomDialog.Builder builder = new CustomDialog.Builder(StuMainActivity.this);
//                builder.setMessage("这个就是自定义的提示框");
        builder.setTitle("入园须知");
        builder.setPositiveButton("我知道了！", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //设置你的操作事项
            }
        });

        builder.setNegativeButton("",
                new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ((RadioButton) radioGroup.findViewById(R.id.radio0)).setChecked(true);// 设置radiogroup的机制

        RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
        RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);

        Intent intent = getIntent();
        arrStr = intent.getStringExtra("jsonArray");
        String menuList = intent.getStringExtra("menuList");

        menuDataMap = intent.getStringExtra("menuDataMap");
        hideMenuList = intent.getStringExtra("hideMenuList");


        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
//        Resources resources = mContext.getResources().getDrawable(R.drawable.nav_news);
//        Drawable drawable = resources.getDrawable(R.drawable.nav_news);
//        mToolbar.setRightButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_news));
//        mToolbar.setLeftButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_scan_code));
        mToolbar.setTitle("教务客户化平台");
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.drawable.nav_news));

        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(StuMainActivity.this, MessagAlertActivity.class);
                startActivity(intent2);
            }
        });

//        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toCamera();
//            }
//        });

    }


    public void initFragment() {
        Fragment studyFragment = new StudyFragment();
        MenuFragment menuFragment = new MenuFragment();
        Fragment meFragment = new MeFragment();

        Bundle studyBundle = new Bundle();
        studyBundle.putString("arrStr", arrStr);
        studyBundle.putString("menuDataMap", menuDataMap);
        studyFragment.setArguments(studyBundle);

        Bundle menuBundle = new Bundle();
        menuBundle.putString("menuDataMap", menuDataMap);
        menuFragment.setArguments(menuBundle);

        Bundle meBundle = new Bundle();
        meBundle.putString("hideMenuList", hideMenuList);
        meFragment.setArguments(meBundle);

        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(studyFragment);
        mFragments.add(menuFragment);
        mFragments.add(meFragment);
        stutabAdapter = new StuFragmentTabAdapter(this, mFragments, R.id.content, radioGroup);

        stutabAdapter.setOnRgsExtraCheckedChangedListener(new StuFragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                // TODO Auto-generated method stub
                super.OnRgsExtraCheckedChanged(radioGroup, checkedId, index);
                switch (checkedId) {
                    case R.id.radio0:

                        break;
                    case R.id.radio2:

                        break;
                    case R.id.radio3:

                        break;

                }
            }
        });
    }

    public void fragmentClick() {
        radio3.setChecked(true);
    }


    public void toCamera() {

        boolean emui = AndtoidRomUtil.isEMUI();
        boolean miui = AndtoidRomUtil.isMIUI();
        boolean flyme = AndtoidRomUtil.isFlyme();

        if (emui) {
            //华为
//                    PackageManager pm = getActivity().getPackageManager();
//                    //MediaStore.ACTION_IMAGE_CAPTURE android.permission.RECORD_AUDIO
//                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                            pm.checkPermission("MediaStore.ACTION_IMAGE_CAPTURE", "packageName"));
//                    if (permission) {
//                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                        startActivityForResult(intent, 1);
//                    } else {
//                        Constant.goHuaWeiSetting(getActivity());
//                    }
            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 1);
        } else if (miui) {
            //小米
            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 1);
        } else if (flyme) {
            //魅族rom
            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private static long exitTime = 0;// 退出时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {

                String msg = "再按一次退出";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                Toast.makeText(this, "直接退出", Toast.LENGTH_SHORT).show();
                CloseActivityClass.exitClient(this);
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                toCamera();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

    }
}
