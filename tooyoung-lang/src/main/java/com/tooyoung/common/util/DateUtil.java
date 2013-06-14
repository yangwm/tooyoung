/**
 * 
 */
package com.tooyoung.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * date util 
 * 
 * @author yangwm May 18, 2013 4:52:23 PM
 */
public class DateUtil {

    private static ThreadLocal<DateFormat> yearMonthSdf = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yy_MM", Locale.ENGLISH);
        }
    };
    
    private static ThreadLocal<DateFormat> yearMonthDaySdf = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat ("yyyy-MM-dd");
        }
    };
    
    private static ThreadLocal<DateFormat> dateTimeSdf = new ThreadLocal<DateFormat>(){
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String getYearMonth(Date date){     
        return yearMonthSdf.get().format(date);
    }
    
    public static String formateYearMonthDay(Date date){ 
        return yearMonthDaySdf.get().format(date);
    }
    public static Date parseYearMonthDay(String timeStr, Date defaultValue){
        if(timeStr == null){
            return defaultValue;
        }
        try {
            return yearMonthDaySdf.get().parse(timeStr);
        } catch (ParseException e) {
            return defaultValue;
        }
    }
    
    public static String formateDateTime(Date date){ 
        return dateTimeSdf.get().format(date);
    }
    public static Date parseDateTime(String timeStr, Date defaultValue){
        if(timeStr == null){
            return defaultValue;
        }
        try {
            return dateTimeSdf.get().parse(timeStr);
        } catch (ParseException e) {
            return defaultValue;
        }
    }
    
    public static final int getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    public static final int getLastHour() {
        int hour = getCurrentHour();
        return hour == 0 ? 23 : hour - 1;
    }
    
    public static final int getNextHour() {
        int hour = getCurrentHour();
        return hour == 23 ? 0 : hour + 1;
    }
    

    public static Date getFirstDayOfCurMonth(){
        Calendar calendar = Calendar.getInstance();
        
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
    public static Date getFirstDayInMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        if(date != null){
            calendar.setTime(date);
        }       
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * 获得距离这个月的n个月的起始日期
     * @param month：负数表示 之前的月份; 正数表示以后的月份；0表示当前月份:
     * @return
     */
    public static Date getFirstDayInMonth(int month){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, month);
        return getFirstDayInMonth(c.getTime());
    }

    public static boolean isCurrentMonth(Date date){
        if(date != null){
            Calendar dest = Calendar.getInstance();
            dest.setTime(date);
            Calendar now = Calendar.getInstance();
            return now.get(Calendar.YEAR) == dest.get(Calendar.YEAR)
                && now.get(Calendar.MONTH) == dest.get(Calendar.MONTH);
        }
        return false;
    }
    
}
