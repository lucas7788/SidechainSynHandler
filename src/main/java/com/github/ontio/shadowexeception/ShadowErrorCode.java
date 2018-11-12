package com.github.ontio.shadowexeception;


import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class ShadowErrorCode {
    public static String getError(int code, String msg) {
        Map map = new HashMap();
        map.put("Error", code);
        map.put("Desc", msg);
        return JSON.toJSONString(map);
    }
    public static String PARA_ERROR = getError(60001, "parameter error");

    public static String OtherError(String msg) {
        return getError(60000, "Other Error," + msg);
    }
}
