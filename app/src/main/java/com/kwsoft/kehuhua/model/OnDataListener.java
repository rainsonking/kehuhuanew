package com.kwsoft.kehuhua.model;


public interface OnDataListener {
	void onGetDataSuccess(String jsonData);
	void onGetDataError();
	void onLoading(long total, long current);
}
