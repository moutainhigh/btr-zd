package com.baturu.zd.util;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author CaiZhuliang
 * @since 2019-3-22
 */
public class DateUtil {

    private static final String YYYYMMDD = "yyyy-MM-dd";
    /**
     * 24小时制
     */
    private static final String YYYYMMDDHHMMSS_24 = "yyyy-MM-dd HH:mm:ss";
    /**
     * 12小时制
     */
    private static final String YYYYMMDDHHMMSS_12 = "yyyy-MM-dd hh:mm:ss";

    private DateUtil() {
    }

    /**
     * 格式化日期.返回格式 : yyyy-MM-dd
     * @param date 被格式化的日期
     */
    public static String formatYYYYMMDD(Date date) {
        return getDateStr(date, YYYYMMDD);
    }

    /**
     * 格式化日期.返回格式 : yyyy-MM-dd HH:mm:ss
     * @param date 被格式化的日期
     */
    public static String formatYYYYMMDDHHMMSS24(Date date) {
        return getDateStr(date, YYYYMMDDHHMMSS_24);
    }

    /**
     * 格式化日期.返回格式 : yyyy-MM-dd hh:mm:ss
     * @param date 被格式化的日期
     */
    public static String formatYYYYMMDDHHMMSS12(Date date) {
        return getDateStr(date, YYYYMMDDHHMMSS_12);
    }

    /**
     * 减日期。比如：今天是2019-3-22 xx:xx:xx，days传7,那么返回2019-3-15 xx:xx:xx
     * @param days 要减少的天数
     */
    public static Date countdown(int days) {
        Date currentDate = new Date();
        if (days > 0) {
            days = 0 - days;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    /**
     * 获取当前日期。返回默认的日期格式
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    /**
     * 获取当天的开始时间
     *
     * @return
     */
    public static Date getTodayeStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当天的结束时间
     *
     * @return
     */
    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 返回时间指定格式的字符串
     * @param date 日期时间
     * @param format 格式
     * @return
     */
    public static String getDateStr(Date date, String format) {
        if (null == date || StringUtils.isBlank(format)) {
            return StringUtils.EMPTY;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
