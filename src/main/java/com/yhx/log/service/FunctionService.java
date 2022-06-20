package com.yhx.log.service;

/**
 * @author yanghuaxu
 * @date 2022/6/8 14:43
 */
public interface FunctionService {

    String apply(String functionName, Object value);

    String apply(String functionName, Object value, int index);

    boolean beforeFunction(String functionName);

}
