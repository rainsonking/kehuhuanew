package com.kwsoft.kehuhua.model;

/**
 * Description:请求网络数据接口
 * 
 * @author xbb
 */
public interface DataModel {
	void getData(String url, String[] paramsName, String[] paramsValue, OnDataListener listener);
	void upLoad(String url, String path, OnDataListener listener);
}
