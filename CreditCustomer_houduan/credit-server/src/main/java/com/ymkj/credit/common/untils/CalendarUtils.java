package com.ymkj.credit.common.untils;

import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.ymkj.credit.common.ex.PlatformException;

/**
 * 常用日期格式
 */
public class CalendarUtils {

	/** 完整日期时间无间隔格式 */
	public static final String LONG_FORMAT = "yyyyMMddHHmmss";
	/** 日期无间隔格式 */
	public static final String SHORT_FORMAT = "yyyyMMdd";
	/** 无间隔时间格式 */
	public static final String SHORT_FORMAT_TIME="HHmmss";
	/** 完整日期时间横线与冒号分隔格式 */
	public static final String LONG_FORMAT_LINE="yyyy-MM-dd HH:mm:ss";
	/** 横线分隔日期格式 */
	public static final String SHORT_FORMAT_LINE="yyyy-MM-dd";
	/** 冒号分隔时间格式 */
	public static final String SHORT_FORMAT_TIME_COLON="HH:mm:ss";
	/**
	 * 无间隔天格式
	 */
	public static final String SHORT_FORMAT_DAY = "ddHHmmss";
	
	public static Calendar parseCalendar(String str, String... parsePatterns) {
		Calendar cal = Calendar.getInstance();
		try {
			if (ArrayUtils.isEmpty(parsePatterns)) {
				cal.setTime(DateUtils.parseDate(str, new String[]{LONG_FORMAT_LINE}));
			} else {
				cal.setTime(DateUtils.parseDate(str, parsePatterns));
			}

		} catch (ParseException e) {
			throw new PlatformException("日期格式转化错误",e);
		}
		return cal;
	}
	
	public static String parsefomatCalendar(Calendar cal, String parsePattern) {
		if (cal == null) {
			return "";
		}
		String str = "";
		if (StringUtils.isEmpty(parsePattern)) {
			str = DateFormatUtils.format(cal.getTimeInMillis(), LONG_FORMAT_LINE);
		} else {
			str = DateFormatUtils.format(cal.getTimeInMillis(), parsePattern);
		}
		return str;
	}
	
	public static Calendar parseCalendar(Calendar cal, String parsePattern){
		if (cal == null) {
			return null;
		}
		String strCal = parsefomatCalendar(cal, parsePattern);
		return parseCalendar(strCal,parsePattern);
	}
	
	public static Calendar getNextDay(Calendar cal) {
		cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + 1);
		return cal;
	}
	
	public static String getFormatNow(){
		return parsefomatCalendar(Calendar.getInstance(), CalendarUtils.LONG_FORMAT);
	}
	public static String getShortFormatNow(){
		return parsefomatCalendar(Calendar.getInstance(), CalendarUtils.SHORT_FORMAT);
	}
	
	public static String getFormatNow(String parsePattern){
		return parsefomatCalendar(Calendar.getInstance(), parsePattern);
	}
}
