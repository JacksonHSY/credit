package com.ymkj.credit.common.untils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;

/**
 * 通用日期工具类
 *
 * @author longjw
 */
public class DateUtil {

    public final static String CEN_YYYYMMDD = "yyyy/MM/dd";
    public final static String DATAFORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public final static String CN_YYYY_MM_DD = "yyyy年MM月dd日";
    public final static String DATAFORMAT_YYYYMMDD = "yyyyMMdd";
    public final static String DATAFORMAT_YYYY_MM_DDHHmm = "yyyy-MM-dd HH:mm";
    public final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_SSS_FORMAT = "yyyyMMddHHmmSSS";
    public final static String DATEFORMAT_yyyy = "yyyy/MM/dd HH:mm:ss";
    public final static String DATE_TIME_HH_FORMAT = "HH";
    public final static String CN_MM_DD = "MM月dd日";
    /**上一月*/
    public final static String MONTH_NEXT = "next";
    /**下一月*/
    public final static String MONTH_PREVIOUS = "previous";
    /**上一周*/
    public final static String WEEK_NEXT = "next";
    /**下一周*/
    public final static String WEEK_PREVIOUS = "previous";

    /**
     * 日期字符串转日期
     *
     * @param dateString
     * @return
     */
    public static Date strToDate(String dateString, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        try {
            Date date = df.parse(dateString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getTime() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 将传入日期格式化成指定格式。
     *
     * @param d       要格式化的日期
     * @param pattern 格式化的样式
     * @return 日期的字符串形式
     */
    public static String format(Date d, String pattern) {
        if (d == null)
            return null;
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(d);
    }

    public static Date getCommStyleTime(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Date getCommStyleTimeForMonth(String date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM");
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date getCommStyleTime(String date, String format) {
        DateFormat df = new SimpleDateFormat(format);

        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 两个日期相减得天数
     *
     * @param enddate   结束日期 date类型
     * @param begindate 开始日期 date类型
     * @return 天数
     */
    public static int getIntervalDays(Date enddate, Date begindate) {
        long millisecond = enddate.getTime() - begindate.getTime();
        int day = (int) (millisecond / 24L / 60L / 60L / 1000L);
        return day;
    }

    /**
     * 两个日期相减得天数
     *
     * @param begindate    开始日期 (减数)
     * @param enddate      结束日期 (被减数)
     * @param formatString 日期格式
     * @return 天数
     * @throws ParseException
     */
    public static int getIntervalDays(String begindate, String enddate, String formatString) throws ParseException {
        int oneday = 1000 * 60 * 60 * 24;
        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTimeInMillis(stringToDateLong(begindate, formatString));
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(stringToDateLong(enddate, formatString));
        int interval = (int) ((endCalendar.getTimeInMillis() - beginCalendar.getTimeInMillis()) / oneday);
        return interval;
    }

    private static long stringToDateLong(String adate, String formatString) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        return formatter.parse(adate).getTime();
    }

    /**
     * 修正天数 正数向后添加天数，负数减去相应天数
     *
     * @param date 日期
     * @param days
     * @return description:
     * @date 2013-7-19 上午10:42:38
     */
    public static Date getReviseDate(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);

        return cal.getTime();
    }

    /**
     * 修正小时 正数向后添加小时，负数减去相应小时
     *
     * @param date 日期
     * @param hour
     * @return description:
     * @date 2013-7-19 上午10:42:38
     */
    public static Date getReviseHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }
    /**
     * 工作日偏移，不考虑节假日，周一至周五 视为工作日
     */
    public static Date workdayRevise(Date date, int offset) {
        if (null == date) {
            throw new RuntimeException("日期不能为空");
        }
        int increment = 1;
        if (offset < 0) {
            increment = -1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        for (int i = 0; i < Math.abs(offset); i++) {
            cal.add(Calendar.DATE, increment);
            int temp = cal.get(Calendar.DAY_OF_WEEK);
            while (Calendar.SATURDAY == temp || Calendar.SUNDAY == temp) {
                cal.add(Calendar.DATE, increment);
                temp = cal.get(Calendar.DAY_OF_WEEK);
            }
        }
        return DateUtils.truncate(cal.getTime(), Calendar.DATE);
    }

    /**
     * 获取指定时间的前一天日期
     *
     * @param theDay
     * @return
     */
    public static String getBeforeTheDay(Date theDay, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        Calendar cal = Calendar.getInstance();
        cal.setTime(theDay);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf.format(cal.getTime());
    }

    /**
     * 取得在指定时间上加减days天后的时间
     *
     * @param date 指定的时间
     * @param days 天数,正为加，负为减
     * @return 在指定时间上加减days天后的时间
     */
    public static Date addDays(Date date, int days) {
        Date time = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        time = cal.getTime();
        return time;
    }

    /**
     * 获取指定时间的前一天日期
     * @return
     */
    public static String getBeforeDay(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf.format(cal.getTime());
    }
    /**
     * 获取指定时间的前一天日期
     * @return
     */
    public static Date getBeforeDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    /**
     * 获取当前时间的前一天
     * @return
     */
    public static String getBeforeDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return sdf.format(cal.getTime());
    }

    /**
     * 返回日期的天
     *
     * @param date
     * @return
     */
    public static int getDayOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 日期转换成字符串
     *
     * @param date
     * @param formatPattern
     * @return
     */
    public static String getDateFormatString(Date date, String formatPattern) {
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(date);
    }

    /**
     * 日期转换成指定格式的日期
     *
     * @param date
     * @return
     */
    public static Date getDateFormatDate(Date date) {
        String dateStr = DateUtil.getDateFormatString(date, CEN_YYYYMMDD);
        SimpleDateFormat sdf = new SimpleDateFormat(CEN_YYYYMMDD);
        Date date_ = null;
        try {
            date_ = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date_;
    }

    /**
     * 获取两个日期相差的月数
     *
     * @param d1 较大的日期
     * @param d2 较小的日期
     * @return 如果d1>d2返回 月数差 否则返回0
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (c1.getTimeInMillis() < c2.getTimeInMillis()) return 0;
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 假设 d1 = 2015-8-16  d2 = 2011-9-30
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) yearInterval--;
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) monthInterval--;
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval;
    }

    /**
     * 获取指定时间的月份的第一天  返回指定日期格式的字符串
     *
     * @param date
     * @return
     * @author jinzg
     */
    public static String getFirstDayOfTheMonth(Date date, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date startDate = cal.getTime();
        return DateUtil.format(startDate, pattern);
    }

    /**
     * 获取指定月份的天数
     *
     * @param date
     * @return
     */
    public static int getMonthLastDay(Date date) {
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        int month = Integer.parseInt(new SimpleDateFormat("MM").format(date));
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 根据当前时间加多少分中、多少秒
     * @return
     */
    public static String getCurrentTimeAddMinutes(String beginDate) {
        String time = "";
        SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_yyyy);
        Date date;
        try {
            date = df.parse(beginDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MINUTE, -1);
            // c.add(Calendar.SECOND, -30);
            time = df.format(c.getTime());
        } catch (ParseException e) {
            return "";
        }

        return time;
    }

    /**
     * 判断日期是否在今天及之前
     *
     * @param date
     * @return
     */
    public static boolean beforeNow(Date date) {
        Date nowDate = strToDate(format(new Date(), DATAFORMAT_YYYY_MM_DD), DATAFORMAT_YYYY_MM_DD);
        return (null != date && getIntervalDays(date, nowDate) <= 0);
    }

    /**
     * 判断日期是否在今天(+num)及之前
     *
     * @param date
     * @param num
     * @return
     */
    public static boolean beforeNowAddNum(Date date, int num) {
        Date nowDate = strToDate(format(new Date(), DATAFORMAT_YYYY_MM_DD), DATAFORMAT_YYYY_MM_DD);
        return (null != date && getIntervalDays(date, DateUtil.addDays(nowDate, num)) <= 0);
    }

    /**
     * 获取两个日期相差的月数 和day无关
     *
     * @param d1 较大的日期
     * @param d2 较小的日期
     * @return 如果d1>d2返回 月数差 否则返回0
     */
    public static int getMonthDiffNoDate(Date d1, Date d2) {
        int c = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (c1.getTimeInMillis() < c2.getTimeInMillis()) return 0;
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int yearInterval = year1 - year2;
        if (yearInterval >= 1) {
            //跨年
            c = (yearInterval * 12) + c1.get(Calendar.MONDAY) - c2.get(Calendar.MONTH) + 1;
        } else {
            c = c1.get(Calendar.MONDAY) - c2.get(Calendar.MONTH) + 1;
        }
        return c;
    }

//    /**
//     * 获取两个日期间的月份集合
//     * @param minDate
//     * @param maxDate
//     * @return
//     */
//    public static List<MaxMinTimeDto> getMonthList(Date minDate, Date maxDate, String queryDate){
//
//        List<MaxMinTimeDto> dtos = new ArrayList<MaxMinTimeDto>();
//        if(minDate.getYear() == maxDate.getYear() && minDate.getMonth() == maxDate.getMonth()){
//            MaxMinTimeDto minTimeDto = new MaxMinTimeDto();
//
//            minTimeDto.setMinTime(minDate);
//            minTimeDto.setMaxTime(maxDate);
//            dtos.add(minTimeDto);
//        }else{
//            if(minDate.before(maxDate)){
//                Date minTime = minDate;
//                Date maxTime = maxDate;
//                while(minTime.before(maxTime)){//若最小日期在最大日期之前
//                    MaxMinTimeDto minTimeDto = new MaxMinTimeDto();
//                    minTimeDto.setMinTime(minTime);//设置最小日期
//                    Date tempMaxTime = getEndDayOfTheMonth(minTime);//获取本月最后一天
//                    if(tempMaxTime.before(maxDate)){//如果下月最后一天不在最大日期之后,终止取值
//                        minTimeDto.setMaxTime(tempMaxTime);//设置最大日期
//                        dtos.add(minTimeDto);
//                    }else{
//                        if(StringUtils.isNotBlank(queryDate)){//有值
//                            queryDate = queryDate.replace("-","/");
//                            Date max = strToDate(queryDate,CEN_YYYYMMDD);
//                            minTimeDto.setMaxTime(max);
//                        }else{
//                            minTimeDto.setMaxTime(maxDate);//设置最大日期
//                        }
//                        dtos.add(minTimeDto);
//                        break;
//                    }
//                    minTime = getNextMonthFirstDay(minTime);
//                }
//            }
//        }
//        return dtos;
//    }

    /**
     * 得到指定月份的最后一天
     * @param date
     * @return
     */
    public static Date getEndDayOfTheMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取当前时间下一月的第一天
     * @param date
     * @return
     */
    public static Date getNextMonthFirstDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH,1);

        return calendar.getTime();
    }

    /**
     * 获取月初到指定日期的日期组合,格式为:3月1日,3月2日
     * @param currDate 指定日期
     * @return
     */
    public static List<String> getDayListFromStartToToday(Date currDate){

        List<String> monthList = new ArrayList<String>();

        Date monthStart = getMonthStart(currDate);
        SimpleDateFormat format = new SimpleDateFormat(CN_MM_DD);
        if(monthStart == currDate){//当前日期是当月第一天
            monthList.add(format.format(monthStart));
        }else{
            while (!monthStart.after(currDate)) {
                monthList.add(format.format(monthStart));
                monthStart = getNext(monthStart);
            }
        }

        return monthList;
    }

    private static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (1 - index));
        return calendar.getTime();
    }

    private static Date getNext(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     * 获取日期 星期天数
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }
    
	 /**
	  * 当月第几天
	  * @TODO
	  * @param date
	  * @param days
	  * @return
	  * Date
	  * @author changj@yuminsoft.com
	  * @date2018年5月15日
	  */
    public static Date getDayOfMonth(Date date,int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH,days);
        return calendar.getTime();
    }
}
