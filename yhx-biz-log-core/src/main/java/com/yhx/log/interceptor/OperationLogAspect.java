package com.yhx.log.interceptor;

import com.google.common.collect.Lists;
import com.yhx.log.annotation.OperationLog;
import com.yhx.log.bean.CommonOperationLog;
import com.yhx.log.bean.OperationLogRecord;
import com.yhx.log.bean.Operator;
import com.yhx.log.context.OperationRecordContext;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.factory.PatternFactory;
import com.yhx.log.parser.LogValueParser;
import com.yhx.log.service.CommonOperationLogService;
import com.yhx.log.service.OperatorService;
import com.yhx.log.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author yanghuaxu
 * @date 2022/6/8 15:46
 */
@Aspect
@Slf4j
public class OperationLogAspect extends LogValueParser implements InitializingBean {

    private CommonOperationLogService logService;

    private TraceService traceService;

    private OperatorService operatorService;

    private final OperationLogSource operationLogSource = new OperationLogSource();

    public OperationLogAspect(OperationLogExpressionEvaluator evaluator) {
        super(evaluator);
    }

    @Value("${common.operation.log.isThrowable:true}")
    private boolean isThrowable;

    @Around("@annotation(operationLog)")
    public Object aroundOperationLogMethod(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return execute(joinPoint, joinPoint.getThis(), method, joinPoint.getArgs());
    }

    private Object execute(ProceedingJoinPoint joinPoint, Object target, Method method, Object[] args) throws Throwable {
        Class<?> targetClass = getTargetClass(target);
        OperationRecordContext.putEmptyContext();
        Collection<OperationLogRecord> logRecords = new ArrayList<>();
        Map<String, String> functionNameReturnValueMap = new HashMap<>();
        Object ret = null;
        boolean success = true;
        Throwable throwable = null;
        try {
            long start = System.currentTimeMillis();
            logRecords = operationLogSource.computeOperationLog(method, targetClass);
            List<String> spelTemplates = getBeforeExecuteFunctionTemplate(logRecords);
            functionNameReturnValueMap = processBeforeExecuteFunctionTemplate(spelTemplates, targetClass, method, args);
            long end = System.currentTimeMillis();
            log.info("解析前置执行函数所花费的时间为：{}s", (end - start) * 1.0 / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("解析{}_{}OperationLog注解异常", targetClass.getName(), method.getName());
        }
        try {
            ret = joinPoint.proceed();
        } catch (Throwable e) {
            success = false;
            throwable = e;
            log.error("业务逻辑执行发生异常", e);
        }
        try {
            if (!CollectionUtils.isEmpty(logRecords)) {
                long start = System.currentTimeMillis();
                processLog(ret, method, args, logRecords, targetClass, success, throwable == null ? null :
                        throwable.getMessage(), functionNameReturnValueMap);
                long end = System.currentTimeMillis();
                log.info("解析@OperationLog注解逻辑所花费的时间为：{}s", (end - start) * 1.0 / 1000);
            }
        } catch (Exception e) {
            log.error("{}_{}记录日志发生异常", targetClass.getName(), method.getName(), e);
        } finally {
            OperationRecordContext.destroy();
        }
        if (isThrowable && throwable != null) {
            throw throwable;
        }
        return ret;
    }

    private void processLog(Object ret, Method method, Object[] args, Collection<OperationLogRecord> records, Class<?> targetClass, boolean success, String errorMsg, Map<String, String> functionReturnMap) {
        for (OperationLogRecord record : records) {
            try {
                String action = getActionContent(success, record);
                if (StringUtils.isEmpty(action)) {
                    log.warn("{}_{}没有配置日志模板，忽略", targetClass.getName(), method.getName());
                    continue;
                }
                List<String> spelTemplates = getSpelTemplates(record, action);
                Operator operator = Optional.of(this.operatorService.getOperator()).orElse(new Operator());
                if (record.isBatch()) {
                    Map<String, List<String>> expressionValues = processBatchTemplate(spelTemplates, ret, targetClass, method, args, errorMsg, functionReturnMap, record.getBizNo());
                    List<CommonOperationLog> commonOperationLogList = IntStream.range(0, expressionValues.get(record.getBizNo()).size()).boxed()
                            .filter(x -> {
                                String condition = record.getCondition();
                                return StringUtils.isEmpty(condition) || StringUtils.endsWithIgnoreCase(expressionValues.get(condition).get(x), "true");
                            }).map(x -> CommonOperationLog.builder()
                                    .bizNo(expressionValues.get(record.getBizNo()).get(x))
                                    .module(record.getModule()).traceId(this.traceService.getTraceId())
                                    .action(expressionValues.get(action).get(x))
                                    .crtName(!StringUtils.isEmpty(record.getOperator()) ? record.getOperator() : operator.getOperatorName())
                                    .crtUser(!StringUtils.isEmpty(record.getOperator()) ? record.getOperator() : operator.getOperatorId())
                                    .crtTime(new Date())
                                    .detail(StringUtils.isEmpty(record.getDetail()) ? null : expressionValues.get(record.getDetail()).get(x)).build())
                            .filter(x -> !StringUtils.isEmpty(x.getAction()))
                            .collect(Collectors.toList());
                    logService.record(commonOperationLogList);
                } else {
                    Map<String, String> expressionValues = processTemplate(spelTemplates, ret, targetClass, method, args, errorMsg, functionReturnMap);
                    if (condition(record, expressionValues)) {
                        CommonOperationLog commonLog = CommonOperationLog.builder()
                                .bizNo(expressionValues.get(record.getBizNo()))
                                .module(record.getModule()).traceId(this.traceService.getTraceId())
                                .action(expressionValues.get(action))
                                .crtName(!StringUtils.isEmpty(record.getOperator()) ? record.getOperator() : operator.getOperatorName())
                                .crtUser(!StringUtils.isEmpty(record.getOperator()) ? record.getOperator() : operator.getOperatorId())
                                .detail(expressionValues.get(record.getDetail())).crtTime(new Date()).build();
                        if (!StringUtils.isEmpty(commonLog.getAction())) {
                            // 开启新事务
                            logService.record(commonLog);
                        }
                    } else {
                        log.warn("{}_{}不满足条件{}，忽略执行", targetClass.getName(), method.getName(), record.getCondition());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("{}_{}记录日志发生异常", targetClass.getName(), method.getName(), e);
            }
        }
    }

    private boolean condition(OperationLogRecord record, Map<String, String> expressionValues) {
        return StringUtils.isEmpty(record.getCondition()) || StringUtils.endsWithIgnoreCase(expressionValues.get(record.getCondition()), "true");
    }

    private String getActionContent(boolean success, OperationLogRecord record) {
        return success ? record.getSuccess() : record.getFail();
    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<OperationLogRecord> records) {
        List<String> spelTemplates = new ArrayList<>();
        for (OperationLogRecord record : records) {
            spelTemplates.addAll(getSpelTemplates(record, record.getSuccess()));
        }
        return spelTemplates;
    }

    private List<String> getSpelTemplates(OperationLogRecord record, String action) {
        List<String> spelTemplates = Lists.newArrayList(record.getBizNo(), action);
        if (!StringUtils.isEmpty(record.getCondition())) {
            spelTemplates.add(record.getCondition());
        }
        if (!StringUtils.isEmpty(record.getDetail())) {
            String detail = record.getDetail();
            if (record.getDetailArgs() != null && record.getDetailArgs().length > 0) {
                detail = String.format(detail, record.getDetailArgs());
                record.setDetail(detail);
            }
            spelTemplates.add(detail);
        }
        return spelTemplates.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        this.logService = this.beanFactory.getBean(CommonOperationLogService.class);
        this.patternFactory = this.beanFactory.getBean(PatternFactory.class);
        this.operatorService = this.beanFactory.getBean(OperatorService.class);
        this.traceService = this.beanFactory.getBean(TraceService.class);
        Assert.notNull(this.logService, "无法找到CommonLogService实例");
    }

    private Class<?> getTargetClass(Object target) {
        return AopProxyUtils.ultimateTargetClass(target);
    }
}
