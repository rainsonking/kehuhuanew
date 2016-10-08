package kwsoft.coursetabledemo.base;


import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import kwsoft.coursetabledemo.R;


/**
 * Activity的基类，实现了IActivitySupport接口
 * @author xbb
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity{
	protected Context mContext = null;
	protected SharedPreferences preferences;
	protected ProgressDialog pg = null;
	protected NotificationManager notificationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		getSupportActionBar().hide();
		mContext = this;
		preferences = getSharedPreferences("userInfo",MODE_PRIVATE);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		pg = new ProgressDialog(mContext);
		pg.setCanceledOnTouchOutside(false);
		pg.setMessage(getResources().getString(R.string.loading));

	}

	/**
	 * 初始化页面布局
	 */
	public abstract void initView();

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获取数据
	 */
	public void requestData() {

	}
	
	/**
	 * 获取ActionBar
	 */
	public View getSelfActionBar() {
		return null;
	}

	@Override
	public ProgressDialog getProgressDialog() {
		return pg;
	}

	/**
	 * 在这里开启所有需要开启的服务
	 */
	@Override
	public void startService() {

	}

	/**
	 * 在这里关闭所有需要开启的服务
	 */
	@Override
	public void stopService() {

	}

	/**
	 * 停止服务并结束所有的Activity退出应用
	 */
	@Override
	public void isExit() {
		new AlertDialog.Builder(mContext).setTitle("确定退出吗?")
				.setNeutralButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						stopService();
					//	myApplication.exit();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}

	/**
	 * 判断是否有网络连接,没有返回false
	 */
	@Override
	public boolean hasInternetConnected() {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null) {
			NetworkInfo network = manager.getActiveNetworkInfo();
			if (network != null && network.isConnectedOrConnecting()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络连接，若没有，则弹出网络设置对话框，返回false
	 */
	@Override
	public boolean validateInternet() {
		ConnectivityManager manager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			openWirelessSet();
			return false;
		} else {
			NetworkInfo[] info = manager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		openWirelessSet();
		return false;
	}

	/**
	 * 判断GPS定位服务是否开启
	 */
	@Override
	public boolean hasLocationGPS() {
		LocationManager manager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		if (manager
				.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断基站定位是否开启
	 */
	@Override
	public boolean hasLocationNetWork() {
		LocationManager manager = (LocationManager) mContext
				.getSystemService(Context.LOCATION_SERVICE);
		if (manager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查内存卡可读
	 */
	@Override
	public void checkMemoryCard() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			new AlertDialog.Builder(mContext)
					.setTitle("检测内存卡")
					.setMessage("请检查内存卡")
					.setPositiveButton("设置",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
									Intent intent = new Intent(
											Settings.ACTION_SETTINGS);
									mContext.startActivity(intent);
								}
							})
					.setNegativeButton("退出",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							}).create().show();
		}
	}

	/**
	 * 打开网络设置对话框
	 */
	public void openWirelessSet() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
		dialogBuilder
				.setTitle("网络设置")
				.setMessage("检查网络")
				.setPositiveButton("网络设置",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								Intent intent = new Intent(
										Settings.ACTION_WIRELESS_SETTINGS);
								mContext.startActivity(intent);
							}
						})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		dialogBuilder.show();
	}

	/**
	 * 关闭键盘
	 */
	public void closeInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputMethodManager != null && this.getCurrentFocus() != null) {
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 
	 * 发出Notification
	 * 
	 * @param iconId
	 *            图标
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            你内容
	 * @param activity
	 */
	@SuppressWarnings("deprecation")
	public void PushNotification(int iconId, String contentTitle,
			String contentText, Class<?> activity, String to) {

		// 创建新的Intent，作为点击Notification留言条时， 会运行的Activity
		Intent notifyIntent = new Intent(this, activity);
		notifyIntent.putExtra("to", to);
		// 创建PendingIntent作为设置递延运行的Activity
		PendingIntent appIntent = PendingIntent.getActivity(mContext, 0,
				notifyIntent, 0);
		/* 创建Notication，并设置相关参数 */
		Notification myNoti = new Notification();
		// 点击自动消失
		myNoti.flags = Notification.FLAG_AUTO_CANCEL;
		/* 设置statusbar显示的icon */
		myNoti.icon = iconId;
		/* 设置statusbar显示的文字信息 */
		myNoti.tickerText = contentTitle;
		/* 设置notification发生时同时发出默认声音 */
		myNoti.defaults = Notification.DEFAULT_SOUND;
		/* 设置Notification留言条的参数 */
//		myNoti.setLatestEventInfo(mContext, contentTitle, contentText,
//				appIntent);
		/* 送出Notification */
		notificationManager.notify(0, myNoti);
	}

	/**
	 * 返回上下文对象
	 */
	@Override
	public Context getContext() {
		return mContext;
	}

	/**
	 * 返回登录用户的SharedPreferences对象
	 */
	@Override
	public SharedPreferences getLoginUserSharedPre() {
		return preferences;
	}

	/**
	 * 获取用户在线状态
	 */
	@Override
	public boolean getUserOnlineState() {
		return false;
	}

	/**
	 * 设置用户在线状态
	 */
	@Override
	public void setUserOnlineState(boolean isOnline) {

	}

}
