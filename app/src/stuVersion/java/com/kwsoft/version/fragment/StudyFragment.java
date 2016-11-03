package com.kwsoft.version.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.kwsoft.kehuhua.adcustom.ListActivity2;
import com.kwsoft.kehuhua.adcustom.MessagAlertActivity;
import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.zxing.CaptureActivity;
import com.kwsoft.version.StuInfoActivity;
import com.kwsoft.version.StuMainActivity;
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.view.StudyGridView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class StudyFragment extends Fragment implements View.OnClickListener {

    private TextView stuName;
    //PullToRefreshGridView homeGridView;
    private StudyGridView homeGridView;
    private List<Map<String, Object>> parentList = new ArrayList<>();
    private SimpleAdapter simpleAdapter = null;

    private int[] imgs2 = {R.drawable.stu_see_record_task, R.drawable.stu_see_record_curriculum,
            R.drawable.stu_see_record_leave, R.drawable.stu_see_record_curriculumb};
    private GridView gridView;
    private List<Map<String, Object>> menuListAll = new ArrayList<>();
    private List<Map<String, Object>> menuListMap = new ArrayList<>();
    private PullToRefreshScrollView pull_refresh_scrollview;
    private SharedPreferences sPreferences;
    private Boolean isLogin = false;
    public String arrStr, menuStr, stuUrl;
    public Bundle arrBundle;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        initView(view);
        stuUrl = Constant.sysUrl + Constant.projectLoginUrl;
        ButterKnife.bind(this, view);
        return view;
    }

    public void initView(View view) {
        stuName = (TextView) view.findViewById(R.id.stu_name);

        try {
            String username = Constant.loginName;
            stuName.setText(username);
        } catch (Exception e) {
            e.printStackTrace();
        }


        RollPagerView mRollViewPager = (RollPagerView) view.findViewById(R.id.roll_view_pager);

        //设置播放时间间隔
        mRollViewPager.setPlayDelay(4000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(1500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());


        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
        mRollViewPager.setHintView(new ColorPointHintView(getActivity(), getResources().getColor(R.color.text6), getResources().getColor(R.color.text5)));
        mRollViewPager.setHintPadding(0, 0, 0, 40);

        homeGridView = (StudyGridView) view.findViewById(R.id.home_grid);
        homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> itemData = new HashMap<>();
                itemData.put("tableId", String.valueOf(parentList.get(position).get("tableId")));
                itemData.put("pageId", String.valueOf(parentList.get(position).get("penetratePageId")));
                itemData.put("menuName", String.valueOf(parentList.get(position).get("cnName")));

                Constant.stu_index = String.valueOf(parentList.get(position).get("ctType"));

                Constant.stu_homeSetId = String.valueOf(parentList.get(position).get("SourceDataId"));
                try {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ListActivity2.class);
                    intent.putExtra("itemData", JSON.toJSONString(itemData));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gridView = (GridView) view.findViewById(R.id.gridView);
        gridView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        pull_refresh_scrollview = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        //上拉、下拉设定
//        pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.BOTH);
        //上拉监听函数
        pull_refresh_scrollview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                //执行刷新函数

                getLoginData(stuUrl);
            }
        });

        //获取ScrollView布局，此文中用不到
        //mScrollView = mPullRefreshScrollView.getRefreshableView();

        getData();
        // initData();

    }

    @Override
    public void onResume() {
        Log.e("isLogin=", isLogin + "");
        super.onResume();
        if (!isLogin) {
            isLogin = arrBundle.getBoolean("isLogin");
            initData();
        } else {
            getLoginData(stuUrl);
        }
//
    }

    public void initData() {
        parentList = getkanbanData(arrStr);
        setKanbanAdapter(parentList);
        Log.e("isLogin=", isLogin + "");
        //菜单列表中的gridview数据

        if (menuStr != null) {
            menuListAll = JSON.parseObject(menuStr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            if (menuListAll.size() > 0) {
                //展示菜单
                menuListMap = getMenuListData(menuListAll);
                setMenuAdapter(menuListMap);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if ((simpleAdapter.getCount() == (i + 1))) {
                            // if (i == 7) {
                            StuMainActivity activity = (StuMainActivity) getActivity();
                            activity.fragmentClick();
                        } else {
//                            int menuId = (int) menuListMap.get(i).get("menuId");
//                            toItem(menuId, menuListMap.get(i));
                            DataProcess.toList(getActivity(), menuListMap.get(i));
                        }
                    }
                });
            } else {
                Toast.makeText(getActivity(), "无分类数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getData() {
        //设置看板数据
        arrBundle = getArguments();
        arrStr = arrBundle.getString("arrStr");
        menuStr = arrBundle.getString("menuDataMap");
    }

    public void setMenuAdapter(List<Map<String, Object>> menuListMaps) {
        simpleAdapter = new SimpleAdapter(getActivity(), menuListMaps,
                R.layout.fragment_study_gridview_item, new String[]{"image", "menuName"},
                new int[]{R.id.itemImage, R.id.itemName});
        gridView.setAdapter(simpleAdapter);
    }

    public void setKanbanAdapter(List<Map<String, Object>> parentLists) {
        if ((parentLists.size()) % 2 == 1) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", R.color.white);
            map.put("cnName", "");
            map.put("name", "");
            parentLists.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), parentLists,
                R.layout.activity_stu_study_item, new String[]{"image", "cnName", "name"},
                new int[]{R.id.iv_item, R.id.text1, R.id.text2});
        homeGridView.setAdapter(adapter);
    }

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getkanbanData(String arrStr) {
        List<Map<String, Object>> parentLists = new ArrayList<>();
        try {
            List<Map<String, Object>> listMap = JSON.parseObject(arrStr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            String cnName;
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Object> map = new HashMap<>();
                cnName = String.valueOf(listMap.get(i).get("cnName"));
                map.put("ctType", "3");
                map.put("cnName", cnName);
                int j = i % 4;
                map.put("image", imgs2[j]);
                map.put("SourceDataId", listMap.get(i).get("homeSetId") + "_" + listMap.get(i).get("index"));
                map.put("penetratePageId", listMap.get(i).get("phonePageId"));
                map.put("tableId", listMap.get(i).get("tableId"));
                List<Map<String, Object>> listMap1 = (List<Map<String, Object>>) listMap.get(i).get("valueMap");
                String name = "";
                if (listMap1.size() > 0) {
                    if (listMap1.get(0) != null && listMap1.get(0).size() > 0) {
                        name = String.valueOf(listMap1.get(0).get("name"));
                    }
                }
                map.put("name", name);
                parentLists.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return parentLists;
    }

    //获取父类菜单数据，并取不大于7个
    public List<Map<String, Object>> getMenuListData(List<Map<String, Object>> menuListAlls) {
        List<Map<String, Object>> menuListMaps = new ArrayList<>();
        menuListMap = DataProcess.toStuParentList(menuListAlls);
        //大于7个的情况
        if (menuListMap.size() > 7) {
            for (int k = 0; k < 7; k++) {
                menuListMaps.add(menuListMap.get(k));
            }

            Map<String, Object> map = new HashMap<>();
            map.put("menuName", "全部");
            map.put("image", R.drawable.stu_see_all);
            menuListMaps.add(map);
        } else {
            //小于7个的情况
            menuListMaps.addAll(menuListMap);
        }

        Log.e("TAG", "parentList去掉手机端 " + menuListMap.toString());
        return menuListMaps;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                boolean emui = AndtoidRomUtil.isEMUI();
                boolean miui = AndtoidRomUtil.isMIUI();
                boolean flyme = AndtoidRomUtil.isFlyme();

                if (emui) {
                    //华为
//                    PackageManager pm =  getActivity().getPackageManager();
//                    //MediaStore.ACTION_IMAGE_CAPTURE android.permission.RECORD_AUDIO
//                    boolean permission = (PackageManager.PERMISSION_GRANTED ==
//                            pm.checkPermission("MediaStore.ACTION_IMAGE_CAPTURE", "packageName"));
//                    if (permission) {
//                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
//                        startActivityForResult(intent, 1);
//                    } else {
//                        Constant.goHuaWeiSetting(getActivity());
//                    }
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 1);
                } else if (miui) {
                    //小米
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 1);
                } else if (flyme) {
                    //魅族rom
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.layout_1:
                Intent intent2 = new Intent(getActivity(), MessagAlertActivity.class);
                startActivity(intent2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.stu_homepage_info)
    public void onClick() {
        Intent intent = new Intent(getActivity(), StuInfoActivity.class);
        startActivity(intent);
    }

    private class TestNormalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.drawable.stu_see_banner,
                R.drawable.stu_see_banner,
                R.drawable.stu_see_banner,
                R.drawable.stu_see_banner,
        };


        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }


        @Override
        public int getCount() {
            return imgs.length;
        }
    }

    private static final String TAG = "StudyFragment";

    public void getLoginData(String volleyUrl) {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {

            //参数
            sPreferences = getActivity().getSharedPreferences(Constant.proId, MODE_PRIVATE);
            String nameValue = sPreferences.getString("name", "");
            String pwdValue = sPreferences.getString("pwd", "");
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(Constant.USER_NAME, nameValue);
            paramsMap.put(Constant.PASSWORD, pwdValue);
            paramsMap.put(Constant.proIdName, Constant.proId);
            paramsMap.put(Constant.timeName, Constant.menuAlterTime);
            paramsMap.put(Constant.sourceName, Constant.sourceInt);
            //请求
            OkHttpUtils
                    .post()
                    .params(paramsMap)
                    .url(volleyUrl)
                    .build()
                    .execute(new EdusStringCallback(getActivity()) {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ErrorToast.errorToast(mContext, e);
                            pull_refresh_scrollview.onRefreshComplete();

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e(TAG, "onResponse: " + "  id  " + id);
                            mainPage(response);
                        }
                    });
        } else {

            pull_refresh_scrollview.onRefreshComplete();
            Toast.makeText(getActivity(), "无网络", Toast.LENGTH_SHORT).show();
        }
    }

    //此方法传递菜单JSON数据
    @SuppressWarnings("unchecked")
    private void mainPage(String menuData) {
        try {
            Map<String, Object> menuMap = JSON.parseObject(menuData,
                    new TypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> loginfo = (Map<String, Object>) menuMap.get("loginInfo");
            Constant.USERID = String.valueOf(loginfo.get("USERID"));
            List<Map<String, Object>> menuListMap1 = (List<Map<String, Object>>) menuMap.get("roleFollowList");
            List<Map<String, Object>> menuListMap2 = (List<Map<String, Object>>) menuMap.get("menuList");
//看板模块数据
            String arrStr = JSON.toJSONString(menuListMap1);
            parentList.clear();
            parentList = getkanbanData(arrStr);
            setKanbanAdapter(parentList);

            //菜单列表中的gridview数据
            String menuStr = JSON.toJSONString(menuListMap2);
            if (menuStr != null) {
                menuListAll.clear();
                menuListAll = JSON.parseObject(menuStr,
                        new TypeReference<List<Map<String, Object>>>() {
                        });
                if (menuListAll.size() > 0) {
                    //展示菜单
                    menuListMap.clear();
//                    menuListMap =  DataProcess.toStuParentList(menuListAll);
                    menuListMap = getMenuListData(menuListAll);
                    setMenuAdapter(menuListMap);

                } else {
                    Toast.makeText(getActivity(), "无分类数据", Toast.LENGTH_SHORT).show();
                }
                //pull_refresh_scrollview.setMode(PullToRefreshBase.Mode.DISABLED);

                // Call onRefreshComplete when the list has been refreshed.
                //在更新UI后，无需其它Refresh操作，系统会自己加载新的listView
                pull_refresh_scrollview.onRefreshComplete();
                Toast.makeText(getActivity(), "数据已刷新", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}














