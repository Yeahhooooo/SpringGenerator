package com.easyjavatool.builder;

import com.easyjavatool.constants.Constants;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BuildBase {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BuildBase.class);
    public static void execute(){
        build("DateUtils", Constants.PATH_UTILS, Collections.singletonList("package " + Constants.PACKAGE_UTILS + ";\n\n"));

        build("DateTimePatternEnum", Constants.PATH_ENUMS, Collections.singletonList("package " + Constants.PACKAGE_ENUMS + ";\n\n"));

        build("BaseMapper", Constants.PATH_MAPPER, Collections.singletonList("package " + Constants.PACKAGE_MAPPER + ";\n\n"));

        build("PageSizeEnum", Constants.PATH_ENUMS, Collections.singletonList("package " + Constants.PACKAGE_ENUMS + ";\n\n"));

        build("BaseQuery", Constants.PATH_QUERY, Collections.singletonList("package " + Constants.PACKAGE_QUERY + ";\n\n"));

        build("SimplePage", Constants.PATH_QUERY, Arrays.asList("package " + Constants.PACKAGE_QUERY + ";\n\n", "import " + Constants.PACKAGE_ENUMS + ".PageSizeEnum;\n"));

        build("PageResult",Constants.PATH_VO, Collections.singletonList("package " + Constants.PACKAGE_VO + ";\n\n"));

        build("Response", Constants.PATH_VO, Arrays.asList("package " + Constants.PACKAGE_VO + ";\n\n", "import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;\n"));

        build("ResponseCodeEnum", Constants.PATH_ENUMS, Collections.singletonList("package " + Constants.PACKAGE_ENUMS + ";\n\n"));

        build("BusinessException", Constants.PATH_EXCEPTION, Arrays.asList(
                "package " + Constants.PACKAGE_EXCEPTION + ";\n\n",
                "import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;\n"
        ));

        build("GlobalExceptionHandler", Constants.PATH_CONTROLLER, Arrays.asList(
                "package " + Constants.PACKAGE_CONTROLLER + ";\n\n",
                "import " + Constants.PACKAGE_ENUMS + ".ResponseCodeEnum;\n",
                "import " + Constants.PACKAGE_EXCEPTION + ".BusinessException;\n",
                "import " + Constants.PACKAGE_VO + ".Response;\n"
        ));
    }

    private static void build(String fileName, String outputPath, List<String> prefixes){
        File folder = new File(outputPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File javaFile = new File(outputPath, fileName + ".java");

        OutputStream os = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            os = Files.newOutputStream(javaFile.toPath());
            osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            bw = new BufferedWriter(osw);

            String templatePath = BuildBase.class.getClassLoader().getResource("template/" + fileName + ".txt").getPath();
            is = new FileInputStream(templatePath);
            isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            br = new BufferedReader(isr);

            for (String prefix : prefixes) {
                bw.write(prefix);
            }

            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (Exception e) {
            log.error("BuildBase execute error when generate class: {}, error: {}", fileName, e);
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
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
