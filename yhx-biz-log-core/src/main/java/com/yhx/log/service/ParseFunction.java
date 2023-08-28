package com.yhx.log.service;

import com.yhx.log.util.ArrayUtil;

/**
 * @author yanghuaxu
 * @date 2022/6/8 10:04
 */
public interface ParseFunction {

    default boolean executeBefore() {
        return false;
    }

    String functionName();

    String apply(Object value);

    default String apply(Object value, int index) {
        return this.apply(ArrayUtil.get(value, index));
    }

}
