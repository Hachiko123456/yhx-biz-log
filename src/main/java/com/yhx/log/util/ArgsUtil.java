package com.yhx.log.util;

import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

/**
 * @author yanghuaxu
 * @date 2022/6/15 14:15
 */
public final class ArgsUtil {

    private static OperationLogExpressionEvaluator evaluator;

    public ArgsUtil(OperationLogExpressionEvaluator evaluator) {
        ArgsUtil.evaluator = evaluator;
    }

    public static Object[] parseArgs(String argsName, AnnotatedElementKey elementKey, EvaluationContext context) {
        String[] argsNameArray = argsName.split(StringUtil.COMMA);
        Object[] args = new Object[argsNameArray.length];
        for (int i = 0; i < argsNameArray.length; i++) {
            args[i] = evaluator.parseExpression(argsNameArray[i], elementKey, context);
        }
        return args;
    }

    public static Object[] parseArgs(String argsName, AnnotatedElementKey elementKey, EvaluationContext context, int index) {
        String[] argsNameArray = argsName.split(StringUtil.COMMA);
        Object[] args = new Object[argsNameArray.length];
        for (int i = 0; i < argsNameArray.length; i++) {
            Object value = evaluator.parseExpression(argsNameArray[i], elementKey, context);
            if (ArrayUtil.isArray(value)) {
                args[i] = ArrayUtil.get(value, index);
            } else {
                args[i] = value;
            }
        }
        return args;
    }

}
