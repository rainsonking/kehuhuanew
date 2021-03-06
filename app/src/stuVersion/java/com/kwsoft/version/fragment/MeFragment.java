package com.kwsoft.version.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kwsoft.kehuhua.adcustom.R;
import com.kwsoft.kehuhua.config.Constant;
import com.kwsoft.kehuhua.urlCnn.EdusStringCallback;
import com.kwsoft.kehuhua.urlCnn.ErrorToast;
import com.kwsoft.kehuhua.utils.Utils;
import com.kwsoft.version.Common.DataCleanManager;
import com.kwsoft.version.FeedbackActivity;
import com.kwsoft.version.ResetPwdActivity;
import com.kwsoft.version.StuInfoActivity;
import com.kwsoft.version.StuLoginActivity;
import com.kwsoft.version.StuPra;
import com.pgyersdk.update.PgyUpdateManager;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.kwsoft.kehuhua.config.Constant.tableId;

/**
 * Created by Administrator on 2016/9/6 0006.
 */
public class MeFragment extends Fragment implements View.OnClickListener {
    @Bind(R.id.tv_clean_cache)
    TextView tvCleanCache;
    @Bind(R.id.stu_name)
    TextView stuName;
    @Bind(R.id.stu_phone)
    TextView stuPhone;
    @Bind(R.id.stu_school_area)
    TextView stuSchoolArea;
    @Bind(R.id.stu_version)
    TextView stuVersion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    public void initData() {

        tvCleanCache.setText(getCache());
        stuName.setText(Constant.loginName);
        stuPhone.setText(Constant.USERNAME_ALL);
//        stuSchoolArea.setText("北京校区");
        try {
            //开始获取版本号
            String stuVersionCode = "v " + Utils.getVersionName(getActivity());
            stuVersion.setText(stuVersionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
//获取校区
        requestSet();
    }

    public void requestSet() {

        final String volleyUrl = Constant.sysUrl + Constant.requestListSet;
        Log.e("TAG", "学员端请求个人信息地址：" + volleyUrl);

        //参数
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put(tableId, StuPra.stuInfoTableId);
        paramsMap.put(Constant.pageId, StuPra.stuInfoPageId);
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
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e(TAG, "onResponse: " + "  id  " + response);
                        setStore(response);
                    }
                });
    }


    @SuppressWarnings("unchecked")
    private void setStore(String jsonData) {
        String jsonData1 = jsonData.replaceAll("00:00:00", "");
        Map<String, Object> stuInfoMap = null;
        try {
            stuInfoMap = Utils.str2map(jsonData1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> dataList;
        try {
            assert stuInfoMap != null;
            dataList = (List<Map<String, Object>>) stuInfoMap.get("dataList");
            Map<String, Object> pageSetMap = (Map<String, Object>) stuInfoMap.get("pageSet");
            List<Map<String, Object>> pageSet = (List<Map<String, Object>>) pageSetMap.get("fieldSet");
            Log.e("pageSet", pageSet.toString());
            String fieldCnName, fieldAliasName = "";
            for (int i = 0; i < pageSet.size(); i++) {
                Map<String, Object> map = pageSet.get(i);
                fieldCnName = map.get("fieldCnName") + "";
                if (fieldCnName.equals("校区")) {
                    fieldAliasName = map.get("fieldAliasName") + "";
                    break;
                }
            }
            Map<String, Object> map = dataList.get(0);
            if ((fieldAliasName.length() > 0) && (map.containsKey(fieldAliasName))) {
                String school = (String) map.get(fieldAliasName);
                stuSchoolArea.setText(school);
            } else {
                stuSchoolArea.setText("");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            stuSchoolArea.setText("");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.stu_head_image, R.id.stu_version_layout, R.id.stu_log_out, R.id.stu_resetPwd, R.id.stu_info_data, R.id.ll_stu_clear_cache, R.id.ll_stu_feedback})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stu_head_image:
                break;
            case R.id.stu_log_out:
                Intent intentLogout = new Intent(getActivity(), StuLoginActivity.class);
                startActivity(intentLogout);
                getActivity().finish();
                break;
            case R.id.stu_resetPwd:
                Intent intent = new Intent(getActivity(), ResetPwdActivity.class);
                startActivity(intent);
                break;
            case R.id.stu_info_data:
                Intent intentStuInfo = new Intent(getActivity(), StuInfoActivity.class);
                startActivity(intentStuInfo);
                break;
            case R.id.ll_stu_clear_cache:
                dialog1();
                break;
            case R.id.ll_stu_feedback:
                Intent intent1 = new Intent(getActivity(), FeedbackActivity.class);
                startActivity(intent1);
                break;
            case R.id.stu_version_layout:
                //检测更新
                PgyUpdateManager.register(getActivity());

                break;

            default:
                break;
        }
    }


    private void dialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  //先得到构造器
        builder.setTitle("提示"); //设置标题
        builder.setMessage("是否确认清除缓存?"); //设置内容
        //builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //关闭dialog
                clearCache();
                tvCleanCache.setText(getCache());
                Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }


    public String getCache() {
        String cache = "";
        try {
//           cache = DataCleanManager.getVolleyCache(getActivity());
            cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void clearCache() {
        try {
            DataCleanManager.cleanExternalCache(getActivity());

            DataCleanManager.cleanInternalCache(getActivity());


            // cache = DataCleanManager.getTotalCacheSize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
