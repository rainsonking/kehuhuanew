package com.kwsoft.kehuhua.urlCnn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/27 0027.
 *
 */

public class EdusStringCallback extends StringCallback {
    public Context mContext;

    public EdusStringCallback(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }


    @Override
    public void onError(Call call, Exception e, int id) {
        Log.e("TAG", "callback测试  "+call+"  id  "+id);
        SwitchStatueCode.netToast((Activity) mContext);

    }

    @Override
    public void onResponse(String response, int id) {

    }

}
