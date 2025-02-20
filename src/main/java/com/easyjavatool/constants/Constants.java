package com.easyjavatool.constants;

import com.easyjavatool.utils.PropertiesUtils;

public class Constants {

    public static Boolean IGNORE_TABLE_PREFIX;

    public static String SUFFIX_BEAN_QUERY;

    public static String PATH_JAVA = "java";

    public static String PATH_RESOURCES = "resources";

    public static String PATH_BASE;

    public static String PACKAGE_BASE;

    public static String PATH_PO;

    public static String PACKAGE_PO;

    public static String PATH_VO;

    public static String PACKAGE_VO;

    public static String PATH_UTILS;

    public static String PACKAGE_UTILS;

    public static String PATH_ENUMS;

    public static String PACKAGE_ENUMS;

    public static String PATH_QUERY;

    public static String PACKAGE_QUERY;

    public static String PATH_MAPPER;

    public static String PACKAGE_MAPPER;

    public static String PATH_MAPPER_XML;

    public static String PATH_SERVICE;

    public static String PACKAGE_SERVICE;

    public static String PATH_SERVICE_IMPL;

    public static String PACKAGE_SERVICE_IMPL;

    public static String PATH_CONTROLLER;

    public static String PACKAGE_CONTROLLER;

    public static String PATH_EXCEPTION;

    public static String PACKAGE_EXCEPTION;

    public static String PATH_CONFIG;

    public static String PACKAGE_CONFIG;

    public static String AUTHOR;

    // ignore properties
    public static String IGNORE_BEAN_TO_JSON_FIELDS;

    public static String IGNORE_BEAN_TO_JSON_EXPRESSION;

    public static String IGNORE_BEAN_TO_JSON_CLASS;

    // date format serializations and deserializations

    public static String BEAN_DATE_FORMAT_EXPRESSION;

    public static String BEAN_DATE_FORMAT_CLASS;

    public static String BEAN_DATE_PARSE_EXPRESSION;

    public static String BEAN_DATE_PARSE_CLASS;

    public static String SUFFIX_BEAN_QUERY_FUZZY;

    public static String SUFFIX_BEAN_QUERY_TIME_START;

    public static String SUFFIX_BEAN_QUERY_TIME_END;

    public static String SUFFIX_MAPPER;

    public static String SUFFIX_SERVICE;

    public static String SUFFIX_SERVICE_IMPL;

    public static String SUFFIX_CONTROLLER;


    static {
        IGNORE_TABLE_PREFIX = Boolean.valueOf(PropertiesUtils.getProperty("ignore.table.prefix"));

        SUFFIX_BEAN_QUERY = PropertiesUtils.getProperty("suffix.bean.query");

        PATH_BASE = PropertiesUtils.getProperty("path.base");

        PACKAGE_BASE = PropertiesUtils.getProperty("package.base");

        PATH_BASE = (PATH_BASE + "/" + PATH_JAVA + "/" + PACKAGE_BASE).replace(".", "/");

        PATH_PO = PATH_BASE + "/" + PropertiesUtils.getProperty("package.po").replace(".", "/");

        PACKAGE_PO = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.po");

        PATH_VO = PATH_BASE + "/" + PropertiesUtils.getProperty("package.vo").replace(".", "/");

        PACKAGE_VO = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.vo");

        PATH_UTILS = PATH_BASE + "/" + PropertiesUtils.getProperty("package.utils").replace(".", "/");

        PACKAGE_UTILS = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.utils");

        PATH_ENUMS = PATH_BASE + "/" + PropertiesUtils.getProperty("package.enums").replace(".", "/");

        PACKAGE_ENUMS = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.enums");

        PATH_QUERY = PATH_BASE + "/" + PropertiesUtils.getProperty("package.query").replace(".", "/");

        PACKAGE_QUERY = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.query");

        PATH_MAPPER = PATH_BASE + "/" + PropertiesUtils.getProperty("package.mapper").replace(".", "/");

        PACKAGE_MAPPER = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.mapper");

        PATH_MAPPER_XML = PropertiesUtils.getProperty("path.base") + "/" + PATH_RESOURCES + "/" + PACKAGE_MAPPER.replace(".", "/");

        PATH_SERVICE = PATH_BASE + "/" + PropertiesUtils.getProperty("package.service").replace(".", "/");

        PACKAGE_SERVICE = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.service");

        PATH_SERVICE_IMPL = PATH_BASE + "/" + PropertiesUtils.getProperty("package.service.impl").replace(".", "/");

        PACKAGE_SERVICE_IMPL = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.service.impl");

        PATH_CONTROLLER = PATH_BASE + "/" + PropertiesUtils.getProperty("package.controller").replace(".", "/");

        PACKAGE_CONTROLLER = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.controller");

        PATH_EXCEPTION = PATH_BASE + "/" + PropertiesUtils.getProperty("package.exception").replace(".", "/");

        PACKAGE_EXCEPTION = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.exception");

        PATH_CONFIG = PATH_BASE + "/" + PropertiesUtils.getProperty("package.config").replace(".", "/");

        PACKAGE_CONFIG = PACKAGE_BASE + "." + PropertiesUtils.getProperty("package.config");

        AUTHOR = PropertiesUtils.getProperty("author.comment");

        IGNORE_BEAN_TO_JSON_FIELDS = PropertiesUtils.getProperty("ignore.bean.tojson.fields");

        IGNORE_BEAN_TO_JSON_EXPRESSION = PropertiesUtils.getProperty("ignore.bean.tojson.expression");

        IGNORE_BEAN_TO_JSON_CLASS = PropertiesUtils.getProperty("ignore.bean.tojson.class");

        BEAN_DATE_FORMAT_EXPRESSION = PropertiesUtils.getProperty("bean.date.format.expression");

        BEAN_DATE_FORMAT_CLASS = PropertiesUtils.getProperty("bean.date.format.class");

        BEAN_DATE_PARSE_EXPRESSION = PropertiesUtils.getProperty("bean.date.parse.expression");

        BEAN_DATE_PARSE_CLASS = PropertiesUtils.getProperty("bean.date.parse.class");

        SUFFIX_BEAN_QUERY_FUZZY = PropertiesUtils.getProperty("suffix.bean.query.fuzzy");

        SUFFIX_BEAN_QUERY_TIME_START = PropertiesUtils.getProperty("suffix.bean.query.time.start");

        SUFFIX_BEAN_QUERY_TIME_END = PropertiesUtils.getProperty("suffix.bean.query.time.end");

        SUFFIX_MAPPER = PropertiesUtils.getProperty("suffix.mapper");

        SUFFIX_SERVICE = PropertiesUtils.getProperty("suffix.service");

        SUFFIX_SERVICE_IMPL = PropertiesUtils.getProperty("suffix.service.impl");

        SUFFIX_CONTROLLER = PropertiesUtils.getProperty("suffix.controller");
    }

    public static String[] SQL_INTEGER_TYPES = new String[]{"int", "tinyint", "smallint", "mediumint", "integer"};

    public static String[] SQL_LONG_TYPES = new String[]{"bigint"};

    public static String[] SQL_DECIMAL_TYPES = new String[]{"decimal", "float", "double"};

    public static String[] SQL_STRING_TYPES = new String[]{"char", "varchar", "text", "mediumtext", "longtext"};

    public static String[] SQL_DATE_TYPES = new String[]{"date"};

    public static String[] SQL_DATETIME_TYPES = new String[]{"datetime", "timestamp"};


    public static void main(String[] args) {
        System.out.println(PATH_BASE);
        System.out.println(PATH_PO);
    }
}
