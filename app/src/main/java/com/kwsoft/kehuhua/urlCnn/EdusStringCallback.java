package com.kwsoft.kehuhua.urlCnn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.kwsoft.kehuhua.config.Constant;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/27 0027.
 *
 */

public class EdusStringCallback extends StringCallback {
    Context mContext;

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
        Log.e("TAG", "onError: Call  "+call+"  id  "+id);
        onErrorStatus(call, e, id);

    }

    @Override
    public void onResponse(String response, int id) {

    }
    public void onErrorStatus(Call call, Exception e, int id) {
        Log.e("TAG", "onError: Call  "+call+"  id  "+id);
        Constant.netToast((Activity) mContext);

    }
}
