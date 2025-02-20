package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;
import com.easyjavatool.utils.JSONUtils;
import com.easyjavatool.utils.PropertiesUtils;
import com.easyjavatool.utils.RefactorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class BuildTable {

    private static final Logger log = LoggerFactory.getLogger(BuildTable.class);

    private static Connection connection = null;

    private static final String SQL_SHOW_TABLES_STATUS = "show table status";

    private static final String SQL_FULL_FIELDS_FROM = "show full fields from ";

    private static final String SQL_SHOW_INDEX_FROM = "show index from ";

    static {
        String driverName = PropertiesUtils.getProperty("db.driver.name");
        String url = PropertiesUtils.getProperty("db.url");
        String username = PropertiesUtils.getProperty("db.username");
        String password = PropertiesUtils.getProperty("db.password");
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            log.error("Failed to connect to database", e);
        }
    }

    public static List<TableInfo> getTables(){
        PreparedStatement query = null;
        ResultSet res = null;

        List<TableInfo> tableInfoList = new ArrayList<>();

        try {
            query = connection.prepareStatement(SQL_SHOW_TABLES_STATUS);
            res = query.executeQuery();
            while (res.next()) {
                String tableName = res.getString("Name");
                String comment = res.getString("Comment");
                String beanName = tableName;
                if(Constants.IGNORE_TABLE_PREFIX){
                    beanName = tableName.substring(tableName.indexOf("_") + 1);
                }
                beanName = RefactorUtils.refactorName(beanName, true);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setComment(comment);
                tableInfo.setBeanName(beanName);
                tableInfo.setBeanParamName(beanName + Constants.SUFFIX_BEAN_QUERY);
                tableInfo.setHaveDate(false);
                tableInfo.setHaveDateTime(false);
                tableInfo.setHaveBigDecimal(false);

                readFieldInfo(tableInfo);
                readIndexInfo(tableInfo);

                log.info("TableInfo: {}", JSONUtils.toJSONString(tableInfo));
                tableInfoList.add(tableInfo);
            }
        } catch (Exception e) {
            log.error("Failed to get tables", e);
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (query != null) {
                    query.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                log.error("Failed to close resources", e);
            }
        }
        return tableInfoList;
    }

    public static void readFieldInfo(TableInfo tableInfo){
        List<FieldInfo> fieldInfoList = new ArrayList<>();
        PreparedStatement query = null;
        ResultSet res = null;
        try {
            query = connection.prepareStatement(SQL_FULL_FIELDS_FROM + tableInfo.getTableName());
            res = query.executeQuery();
            while (res.next()){
                String fieldName = res.getString("Field");
                String sqlType = res.getString("Type");
                if(sqlType.indexOf("(") > 0){
                    sqlType = sqlType.substring(0, sqlType.indexOf("("));
                }
                String comment = res.getString("Comment");
                String isAutoIncrement = res.getString("Extra");

                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(fieldName);
                fieldInfo.setPropertyName(RefactorUtils.refactorName(fieldName, false));
                fieldInfo.setSqlType(sqlType);
                fieldInfo.setJavaType(RefactorUtils.sqlType2JavaType(sqlType));
                fieldInfo.setComment(comment);
                fieldInfo.setAutoIncrement("auto_increment".equals(isAutoIncrement));
                fieldInfoList.add(fieldInfo);
                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, sqlType)){
                    tableInfo.setHaveDate(true);
                }
                if(ArrayUtils.contains(Constants.SQL_DATETIME_TYPES, sqlType)){
                    tableInfo.setHaveDateTime(true);
                }
                if(ArrayUtils.contains(Constants.SQL_DECIMAL_TYPES, sqlType)){
                    tableInfo.setHaveBigDecimal(true);
                }
            }
            tableInfo.setFieldList(fieldInfoList);
            tableInfo.setExtendedFieldList(new ArrayList<>());
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e) {
                log.error("Failed to close resources", e);
            }
        }
    }

    public static void readIndexInfo(TableInfo tableInfo){
        PreparedStatement query = null;
        ResultSet res = null;

        try {
            query = connection.prepareStatement(SQL_SHOW_INDEX_FROM + tableInfo.getTableName());
            res = query.executeQuery();

            Map<String, FieldInfo> fieldInfoMap = tableInfo.getFieldList().stream().collect(Collectors.toMap(FieldInfo::getFieldName, fieldInfo -> fieldInfo));

            while (res.next()){
                String keyName = res.getString("Key_name");
                String columnName = res.getString("Column_name");
                int nonUnique = res.getInt("Non_unique");

                if(nonUnique == 1){
                    continue;
                }
                List<FieldInfo> keyFieldList = tableInfo.getKeyIndexMap().computeIfAbsent(keyName, k -> new ArrayList<>());
                keyFieldList.add(fieldInfoMap.get(columnName));
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e) {
                log.error("Failed to close resources", e);
            }
        }
    }
}
