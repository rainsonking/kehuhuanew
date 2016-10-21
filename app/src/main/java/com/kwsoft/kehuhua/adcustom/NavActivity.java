package com.kwsoft.kehuhua.adcustom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.jauker.widget.BadgeView;
import com.kwsoft.kehuhua.adcustom.base.BaseActivity;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.utils.CloseActivityClass;
import com.kwsoft.kehuhua.utils.DataProcess;
import com.kwsoft.kehuhua.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NavActivity extends BaseActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, ViewPager.OnPageChangeListener{
    /**
     * Called when the activity is first created.
     */

    private static final int REQUEST_CODE = 0; // 请求码
    public ImageView mSystemConfig, iv_er_code;

    SimpleAdapter adapter;
    //public String menuData;
    //private static long exitTime=0;// 退出时间
    private ImageView[] mImageViews;


    PullToRefreshGridView homeGridView;
    List<Map<String, Object>> mList;
    List<Map<String, Object>> parentList = new ArrayList<>();

    public String menuData;



    BadgeView badgeView;//提示消息条数的view

    MyReceiverMsg receiverMsg;//接收通知的接收器



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        CloseActivityClass.activityList.add(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(v iew, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        initView();
        packMenuList();


        initPager();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        //设定系统登陆人姓名
        View headerView = navigationView.getHeaderView(0);
        TextView sysName = (TextView) headerView.findViewById(R.id.sysName);
        sysName.setText(Constant.loginName);

        receiverMsg = new MyReceiverMsg();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MY_BROADCAST");

        registerReceiver(receiverMsg, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int count=getLoginUserSharedPre().getInt("count",0);
        badgeView.setBadgeCount(count);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiverMsg);
    }

    public void initView() {
        homeGridView = (PullToRefreshGridView) findViewById(R.id.home_grid);
        iv_er_code = (ImageView) findViewById(R.id.iv_er_code);
        iv_er_code.setOnClickListener(this);
        mSystemConfig = (ImageView) findViewById(R.id.systemConfig);
        ImageView messageCenter = (ImageView) findViewById(R.id.messageCenter);
        messageCenter.setOnClickListener(this);
        //xiebubiao修改
        badgeView=new BadgeView(this);
        badgeView.setTargetView(messageCenter);
        badgeView.setHideOnNull(true);
        badgeView.setBadgeGravity(Gravity.TOP | Gravity.RIGHT);
        badgeView.setBackgroundResource(R.drawable.badge_bg);
        badgeView.setBadgeMargin(0,0,15,0);

        ImageView personalCenter = (ImageView) findViewById(R.id.personalCenter);
        personalCenter.setOnClickListener(this);
        mSystemConfig.setOnClickListener(this);
        Log.e("TAG", "首页初始化完毕");
        getIntentData();

    }

    private void getIntentData() {
        //Intent intent = getIntent();
        menuData = Constant.menuData;
        //intent.getStringExtra("menu");
        Log.e("TAG", "menuData "+menuData);


        try {
            Map<String, Object> menuMap = JSON.parseObject(menuData,
                    new TypeReference<Map<String, Object>>() {
                    });
            //保存时间戳
            Constant.menuTime = menuMap.get("alterTime") + "";
            //获取是所有菜单列表数据
            mList = (List<Map<String, Object>>) menuMap.get("menuList");
        } catch (Exception e) {
            e.printStackTrace();

        }
        Log.e("TAG", "首页获取完数据");
        if (mList.size()>0) {
            parentList= DataProcess.toParentList(mList);
        }else{
            Toast.makeText(NavActivity.this, "无菜单数据", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
//        tvx= (TextView) findViewById(R.id.sysName);
//        tvx.setText("");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

            Toast.makeText(this, "查看所有权限", Toast.LENGTH_SHORT).show();


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 封装所有父级菜单数据至GridView展示
     */

    public void packMenuList() {

        if (parentList.size() > 0) {
            adapter = new SimpleAdapter(this, parentList,
                    R.layout.activity_home_item, new String[]{"image", "menuName"},
                    new int[]{R.id.iv_item, R.id.tv_item});
            homeGridView.setAdapter(adapter);
            Log.e("TAG", "适配父级菜单完毕");
//        homeGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
//                //pullDownToRefresh();
//            }
//        });
            homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int menuId = (int) parentList.get(position).get("menuId");

                    Map<String, Object> parentItem = parentList.get(position);
                    Log.e("TAG", "父类菜单menuId" + menuId);
                    toItem(menuId, parentItem);
                }
            });
        } else {
            Toast.makeText(this, "您还没有主菜单数据！！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 跳转至子菜单列表
     */
    public void toItem(int menuId, Map<String, Object> parentItem) {
        List<Map<String, Object>> childList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            if (Integer.valueOf(String.valueOf(mList.get(i).get("parent_menuId"))) == menuId) {
                childList.add(mList.get(i));
            }
        }
        Log.e("TAG","itemData "+String.valueOf(parentItem.get("menuPageUrl")));
        Log.e("TAG","itemData "+parentItem.toString());
        if (childList.size() != 0) {
            String childData = JSONArray.toJSONString(childList);
            Intent intent = new Intent();
            intent.setClass(this, HomeChildActivity.class);
            intent.putExtra("childList", childData);
            intent.putExtra("parent_menuId", menuId);

            startActivity(intent);
        } else if (parentItem.get("menuPageUrl") != null) {
            //Toast.makeText(getActivity(), "定制开发模块", Toast.LENGTH_SHORT).show();
            // && String.valueOf(parentItem.get("menuPageUrl")).equals("dataPlAdd_toShowPage.do?tableId=19&ifAjax=0")
            String parentItemStr = JSON.toJSONString(parentItem);
            Intent intent = new Intent();
            intent.setClass(this, CourseActivity.class);
            intent.putExtra("itemData", parentItemStr);
            startActivity(intent);
        } else {
            //Toast.makeText(getActivity(), "定制开发模块", Toast.LENGTH_SHORT).show();
            String parentItemStr = JSON.toJSONString(parentItem);
            Intent intent = new Intent();
            intent.setClass(this, ListActivity2.class);
            intent.putExtra("itemData", parentItemStr);
            startActivity(intent);
        }
    }


    /*该方法中实现广告滑动效果*/
    private void initPager() {
        LinearLayout group = (LinearLayout) findViewById(R.id.dot);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //载入图片资源ID
        int[] imgIdArray = new int[]{R.mipmap.first, R.mipmap.sec, R.mipmap.third};
        //将点点加入到ViewGroup中
        ImageView[] tips = new ImageView[imgIdArray.length];
        for (int i = 0; i < tips.length; i++) {
            ImageView imageView = new ImageView(this);
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
            ImageView imageView = new ImageView(this);
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
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.imageView:


                break;
            case R.id.iv_back:

                break;
            case R.id.personalCenter:
                //String menuList=JSON.toJSONString(mList);
                Intent intent1 = new Intent(this, BlankActivity.class);
                //intent1.putExtra("menuData",menuList);
                startActivity(intent1);
                break;

            case R.id.messageCenter:
                Intent intent2 = new Intent(this, MessagAlertActivity.class);
                startActivity(intent2);
//                startActivityForResult(intent2,2);
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

    public void toSystemConfig() {
        Intent intent = new Intent();
        intent.setClass(this, SettingsActivity.class);
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


//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        parentList.clear();
//        getIntentData();
//        if (adapter!=null) {
//            adapter.notifyDataSetChanged();
//        }
//
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // 缺少权限时, 进入权限配置页面
//        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
//            Log.e("TAG","检测权限缺失");
//            startPermissionsActivity();
//        }
//    }
//    private void startPermissionsActivity() {
//        Log.e("TAG","检测权限缺失后跳转");
//        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
//    }
//
    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
//        Log.e("TAG","检测权限缺失后返回");
//        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
//            finish();
//        }
    }

    public class MyReceiverMsg extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count=intent.getIntExtra("count",0);
            badgeView.setBadgeCount(count);
        }
    }




}
