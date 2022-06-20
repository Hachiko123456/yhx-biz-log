package com.yhx.log.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * @author yanghuaxu
 * @date 2022/6/17 14:49
 */
public class ArrayUtil {

    public static Object get(List<?> list, int index) {
        Object val;
        if (list.size() <= index) {
            val = list.get(list.size() - 1);
        } else {
            val = list.get(index);
        }
        return val;
    }

    public static Object get(Object array, int index) {
        if (isArray(array)) {
            Object val;
            if (List.class.isAssignableFrom(array.getClass())) {
                return get((List<?>) array, index);
            }
            int length = Array.getLength(array);
            if (length <= index) {
                val = Array.get(array, length - 1);
            } else {
                val = Array.get(array, index);
            }
            return val;
        } else {
            return array;
        }
    }

    public static boolean isArray(Object value) {
        return List.class.isAssignableFrom(value.getClass()) || value.getClass().isArray();
    }

    public static int getLength(Object value) {
        if (isArray(value)) {
            if (List.class.isAssignableFrom(value.getClass())) {
                return ((List<?>) value).size();
            } else {
                return Array.getLength(value);
            }
        } else {
            return 1;
        }
    }

}
