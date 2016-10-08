package com.kwsoft.kehuhua.adcustom.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Activity的支持类接口，主要定义了Activity中常用的功能
 * 
 * @author xbb
 */
public interface IBaseActivity {

//	/**
//	 * 获取Application对象
//	 * 
//	 * @return
//	 */
//	public abstract Application getApplication();

	/**
	 * 开启服务
	 */
	public abstract void startService();

	/**
	 * 停止服务
	 */
	public abstract void stopService();

	/**
	 * 判断是否有网络连接，若没有，则弹出网络设置对话框，返回false
	 * 
	 * @return
	 */
	public abstract boolean validateInternet();

	/**
	 * 
	 * 判断是否有网络连接,没有返回false
	 * 
	 */
	public abstract boolean hasInternetConnected();

	/**
	 * 退出应用
	 */
	public abstract void isExit();

	/**
	 * 判断GPS是否已经开启.
	 * 
	 * @return
	 */
	public abstract boolean hasLocationGPS();

	/**
	 * 判断基站是否已经开启.
	 */
	public abstract boolean hasLocationNetWork();

	/**
	 * 检查内存卡.
	 */
	public abstract void checkMemoryCard();

	/**
	 * 获取进度条.
	 * 
	 * @return
	 */
	public abstract ProgressDialog getProgressDialog();

	/**
	 * 返回当前Activity上下文.
	 */
	public abstract Context getContext();

	/**
	 * 获取当前登录用户的SharedPreferences配置.
	 */
	public SharedPreferences getLoginUserSharedPre();

	/**
	 * 用户是否在线（当前网络是否重连成功）
	 */
	public boolean getUserOnlineState();

	/**
	 * 设置用户在线状态 true 在线 false 不在线
	 * 
	 * @param isOnline
	 */
	public void setUserOnlineState(boolean isOnline);

	/**
	 * 
	 * 发出Notification的method.
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            内容
	 * @param activity
	 */
	public void PushNotification(int iconId, String contentTitle,
								 String contentText, Class<?> activity, String from);
}
