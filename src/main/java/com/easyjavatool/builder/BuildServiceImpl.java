package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
import com.easyjavatool.bean.TableInfo;
import com.easyjavatool.constants.Constants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
public class BuildServiceImpl {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildServiceImpl.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_SERVICE_IMPL);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String serviceImplName = tableInfo.getBeanName() + Constants.SUFFIX_SERVICE_IMPL;
        File serviceImplFile = new File(folder, serviceImplName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(serviceImplFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_SERVICE_IMPL + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.springframework.stereotype.Service;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_PO + "." + tableInfo.getBeanName() + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + "." + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + ";");
            bw.newLine();
            bw.write("import java.util.List;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".PageResult;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + tableInfo.getBeanName() + Constants.SUFFIX_SERVICE + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_MAPPER + "." + tableInfo.getBeanName() + "Mapper;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_QUERY + ".SimplePage;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_ENUMS + ".PageSizeEnum;");

            bw.write("@Service");
            bw.newLine();
            BuildComment.createClassComment(bw, tableInfo.getComment() + "Servive接口");
            bw.write("public class " + serviceImplName + " implements " + tableInfo.getBeanName() + Constants.SUFFIX_SERVICE + " {");
            bw.newLine();

            bw.write("\t@Autowired");
            bw.newLine();
            String mapperName = tableInfo.getBeanName().substring(0, 1).toLowerCase() + tableInfo.getBeanName().substring(1) + "Mapper";
            bw.write("\tprivate " + tableInfo.getBeanName() + "Mapper<" + tableInfo.getBeanName() + "," + tableInfo.getBeanParamName() + "> " + mapperName + ";");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic List<" + tableInfo.getBeanName() + "> findListByParam(" + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".selectList(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list count");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer findCountByParam(" + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".selectCount(query);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "page query");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic PageResult<" + tableInfo.getBeanName() + "> findPageByParam(" + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\tint count = this.findCountByParam(query);");
            bw.newLine();
            bw.write("\t\tint pageNo = query.getPageNo() == null ? 1 : query.getPageNo();");
            bw.write("\t\tint pageSize = query.getPageSize() == null ? PageSizeEnum.PAGE_SIZE_10.getSize() : query.getPageSize();");
            bw.newLine();
            bw.write("\t\tSimplePage page = new SimplePage(pageNo, count, pageSize);");
            bw.newLine();
            bw.write("\t\tquery.setSimplePage(page);");
            bw.newLine();
            bw.write("\t\tList<" + tableInfo.getBeanName() + "> list = this.findListByParam(query);");
            bw.newLine();
            bw.write("\t\treturn new PageResult<>(count, pageSize, pageNo, page.getPageTotal(), list);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert new entity");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer add(" + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + "){");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".insert(" + tableInfo.getBeanName() + ");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert batch entity");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addBatch(List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List){");
            bw.newLine();
            bw.write("\t\tif(" + tableInfo.getBeanName() + "List == null || " + tableInfo.getBeanName() + "List.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".insertBatch(" + tableInfo.getBeanName() + "List);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert or update entity");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addOrUpdate(" + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + "){");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".insertOrUpdate(" + tableInfo.getBeanName() + ");");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert or update batch entity");
            bw.write("\t@Override");
            bw.newLine();
            bw.write("\tpublic Integer addOrUpdateBatch(List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List){");
            bw.newLine();
            bw.write("\t\tif(" + tableInfo.getBeanName() + "List == null || " + tableInfo.getBeanName() + "List.isEmpty()){");
            bw.newLine();
            bw.write("\t\t\treturn 0;");
            bw.newLine();
            bw.write("\t\t}");
            bw.newLine();
            bw.write("\t\treturn " + mapperName + ".insertOrUpdateBatch(" + tableInfo.getBeanName() + "List);");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            Map<String, List<FieldInfo>> keyIndexMap = tableInfo.getKeyIndexMap();
            for(List<FieldInfo> fieldInfoList : keyIndexMap.values()){
                StringBuilder conditions = new StringBuilder();
                StringBuilder methodParams = new StringBuilder();
                StringBuilder callingParams = new StringBuilder();
                for(FieldInfo fieldInfo : fieldInfoList){
                    conditions.append(fieldInfo.getPropertyName().substring(0, 1).toUpperCase()).append(fieldInfo.getPropertyName().substring(1)).append("And");
                    methodParams.append(fieldInfo.getJavaType()).append(" ").append(fieldInfo.getPropertyName()).append(", ");
                    callingParams.append(fieldInfo.getPropertyName()).append(", ");
                }
                conditions.delete(conditions.length() - 3, conditions.length());
                methodParams.delete(methodParams.length() - 2, methodParams.length());
                callingParams.delete(callingParams.length() - 2, callingParams.length());
                // find
                BuildComment.createMethodComment(bw, "query by " + conditions.toString().replace("AND", " and "));
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic " + tableInfo.getBeanName() + " findBy" + conditions + "(" + methodParams + "){");
                bw.newLine();
                bw.write("\t\treturn " + mapperName + ".selectBy" + conditions + "(" + callingParams + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                // update
                BuildComment.createMethodComment(bw, "update by " + conditions.toString().replace("AND", " and "));
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer updateBy" + conditions + "(" + methodParams + ", " + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + "){");
                bw.newLine();
                bw.write("\t\treturn " + mapperName + ".updateBy" + conditions + "(" + callingParams + ", " + tableInfo.getBeanName() + ");");
                bw.newLine();
                bw.write("\t}");
                bw.newLine();

                // delete
                BuildComment.createMethodComment(bw, "delete by " + conditions.toString().replace("AND", " and "));
                bw.write("\t@Override");
                bw.newLine();
                bw.write("\tpublic Integer deleteBy" + conditions + "(" + methodParams + "){");
                bw.newLine();
                bw.write("\t\treturn " + mapperName + ".deleteBy" + conditions + "(" + callingParams + ");");
                bw.newLine();
                bw.write("\t}");
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
