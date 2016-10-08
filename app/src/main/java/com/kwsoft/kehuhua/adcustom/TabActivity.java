package com.kwsoft.kehuhua.adcustom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kwsoft.kehuhua.adapter.PageFragmentAdapter;
import com.kwsoft.kehuhua.bean.Channel;
import com.kwsoft.kehuhua.fragments.TabsFragment;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static com.kwsoft.kehuhua.config.Constant.topBarColor;

public class TabActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private RadioGroup rgChannel = null;
    private HorizontalScrollView hvChannel;
    private PageFragmentAdapter adapter = null;
    private List<Fragment> fragmentList = new ArrayList<>();

    private List<Channel> selectedChannel = new ArrayList<>();
    private CommonToolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        ButterKnife.bind(this);
        CloseActivityClass.activityList.add(this);
        Log.e("TAG", "进入Tab");
        initView();
    }

    private void initView() {

        mToolbar = (CommonToolbar) findViewById(R.id.common_toolbar);


        mToolbar.setBackgroundColor(getResources().getColor(topBarColor));



        //左侧返回按钮
        mToolbar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rgChannel = (RadioGroup) super.findViewById(R.id.rgChannel);
        viewPager = (ViewPager) super.findViewById(R.id.vpNewsList);
        hvChannel = (HorizontalScrollView) super.findViewById(R.id.hvChannel);
        rgChannel.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        viewPager.setCurrentItem(checkedId);
                    }
                });
        viewPager.setOnPageChangeListener(this);
        getIntentData();
        initTab();//动态产生RadioButton
        initViewPager();
        rgChannel.check(0);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String titleName = intent.getStringExtra("titleName");
        String mainId=intent.getStringExtra("mainId");
        Log.e("TAG", "tab页中的mainId："+mainId);
        mToolbar.setTitle(titleName);
        String childTab = intent.getStringExtra("childTab");
        List<Map<String, Object>> childTabList = JSON.parseObject(childTab,
                new TypeReference<List<Map<String, Object>>>() {
                });

        for (int i = 0; i < childTabList.size(); i++) {
            String name = String.valueOf(childTabList.get(i).get("childPageName"));
            String tableId = String.valueOf(childTabList.get(i).get("tableId"));
            String pageId = String.valueOf(childTabList.get(i).get("pageId"));
            selectedChannel.add(new Channel("", name, 0, tableId, pageId, mainId));

        }
        Log.e("TAG", "tab中获取完传递数据");
    }


    private void initTab() {
        for (int i = 0; i < selectedChannel.size(); i++) {
            RadioButton rb = (RadioButton) LayoutInflater.from(this).
                    inflate(R.layout.tab_rb, null);
            rb.setId(i);
            rb.setText(selectedChannel.get(i).getName());
            RadioGroup.LayoutParams params = new
                    RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            rgChannel.addView(rb, params);
        }
        Log.e("TAG", "初始化Tab完毕");
    }

    private void initViewPager() {
        for (int i = 0; i < selectedChannel.size(); i++) {
            TabsFragment frag = new TabsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tableId", selectedChannel.get(i).getTableId());
            bundle.putString("pageId", selectedChannel.get(i).getPageId());
            bundle.putString("mainId", selectedChannel.get(i).getMainId());
            bundle.putString("name", selectedChannel.get(i).getName());
            frag.setArguments(bundle);     //向Fragment传入数据
            fragmentList.add(frag);
        }

        Log.e("TAG", "开始适配viewPager");
        adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(20);//fragment数量大于21个才会销毁第一个fragment
        Log.e("TAG", "初始化viewPager完毕");
    }

    /**
     * 滑动ViewPager时调整ScroollView的位置以便显示按钮
     *
     * @param idx
     */
    private void setTab(int idx) {
        RadioButton rb = (RadioButton) rgChannel.getChildAt(idx);
        rb.setChecked(true);
        int left = rb.getLeft();
        int width = rb.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
        hvChannel.smoothScrollTo(len, 0);//滑动ScroollView
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        setTab(position);
    }

}
