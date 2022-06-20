package com.yhx.log.service;

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

    String apply(Object value, int index);

}
