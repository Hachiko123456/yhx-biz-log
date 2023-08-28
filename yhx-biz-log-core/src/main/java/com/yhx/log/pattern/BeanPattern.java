package com.yhx.log.pattern;

import com.google.common.base.Strings;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.parser.BeanParser;
import com.yhx.log.util.ArgsUtil;
import com.yhx.log.util.ValueHandlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean类型表达式解析类
 * @author yanghuaxu
 * @date 2022/6/9 12:04
 */
@Slf4j
public class BeanPattern implements CommonPattern {

    private BeanParser beanParser;

    private OperationLogExpressionEvaluator evaluator;

    public BeanPattern(BeanParser beanParser, OperationLogExpressionEvaluator evaluator) {
        this.beanParser = beanParser;
        this.evaluator = evaluator;
    }

    /**
     * {service.getById{#orderId,#userId}}
     */
    private static final Pattern BEAN_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*(\\.)\\s*(\\w*)\\((.*?)\\)}");

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before) {
        log.info("{}表达式开始尝试匹配Bean模式", expression);
        Matcher matcher = BEAN_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        while (matcher.find()) {
            String beanName = matcher.group(1);
            String methodName = matcher.group(3);
            String argsName = matcher.group(4);
            // 执行Bean对应的方法，返回对应的结果
            String parseResult = ValueHandlerUtil.convertObject2String(beanParser.getFunctionReturnValue(beanName, methodName, ArgsUtil.parseArgs(argsName, elementKey, context)));
            // 替换表达式的占位符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Strings.nullToEmpty(parseResult)));
        }
        matcher.appendTail(sb);
        log.info("Bean模式解析完成，解析后的结果为{}", sb);
        return sb.toString();
    }

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before, int index) {
        log.info("{}表达式开始尝试匹配Bean模式", expression);
        Matcher matcher = BEAN_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        while (matcher.find()) {
            String beanName = matcher.group(1);
            String methodName = matcher.group(3);
            String argsName = matcher.group(4);
            // 执行Bean对应的方法，返回对应的结果
            String parseResult = ValueHandlerUtil.convertObject2String(beanParser.getFunctionReturnValue(beanName, methodName, ArgsUtil.parseArgs(argsName, elementKey, context, index)));
            // 替换表达式的占位符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Strings.nullToEmpty(parseResult)));
        }
        matcher.appendTail(sb);
        log.info("Bean模式解析完成，解析后的结果为{}", sb);
        return sb.toString();
    }

    @Override
    public String getPatternName() {
        return PatterName.BEAN_PATTERN.name();
    }

    @Override
    public Pattern getPattern() {
        return BEAN_PATTERN;
    }
}
