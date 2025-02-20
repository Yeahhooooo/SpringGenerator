package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;
import com.easyjavatool.utils.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class BuildQuery {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildQuery.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_QUERY);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File poFile = new File(folder, tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(poFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_QUERY + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import lombok.Data;");
            bw.newLine();

            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, "查询" + tableInfo.getComment() + "参数");
            bw.write("@Data");
            bw.newLine();
            bw.write("public class " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + " extends BaseQuery {");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                BuildComment.createFieldComment(bw, fieldInfo.getComment());
                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();

                if(ArrayUtils.contains(Constants.SQL_STRING_TYPES, fieldInfo.getSqlType())){
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY + ";");
                    bw.newLine();
                    bw.newLine();

                    FieldInfo extraFieldInfo = new FieldInfo();
                    extraFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_FUZZY);
                    extraFieldInfo.setJavaType(fieldInfo.getJavaType());
                    extraFieldInfo.setFieldName(fieldInfo.getFieldName());
                    extraFieldInfo.setSqlType(fieldInfo.getSqlType());
                    tableInfo.getExtendedFieldList().add(extraFieldInfo);
                } else if (ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) || ArrayUtils.contains(Constants.SQL_DATETIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START + ";");
                    bw.newLine();
                    bw.newLine();
                    bw.write("\tprivate String " + fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END + ";");
                    bw.newLine();
                    bw.newLine();

                    FieldInfo extraFieldInfo = new FieldInfo();
                    extraFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_START);
                    extraFieldInfo.setJavaType("Date");
                    extraFieldInfo.setFieldName(fieldInfo.getFieldName());
                    extraFieldInfo.setSqlType(fieldInfo.getSqlType());
                    tableInfo.getExtendedFieldList().add(extraFieldInfo);

                    extraFieldInfo = new FieldInfo();
                    extraFieldInfo.setPropertyName(fieldInfo.getPropertyName() + Constants.SUFFIX_BEAN_QUERY_TIME_END);
                    extraFieldInfo.setJavaType("Date");
                    extraFieldInfo.setFieldName(fieldInfo.getFieldName());
                    extraFieldInfo.setSqlType(fieldInfo.getSqlType());
                    tableInfo.getExtendedFieldList().add(extraFieldInfo);
                }
            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            log.error("Build Query error", e);
        } finally {
            try {
                if(bw != null){
                    bw.close();
                }
                if(osw != null){
                    osw.close();
                }
                if(os != null){
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
