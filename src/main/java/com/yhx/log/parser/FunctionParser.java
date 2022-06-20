package com.yhx.log.parser;

import com.yhx.log.service.FunctionService;
import com.yhx.log.util.ValueHandlerUtil;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 函数解析器
 * @author yanghuaxu
 * @date 2022/6/8 14:42
 */
public class FunctionParser {

    private FunctionService functionService;

    public FunctionParser(FunctionService functionService) {
        this.functionService = functionService;
    }

    public String getFunctionReturnValue(Map<String, String> before, Object value, String expression, String functionName) {
        if (StringUtils.isEmpty(functionName)) {
            return value == null ? "" : ValueHandlerUtil.convertObject2String(value);
        }
        String returnValue = "";
        String uKey = getUniqueKey(functionName, expression);
        if (before != null && before.containsKey(uKey)) {
            returnValue = before.get(uKey);
        } else {
            returnValue = this.functionService.apply(functionName, value);
        }
        return returnValue;
    }

    public String getFunctionReturnValue(Map<String, String> before, Object value, String expression, String functionName, int index) {
        if (StringUtils.isEmpty(functionName)) {
            return value == null ? "" : ValueHandlerUtil.convertObject2String(value);
        }
        String returnValue = "";
        String uKey = getUniqueKey(functionName, expression);
        if (before != null && before.containsKey(uKey)) {
            returnValue = before.get(uKey);
        } else {
            returnValue = this.functionService.apply(functionName, value, index);
        }
        return returnValue;
    }

    public String getUniqueKey(String functionName, String param) {
        return functionName + param;
    }

    public boolean beforeFunction(String functionName) {
        return this.functionService.beforeFunction(functionName);
    }

}
