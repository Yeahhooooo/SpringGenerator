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

public class BuildController {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildController.class);

    public static void execute(TableInfo tableInfo){
        File folder = new File(Constants.PATH_CONTROLLER);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String controllerName = tableInfo.getBeanName() + Constants.SUFFIX_CONTROLLER;
        File serviceFile = new File(folder, controllerName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(serviceFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_CONTROLLER + ";");
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
            bw.write("import org.springframework.web.bind.annotation.*;");
            bw.newLine();
            bw.write("import org.springframework.beans.factory.annotation.Autowired;");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_SERVICE + "." + tableInfo.getBeanName() + Constants.SUFFIX_SERVICE + ";");
            bw.newLine();
            bw.write("import " + Constants.PACKAGE_VO + ".Response;");
            bw.newLine();


            BuildComment.createClassComment(bw, tableInfo.getComment() + "Controller接口");
            bw.write("@RestController");
            bw.newLine();
            bw.write("@RequestMapping(\"/" + tableInfo.getBeanName().substring(0, 1).toLowerCase() + tableInfo.getBeanName().substring(1) + "\")");
            bw.newLine();
            bw.write("public class " + controllerName + " {");
            bw.newLine();

            String serviceName = tableInfo.getBeanName().substring(0, 1).toLowerCase() + tableInfo.getBeanName().substring(1) + Constants.SUFFIX_SERVICE;

            bw.write("\t@Autowired");
            bw.newLine();
            bw.write("\tprivate " + tableInfo.getBeanName() + Constants.SUFFIX_SERVICE + " " + serviceName + ";");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list");
            bw.write("\t@GetMapping(\"/list\")");
            bw.newLine();
            bw.write("\tpublic Response<List<" + tableInfo.getBeanName() + ">> get" + tableInfo.getBeanName() + "List(@RequestBody " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + " query){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".findListByParam(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "query list count");
            bw.write("\t@GetMapping(\"/count\")");
            bw.newLine();
            bw.write("\tpublic Response<Integer> get" + tableInfo.getBeanName() + "Count(@RequestBody " + tableInfo.getBeanName() + Constants.SUFFIX_BEAN_QUERY + " query){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".findCountByParam(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "page query");
            bw.write("\t@GetMapping(\"/page\")");
            bw.newLine();
            bw.write("\tpublic Response<PageResult<" + tableInfo.getBeanName() + ">> findPageByParam(@RequestBody " + tableInfo.getBeanParamName() + " query){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".findPageByParam(query));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert new entity");
            bw.write("\t@PostMapping(\"/add\")");
            bw.newLine();
            bw.write("\tpublic Response<Integer> add(@RequestBody " + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + "){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".add(" + tableInfo.getBeanName() + "));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert batch entity");
            bw.write("\t@PostMapping(\"/addBatch\")");
            bw.newLine();
            bw.write("\tpublic Response<Integer> addBatch(@RequestBody List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".addBatch(" + tableInfo.getBeanName() + "List));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

            BuildComment.createMethodComment(bw, "insert or update entity");
            bw.write("\t@PostMapping(\"/addOrUpdate\")");
            bw.newLine();
            bw.write("\tpublic Response<Integer> addOrUpdate(@RequestBody " + tableInfo.getBeanName() + " " + tableInfo.getBeanName() + "){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".addOrUpdate(" + tableInfo.getBeanName() + "));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();


            BuildComment.createMethodComment(bw, "insert or update batch entity");
            bw.write("\t@PostMapping(\"/addOrUpdateBatch\")");
            bw.newLine();
            bw.write("\tpublic Response<Integer> addOrUpdateBatch(@RequestBody List<" + tableInfo.getBeanName() + "> " + tableInfo.getBeanName() + "List){");
            bw.newLine();
            bw.write("\t\treturn Response.success(" + serviceName + ".addOrUpdateBatch(" + tableInfo.getBeanName() + "List));");
            bw.newLine();
            bw.write("\t}");
            bw.newLine();

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
