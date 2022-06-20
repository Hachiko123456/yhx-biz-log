package com.yhx.log.service.impl;

import com.yhx.log.factory.ParseFunctionFactory;
import com.yhx.log.service.FunctionService;
import com.yhx.log.service.ParseFunction;
import com.yhx.log.util.ValueHandlerUtil;

/**
 * @author yanghuaxu
 * @date 2022/6/8 17:27
 */
public class DefaultFunctionServiceImpl implements FunctionService {

    private final ParseFunctionFactory parseFunctionFactory;

    public DefaultFunctionServiceImpl(ParseFunctionFactory parseFunctionFactory) {
        this.parseFunctionFactory = parseFunctionFactory;
    }

    @Override
    public String apply(String functionName, Object value) {
        ParseFunction function = this.parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return ValueHandlerUtil.convertObject2String(value);
        }
        return function.apply(value);
    }

    @Override
    public String apply(String functionName, Object value, int index) {
        ParseFunction function = this.parseFunctionFactory.getFunction(functionName);
        if (function == null) {
            return ValueHandlerUtil.convertObject2String(value);
        }
        return function.apply(value, index);
    }

    @Override
    public boolean beforeFunction(String functionName) {
        return this.parseFunctionFactory.isBeforeFunction(functionName);
    }
}
