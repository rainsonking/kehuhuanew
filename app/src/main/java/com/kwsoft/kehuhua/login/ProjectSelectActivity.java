package com.kwsoft.kehuhua.login;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.model.OnRefreshListener;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DiskLruCacheHelper;
import com.kwsoft.kehuhua.view.DepthPageTransformer;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

public class ProjectSelectActivity extends BaseActivity implements OnRefreshListener, View.OnClickListener {

    //private PullToRefreshListView refreshListView;
    private DiskLruCacheHelper DLCH;
    private List<Map<String, Object>> projectListMap=new ArrayList<>();
    int[] mImgIds = new int[] { R.drawable.pro_img,
            R.drawable.pro_img1, R.drawable.pro_img2 };

    private List<ImageView> mImageViews = new ArrayList<>();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_project_select);
        CloseActivityClass.activityList.add(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        try {
            DLCH = new DiskLruCacheHelper(ProjectSelectActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startAnim();
        requestProjectList();
//        initView();

    }

    @Override
    public void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(getApplicationContext());
    }

    class MyPagerAdapter extends PagerAdapter{

            @Override
            public Object instantiateItem(ViewGroup container, final int position)
            {
                try {
                    container.addView(mImageViews.get(position % projectListMap.size()), 0);
                } catch (Exception e) {
                    //Log.e("TAG1", "出现异常："+position);
                }
                mImageViews.get(position).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Constant.proId = "" + projectListMap.get(position).get("proId");
                        Constant.sysUrl="http://"+
                                String.valueOf(projectListMap.get(position).get("programa_url"))+
                                ":"+
                                String.valueOf(projectListMap.get(position).get("pro_port"))+
                                "/"+
                                String.valueOf(projectListMap.get(position).get("pro_en_name"))+
                                "/";
                        Constant.proName = "" + projectListMap.get(position).get("programa_name");
                        Intent intent = new Intent();

                        intent.setClass(ProjectSelectActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });

                // 实现左右循环时添加捕获异常

                return mImageViews.get(position % projectListMap.size());

            }

            @Override
            public void destroyItem(ViewGroup container, int position,
            Object object)
            {
                container.removeView(mImageViews.get(position%projectListMap.size()));
                Log.e("TAG1", "销毁的位置："+position%projectListMap.size());



//                container.removeView(mImageViews.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object)
            {

                return view == object;
            }
            @Override
            public int getCount(){

                return projectListMap.size();
            }

    }

    private static final String TAG = "ProjectSelectActivity";
    /**
     * 第一步，获取项目列表
     */
    public void requestProjectList() {
        //startAnim();
        String volleyUrl = Constant.sysUrl + Constant.sysLoginUrl;
        Log.e("TAG", "项目列表请求地址" + volleyUrl);

        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("source", "1");
        //请求
        OkHttpUtils
                .post()
                .params(paramsMap)
                .url(volleyUrl)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        stopAnim();
                        Log.e(TAG, "onError: Call  "+call+"  id  "+id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: "+"  id  "+id);
                        analysisData(response);
                        Log.e(TAG, "项目列表请求数据：" + response);
                    }
                });
    }

    @Override
    public void onDownPullRefresh() {

    }

    @Override
    public void onLoadingMore() {

    }


    /**
     * 第二步：解析数据
     * <p/>
     * [
     * {
     * "T_1_0": 1,
     * "programa_name": "主项目",
     * "programa_url": "",
     * "pro_port": "8080",
     * "pro_en_name": "edus_auto",
     * "pro_type": 1,
     * "pro_show_way": 0,
     * "proId": "5704e45c7cf6c0b2d9873da6"
     * },
     * {
     * "pro_en_name": "edus_auto",
     * "programa_name": "学员端",
     * "programa_url": "192.168.6.46",
     * "pro_port": "8080",
     * "T_1_0": 2,
     * "pro_show_way": "1",
     * : "57159822f07e75084cb8a1fe"
     * }
     * ]
     */

    public void analysisData(String jsonData) {
        try {
            projectListMap = JSON.parseObject(jsonData,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            Log.e("TAG", "项目列表：解析项目列表数据完毕"+projectListMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0;i<projectListMap.size();i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mImgIds[i]);
            imageView.setScaleX(0.8f);
            imageView.setScaleY(0.8f);
            mImageViews.add(imageView);
        }

        ViewPager mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
//		mViewPager.setPageTransformer(true, new RotateDownPageTransformer());
        MyPagerAdapter myPagerAdapter=new MyPagerAdapter();
        mViewPager.setAdapter(myPagerAdapter);

        stopAnim();
        Log.e("TAG", "项目列表：初始化完毕");
    }

//    boolean isScrolled = false;
    @Override
    public void onClick(View v) {

    }

    void startAnim() {
        findViewById(R.id.avLoadingIndicatorViewLayoutPro).setVisibility(View.VISIBLE);
    }

    void stopAnim() {
        findViewById(R.id.avLoadingIndicatorViewLayoutPro).setVisibility(View.GONE);
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

}








//mViewPager.setCurrentItem(mImageViews.size()*100);
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            try {
//                adapter.notifyDataSetChanged();
//                refreshListView.onRefreshComplete();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

//        for (int imgId : mImgIds)
//        {
//            ImageView imageView = new ImageView(getApplicationContext());
//            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setImageResource(imgId);
//            imageView.setScaleX(0.8f);
//            imageView.setScaleY(0.8f);
//            mImageViews.add(imageView);
//        }

//        refreshListView.setAdapter(adapter);
//stopAnim();
//            adapter = new SimpleAdapter(this, projectListMap,
//                    R.layout.activity_project_select_item, new String[]{"programa_name"},
//                    new int[]{R.id.programa_name});

//    private void initView() {
//
//        try {
//
//            Log.e("TAG", "请求数据完毕");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        refreshListView = (PullToRefreshListView) findViewById(R.id.project_list);
//
//        assert refreshListView != null;
//        refreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//                pullDownToRefresh();
//            }
//        });
//        refreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
///**
// *点击第几项目判断是否进入登陆界面
// *
// */
//                if (position > 0) {//去掉0的下拉刷新head位置
//                    try {
//                        Constant.proId = "" + projectListMap.get(position - 1).get("proId");
//                        Constant.sysUrl="http://"+String.valueOf(projectListMap.get(position-1).get("programa_url"))+":"+
//                                String.valueOf(projectListMap.get(position-1).get("pro_port"))+"/"+String.valueOf(projectListMap.get(position-1).get("pro_en_name"))+"/";
//                        Log.e("TAG", "项目主地址 "+Constant.sysUrl);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(ProjectSelectActivity.this, "项目列表信息错误", Toast.LENGTH_SHORT).show();
//                        //stopAnim();
//                    }finally {
//                        Intent intent = new Intent();
//                        intent.setClass(ProjectSelectActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            }
//        });
//    }

/**
 * 下拉刷新
 */
//    private void pullDownToRefresh() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(null != Utils.getActiveNetwork(ProjectSelectActivity.this)) {
//                    try {
//                            projectListMap.clear();
//                            Log.e("TAG","clear成功");
//                            requestProjectList();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }else{
//                    Toast.makeText(ProjectSelectActivity.this, "世界上最遥远的距离就是没有网", Toast.LENGTH_SHORT).show();
//                }
//                mHandler.sendEmptyMessage(0);
//            }
//        }).start();
//    }




















