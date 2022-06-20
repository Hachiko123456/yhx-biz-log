package com.yhx.log.pattern;

import com.google.common.base.Strings;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.parser.DiffParser;
import com.yhx.log.parser.FunctionParser;
import com.yhx.log.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 函数表达式解析类
 * @author yanghuaxu
 * @date 2022/6/9 12:06
 */
@Slf4j
public class FunctionPattern implements CommonPattern {
    /**
     * {ORDER#{#orderId}},{{order}},{{order.orderId}}
     */
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");

    private FunctionParser functionParser;

    private OperationLogExpressionEvaluator evaluator;

    private DiffParser diffParser;

    public FunctionPattern(FunctionParser functionParser, OperationLogExpressionEvaluator evaluator, DiffParser diffParser) {
        this.functionParser = functionParser;
        this.evaluator = evaluator;
        this.diffParser = diffParser;
    }

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before) {
        log.info("{}表达式开始尝试匹配自定义函数模式", expression);
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        while (matcher.find()) {
            String expressionString = matcher.group(2);
            String functionName = matcher.group(1);
            if (DiffParser.diffFunctionName.equals(functionName)) {
                expressionString = this.diffParser.getDiffFunctionValue(context, elementKey, expressionString, evaluator);
            } else {
                // 解析SPEL表达式
                Object value = evaluator.parseExpression(expressionString, elementKey, context);
                // 执行函数
                expressionString = functionParser.getFunctionReturnValue(before, value, expressionString, functionName);
            }
            // 替换表达式的占位符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Strings.nullToEmpty(expressionString)));
        }
        matcher.appendTail(sb);
        log.info("自定义函数模式解析完成，解析后的结果为{}", sb);
        return sb.toString();
    }

    @Override
    public String parse(String expression, Method method, Class<?> targetClass, EvaluationContext context, Map<String, String> before, int index) {
        log.info("{}表达式开始尝试匹配自定义函数模式", expression);
        Matcher matcher = FUNCTION_PATTERN.matcher(expression);
        StringBuffer sb = new StringBuffer();
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        while (matcher.find()) {
            String expressionString = matcher.group(2);
            String functionName = matcher.group(1);
            if (DiffParser.diffFunctionName.equals(functionName)) {
                expressionString = this.diffParser.getDiffFunctionValue(context, elementKey, expressionString, evaluator, index);
            } else {
                // 解析SPEL表达式
                Object value = evaluator.parseExpression(expressionString, elementKey, context);
                if (ArrayUtil.isArray(value)) {
                    // 数组
                    expressionString = functionParser.getFunctionReturnValue(before, ArrayUtil.get(value, index), expressionString, functionName, index);
                } else {
                    // 非数组
                    expressionString = functionParser.getFunctionReturnValue(before, value, expressionString, functionName);
                }
            }
            // 替换表达式的占位符
            matcher.appendReplacement(sb, Matcher.quoteReplacement(Strings.nullToEmpty(expressionString)));
        }
        matcher.appendTail(sb);
        log.info("自定义函数模式解析完成，解析后的结果为{}", sb);
        return sb.toString();
    }

    @Override
    public String getPatternName() {
        return PatterName.FUNCTION_PATTERN.name();
    }

    @Override
    public Pattern getPattern() {
        return FUNCTION_PATTERN;
    }
}
