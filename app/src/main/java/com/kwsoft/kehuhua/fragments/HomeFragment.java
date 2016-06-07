package com.kwsoft.kehuhua.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.kwsoft.kehuhua.adapter.MyFunctMenuAdapter;
import com.kwsoft.kehuhua.adcustom.HomeChildActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.SettingsActivity;
import com.kwsoft.kehuhua.bean.FunctionMenuBean;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.view.FunctionGridView;
import com.kwsoft.kehuhua.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener,ViewPager.OnPageChangeListener{
    public MyFunctMenuAdapter adapter;
    public Context context;
    public ImageView iv_back,mSystemConfig,iv_er_code;
    public int[] imgs = {R.mipmap.fuc_menu_1, R.mipmap.fuc_menu_6,
            R.mipmap.fuc_menu_3, R.mipmap.fuc_menu_4,
            R.mipmap.fuc_menu_5,R.mipmap.fuc_menu_2,
            R.mipmap.fuc_menu_7,R.mipmap.fuc_menu_8};
    public String menuStr;
    private static long exitTime=0;// 退出时间
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private List<String> parentNameList;
    private Map<String, List<Map<String,Object>>> mListValue;
    private String timeInterface,secondMenuName;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.activity_home_fragment1, container, false);
        initView(view);
        initPager(view);
        return view;
    }
    private void initView(View view) {

        context = getActivity();
        FunctionGridView myGridview = (FunctionGridView) view.findViewById(R.id.gridview);
        iv_er_code=(ImageView)view.findViewById(R.id.iv_er_code);
        iv_er_code.setOnClickListener(this);
        mSystemConfig=(ImageView) view.findViewById(R.id.systemConfig);
        mSystemConfig.setOnClickListener(this);
        final List<FunctionMenuBean> lists = new ArrayList<>();
        parentNameList=new ArrayList<>();
        Intent intent = getActivity().getIntent();
        menuStr = intent.getStringExtra("menu");

        if(menuStr!=null){
            Map<String, Object> menuMap = JSON.parseObject(menuStr, Map.class);
            List<Map<String, Object>> mList = (List<Map<String, Object>>) menuMap.get("menuList");
            timeInterface= (String) menuMap.get("timeInterface");
            mListValue=new HashMap<>();
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).get("parent_phMenuName").equals("")&&
                        mList.get(i).get("parent_phMenuName")!=null) {//判断父级菜单名称为空串的元素
                    String parentName= (String) mList.get(i).get("phoneMenuName");//获得一级菜单名称
                    int parentMenuId= (int) mList.get(i).get("phoneMenuId");
                    List<Map<String,Object>> parentContent=new ArrayList<>();//创建存储该一级名称下的所有子菜单内容

                    for(int j = 0; j < mList.size(); j++) {//仍然遍历所有菜单列表
                        Map<String,Object> childContent=new HashMap<>();//创建每一个子菜单的存储类型为map
                        if ((int) mList.get(j).get("parent_phMenuId")==parentMenuId) {
                            //将数据装进子菜单Map
                            childContent.put("phoneMenuName", mList.get(j).get("phoneMenuName"));
                            childContent.put("tableId", mList.get(j).get("tableId"));
                            childContent.put("phonePageId", mList.get(j).get("phonePageId"));
                            parentContent.add(childContent);
                        }
                    }
                    //将搜集到的值放到linkedhashmap中
                    parentNameList.add(parentName);//key列表
                    mListValue.put(parentName, parentContent);
                }
            }
            if (parentNameList.size() != 0) {
                for (int i = 0; i < parentNameList.size(); i++) {
                    FunctionMenuBean functionMenuBean = new FunctionMenuBean(imgs[i], parentNameList.get(i));
                    lists.add(functionMenuBean);
                }
                Log.e("TAG", "第一层菜单数据" + parentNameList.get(0) + "'");
                Log.e("TAG", "第一层菜单数据!!!!!" + lists + "'");
                adapter = new MyFunctMenuAdapter(getActivity(), lists);
                myGridview.setAdapter(adapter);
                myGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        List<Map<String, Object>> mList1=mListValue.get(parentNameList.get(i));
                        String z=JSON.toJSONString(mList1);
                        Log.e("TAG", "即将传给第二层的菜单数据" + z);

                        secondMenuName=lists.get(i).getTvName();
                        startTo(z);
                    }
                });
            } else {
                Toast.makeText(getActivity(), "您还没有功能数据！！", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getActivity(), "您还没有功能数据！！", Toast.LENGTH_SHORT).show();
        }
    }

    /*该方法中实现广告滑动效果*/
    private void initPager(View view) {
        LinearLayout group = (LinearLayout) view.findViewById(R.id.dot);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        //载入图片资源ID
        int[] imgIdArray = new int[]{R.mipmap.first, R.mipmap.sec, R.mipmap.third};
        //将点点加入到ViewGroup中
        tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
            tips[i] = imageView;
            if (i == 0) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 3;
            layoutParams.rightMargin = 3;
            group.addView(imageView, layoutParams);
        }
        //将图片装载到数组中
        mImageViews = new ImageView[imgIdArray.length];
        for (int i = 0; i < mImageViews.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            mImageViews[i] = imageView;
            imageView.setBackgroundResource(imgIdArray[i]);
        }



        //设置Adapter
        viewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        viewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
        viewPager.setCurrentItem((mImageViews.length) * 100);
    }

    private void startTo(String menuStr) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), HomeChildActivity.class);
        intent.putExtra("secondMenuName", secondMenuName);
        intent.putExtra("menu", menuStr);
        intent.putExtra("timeInterface", timeInterface);

        startActivity(intent);
        //getActivity().overridePendingTransition(R.anim.activity_zoomin,R.anim.activity_zoomout);
    }
    public class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            try {
                ((ViewPager) container).addView(mImageViews[position % mImageViews.length], 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mImageViews[position % mImageViews.length];

        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_er_code://二维码扫描
                Intent intent=new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.imageView:


                break;
            case R.id.iv_back:

                break;
            case R.id.systemConfig:
                toSystemConfig();
                break;
            case R.id.systemConfig1:
                toSystemConfig();
                break;
            default:
                break;
        }
    }
    /**
     * 设置选中的tip的背景
     *
     * @param selectItems
     */
    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
            }
        }
    }


    public   boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {

                String msg = "再按一次返回键退出系统";
                Toast.makeText(getActivity(), msg,Toast.LENGTH_SHORT).show();

                exitTime = System.currentTimeMillis();
            } else {
                CloseActivityClass.exitClient(getActivity());
            }
            return true;
        }
        return true;
    }
    public void toSystemConfig(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), SettingsActivity.class);
        startActivity(intent);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
