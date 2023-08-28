package com.yhx.log.util;


/**
 * @author yanghuaxu
 * @date 2022/6/10 11:00
 */
public class ValueHandlerUtil {

    public static String convertObject2String(Object value) {
        if (value == null) {
            return "";
        }
        return value instanceof String ? (String) value : GsonUtil.toJson(value);
    }

}
