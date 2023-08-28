package com.yhx.log.interceptor;

import com.yhx.log.annotation.OperationLog;
import com.yhx.log.bean.OperationLogRecord;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author yanghuaxu
 * @date 2022/6/6 20:21
 */
public class OperationLogSource {

    /**
     * 解析被{@link OperationLog}注解的方法
     * @param method
     * @param targetClass
     * @return java.util.Collection<com.cvte.his.dto.OperationLogRecord>
     **/
    public Collection<OperationLogRecord> computeOperationLog(Method method, Class<?> targetClass) {
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Method specialMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        specialMethod = BridgeMethodResolver.findBridgedMethod(specialMethod);
        return processParseLogRecord(specialMethod);
    }

    private Collection<OperationLogRecord> processParseLogRecord(AnnotatedElement element) {
        Collection<OperationLog> operationLogs = AnnotatedElementUtils.findAllMergedAnnotations(element, OperationLog.class);
        Collection<OperationLogRecord> records = null;
        if (!CollectionUtils.isEmpty(operationLogs)) {
            records = new ArrayList<>();
            for (OperationLog operationLog : operationLogs) {
                records.add(convertAnnotation2Object(element, operationLog));
            }
        }
        return records;
    }

    private OperationLogRecord convertAnnotation2Object(AnnotatedElement element, OperationLog operationLog) {
        check(element, operationLog);
        return OperationLogRecord.builder()
                .module(operationLog.module())
                .bizNo(operationLog.bizNo())
                .traceId(operationLog.traceId())
                .operator(operationLog.operator())
                .detail(operationLog.detail())
                .success(operationLog.success())
                .detailArgs(operationLog.detailArgs())
                .condition(operationLog.condition())
                .isBatch(operationLog.isBatch())
                .fail(operationLog.fail()).build();
    }

    private void check(AnnotatedElement element, OperationLog operationLog) {
        if (StringUtils.isEmpty(operationLog.bizNo())) {
            throw new InvalidParameterException(String.format("%s上的OperationLog注解缺少必填参数bizNo", element.toString()));
        }
        if (StringUtils.isEmpty(operationLog.success()) && StringUtils.isEmpty(operationLog.fail())) {
            throw new InvalidParameterException(String.format("%s上的OperationLog注解缺少必填参数success或者fail", element.toString()));
        }
    }

}
