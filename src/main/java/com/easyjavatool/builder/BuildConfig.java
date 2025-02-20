package com.easyjavatool.builder;

import com.easyjavatool.bean.FieldInfo;
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

public class BuildConfig {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildConfig.class);

    public static void execute(){
        File folder = new File(Constants.PATH_CONFIG);
        if(!folder.exists()){
            folder.mkdirs();
        }

        String configName = "WebConfig";
        File serviceFile = new File(folder, configName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            os = Files.newOutputStream(serviceFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            bw.write("package " + Constants.PACKAGE_CONFIG + ";");
            bw.newLine();
            bw.newLine();

            bw.write("import org.springframework.context.annotation.Configuration;");
            bw.newLine();
            bw.write("import org.springframework.web.servlet.config.annotation.InterceptorRegistry;");
            bw.newLine();
            bw.write("import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;");
            bw.newLine();

            bw.newLine();
            bw.write("@Configuration");
            bw.newLine();
            bw.write("public class " + configName + " implements WebMvcConfigurer {");
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
