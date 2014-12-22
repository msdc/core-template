package com.isoftstone.crawl.template.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.lj.util.string.StringHelper;

/**
 * 
 * @ClassName: DateFormatter
 * @Description: TODO(日期格式化)
 * @author lj
 * @date 2014年7月25日 下午12:42:16
 * 
 */
public class DateFormatter {
	public static final String YYMMDDHH = "yyMMddHH";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYYMMDD_LINE = "yyyy-MM-dd";
	public static final String YYYYMMDD_SLASH = "yyyy/MM/dd";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
	public static final String HHMM = "HH-mm";

	public static final String LONG = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})";
	public static final String MIDDLE = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?\\s+\\d{1,2}:\\d{1,2})";
	public static final String SHORT = "(20\\d{2}[-|/|年](?:0[1-9]|1[012])[-|/|月](?:0[1-9]|[12][0-9]|3[01])日?)";

	private static Pattern pattern_hhmm = StringHelper.initPattern("天\\s*(?<hh>\\d{2}):(?<mm>\\d{2})");
	private static Pattern pattern_mm = StringHelper.initPattern("(?<mm>\\d+).*?前");
	private static Matcher matcher_hhmm, matcher_mm;
	private static Calendar calendar = Calendar.getInstance();

	public static String formatDate2Str(Date date, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);

			return dateFormat.format(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	/*
	 * 时间戳转换为日期
	 */
	public static Date formatbyTimestamp(String timestamp, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return formatStr2Date(dateFormat.format(new Date(Long.parseLong(timestamp) * 1000)), format);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Date formatStr2Date(String date, String format) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.parse(date);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: formatDate
	 * @Description: TODO(将日期格式化为yyyy-MM-dd HH:mm:ss输出)
	 * @param @param date
	 * @param @param format
	 * @param @return 设定文件
	 * @return Date 返回类型
	 * @author lj
	 * @throws
	 */
	public static Date formatDate(String date) {
		try {
			if(date!=null && date != "")
			{
				// 1、标准化
				date = date.replaceAll("\\s+", " ").replaceAll("年|月|/", "-").replace("日", "").trim();
				// 2、判断日期格式,将其统一
				if (date.matches(SHORT)) {
					date = date + " 00:00:00";
				} else if (date.matches(MIDDLE)) {
					date = date + ":00";
				} else if (date.matches(LONG)) {
					//
				}
				return formatStr2Date(date, YYYYMMDDHHMMSS);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new Date();
	}

	public static Date format(String date, String format, String source) {
		try {

			calendar.setTime(new Date());
			if (source == "网易微博") {
				matcher_hhmm = pattern_hhmm.matcher(date);
				matcher_mm = pattern_mm.matcher(date);
				int hh = 0, mm = 0;
				if (matcher_hhmm.find()) {
					mm = Integer.parseInt(matcher_hhmm.group("mm"));
					hh = Integer.parseInt(matcher_hhmm.group("hh"));
				} else if (matcher_mm.find()) {
					mm = Integer.parseInt(matcher_mm.group("mm"));
				}

				if (date.contains("今天")) {
					calendar.set(calendar.MINUTE, mm);
					calendar.set(calendar.HOUR_OF_DAY, hh);
					return calendar.getTime();
				} else if (date.contains("昨天")) {
					calendar.set(calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
					calendar.set(calendar.MINUTE, mm);
					calendar.set(calendar.HOUR_OF_DAY, hh);
					return calendar.getTime();
				} else if (date.contains("分钟前")) {
					calendar.set(calendar.MINUTE, calendar.get(calendar.MINUTE) - mm);
					return calendar.getTime();
				} else if (date.contains("秒前")) {
					calendar.set(calendar.SECOND, calendar.get(calendar.SECOND) - mm);
					return calendar.getTime();
				} else {
					return DateFormatter.formatStr2Date(date, DateFormatter.YYYYMMDDHHMM);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @Title: compare
	 * @Description: TODO 比较两个日期大小
	 * @param @param date1
	 * @param @param date2
	 * @param @return 设定文件
	 * @return boolean true：小于 false:大于
	 * @author lj
	 * @throws
	 */
	public static boolean compare(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			return date1.before(date2);
		}
		return false;
	}

	/**
	 * 
	 * @Title: compare2
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param date1
	 * @param @param date2
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @author lj
	 * @throws
	 */
	public static int compare2(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			if (date1.getTime() > date2.getTime()) {
				return 1;
			} else if (date1.getTime() < date2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		}
		return 0;
	}

	public static String nDaysBeforeToday(int n) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar rightNow = Calendar.getInstance();

		rightNow.add(Calendar.DAY_OF_MONTH, -n);

		return sdf.format(rightNow.getTime());
	}

	public static Date nDaysBeforeTodayDate(int n) {
		Calendar rightNow = Calendar.getInstance();

		rightNow.add(Calendar.DAY_OF_MONTH, -n);

		return rightNow.getTime();
	}

	public static Date nSecondBeforeNow(int n) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.SECOND, -n);
		return rightNow.getTime();
	}

	public static Date nMinuteBeforeNow(int n) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.MINUTE, -n);
		return rightNow.getTime();
	}

	public static Date nHourBeforeNow(int n) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.add(Calendar.HOUR_OF_DAY, -n);
		return rightNow.getTime();
	}

	public static void main(String[] args) {
		System.out.println(formatDate("2014年10月10日 12:00:00"));
		System.out.println(formatDate("2014/10/10 12:00:00"));
		System.out.println(formatDate("2014-10-10     00:00   "));
		System.out.println(formatDate("2014-12-11"));
		System.out.println(formatDate("2014-12-12 00:00:00"));
		System.out.println(formatDate("2014-12-10 00:00"));
		System.out.println(formatDate("2014/02/12"));
	}
}
