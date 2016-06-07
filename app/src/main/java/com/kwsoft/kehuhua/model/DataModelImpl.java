package com.kwsoft.kehuhua.model;

import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

import java.io.File;

public class DataModelImpl implements DataModel{

	@Override
	public void getData(String url,String[] paramsName, String[] paramsValue,
			final OnDataListener listener) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils=new HttpUtils();
		RequestParams params=new RequestParams();
		for (int i = 0; i < paramsValue.length; i++) {
			params.addBodyParameter(paramsName[i], paramsValue[i]);
		}
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.i("123","onGetDataError=====>"+arg1);
				listener.onGetDataError();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				listener.onGetDataSuccess(arg0.result);
			}
		});
	}
	
	@Override
	public void upLoad(String url,String path,
			final OnDataListener listener) {
		// TODO Auto-generated method stub
		HttpUtils httpUtils=new HttpUtils();
		RequestParams params=new RequestParams();
		params.addBodyParameter("file", new File(path));
		httpUtils.send(HttpMethod.POST, url, params, new RequestCallBack<String>() {

			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				// TODO Auto-generated method stub
				super.onLoading(total, current, isUploading);
				listener.onLoading(total, current);
			}
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				listener.onGetDataError();
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				listener.onGetDataSuccess(arg0.result);
			}
		});
	}
	
}
