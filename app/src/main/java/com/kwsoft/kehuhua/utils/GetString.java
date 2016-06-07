package com.kwsoft.kehuhua.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kwsoft.kehuhua.config.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/10 0010.
 */
public class GetString {

    private Context context;
    private String data=null;
    private String userName;
    private String passWord;

    public void GetString(Context context){

        this.context=context;

    }

    public String getString(String url,String inuserName,String inpassWord){
        userName=inuserName;
        passWord=inpassWord;
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        StringRequest loginInterfaceData = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String menuData) {

                        Log.e("TAG", menuData);//在此获得菜单数据
                        data= menuData;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "" + error);

                Toast.makeText(context, "请检查网络或服务器", Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put(Constant.USER_NAME, userName);//"15535211113"
                map.put(Constant.PASSWORD, passWord);//"670b14728ad9902aecba32e22fa4f6bd"
                return map;
            }
        };
        mRequestQueue.add(loginInterfaceData);

         return data;



    }

}
