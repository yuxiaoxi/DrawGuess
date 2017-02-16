/**   
 * 用一句话描述该文件做什么.
 * @title DateUtil.java
 * @package com.sinsoft.android.util
 * @author shimiso  
 * @update 2012-6-26 上午9:57:56  
 */
package com.zhy.graph.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @ClassName: DateUtil
 * @Description: 日期工具类
 * @author 余卓
 * @date 2014年10月21日 下午6:47:15
 * 
 */

public class DateUtil {

	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date str2Date(String str) {
		return str2Date(str, null);
	}

	public static Date str2Date(String str, String format) {
		if (str == null || str.length() == 0) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.parse(str);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;

	}

	public static Calendar str2Calendar(String str) {
		return str2Calendar(str, null);

	}

	public static Calendar str2Calendar(String str, String format) {

		Date date = str2Date(str, format);
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);

		return c;

	}

	public static String date2Str(Calendar c) {// yyyy-MM-dd HH:mm:ss
		return date2Str(c, null);
	}

	public static String date2Str(Calendar c, String format) {
		if (c == null) {
			return null;
		}
		return date2Str(c.getTime(), format);
	}

	public static String date2Str(Date d) {// yyyy-MM-dd HH:mm:ss
		return date2Str(d, null);
	}

	public static String date2Str(Date d, String format) {// yyyy-MM-dd HH:mm:ss
		if (d == null) {
			return null;
		}
		if (format == null || format.length() == 0) {
			format = FORMAT;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String s = sdf.format(d);
		return s;
	}

	public static String getCurDateStr() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-"
				+ c.get(Calendar.DAY_OF_MONTH) + "-"
				+ c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)
				+ ":" + c.get(Calendar.SECOND);
	}

	/**
	 * 获得当前日期的字符串格式
	 * 
	 * @param format
	 * @return
	 */
	public static String getCurDateStr(String format) {
		Calendar c = Calendar.getInstance();
		return date2Str(c, format);
	}

	// 格式到秒
	public static String getMillon(long time) {

		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(time);

	}

	// 格式到天
	public static String getDay(long time) {

		return new SimpleDateFormat("yyyy年MM月dd日").format(time);

	}

	// 格式到毫秒
	public static String getSMillon(long time) {

		return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(time);

	}

	//距离当前时间
	public static String getLeaveNow(long ltime, long ntime) {
		long h = ntime - ltime;
		int minute = (int) (h/60);		
		if (minute >= 1440) {
			return minute / 1440 + "天前";
		} else if (minute >= 60) {
			return minute / 60 + "小时前";
		} else {
			return minute + "分钟前";
		}

	}

	public static String getMandD(long time) {
		String str = new SimpleDateFormat("MM-dd").format(time);
		String m = str.split("-")[0];
		String newM = "";
		if (m.equals("01")) {
			newM = "一月";
		} else if (m.equals("02")) {
			newM = "二月";
		} else if (m.equals("03")) {
			newM = "三月";
		} else if (m.equals("04")) {
			newM = "四月";
		} else if (m.equals("05")) {
			newM = "五月";
		} else if (m.equals("06")) {
			newM = "六月";
		} else if (m.equals("07")) {
			newM = "七月";
		} else if (m.equals("08")) {
			newM = "八月";
		} else if (m.equals("09")) {
			newM = "九月";
		} else if (m.equals("10")) {
			newM = "十月";
		} else if (m.equals("11")) {
			newM = "十一月";
		} else if (m.equals("12")) {
			newM = "十二月";
		}
		return newM + "-" + str.split("-")[1];
	}
}
