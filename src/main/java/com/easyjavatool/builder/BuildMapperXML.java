package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildMapperXML {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildMapperXML.class);

    private static final String SQL_BASE_COLUMN_LIST = "Base_Column_List";

    private static final String SQL_BASE_CONDITION_LIST = "Base_Condition_List";

    private static final String SQL_EXTENDED_CONDITION_LIST = "Extended_Condition_List";

    private static final String SQL_QUERY_CONDITION_LIST = "Query_Condition_List";

    public static void execute(TableInfo tableInfo) {
        File folder = new File(Constants.PATH_MAPPER_XML);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String mapperClassName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File mapperFile = new File(folder, mapperClassName + ".xml");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(mapperFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
            bw.newLine();
            bw.write("<mapper namespace=\"" + Constants.PACKAGE_MAPPER + "." + mapperClassName + "\">");
            bw.newLine();
            bw.newLine();

            // basic information
            resultMap(bw, tableInfo);
            bw.newLine();
            baseColumn(bw, tableInfo);
            bw.newLine();
            baseCondition(bw, tableInfo);
            bw.newLine();
            extendedCondition(bw, tableInfo);
            bw.newLine();
            queryCondition(bw, tableInfo);
            bw.newLine();

            // select
            baseSelect(bw, tableInfo);
            bw.newLine();

            // insert
            baseInsert(bw, tableInfo);
            bw.newLine();

            // insert or update
            baseInsertOrUpdate(bw, tableInfo);
            bw.newLine();


            // query by index
            queryByIndex(bw, tableInfo);
            bw.newLine();


            // update by param
            buildUpdateByParam(bw, tableInfo);
            bw.newLine();


            bw.write("</mapper>");
            bw.flush();
        } catch (Exception e) {
            log.error("Build Mapper XML error", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (osw != null) {
                    osw.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void resultMap(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        List<FieldInfo> primaryKeyList = tableInfo.getKeyIndexMap().get("PRIMARY");
        String primaryKey = "";
        if(primaryKeyList != null && primaryKeyList.size() == 1){
            primaryKey = primaryKeyList.get(0).getFieldName();
        }

        bw.write("\t<!-- result map -->");
        bw.newLine();
        bw.write("\t<resultMap id=\"BaseResultMap\" type=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
        bw.newLine();

        for(FieldInfo fieldInfo : tableInfo.getFieldList()) {
            bw.write("\t\t<!-- " + fieldInfo.getComment() + " -->");
            bw.newLine();
            if(!primaryKey.isEmpty() && fieldInfo.getFieldName().equals(primaryKey)) {
                bw.write("\t\t<id column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\" />");
            } else {
                bw.write("\t\t<result column=\"" + fieldInfo.getFieldName() + "\" property=\"" + fieldInfo.getPropertyName() + "\" />");
            }
            bw.newLine();
        }

        bw.write("\t</resultMap>");
        bw.newLine();
    }

    private static void baseColumn(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- general column list -->");
        bw.newLine();
        bw.write("\t<sql id=\"" + SQL_BASE_COLUMN_LIST + "\">");
        bw.newLine();
        bw.write("\t\t" + tableInfo.getFieldList().stream().map(FieldInfo::getFieldName).reduce((a, b) -> a + ", " + b).orElse(""));
        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
    }

    private static void baseCondition(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- general condition list -->");
        bw.newLine();
        bw.write("\t<sql id=\"" + SQL_BASE_CONDITION_LIST + "\">");
        bw.newLine();

        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            String stringQuery = "";

            if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())){
                stringQuery = " and query." + fieldInfo.getPropertyName() + " != ''";
            }

            bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null" + stringQuery + "\">");
            bw.newLine();
            bw.write("\t\t\tAND " + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "}");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }

        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
    }

    public static void extendedCondition(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- extended condition list -->");
        bw.newLine();
        bw.write("\t<sql id=\"" + SQL_EXTENDED_CONDITION_LIST + "\">");
        bw.newLine();

        List<FieldInfo> extendedFieldList = tableInfo.getExtendedFieldList();

        for(FieldInfo fieldInfo : extendedFieldList){
            String condition = "";
            if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())){
                condition = "\t\t\tAND " + fieldInfo.getFieldName() + " like concat('%', #{query." + fieldInfo.getPropertyName() + "}, '%')";
            } else if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATETIME_TYPES, fieldInfo.getSqlType())){
                if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_START)) {
                    condition = "\t\t\t<![CDATA[ AND " + fieldInfo.getFieldName() + " >= str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d')]]>";
                } else if(fieldInfo.getPropertyName().endsWith(Constants.SUFFIX_BEAN_QUERY_TIME_END)) {
                    condition = "\t\t\t<![CDATA[ AND " + fieldInfo.getFieldName() + " <= date_sub(str_to_date(#{query." + fieldInfo.getPropertyName() + "}, '%Y-%m-%d'), interval -1 day)]]>";
                }
            }

            bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null and query." + fieldInfo.getPropertyName() + " != ''\">");
            bw.newLine();
            bw.write(condition);
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }

        bw.write("</sql>");
        bw.newLine();
    }

    public static void queryCondition(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- query condition list -->");
        bw.newLine();
        bw.write("\t<sql id=\"Query_Condition_List\">");
        bw.newLine();
        bw.write("\t\t<where>");
        bw.newLine();
        bw.write("\t\t\t<include refid=\"" + SQL_BASE_CONDITION_LIST + "\" />");
        bw.newLine();
        bw.write("\t\t\t<include refid=\"" + SQL_EXTENDED_CONDITION_LIST + "\" />");
        bw.newLine();
        bw.write("\t\t</where>");
        bw.newLine();
        bw.write("\t</sql>");
        bw.newLine();
    }

    public static void baseSelect(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- select list -->");
        bw.newLine();
        bw.write("\t<select id=\"selectList\" resultMap=\"BaseResultMap\">");
        bw.newLine();
        bw.write("\t\tSELECT");
        bw.newLine();
        bw.write("\t\t<include refid=\"" + SQL_BASE_COLUMN_LIST + "\" />");
        bw.newLine();
        bw.write("\t\tFROM " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<include refid=\"" + SQL_QUERY_CONDITION_LIST + "\" />");
        bw.newLine();
        bw.write("\t\t<if test=\"query.orderBy != null and query.orderBy != ''\">");
        bw.newLine();
        bw.write("\t\t\tORDER BY ${query.orderBy}");
        bw.newLine();
        bw.write("\t\t</if>");
        bw.newLine();
        bw.write("\t\t<if test=\"query.simplePage != null\">");
        bw.newLine();
        bw.write("\t\t\tLIMIT #{query.simplePage.start}, #{query.simplePage.end}");
        bw.newLine();
        bw.write("\t\t</if>");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();

        bw.write("\t<!-- query count -->");
        bw.newLine();
        bw.write("\t<select id=\"selectCount\" resultType=\"java.lang.Integer\">");
        bw.newLine();
        bw.write("\t\tSELECT COUNT(1)");
        bw.newLine();
        bw.write("\t\tFROM " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<include refid=\"" + SQL_QUERY_CONDITION_LIST + "\" />");
        bw.newLine();
        bw.write("\t</select>");
        bw.newLine();

    }

    private static void selectPrimaryKey(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        List<FieldInfo> primaryKeyList = tableInfo.getKeyIndexMap().get("PRIMARY");
        FieldInfo primaryKey = null;
        if(primaryKeyList != null && primaryKeyList.size() == 1){
            primaryKey = primaryKeyList.get(0);
        }
        if(primaryKey == null || !primaryKey.getAutoIncrement()){
            return;
        }


        bw.write("\t\t<!-- get auto increment -->");
        bw.newLine();
        bw.write("\t\t<selectKey keyProperty=\"bean." + primaryKey.getPropertyName() + "\" resultType=\"java.lang." + primaryKey.getJavaType() + "\" order=\"AFTER\">");
        bw.newLine();
        bw.write("\t\t\tSELECT LAST_INSERT_ID()");
        bw.newLine();
        bw.write("\t\t</selectKey>");
        bw.newLine();
    }

    public static void baseInsert(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.write("\t<!-- insert -->");
        bw.newLine();
        bw.write("\t<insert id=\"insert\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
        bw.newLine();
        selectPrimaryKey(bw, tableInfo);
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write(("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">"));
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            if(fieldInfo.getAutoIncrement()){
                continue;
            }
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\tVALUES");
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            if(fieldInfo.getAutoIncrement()){
                continue;
            }
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();

        bw.write("\t<!-- insert batch -->");
        bw.newLine();
        bw.write("\t<insert id=\"insertBatch\" parameterType=\"java.util.List\">");
        bw.newLine();
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            if(fieldInfo.getAutoIncrement()){
                continue;
            }
            bw.write("\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\tVALUES");
        bw.newLine();
        bw.write("\t\t<foreach collection=\"list\" item=\"bean\" separator=\",\">");
        bw.newLine();
        bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            if(fieldInfo.getAutoIncrement()){
                continue;
            }
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
        }
        bw.write("\t\t\t</trim>");
        bw.newLine();
        bw.write("\t\t</foreach>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
    }

    public static void baseInsertOrUpdate(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        HashMap<String, String> primaryKeys = new HashMap<>();
        List<FieldInfo> primaryKeyList = tableInfo.getKeyIndexMap().get("PRIMARY");
        if(primaryKeyList != null){
            primaryKeys.put(primaryKeyList.get(0).getFieldName(), primaryKeyList.get(0).getFieldName());
        }

        bw.write("\t<!-- insert or update -->");
        bw.newLine();
        bw.write("\t<insert id=\"insertOrUpdate\" parameterType=\"" + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + "\">");
        bw.newLine();
        selectPrimaryKey(bw, tableInfo);
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\tVALUES");
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\tON DUPLICATE KEY UPDATE");
        bw.newLine();
        bw.write("\t\t<trim suffixOverrides=\",\">");
        bw.newLine();

        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            // primary key should not be updated
            if(primaryKeys.containsKey(fieldInfo.getFieldName())){
                continue;
            }
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = values(" + fieldInfo.getFieldName() + "),");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();


        bw.write("\t<!-- insert or update batch -->");
        bw.newLine();
        bw.write("\t<insert id=\"insertOrUpdateBatch\" parameterType=\"java.util.List\">");
        bw.newLine();
        bw.write("\t\tINSERT INTO " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t\t" + fieldInfo.getFieldName() + ",");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t\tVALUES");
        bw.newLine();
        bw.write("\t\t<foreach collection=\"list\" item=\"bean\" separator=\",\">");
        bw.newLine();
        bw.write("\t\t\t<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t\t\t#{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
        }
        bw.write("\t\t\t</trim>");
        bw.newLine();
        bw.write("\t\t</foreach>");
        bw.newLine();
        bw.write("\t\tON DUPLICATE KEY UPDATE");
        bw.newLine();
        bw.write("\t\t<trim suffixOverrides=\",\">");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            // primary key should not be updated
            if(primaryKeys.containsKey(fieldInfo.getFieldName())){
                continue;
            }
            bw.write("\t\t\t" + fieldInfo.getFieldName() + " = values(" + fieldInfo.getFieldName() + "),");
            bw.newLine();
        }
        bw.write("\t\t</trim>");
        bw.newLine();
        bw.write("\t</insert>");
        bw.newLine();
    }

    public static void queryByIndex(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
        List<FieldInfo> primaryKeyList = keyIndexMap.get("PRIMARY");
        for(List<FieldInfo> fieldInfoList : keyIndexMap.values()){
            StringBuilder conditions = new StringBuilder();
            for(FieldInfo fieldInfo : fieldInfoList){
                conditions.append(fieldInfo.getPropertyName().substring(0, 1).toUpperCase())
                        .append(fieldInfo.getPropertyName().substring(1))
                        .append("And");
            }
            conditions.delete(conditions.length() - 3, conditions.length());
            // select
            bw.newLine();
            bw.write("\t<!-- select by " + conditions.toString().replace("And", " and ") + " -->");
            bw.newLine();
            bw.write("\t<select id=\"selectBy" + conditions + "\" resultMap=\"BaseResultMap\">");
            bw.newLine();
            bw.write("\t\tSELECT + <include refid=\"" + SQL_BASE_COLUMN_LIST + "\" />");
            bw.newLine();
            bw.write("\t\tFROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\tWHERE ");
            for(FieldInfo fieldInfo : fieldInfoList){
                bw.write(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "} and ");
            }
            bw.write("1 = 1");
            bw.newLine();
            bw.write("\t</select>");
            bw.newLine();

            // update
            bw.newLine();
            bw.write("\t<!-- update by " + conditions.toString().replace("And", " and ") + " -->");
            bw.newLine();
            bw.write("\t<update id=\"updateBy" + conditions + "\">");
            bw.newLine();
            bw.write("\t\tUPDATE " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\t<set>");
            bw.newLine();
            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                if(primaryKeyList.contains(fieldInfo)){
                    continue;
                }
                bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
                bw.newLine();
                bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
                bw.newLine();
                bw.write("\t\t\t</if>");
                bw.newLine();
            }
            bw.write("\t\t</set>");
            bw.newLine();
            bw.write("\t\tWHERE ");
            for(FieldInfo fieldInfo : fieldInfoList){
                bw.write(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "} and ");
            }
            bw.write("1 = 1");
            bw.newLine();
            bw.write("\t</update>");
            bw.newLine();


            //delete
            bw.newLine();
            bw.write("\t<!-- delete by " + conditions.toString().replace("And", " and ") + " -->");
            bw.newLine();
            bw.write("\t<delete id=\"deleteBy" + conditions + "\">");
            bw.newLine();
            bw.write("\t\tDELETE FROM " + tableInfo.getTableName());
            bw.newLine();
            bw.write("\t\tWHERE ");
            for(FieldInfo fieldInfo : fieldInfoList){
                bw.write(fieldInfo.getFieldName() + " = #{" + fieldInfo.getPropertyName() + "} and ");
            }
            bw.write("1 = 1");
            bw.newLine();
            bw.write("\t</delete>");
            bw.newLine();

        }

    }

    private static void buildUpdateByParam(BufferedWriter bw, TableInfo tableInfo) throws Exception {
        bw.newLine();
        bw.write("\t<!-- update by param -->");
        bw.newLine();
        bw.write("\t<update id=\"updateByParam\">");
        bw.newLine();
        bw.write("\t\tUPDATE " + tableInfo.getTableName());
        bw.newLine();
        bw.write("\t\t<set>");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t\t<if test=\"bean." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t\t" + fieldInfo.getFieldName() + " = #{bean." + fieldInfo.getPropertyName() + "},");
            bw.newLine();
            bw.write("\t\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t</set>");
        bw.newLine();
        bw.write("\t\tWHERE ");
        bw.newLine();
        for(FieldInfo fieldInfo : tableInfo.getFieldList()){
            bw.write("\t\t<if test=\"query." + fieldInfo.getPropertyName() + " != null\">");
            bw.newLine();
            bw.write("\t\t\t" + fieldInfo.getFieldName() + " = #{query." + fieldInfo.getPropertyName() + "} AND");
            bw.newLine();
            bw.write("\t\t</if>");
            bw.newLine();
        }
        bw.write("\t\t1 = 1");
        bw.newLine();
        bw.write("\t</update>");
        bw.newLine();
    }

}
