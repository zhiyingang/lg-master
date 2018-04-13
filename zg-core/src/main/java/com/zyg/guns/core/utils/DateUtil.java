package com.zyg.guns.core.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类(线程安全)
 *
 * @author jung.fan
 */
public class DateUtil {

    public static final String PATTERN_ISO_DATE = "yyyy-MM-dd";
    public static final String PATTERN_ISO_TIME = "HH:mm:ss";
    public static final String PATTERN_ISO_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_ISO_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_SIMPLE_DATE = "yyyyMMdd";
    public static final String PATTERN_SIMPLE_TIME = "HHmmss";
    public static final String PATTERN_SIMPLE_DATETIME = "yyyyMMddHHmmss";
    public static final String PATTERN_SIMPLE_FULL = "yyyyMMddHHmmssSSS";
    public static final String[] PATTERNS = {PATTERN_ISO_DATE, PATTERN_ISO_TIME, PATTERN_ISO_DATETIME, PATTERN_ISO_FULL, PATTERN_SIMPLE_DATE,
            PATTERN_SIMPLE_TIME, PATTERN_SIMPLE_DATETIME, PATTERN_SIMPLE_FULL};

    /**
     * 日期转字符串
     *
     * @param date    日期类型
     * @param pattern 格式字符串
     * @return String 格式化后的字符串
     */
    public static String formatDate(Date date, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN_ISO_DATE;
        }
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 日期转字符串
     *
     * @param calendar 日历类型
     * @param pattern  格式字符串
     * @return String 格式化后的字符串
     */
    public static String formatDate(Calendar calendar, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = PATTERN_ISO_DATE;
        }
        return DateFormatUtils.format(calendar, pattern);
    }

    /**
     * 字符串转日期
     *
     * @param strDate 日期字符串
     * @return Date 解析后的日期类型
     * @throws java.text.ParseException
     */
    public static Date parseDate(String strDate) throws ParseException {
        if (StringUtils.isEmpty(strDate)) {
            return null;
        }
        return DateUtils.parseDate(strDate, PATTERNS);
    }

    /**
     * 字符串转日期
     *
     * @param strDate  日期字符串
     * @param patterns 格式字符串
     * @return Date 日期类型
     * @throws java.text.ParseException
     */
    public static Date parseDate(String strDate, String... patterns) throws ParseException {
        if (StringUtils.isEmpty(strDate)) {
            return null;
        }
        return DateUtils.parseDate(strDate, patterns);
    }

    /**
     * 获取当前时间的指定格式
     *
     * @param pattern
     * @return String
     */
    public static String getNow(String pattern) {
        return formatDate(Calendar.getInstance(), pattern);
    }

    /**
     * 计算日期间隔天数
     *
     * @param startDate
     * @param endDate
     * @return BigDecimal
     */
    public static Long getDiffDays(Date startDate, Date endDate) {
        long time = endDate.getTime() - startDate.getTime();
        return time / (1000 * 60 * 60 * 24);
    }

    /**
     * 计算日期加上天数后得到的日期
     *
     * @param date
     * @param days
     * @return Date
     */
    public static Date addDate(Date date, int days) {
        return DateUtils.addDays(date, days);
    }

    /**
     * 获取日期的年份
     *
     * @param date
     * @return int
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取日期的月份
     *
     * @param date
     * @return int
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取日期的日
     *
     * @param date
     * @return int
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * 获取某天所在月份的第一天.
     * @param date 某一天
     * @ return String 所在月份第一天
     * */
    public static String getFirstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        SimpleDateFormat format = new SimpleDateFormat(PATTERN_ISO_DATE);
        return format.format(calendar.getTime());
    }

    /**
     * 获取某天所在月份的最后一天.
     * @param date 某一天
     * @ return String 所在月份最后一天
     * */
    public static String getLastDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        SimpleDateFormat format = new SimpleDateFormat(PATTERN_ISO_DATE);
        return format.format(calendar.getTime());
    }
    
    /**
     * 获取当前系统的UNIX时间戳
     * @return  Long
     */
    public static Long getCurrentUnixTimestamp() {
    	return (System.currentTimeMillis() / 1000);
    }
    public static void main(String args[]) {
    	long epoch = System.currentTimeMillis();
    	System.out.println(epoch / 1000);
    }
}