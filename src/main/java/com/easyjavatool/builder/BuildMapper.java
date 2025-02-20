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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BuildMapper {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildMapper.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_MAPPER);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String mapperClassName = tableInfo.getBeanName() + Constants.SUFFIX_MAPPER;
        File mapperFile = new File(folder, mapperClassName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(mapperFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_MAPPER + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Param;");
            bw.newLine();
            bw.write("import org.apache.ibatis.annotations.Mapper;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Mapper接口");
            bw.write("@Mapper");
            bw.newLine();
            bw.write("public interface " + mapperClassName + "<T,P> extends BaseMapper<T,P> {");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(Map.Entry<String, List<FieldInfo>> entry : keyIndexMap.entrySet()){
                List<FieldInfo> fieldInfoList = entry.getValue();
                StringBuilder conditions = new StringBuilder();
                for(FieldInfo fieldInfo : fieldInfoList){
                    conditions.append(fieldInfo.getPropertyName().substring(0, 1).toUpperCase())
                                .append(fieldInfo.getPropertyName().substring(1))
                                .append("And");
                }
                conditions.delete(conditions.length() - 3, conditions.length());

                StringBuilder params = new StringBuilder("(");

                for(FieldInfo fieldInfo : fieldInfoList){
                    params.append("@Param(\"")
                            .append(fieldInfo.getPropertyName())
                            .append("\") ")
                            .append(fieldInfo.getJavaType())
                            .append(" ")
                            .append(fieldInfo.getPropertyName())
                            .append(", ");
                }
                params.delete(params.length() - 2, params.length());

                String selectMethod = "\tT selectBy" + conditions + params + ");";
                BuildComment.createMethodComment(bw, "根据" + entry.getKey() + "索引查询" + tableInfo.getComment());
                bw.write(selectMethod);
                bw.newLine();
                bw.newLine();

                String updateMethod = "\tInteger updateBy" + conditions + params + ", @Param(\"bean\") T t);";
                BuildComment.createMethodComment(bw, "根据" + entry.getKey() + "索引更新" + tableInfo.getComment());
                bw.write(updateMethod);
                bw.newLine();
                bw.newLine();

                String deleteMethod = "\tInteger deleteBy" + conditions + params + ");";
                BuildComment.createMethodComment(bw, "根据" + entry.getKey() + "索引删除" + tableInfo.getComment());
                bw.write(deleteMethod);
                bw.newLine();
                bw.newLine();
            }


            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            log.error("Build Mapper error", e);
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
