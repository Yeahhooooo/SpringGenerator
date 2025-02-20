package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;
import com.easyjavatool.utils.DateUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BuildPO {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildPO.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_PO);
        if(!folder.exists()){
            folder.mkdirs();
        }

        File poFile = new File(folder, tableInfo.getBeanName() + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(poFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_PO + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import java.io.Serializable;");
            bw.newLine();
            bw.write("import lombok.Data;");
            bw.newLine();

            if(tableInfo.getHaveBigDecimal()){
                bw.write("import java.math.BigDecimal;");
                bw.newLine();
            }

            for (FieldInfo fieldInfo : tableInfo.getFieldList()) {
                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TO_JSON_FIELDS.split(","), fieldInfo.getPropertyName())){
                    bw.write(Constants.IGNORE_BEAN_TO_JSON_CLASS);
                    bw.newLine();
                    break;
                }
            }

            if(tableInfo.getHaveDate() || tableInfo.getHaveDateTime()){
                bw.write("import java.util.Date;");
                bw.newLine();
                bw.write(Constants.BEAN_DATE_FORMAT_CLASS);
                bw.newLine();
                bw.write(Constants.BEAN_DATE_PARSE_CLASS);
                bw.newLine();
            }
            bw.newLine();
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment());
            bw.write("@Data");
            bw.newLine();
            bw.write("public class " + tableInfo.getBeanName() + " implements Serializable {");
            bw.newLine();

            for(FieldInfo fieldInfo : tableInfo.getFieldList()){
                BuildComment.createFieldComment(bw, fieldInfo.getComment());

                if(ArrayUtils.contains(Constants.SQL_DATETIME_TYPES, fieldInfo.getSqlType())) {
                    bw.write(String.format("\t" + Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                    bw.write(String.format("\t" + Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD_HH_MM_SS));
                    bw.newLine();
                }

                if(ArrayUtils.contains(Constants.SQL_DATE_TYPES, fieldInfo.getSqlType()) ) {
                    bw.write(String.format("\t" + Constants.BEAN_DATE_FORMAT_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                    bw.write(String.format("\t" + Constants.BEAN_DATE_PARSE_EXPRESSION, DateUtils.YYYY_MM_DD));
                    bw.newLine();
                }

                if(ArrayUtils.contains(Constants.IGNORE_BEAN_TO_JSON_FIELDS.split(","), fieldInfo.getPropertyName())){
                    bw.write(Constants.IGNORE_BEAN_TO_JSON_EXPRESSION);
                    bw.newLine();
                }

                bw.write("\tprivate " + fieldInfo.getJavaType() + " " + fieldInfo.getPropertyName() + ";");
                bw.newLine();
                bw.newLine();
            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            log.error("Build PO error", e);
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
