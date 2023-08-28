package com.yhx.log.pattern;

import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author yanghuaxu
 * @date 2022/6/9 12:04
 */
public interface CommonPattern extends Ordered {

    enum PatterName {
        BEAN_PATTERN,
        FUNCTION_PATTERN,
        FUNCTION_BEAN_PATTERN
    }

    Comparator<CommonPattern> PATTERN_COMPARATOR = Comparator.comparingInt(Ordered::getOrder);

    /**
     * 解析表达式
     * @param expression  表达式
     * @param method      注解对应的方法
     * @param targetClass 方法所在的Class
     * @param context     替换表达式变量的上下文
     * @param before      前置变量解析完成的缓存map
     * @return java.lang.String
     **/
    String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before);

    /**
     * 批量解析表达式
     * @param expression  表达式
     * @param method      注解对应的方法
     * @param targetClass 方法所在的Class
     * @param context     替换表达式变量的上下文
     * @param before      前置变量解析完成的缓存map
     * @param index       数组的下标
     * @return java.lang.String
     **/
    String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before, int index);

    String getPatternName();

    Pattern getPattern();

    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
