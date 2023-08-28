package com.yhx.log.pattern;

import com.google.common.base.Strings;
import com.yhx.log.parser.BeanParser;
import com.yhx.log.parser.FunctionParser;
import com.yhx.log.util.ArgsUtil;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 函数和Bean类型结合表达式解析类
 * 函数在外，Bean表达式在内
 * @author yanghuaxu
 * @date 2022/6/15 11:49
 */
public class FunctionBeanPattern implements CommonPattern {

    private static final Pattern FUNCTION_BEAN_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{\\s*(\\w*)\\s*\\.\\s*(\\w*)\\s*\\((.*?)\\)}}");

    private FunctionParser functionParser;

    private BeanParser beanParser;

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before) {
        Matcher matcher = FUNCTION_BEAN_PATTERN.matcher(expression);
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String functionName = matcher.group(1);
            String beanName = matcher.group(2);
            String methodName = matcher.group(3);
            String argsName = matcher.group(4);
            // 先解析内层的Bean表达式结果
            Object result = beanParser.getFunctionReturnValue(beanName, methodName, ArgsUtil.parseArgs(argsName, elementKey, context));
            // 再解析外层的函数表达式结果
            String replace = functionParser.getFunctionReturnValue(before, result, beanName + methodName + argsName, functionName);
            // 替换表达式的占位符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Strings.nullToEmpty(replace)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before, int index) {
        return expression;
    }

    @Override
    public String getPatternName() {
        return PatterName.FUNCTION_BEAN_PATTERN.name();
    }

    @Override
    public Pattern getPattern() {
        return null;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE - 1;
    }

    public void setFunctionParser(FunctionParser functionParser) {
        this.functionParser = functionParser;
    }

    public void setBeanParser(BeanParser beanParser) {
        this.beanParser = beanParser;
    }

}
