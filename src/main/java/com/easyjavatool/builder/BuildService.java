package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class BuildService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildService.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_SERVICE);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String serviceName = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE;
        File serviceFile = new File(folder, serviceName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(serviceFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_SERVICE + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PageResult;");
            bw.newLine();

            BuildComment.createClassComment(bw, tableInfo.getComment() + "Service接口");
            bw.write("public interface " + serviceName + " {");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list");
            bw.write("\tList<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list count");
            bw.write("\tInteger findCountByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            BuildComment.createMethodComment(bw, "page query");
            bw.write("\tPageResult<" + tableInfo.getBeanName() + "> findPageByParam(" + tableInfo.getBeanParamName() + " query);");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert new entity");
            bw.write("\tInteger add(" + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + ");");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert batch entity");
            bw.write("\tInteger addBatch(List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List);");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert or update entity");
            bw.write("\tInteger addOrUpdate(" + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + ");");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert or update batch entity");
            bw.write("\tInteger addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List);");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(List<FieldInfo> fieldInfoList : keyIndexMap.values()){
                StringBuilder conditions = new StringBuilder();
                StringBuilder params = new StringBuilder();
                for(FieldInfo fieldInfo : fieldInfoList){
                    conditions.append(fieldInfo.getPropertyName().substring(0, 1).toUpperCase()).append(fieldInfo.getPropertyName().substring(1)).append("And");
                    params.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                }
                conditions.delete(conditions.length() - 3, conditions.length());
                params.delete(params.length() - 2, params.length());

                // find
                BuildComment.createMethodComment(bw, "query by " + conditions.toString().replace("And", " and "));
                bw.write("\t" + tableInfo.getBeanName() + " findBy" + conditions + "(" + params + ");");
                bw.newLine();

                // update
                BuildComment.createMethodComment(bw, "update by " + conditions.toString().replace("And", " and "));
                bw.write("\tInteger updateBy" + conditions + "(" + params + ", " + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + ");");
                bw.newLine();

                // delete
                BuildComment.createMethodComment(bw, "delete by " + conditions.toString().replace("And", " and "));
                bw.write("\tInteger deleteBy" + conditions + "(" + params + ");");
                bw.newLine();
            }

            bw.newLine();
            bw.write("}");
            bw.flush();
        } catch (Exception e) {
            log.error("Build Service error", e);
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
