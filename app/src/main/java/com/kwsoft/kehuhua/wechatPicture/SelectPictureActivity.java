package com.kwsoft.kehuhua.wechatPicture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kwsoft.kehuhua.adcustom.AddItemsActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.RowsAddActivity;
import com.kwsoft.kehuhua.adcustom.RowsEditActivity;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.MultipartRequest;
import com.kwsoft.kehuhua.utils.VolleySingleton;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.iwf.photopicker.PhotoPicker;

import static com.kwsoft.kehuhua.config.Constant.img_Paths;
import static com.kwsoft.kehuhua.config.Constant.pictureUrl;
import static com.kwsoft.kehuhua.config.Constant.sysUrl;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

//import static com.google.gson.jpush.internal.a.z.R;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public class SelectPictureActivity extends BaseActivity implements View.OnClickListener {
    private CommonToolbar mToolbar;
    String position;
    @Bind(R.id.gridView)
    GridView gridView;
    private ArrayList<String> imgPaths = new ArrayList<>();
    private PhotoPickerAdapter adapter;

    String codeListStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture_layout);
        ButterKnife.bind(this);
        initView();
//        initData();
//        setListener();
    }

    public void initView() {
        Intent intent = getIntent();
        position = intent.getStringExtra("position");


        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);
        mToolbar.setTitle("作业");
        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));
        //左侧返回按钮
        mToolbar.setRightButtonIcon(getResources().getDrawable(R.mipmap.nav_scan_file));
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mToolbar.showRightImageButton();
        //右侧下拉按钮
        mToolbar.setRightButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                upload();
            }
        });


        adapter = new PhotoPickerAdapter(imgPaths);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == imgPaths.size()) {
                    PermissionGen.with(SelectPictureActivity.this)
                            .addRequestCode(100)
                            .permissions(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .request();
//                    PhotoPicker.builder()
//                            .setPhotoCount(9)
//                            .setShowCamera(true)
//                            .setSelected(imgPaths)
//                            .setShowGif(true)
//                            .setPreviewEnabled(true)
//                            .start(SelectPictureActivity.this, PhotoPicker.REQUEST_CODE);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("imgPaths", imgPaths);
                    bundle.putInt("position", position);
                    goToActivityForResult(SelectPictureActivity.this, EnlargePicActivity.class, bundle, position);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 100)
    public void doCapture() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setSelected(imgPaths)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(SelectPictureActivity.this, PhotoPicker.REQUEST_CODE);
    }

    @PermissionFail(requestCode = 100)
    public void doFailedCapture() {
        Toast.makeText(SelectPictureActivity.this, "获取权限失败", Toast.LENGTH_SHORT).show();
    }

    public void goToActivityForResult(Context context, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, cls);
        if (bundle == null) {
            bundle = new Bundle();
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                imgPaths.clear();
                ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                imgPaths.addAll(photos);
                adapter.notifyDataSetChanged();
            }
        }

        if (resultCode == RESULT_OK && requestCode >= 0 && requestCode <= 8) {
            imgPaths.remove(requestCode);
            adapter.notifyDataSetChanged();
        }
        img_Paths.clear();
        img_Paths.addAll(imgPaths);

    }


    //上传文件
    public void upload() {

        String url = sysUrl + pictureUrl;
        //待上传的两个文件
        List<File> files = new ArrayList<>();
        if (img_Paths.size() > 0) {
            for (int i = 0; i < img_Paths.size(); i++) {

                files.add(new File(img_Paths.get(i)));
            }
//                uploadMethod(params, uploadHost);
            //请求的URL
            //post请求，三个参数分别是请求地址、请求参数、请求的回调接口
            Log.e("TAG", "listPath.toString()" + img_Paths.toString());

            if (files.size() > 0) {
                Log.e("TAG", "files.toString()" + files.toString());
                uploadMethod(url, files);
            }
        } else {
            Toast.makeText(SelectPictureActivity.this, "尚未选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadMethod(String url, List<File> files) {
        Log.e("TAG", "uploadMethod1");
        MultipartRequest request = new MultipartRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                getFileCode(response);


//                TrendCreateHttpManager.toTrendCreateHttpActionSuccess();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toast.makeText(SelectPictureActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
//                TrendCreateHttpManager.toTrendCreateHttpActionError();
            }
        }, "myFiles", files, null) {


            //重写getHeaders 默认的key为cookie，value则为localCookie
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (Constant.localCookie != null && Constant.localCookie.length() > 0) {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("cookie", Constant.localCookie);
                    //Log.d("调试", "headers----------------" + headers);
                    return headers;
                } else {
                    return super.getHeaders();
                }
            }
        };
        VolleySingleton.getVolleySingleton(SelectPictureActivity.this).addToRequestQueue(
                request);
    }


    //解析文件上传成功的code值
    private void getFileCode(String response) {
        codeListStr = "";
        Log.e("TAG", "uploadMethod2:" + response);
        Toast.makeText(SelectPictureActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
        List<Integer> codeList = new ArrayList<>();
        if (response.contains(":")) {
            String[] value = response.split(",");
            for (String valueTemp : value) {
                String[] valueTemp1 = valueTemp.split(":");
                int valueCode = Integer.valueOf(valueTemp1[1]);
                codeList.add(valueCode);
            }
            Log.e("TAG", "文件上传codeList:" + codeList.toString());
            int leg = codeList.size();
            if (leg > 0) {
                for (int i = 0; i < leg; i++) {
                    if (i == (leg - 1)) {
                        codeListStr = codeListStr + codeList.get(i);
                    } else {
                        codeListStr = codeListStr + codeList.get(i) + ",";
                    }
                }
            }
        } else {
            Toast.makeText(SelectPictureActivity.this, "文件值解析出现问题", Toast.LENGTH_SHORT).show();
        }
        Log.e("TAG", "文件上传码codeListStr:" + codeListStr);
        jump2Activity();
    }


    private void jump2Activity() {
        Intent intentTree = new Intent();
        if (Constant.jumpNum == 1) {
            intentTree.setClass(SelectPictureActivity.this, AddItemsActivity.class);
        } else if (Constant.jumpNum == 2) {
            intentTree.setClass(SelectPictureActivity.this, RowsEditActivity.class);
        } else if (Constant.jumpNum == 3) {
            intentTree.setClass(SelectPictureActivity.this, RowsAddActivity.class);
        }

        Bundle bundle = new Bundle();

        bundle.putString("position", position);
        bundle.putString("codeListStr", codeListStr);
        intentTree.putExtra("bundle", bundle);
        setResult(101, intentTree);
        this.finish();
    }

    @Override
    public void onClick(View view) {

    }
}
