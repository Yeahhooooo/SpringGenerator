package com.easyjavatool.utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesUtils {

    private static Properties properties = new Properties();

    private static Map<String, String> propertiesMap= new ConcurrentHashMap<>();

    static {
        InputStream is = null;
        try {
            is = PropertiesUtils.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(is);
            Iterator<Object> it = properties.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                propertiesMap.put(key, properties.getProperty(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static String getProperty(String key) {
        return propertiesMap.get(key);
    }


    public static void main(String[] args) {
        System.out.println(PropertiesUtils.getProperty("db.driver.name"));
    }
}
