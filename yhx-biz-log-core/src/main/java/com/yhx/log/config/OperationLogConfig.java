package com.yhx.log.config;

import com.yhx.log.constant.LogRecordProperties;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.factory.ParseFunctionFactory;
import com.yhx.log.factory.PatternFactory;
import com.yhx.log.interceptor.OperationLogAspect;
import com.yhx.log.parser.BeanParser;
import com.yhx.log.parser.DiffParser;
import com.yhx.log.parser.FunctionParser;
import com.yhx.log.pattern.BeanPattern;
import com.yhx.log.pattern.CommonPattern;
import com.yhx.log.pattern.FunctionBeanPattern;
import com.yhx.log.pattern.FunctionPattern;
import com.yhx.log.service.*;
import com.yhx.log.service.impl.*;
import com.yhx.log.util.ArgsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.List;

/**
 * @author yanghuaxu
 * @date 2022/6/8 17:26
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({LogRecordProperties.class})
public class OperationLogConfig {

    private AnnotationAttributes enableLogRecord;

    @Bean("functionService")
    @ConditionalOnMissingBean(FunctionService.class)
    public FunctionService functionService(ParseFunctionFactory parseFunctionFactory) {
        return new DefaultFunctionServiceImpl(parseFunctionFactory);
    }

    @Bean("defaultParseFunction")
    public ParseFunction defaultParseFunction(MarkFieldLogService fieldLogService) {
        DefaultParseFunction function = new DefaultParseFunction();
        function.setFieldLogService(fieldLogService);
        return function;
    }

    @Bean("parseFunctionFactory")
    public ParseFunctionFactory parseFunctionFactory(@Autowired List<ParseFunction> functionList) {
        return new ParseFunctionFactory(functionList);
    }

    @Bean
    public MarkFieldLogService fieldLogService() {
        return new MarkFieldLogServiceImpl();
    }

    @Bean("operationLogExpressionEvaluator")
    public OperationLogExpressionEvaluator operationLogExpressionEvaluator() {
        return new OperationLogExpressionEvaluator();
    }

    @Bean("beanParser")
    public BeanParser beanParser(BeanFactory beanFactory) {
        return new BeanParser(beanFactory);
    }

    @Bean("functionParser")
    @DependsOn({"functionService"})
    public FunctionParser functionParser(FunctionService functionService) {
        return new FunctionParser(functionService);
    }


    @Bean("operationLogAspect")
    @DependsOn({"operationLogExpressionEvaluator", "beanParser", "functionParser"})
    public OperationLogAspect logAspect(OperationLogExpressionEvaluator evaluator, BeanParser beanParser, FunctionParser functionParser) {
        OperationLogAspect aspect = new OperationLogAspect(evaluator);
        aspect.setBeanParser(beanParser);
        aspect.setFunctionParser(functionParser);
        return aspect;
    }

    @Bean
    public PatternFactory patternFactory(@Autowired List<CommonPattern> patternList) {
        return new PatternFactory(patternList);
    }

    @Bean
    @DependsOn({"beanParser", "operationLogExpressionEvaluator"})
    public CommonPattern beanPattern(BeanParser beanParser, OperationLogExpressionEvaluator evaluator) {
        return new BeanPattern(beanParser, evaluator);
    }

    @Bean
    @DependsOn({"functionParser", "operationLogExpressionEvaluator", "diffParser"})
    public CommonPattern functionPattern(FunctionParser functionParser, OperationLogExpressionEvaluator evaluator, DiffParser diffParser) {
        return new FunctionPattern(functionParser, evaluator, diffParser);
    }

    @Bean
    @DependsOn({"beanParser", "functionParser"})
    public CommonPattern functionBeanPattern(BeanParser beanParser, FunctionParser functionParser) {
        FunctionBeanPattern functionBeanPattern = new FunctionBeanPattern();
        functionBeanPattern.setBeanParser(beanParser);
        functionBeanPattern.setFunctionParser(functionParser);
        return functionBeanPattern;
    }

    @Bean
    @ConditionalOnMissingBean(OperatorService.class)
    public OperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(TraceService.class)
    public TraceService traceService() {
        return new DefaultTraceServiceImpl();
    }

    @Bean
    public ArgsUtil argsUtil(OperationLogExpressionEvaluator evaluator) {
        return new ArgsUtil(evaluator);
    }

    @Bean
    @ConditionalOnMissingBean(CommonOperationLogService.class)
    public CommonOperationLogService commonOperationLogService() {
        return new DefaultOperationLogServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(DiffItemsToLogContentService.class)
    public DiffItemsToLogContentService diffItemsToLogContentService(FunctionService functionService, LogRecordProperties logRecordProperties) {
        return new DefaultDiffItemsToLogContentServiceImpl(functionService, logRecordProperties);
    }

    @Bean
    public DiffParser diffParser(DiffItemsToLogContentService diffItemsToLogContentService) {
        DiffParser function = new DiffParser();
        function.setDiffItemsToLogContentService(diffItemsToLogContentService);
        return function;
    }

}
