package com.kwsoft.version;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.wechatPicture.SelectPictureActivity;
import com.kwsoft.kehuhua.widget.CnToolbar;
import com.kwsoft.kehuhua.zxing.CaptureActivity;
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.fragment.AssortFragment;
import com.kwsoft.version.fragment.CourseFragment;
import com.kwsoft.version.fragment.MeFragment;
import com.kwsoft.version.fragment.StuFragmentTabAdapter;
import com.kwsoft.version.fragment.StudyFragment;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;

/**
 * 学员端看板界面
 * wyl
 */
public class StuMainActivity extends BaseActivity implements View.OnClickListener {
    StuFragmentTabAdapter stutabAdapter;
    private RadioGroup radioGroup;
    private RadioButton radio3;
    private String arrStr, menuList, menuDataMap;//看板数据、课程表数据、主菜单数据
    private CnToolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_main);
//        getSupportActionBar().hide();
        CloseActivityClass.activityList.add(this);
        initView();
        initFragment();
    }

    @Override
    public void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        ((RadioButton) radioGroup.findViewById(R.id.radio0)).setChecked(true);// 设置radiogroup的机制

        RadioButton radio0 = (RadioButton) findViewById(R.id.radio0);
        RadioButton radio1 = (RadioButton) findViewById(R.id.radio1);
        RadioButton radio2 = (RadioButton) findViewById(R.id.radio2);
        radio3 = (RadioButton) findViewById(R.id.radio3);

        Intent intent = getIntent();
        arrStr = intent.getStringExtra("jsonArray");
        menuList = intent.getStringExtra("menuList");

        menuDataMap = intent.getStringExtra("menuDataMap");


        mToolbar = (CnToolbar) findViewById(R.id.stu_toolbar);
//        Resources resources = mContext.getResources().getDrawable(R.drawable.nav_news);
//        Drawable drawable = resources.getDrawable(R.drawable.nav_news);
//        mToolbar.setRightButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_news));
//        mToolbar.setLeftButtonIcon(mContext.getResources().getDrawable(R.drawable.nav_scan_code));
        mToolbar.setTitle("学习");
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(StuMainActivity.this, MessagAlertActivity.class);
                startActivity(intent2);
            }
        });

        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionGen.with(StuMainActivity.this)
                        .addRequestCode(105)
                        .permissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request();
            }
        });

    }


    public void initFragment() {
        Fragment studyFragment = new StudyFragment();
        Fragment courseFragment = new CourseFragment();
        AssortFragment menuFragment = new AssortFragment();
        Fragment meFragment = new MeFragment();

        Bundle studyBundle = new Bundle();
        studyBundle.putString("arrStr", arrStr);
        studyBundle.putString("menuDataMap", menuDataMap);
        studyFragment.setArguments(studyBundle);

        Bundle courseBundle = new Bundle();
        courseBundle.putString("menuList", menuList);
        courseFragment.setArguments(courseBundle);

        Bundle menuBundle = new Bundle();
        menuBundle.putString("menuDataMap", menuDataMap);
        menuFragment.setArguments(menuBundle);

        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(studyFragment);
        mFragments.add(menuFragment);
        mFragments.add(courseFragment);
        mFragments.add(meFragment);
        stutabAdapter = new StuFragmentTabAdapter(this, mFragments, R.id.content, radioGroup);

        stutabAdapter.setOnRgsExtraCheckedChangedListener(new StuFragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                // TODO Auto-generated method stub
                super.OnRgsExtraCheckedChanged(radioGroup, checkedId, index);
                switch (checkedId) {
                    case R.id.radio0:
                        mToolbar.setTitle("学习");
                        break;
                    case R.id.radio1:
                        mToolbar.setTitle("课程表");
                        break;
                    case R.id.radio2:
                        mToolbar.setTitle("学员端");
                        break;
                    case R.id.radio3:
                        mToolbar.setTitle("分类");
                        break;

                }
            }
        });
    }

    public void fragmentClick() {
        radio3.setChecked(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 105)
    public void doCapture() {
        toCamera();
    }

    @PermissionFail(requestCode = 105)
    public void doFailedCapture() {
        Toast.makeText(StuMainActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    public void toCamera() {

        Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 1);

//        boolean emui = AndtoidRomUtil.isEMUI();
//        boolean miui = AndtoidRomUtil.isMIUI();
//        boolean flyme = AndtoidRomUtil.isFlyme();
//
//        if (emui) {
//            //华为
////                    PackageManager pm = getActivity().getPackageManager();
////                    //MediaStore.ACTION_IMAGE_CAPTURE android.permission.RECORD_AUDIO
////                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
////                            pm.checkPermission("MediaStore.ACTION_IMAGE_CAPTURE", "packageName"));
////                    if (permission) {
////                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
////                        startActivityForResult(intent, 1);
////                    } else {
////                        Constant.goHuaWeiSetting(getActivity());
////                    }
//            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, 1);
//        } else if (miui) {
//            //小米
//            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, 1);
//        } else if (flyme) {
//            //魅族rom
//            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, 1);
//        }else {
//            Intent intent = new Intent(StuMainActivity.this, CaptureActivity.class);
//            startActivityForResult(intent, 1);
//        }
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
