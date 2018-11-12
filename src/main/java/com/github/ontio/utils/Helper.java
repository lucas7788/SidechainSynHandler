package com.github.ontio.utils;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.paramBean.Result;

import java.math.BigInteger;

public class Helper {


    private static final String SEPARATOR = "\\|\\|";

    private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);


    /**
     * @param action
     * @param error
     * @param desc
     * @param version
     * @param rs
     * @return
     */
    public static Result result(String action, long error, String desc, String version, Object rs) {
        Result rr = new Result();
        rr.Error = error;
        rr.Action = action;
        rr.Desc = desc;
        rr.Version = version;
        rr.Result = rs;
        return rr;
    }

    /**
     * check param whether is null or ''
     *
     * @param params
     * @return boolean
     */
    public static Boolean isEmptyOrNull(Object... params) {
        if (params != null) {
            for (Object val : params) {
                if ("".equals(val) || val == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * judge whether the string is in json format.
     *
     * @param str
     * @return
     */
    public static Boolean isJSONStr(String str) {
        try{
            JSONObject obj = JSONObject.parseObject(str);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public static String currentMethod() {
        return new Exception("").getStackTrace()[1].getMethodName();
    }


    public static String asUnsignedDecimalString(long l) {
        BigInteger b = BigInteger.valueOf(l);
        if (b.signum() < 0) {
            b = b.add(TWO_64);
        }
        return b.toString();
    }

}
