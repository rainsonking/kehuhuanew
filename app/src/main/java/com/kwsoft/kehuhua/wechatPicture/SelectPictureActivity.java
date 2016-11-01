package com.kwsoft.kehuhua.wechatPicture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.OperateDataActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.wechatPicture.andio.AudioRecordButton;
import com.kwsoft.kehuhua.wechatPicture.andio.MediaManager;
import com.kwsoft.kehuhua.wechatPicture.andio.Recorder;
import com.kwsoft.kehuhua.wechatPicture.andio.RecorderAdapter;
import com.kwsoft.kehuhua.widget.CommonToolbar;
import com.zhy.http.okhttp.OkHttpUtils;

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
import okhttp3.Call;

import static com.kwsoft.kehuhua.config.Constant.img_Paths;
import static com.kwsoft.kehuhua.config.Constant.pictureUrl;
import static com.kwsoft.kehuhua.config.Constant.sysUrl;
import static com.kwsoft.kehuhua.config.Constant.topBarColor;

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
    private static final String TAG = "SelectPictureActivity";
    private WaterWaveProgress waveProgress;

    //录制视频参数
    AudioRecordButton button;
    private ListView mlistview;
    private ArrayAdapter<Recorder> mAdapter;
    private View viewanim;
    private List<Recorder> mDatas = new ArrayList<Recorder>();
    private Button btn_up;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture_layout);
        ButterKnife.bind(this);
        initView();
//        initData();
//        setListener();
        //展示音频
        initAudioView();
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
                waveProgress.setVisibility(View.VISIBLE);
                waveProgress.setProgress(0);
                upload();
            }
        });

        waveProgress = (WaterWaveProgress) findViewById(R.id.waterWaveProgress1);

        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        //  int height = wm.getDefaultDisplay().getHeight();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) waveProgress.getLayoutParams();
        //layoutParams.setMargins(width/4,12,10,5);//4个参数按顺序分别是左上右下
        layoutParams.setMarginStart(width / 3);
        waveProgress.setLayoutParams(layoutParams); //mView是控件
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

    private void initAudioView() {
        //展示音频
        mlistview = (ListView) findViewById(R.id.listview);
        button = (AudioRecordButton) findViewById(R.id.recordButton);
        btn_up = (Button) findViewById(R.id.btn_up);
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Recorder recorder = mDatas.get(0);
                String path = recorder.getFilePathString();
                uploadAudio(path);
            }
        });

        PermissionGen.with(SelectPictureActivity.this)
                .addRequestCode(106)
                .permissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request();

        button.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                // TODO Auto-generated method stub
                Log.e("filepath=", filePath);
                Recorder recorder = new Recorder(seconds, filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mlistview.setSelection(mDatas.size() - 1);
            }
        });

        mAdapter = new RecorderAdapter(this, mDatas);
        mlistview.setAdapter(mAdapter);


        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 播放动画
                if (viewanim != null) {//让第二个播放的时候第一个停止播放
                    // viewanim.setBackgroundResource(R.id.id_recorder_anim);
                    //  viewanim.setBackgroundResource(R.mipmap.adj);
                    viewanim.setBackgroundResource(R.mipmap.ic_launcher);
                    viewanim = null;
                }
                viewanim = view.findViewById(R.id.id_recorder_anim);
                viewanim.setBackgroundResource(R.drawable.play);
                AnimationDrawable drawable = (AnimationDrawable) viewanim
                        .getBackground();
                drawable.start();
                // 播放音频
                MediaManager.playSound(mDatas.get(i).filePathString,
                        new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // viewanim.setBackgroundResource(R.id.id_recorder_anim);
                                viewanim.setBackgroundResource(R.mipmap.ic_launcher);
                            }
                        });
            }
        });

    }

    /**
     * 上传音频
     *
     */
    private void uploadAudio(String path) {
        File audioFile = new File(path);
        Log.e("audioFile=", audioFile.getPath());
        String url = sysUrl + pictureUrl;

        OkHttpUtils.post()//
                .addFile("audiofile", audioFile.getName(), audioFile)
                .url(url)
                .build()
                .execute(new EdusStringCallback(SelectPictureActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("response", response);
                        getFileCode(response);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 106)
    public void doSomething() {
        Toast.makeText(this, "打开权限成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionFail(requestCode = 106)
    public void doFailSomething() {
        Toast.makeText(this, "Contact permission is not granted", Toast.LENGTH_SHORT).show();
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

        Map<String, File> myFile = new HashMap<>();
        if (img_Paths.size() > 0) {
            for (int i = 0; i < img_Paths.size(); i++) {
                File file = new File(img_Paths.get(i));

                if (!myFile.containsKey(file.getName())) {
                    myFile.put(file.getName(), file);
                } else {
                    myFile.put(file.getName() + i, file);
                }
            }
            Log.e(TAG, "uploadMethod: 开始上传文件 " + myFile.toString());
            //上传文件
            uploadMethod(url, myFile);
        } else {
            Toast.makeText(SelectPictureActivity.this, "您尚未选择图片", Toast.LENGTH_SHORT).show();
            waveProgress.setVisibility(View.GONE);
        }
    }

    public void uploadMethod(String url, Map<String, File> files) {
        Log.e(TAG, "uploadMethod: 开始上传文件");

        OkHttpUtils.post()//
                .files("myFiles", files)
                .url(url)
                .build()
                .execute(new EdusStringCallback(SelectPictureActivity.this) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ErrorToast.errorToast(mContext, e);
                        waveProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        getFileCode(response);
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        if ((int) (100 * progress) == 100) {
                            waveProgress.setVisibility(View.GONE);
                            Log.e("total", total + "");
                        } else {

                            waveProgress.setProgress((int) (100 * progress));
                            Log.e("progress", progress + "");
                        }
                    }

                });
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

        intentTree.setClass(SelectPictureActivity.this, OperateDataActivity.class);

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

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MediaManager.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
