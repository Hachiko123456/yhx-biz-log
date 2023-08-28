package com.yhx.log.parser;

import com.yhx.log.constant.ContextValue;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.factory.PatternFactory;
import com.yhx.log.pattern.CommonPattern;
import com.yhx.log.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;

/**
 * 日志表达式解析类
 * @author yanghuaxu
 * @date 2022/6/8 11:15
 */
@Slf4j
public class LogValueParser implements BeanFactoryAware {

    protected BeanFactory beanFactory;

    private final OperationLogExpressionEvaluator evaluator;

    protected BeanParser beanParser;

    protected FunctionParser functionParser;

    protected PatternFactory patternFactory;

    public LogValueParser(OperationLogExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    /**
     * 解析单个模板
     * @param templates
     * @param ret
     * @param targetClass
     * @param method
     * @param args
     * @param errorMsg
     * @param before
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    public Map<String, String> processTemplate(Collection<String> templates, Object ret, Class<?> targetClass, Method method, Object[] args, String errorMsg,
                                               Map<String, String> before) {
        Map<String, String> expressionValues = new HashMap<>();
        EvaluationContext context = evaluator.createEvaluationContext(method, args, targetClass, ret, errorMsg, this.beanFactory);

        // 遍历需要解析的表达式
        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains("{")) {
                String expressResult = expressionTemplate;
                if (patternFactory != null) {
                    // 遍历正则表达式解析器，获取结果
                    for (CommonPattern pattern : patternFactory.getPatternList()) {
                        expressResult = pattern.parse(expressResult, method, targetClass, context, before);
                    }
                }
                expressionValues.put(expressionTemplate, expressResult);
            } else {
                expressionValues.put(expressionTemplate, expressionTemplate);
            }
        }
        return expressionValues;
    }

    /**
     * 解析批量模板
     * @param templates
     * @param ret
     * @param targetClass
     * @param method
     * @param args
     * @param errorMsg
     * @param before
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    public Map<String, List<String>> processBatchTemplate(Collection<String> templates, Object ret, Class<?> targetClass, Method method, Object[] args, String errorMsg,
                                                          Map<String, String> before, String bizNoTemplate) {
        Map<String, List<String>> expressionValues = new HashMap<>();
        EvaluationContext context = evaluator.createEvaluationContext(method, args, targetClass, ret, errorMsg, this.beanFactory);
        Object bizNoObject = bizNoTemplate;
        // 先解析一遍bizNo
        if (bizNoTemplate.contains("{")) {
            Matcher matcher = patternFactory.getPattern(CommonPattern.PatterName.FUNCTION_PATTERN.name()).getPattern().matcher(bizNoTemplate);
            if (matcher.find()) {
                String expression = matcher.group(2);
                AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
                bizNoObject = evaluator.parseExpression(expression, elementKey, context);
            }
        }
        int length = ArrayUtil.getLength(bizNoObject);
        // 遍历需要解析的表达式
        for (String expressionTemplate : templates) {
            for (int i = 0; i < length; i++) {
                List<String> values = new ArrayList<>();
                if (expressionTemplate.contains("{")) {
                    String expressResult = expressionTemplate;
                    if (patternFactory != null) {
                        // 遍历正则表达式解析器，获取结果
                        for (CommonPattern pattern : patternFactory.getPatternList()) {
                            expressResult = pattern.parse(expressResult, method, targetClass, context, before, i);
                        }
                    }
                    values.add(expressResult);
                } else {
                    values.add(expressionTemplate);
                }
                expressionValues.merge(expressionTemplate, values, (x, y) -> {
                    x.addAll(y);
                    return x;
                });
            }
        }
        return expressionValues;
    }

    /**
     * 获取需要前置执行的函数的结果
     * @param templates   需要解析的模板
     * @param targetClass 当前执行方法所在的类
     * @param method      当前方法所在的类
     * @param args        方法对应的参数
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    public Map<String, String> processBeforeExecuteFunctionTemplate(Collection<String> templates, Class<?> targetClass, Method method, Object[] args) {
        Map<String, String> beforeExecuteFunctionReturnValueMap = new HashMap<>();
        EvaluationContext context = evaluator.createEvaluationContext(method, args, targetClass, null, null, beanFactory);
        for (String expressionTemplate : templates) {
            if (expressionTemplate.contains(ContextValue.SPEL_FLAG_LEFT)) {
                Matcher matcher = patternFactory.getPattern(CommonPattern.PatterName.FUNCTION_PATTERN.name()).getPattern().matcher(expressionTemplate);
                while (matcher.find()) {
                    String expression = matcher.group(2);
                    if (expression.contains(ContextValue.VARIABLE_FLAG + ContextValue.RETURN_VALUE_KEY) || expression.contains(ContextValue.VARIABLE_FLAG + ContextValue.ERROR_MESSAGE_KEY)) {
                        continue;
                    }
                    AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
                    String functionName = matcher.group(1);
                    if (functionParser.beforeFunction(functionName)) {
                        Object value = evaluator.parseExpression(expression, elementKey, context);
                        String functionReturnValue = functionParser.getFunctionReturnValue(null, value, expression, functionName);
                        String uKey = functionParser.getUniqueKey(functionName, expression);
                        beforeExecuteFunctionReturnValueMap.put(uKey, functionReturnValue);
                    }
                }
            }
        }
        return beforeExecuteFunctionReturnValueMap;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setBeanParser(BeanParser beanParser) {
        this.beanParser = beanParser;
    }

    public void setFunctionParser(FunctionParser functionParser) {
        this.functionParser = functionParser;
    }
}
