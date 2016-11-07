package com.kwsoft.version.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.kwsoft.kehuhua.adcustom.BlankActivity;
import com.kwsoft.kehuhua.adcustom.ChartActivity;
import com.kwsoft.kehuhua.adcustom.CourseActivity;
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
import com.kwsoft.version.androidRomType.AndtoidRomUtil;
import com.kwsoft.version.view.StudyGridView;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class StudyFragment extends Fragment implements View.OnClickListener {

    private TextView stuName;
    // private KanbanGridView homeGridView;
    private StudyGridView homeGridView;
    private List<Map<String, Object>> parentList = new ArrayList<>();
    private int[] image = {R.mipmap.edus_see_scan, R.mipmap.edus_see_form,
            R.mipmap.edus_see_news, R.mipmap.edus_see_set};
    private int[] imgs2 = {R.drawable.stu_see_record_task, R.drawable.stu_see_record_curriculum,
            R.drawable.stu_see_record_leave, R.drawable.stu_see_record_curriculumb};
    private GridView gridView;
    private List<Map<String, Object>> menuListAll = new ArrayList<>();
    private List<Map<String, Object>> menuListMap = new ArrayList<>();
    private PullToRefreshScrollView pull_refresh_scrollview;
    private SharedPreferences sPreferences;
    private TextView tvUserrole, tvMonth, tvDay;
    private Boolean isLogin = false;
    public String arrStr;
    public Bundle arrBundle;
    public String teachUrl, homePageListstr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        teachUrl = Constant.sysUrl + Constant.projectLoginUrl;
        initView(view);
        ButterKnife.bind(this, view);
        return view;
    }

    public void initView(View view) {
        stuName = (TextView) view.findViewById(R.id.stu_name);
        tvUserrole = (TextView) view.findViewById(R.id.tv_userrole);
        tvMonth = (TextView) view.findViewById(R.id.tv_month);
        tvDay = (TextView) view.findViewById(R.id.tv_day);

        try {
            String username = Constant.loginName;
            stuName.setText(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        homeGridView = (KanbanGridView) view.findViewById(R.id.home_grid);
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

                getLoginData(teachUrl);
            }
        });
        //获取ScrollView布局，此文中用不到
        //mScrollView = mPullRefreshScrollView.getRefreshableView();
        getData();
        //菜单列表中的gridview数据
        setMenuModel();
        // initData();
    }
    public int isResume=0;
    @Override
    public void onResume() {
        isResume=1;
        super.onResume();
        if (!isLogin) {
            isLogin = arrBundle.getBoolean("isLogin");
            initData();
        } else {
            getLoginData(teachUrl);
        }
    }

    public void initData() {
        //设置看板数据
        parentList = getkanbanData(arrStr);
        setKanbanAdapter(parentList);
    }

    private void setMenuModel() {
        //菜单列表中的gridview数据
        if ((homePageListstr != null) && (homePageListstr.length() > 0)) {
            List<Map<String, Object>> listMap = JSON.parseObject(homePageListstr,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
            if ((listMap != null) && (listMap.size() > 0)) {
                int leg;
                menuListAll.clear();
                for (int i = 0; i < listMap.size(); i++) {
                    Map<String, Object> map = listMap.get(i);
                    leg = (map.get("menuName").toString()).length();
                    map.put("menuName", map.get("menuName").toString().substring(0,leg-5));
                    map.put("image", image[i]);
                    menuListAll.add(map);
                }
            } else {
                initModel();
            }
        } else {
            initModel();

        }
        setMenuAdapter(menuListAll);
    }

    private void initModel() {
        Map<String, Object> map = new HashMap<>();
        map.put("menuName", "扫码考勤");
        map.put("image", image[0]);
        menuListAll.add(map);
        map = new HashMap<>();
        map.put("menuName", "报表管理");
        map.put("image", image[1]);
        menuListAll.add(map);
        map = new HashMap<>();
        map.put("menuName", "消息提醒");
        map.put("image", image[2]);
        menuListAll.add(map);
        map = new HashMap<>();
        map.put("menuName", "系统设置");
        map.put("image", image[3]);
        menuListAll.add(map);
    }

    private void getData() {
        arrBundle = getArguments();
        arrStr = arrBundle.getString("arrStr");
       homePageListstr = arrBundle.getString("homePageList");
        Log.e("homePageListstr",homePageListstr);
    }

    public void setMenuAdapter(final List<Map<String, Object>> menuListMaps) {
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), menuListMaps,
                R.layout.fragment_study_gridview_item, new String[]{"image", "menuName"},
                new int[]{R.id.itemImage, R.id.itemName});
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    PermissionGen.needPermission(StudyFragment.this, 106,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }
                    );
                } else if (i == 1) {
                    Intent intent = new Intent(getActivity(), ChartActivity.class);
                    intent.putExtra("titleName", String.valueOf(menuListMaps.get(i).get("menuName")));
                    startActivity(intent);
                } else if (i == 2) {
                    Intent intent = new Intent(getActivity(), MessagAlertActivity.class);
                    startActivity(intent);
                } else if (i == 3) {
                    Intent intent = new Intent(getActivity(), BlankActivity.class);
                    intent.putExtra("titleName", String.valueOf(menuListMaps.get(i).get("menuName")));
                    startActivity(intent);
                }
            }
        });
    }

    private static final String TAG = "StudyFragment";

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = 106)
    public void doCapture() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }

    @PermissionFail(requestCode = 106)
    public void doFailedCapture() {
        Toast.makeText(getActivity(), "获取权限失败", Toast.LENGTH_SHORT).show();
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
        menuListMap = DataProcess.toParentList(menuListAlls);
        //大于7个的情况
        if (menuListMap.size() > 7) {
            for (int k = 0; k < 7; k++) {
                menuListMaps.add(menuListMap.get(k));
            }
        } else {
            //小于7个的情况
            menuListMaps.addAll(menuListMap);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("menuName", "全部");
        map.put("image", R.drawable.stu_see_all);
        menuListMaps.add(map);
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

//    private class TestNormalAdapter extends StaticPagerAdapter {
//        private int[] imgs = {
//                R.drawable.stu_see_banner,
//                R.drawable.img2,
//                R.drawable.img3,
//                R.drawable.img4,
//        };
//
//
//        @Override
//        public View getView(ViewGroup container, int position) {
//            ImageView view = new ImageView(container.getContext());
//            view.setImageResource(imgs[position]);
//            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            return view;
//        }
//
//
//        @Override
//        public int getCount() {
//            return imgs.length;
//        }
//    }

    public void toItem(int menuId, Map<String, Object> itemData) {

        //获取子列表
        List<Map<String, Object>> childList = new ArrayList<>();
        for (int i = 0; i < menuListAll.size(); i++) {
            if (Integer.valueOf(String.valueOf(menuListAll.get(i).get("parent_menuId"))) == menuId) {
                childList.add(menuListAll.get(i));
            }
        }

        if (childList.size() > 0) {
            childList = DataProcess.toImgList(childList);

        }
        //转换整项为字符串准备发送
        String itemDataString = JSONArray.toJSONString(itemData);

        //转换子列表对象为字符串准备发送
        String childString = JSONArray.toJSONString(childList);
        Intent intent = new Intent();
        if (itemData.get("menuPageUrl") == null) {
            intent.setClass(getActivity(), ListActivity2.class);
        } else {
            intent.setClass(getActivity(), CourseActivity.class);
        }
        intent.putExtra("itemData", itemDataString);
        intent.putExtra("childData", childString);
        startActivity(intent);
    }

    public void getLoginData(String volleyUrl) {
        if (((BaseActivity) getActivity()).hasInternetConnected()) {

            //参数
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put(Constant.USER_NAME, Constant.USERNAME_ALL);
            paramsMap.put(Constant.PASSWORD, Constant.PASSWORD_ALL);
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
            // List<Map<String, Object>> menuListMap2 = (List<Map<String, Object>>) menuMap.get("menuList");
//看板模块数据
            String arrStr = JSON.toJSONString(menuListMap1);
            parentList.clear();
            parentList = getkanbanData(arrStr);
            setKanbanAdapter(parentList);

            //在更新UI后，无需其它Refresh操作，系统会自己加载新的listView
            pull_refresh_scrollview.onRefreshComplete();
            pull_refresh_scrollview.onRefreshComplete();
            if (isResume==0) {
                Toast.makeText(getActivity(), "数据已刷新", Toast.LENGTH_SHORT).show();
            }


            isResume=0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}















