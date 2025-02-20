package com.easyjavatool.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class JSONUtils {

    public static String toJSONString(Object object) {
        if(object == null){
            return null;
        }
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }


}
