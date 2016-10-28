package com.kwsoft.kehuhua.urlCnn;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2016/10/27 0027.
 *
 */

public abstract class EdusStringCallback extends Callback<String> {

    
    public Context mContext;
    private static final String TAG = "EdusStringCallback";


    public EdusStringCallback(Context mContext) {
        this.mContext = mContext;
        Log.e(TAG, "EdusStringCallback: 是否已走callback");
        SwitchStatueCode.netToast((Activity) this.mContext);
    }


    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        return response.body().string();
    }


}
