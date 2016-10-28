package com.kwsoft.kehuhua.urlCnn;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/10/28 0028.
 *
 */

public class ErrorToast {

    public static void errorToast(Context context, Exception e) {
        //专门处理error的库，下面就是调用了其中的一些，可以方便调试的时候查找到错误
        Class errorClass = e.getClass();
        if (errorClass == java.net.SocketTimeoutException.class) {
            Log.d("TAG", "NetworkError");
            Toast.makeText(context, "连接超时", Toast.LENGTH_LONG).show();
        }

    }
}
