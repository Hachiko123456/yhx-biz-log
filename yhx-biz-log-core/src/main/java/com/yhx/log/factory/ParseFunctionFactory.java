package com.yhx.log.factory;

import com.yhx.log.service.ParseFunction;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghuaxu
 * @date 2022/6/8 10:05
 */
public class ParseFunctionFactory {

    private Map<String, ParseFunction> functionMap;

    public ParseFunctionFactory(List<ParseFunction> parseFunctions) {
        if (CollectionUtils.isEmpty(parseFunctions)) {
            return;
        }
        this.functionMap = new HashMap<>();
        for (ParseFunction function : parseFunctions) {
            if (StringUtils.isEmpty(function.functionName())) {
                continue;
            }
            if (this.functionMap.containsKey(function.functionName())) {
                throw new InvalidParameterException(String.format("存在重复的自定义函数名称%s", function.functionName()));
            }
            this.functionMap.put(function.functionName(), function);
        }
    }

    public ParseFunction getFunction(String functionName) {
        if (!this.functionMap.containsKey(functionName)) {
            throw new InvalidParameterException(String.format("不存在%s对应的自定义函数", functionName));
        }
        return this.functionMap.get(functionName);
    }

    public boolean isBeforeFunction(String functionName) {
        if (!this.functionMap.containsKey(functionName)) {
            return false;
        }
        return this.functionMap.get(functionName).executeBefore();
    }

}
