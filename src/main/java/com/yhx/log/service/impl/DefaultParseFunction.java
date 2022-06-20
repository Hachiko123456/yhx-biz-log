package com.yhx.log.service.impl;

import com.yhx.log.service.MarkFieldLogService;
import com.yhx.log.service.ParseFunction;
import com.yhx.log.util.ArrayUtil;

/**
 * @author yanghuaxu
 * @date 2022/6/14 20:19
 */
public class DefaultParseFunction implements ParseFunction {

    public static final String IMPORT_FUNCTION_NAME = "_IMP";

    private static MarkFieldLogService fieldLogService;

    public void setFieldLogService(MarkFieldLogService fieldLogService) {
        DefaultParseFunction.fieldLogService = fieldLogService;
    }

    @Override
    public String functionName() {
        return IMPORT_FUNCTION_NAME;
    }

    @Override
    public String apply(Object value) {
        return fieldLogService.logImportField(value);
    }

    @Override
    public String apply(Object value, int index) {
        return fieldLogService.logImportField(ArrayUtil.get(value, index));
    }
}
