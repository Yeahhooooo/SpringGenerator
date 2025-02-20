package com.easyjavatool.utils;

import com.easyjavatool.constants.Constants;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

/**
 * Change name from database like user_info to UserInfo
 */
public class RefactorUtils {

    public static String refactorName(String fieldName, boolean firstCharUpper) {
        if(fieldName == null || fieldName.isEmpty()) {
            return "";
        }
        String[] split = fieldName.split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            if (s.isEmpty()) {
                continue;
            }
            sb.append(s.substring(0, 1).toUpperCase()).append(s.substring(1));
        }
        if (!firstCharUpper) {
            sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
        }
        return sb.toString();
    }

    public static String sqlType2JavaType(String sqlType) {
        if(sqlType == null || sqlType.isEmpty()) {
            throw new RuntimeException("sqlType is empty");
        }
        if(sqlType.contains("(")) {
            sqlType = sqlType.substring(0, sqlType.indexOf("("));
        }
        if(ArrayUtils.contains(Constants.SQL_INTEGER_TYPES, sqlType)) {
            return "Integer";
        } else if(ArrayUtils.contains(Constants.SQL_LONG_TYPES, sqlType)) {
            return "Long";
        } else if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, sqlType)) {
            return "BigDecimal";
        } else if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, sqlType)) {
            return "String";
        } else if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlType) || ArrayUtils.contains(Constants.SQL_DATETIME_TYPES, sqlType)) {
            return "Date";
        } else {
            throw new RuntimeException("Unknown sql type: " + sqlType);
        }
    }

}
