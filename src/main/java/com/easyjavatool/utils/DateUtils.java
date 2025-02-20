package com.easyjavatool.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String INCLINE_YYYY_MM_DD = "yyyy/MM/dd";
    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
