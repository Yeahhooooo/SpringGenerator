package com.easyjavatool.builder;

import com.easyjavatool.constants.Constants;
import com.easyjavatool.utils.DateUtils;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildComment {

    public static void createClassComment(BufferedWriter bw, String comment) throws Exception {

        bw.write("/**");
        bw.newLine();
        bw.write("* @Description: " +(comment == null ? "" : comment));
        bw.newLine();
        bw.write("* @Author: " + Constants.AUTHOR);
        bw.newLine();
        bw.write("* @Date: " + DateUtils.format(new Date(), DateUtils.INCLINE_YYYY_MM_DD));
        bw.newLine();
        bw.write("*/\n");
    }

    public static void createFieldComment(BufferedWriter bw, String comment) throws Exception {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t* " + (comment == null ? "" : comment));
        bw.newLine();
        bw.write("\t*/\n");
    }

    public static void createMethodComment(BufferedWriter bw, String comment) throws Exception {
        bw.write("\t/**");
        bw.newLine();
        bw.write("\t* " + (comment == null ? "" : comment));
        bw.newLine();
        bw.write("\t*/");
        bw.newLine();
    }
}
