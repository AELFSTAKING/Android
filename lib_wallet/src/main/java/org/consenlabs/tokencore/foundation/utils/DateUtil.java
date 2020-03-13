package org.consenlabs.tokencore.foundation.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

    public static final String DMY_TIME_24_HOURS = "dd-MM-yyyy kk:mm";
    public static final String YMD_TIME_24_HOURS = "yyyy-MM-dd kk:mm";
    public static final String MDY_TIME_24_HOURS = "MM-dd-yyyy kk:mm";

    public final static String FMT_STD_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String FMT_STD_TIME = "HH:mm:ss";
    public final static String FMT_STD_DATE = "yyyy-MM-dd";
    public final static String FMT_MM_DD_HH_MM_SS = "MM-dd HH:mm:ss";
    public final static String FMT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private final static Calendar calendar = Calendar.getInstance();


    public static String formatDateTime(long time) {
        return formatDateTime(null, time);
    }

    public static String formatDateTime(String pattern, long time) {
        if (null == pattern) {
            // default format
            pattern = FMT_STD_DATE_TIME;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(time));
    }

    public static Date getTime(String pattern, String formatDateTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(formatDateTime);
    }

    public static String formatDateTime(String time) {
        return formatDateTime(null, time);
    }

    public static String formatDateTime(String pattern, String time) {
        if (null == pattern) {
            // default format
            pattern = FMT_STD_DATE_TIME;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(Long.valueOf(time)));
    }


    /**
     * date转换为字符串，默认为 yyyy-MM-dd HH:mm:ss
     */
    public static String date2String(Date date) {
        return date2String(date, null);
    }

    /**
     * date转换为字符串
     *
     * @param format 格式
     */
    public static String date2String(Date date, String format) {
        if (null == format) {
            // default format
            format = FMT_STD_DATE_TIME;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public static Date getDateFromCalender(int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar.getTime();
    }

    public static long getUTCTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return cal.getTimeInMillis() / 1000;
    }
}
