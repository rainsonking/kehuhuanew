package com.kwsoft.kehuhua.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.kwsoft.kehuhua.adcustom.EditActivity;
import com.kwsoft.kehuhua.adcustom.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class DatePickDialogUtil implements OnDateChangedListener,
		OnTimeChangedListener {

	private DatePicker datePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	// 返回的时间
	public String callbackTime;

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期初始值
	 */
	public DatePickDialogUtil(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public void init(DatePicker datePicker) {

		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + "年"
					+ calendar.get(Calendar.MONTH) + "月"
					+ calendar.get(Calendar.DAY_OF_MONTH) + "日 ";
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
	}

	/**
	 *
	 *
	 * @param inputDate
	 * @param position
	 * @param mList
	 * @param key
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate, final int position, final List<Map<String, Object>> mList,final String key) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.common_date, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		init(datePicker);
		ad = new AlertDialog.Builder(activity)
				.setTitle(initDateTime)
				.setView(dateTimeLayout)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Log.e("TAG", "是否走callback" + callbackTime);
						Log.e("TAG", "inputDate" + inputDate);
						inputDate.setText(callbackTime);
						mList.get(position).put(key, callbackTime);
						if (activity instanceof EditActivity){
							EditActivity.mapCommit.put(mList.get(position).get("jiChuKey") + "", mList.get(position).get("rightData") + "");
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
//						inputDate.setText("");
							}
						}

				).show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}
	
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth());
		Log.e("TAG", "取完后的年" + datePicker.getYear());
		Log.e("TAG", "取完后的月" + datePicker.getMonth());
		Log.e("TAG", "取完后的日" + datePicker.getDayOfMonth());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		dateTime = sdf.format(calendar.getTime());
		callbackTime = sdf2.format(calendar.getTime());
		Log.e("TAG", "取完后的值" + callbackTime);
		ad.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日拆分成年 月 日,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012年07月02日拆分成年 月 日
		String yearStr = spliteString(initDateTime, "-", "index", "front"); // 年份
		String monthAndDay = spliteString(initDateTime, "-", "index", "back"); // 月日

		String monthStr = spliteString(monthAndDay, "-", "index", "front"); // 月
		String dayStr = spliteString(monthAndDay, "-", "index", "back"); // 日

		System.out.println("yearStr===>" + yearStr);
		int currentYear = Integer.valueOf(yearStr.trim()).intValue();
		int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
		int currentDay = Integer.valueOf(dayStr.trim()).intValue();

		calendar.set(currentYear, currentMonth, currentDay);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}
}
