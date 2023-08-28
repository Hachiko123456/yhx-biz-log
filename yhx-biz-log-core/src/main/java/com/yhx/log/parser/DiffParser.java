package com.yhx.log.parser;

import com.yhx.log.context.OperationRecordContext;
import com.yhx.log.evaluator.OperationLogExpressionEvaluator;
import com.yhx.log.service.DiffItemsToLogContentService;
import com.yhx.log.util.StringUtil;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;

/**
 * 对象对比解析器
 * @author yanghuaxu
 * @date 2022/6/8 17:32
 */
@Slf4j
public class DiffParser {
    public static final String diffFunctionName = "_DIFF";
    public static final String OLD_OBJECT = "_oldObj";

    private static DiffItemsToLogContentService diffItemsToLogContentService;

    //@Override
    public String functionName() {
        return diffFunctionName;
    }

    //@Override
    public String diff(Object source, Object target) {
        if (source == null && target == null) {
            return "";
        }
        if (source == null || target == null) {
            try {
                Class<?> clazz = source == null ? target.getClass() : source.getClass();
                source = source == null ? clazz.getDeclaredConstructor().newInstance() : source;
                target = target == null ? clazz.getDeclaredConstructor().newInstance() : target;
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        if (!Objects.equals(source.getClass(), target.getClass())) {
            log.error("diff的两个对象类型不同, source.class={}, target.class={}", source.getClass().toString(), target.getClass().toString());
            return "";
        }
        DiffNode diffNode = ObjectDifferBuilder.buildDefault().compare(target, source);
        return diffItemsToLogContentService.toLogContent(diffNode, source, target);
    }

    public String diff(Object newObj) {
        Object oldObj = OperationRecordContext.get(OLD_OBJECT);
        return diff(oldObj, newObj);
    }

    public String diff(Object newObj, int index) {
        Object oldObj = OperationRecordContext.get(OLD_OBJECT);
        if (oldObj instanceof List) {
            List<?> oldList = (List<?>) oldObj;
            if (oldList.size() <= index) {
                return diff(oldList.get(oldList.size() - 1), newObj);
            } else {
                return diff(oldList.get(index), newObj);
            }
        } else {
            return diff(oldObj, newObj);
        }
    }

    public void setDiffItemsToLogContentService(DiffItemsToLogContentService diffItemsToLogContentService) {
        DiffParser.diffItemsToLogContentService = diffItemsToLogContentService;
    }

    public String getDiffFunctionValue(EvaluationContext evaluationContext, AnnotatedElementKey annotatedElementKey, String expression, OperationLogExpressionEvaluator evaluator) {
        String[] params = parseDiffFunction(expression);
        if (params.length == 1) {
            Object targetObj = evaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            expression = this.diff(targetObj);
        } else if (params.length == 2) {
            Object sourceObj = evaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            Object targetObj = evaluator.parseExpression(params[1], annotatedElementKey, evaluationContext);
            expression = this.diff(sourceObj, targetObj);
        }
        return expression;
    }

    public String getDiffFunctionValue(EvaluationContext evaluationContext, AnnotatedElementKey annotatedElementKey, String expression, OperationLogExpressionEvaluator expressionEvaluator, int index) {
        String[] params = parseDiffFunction(expression);
        if (params.length == 1) {
            Object targetObj = expressionEvaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            if (targetObj instanceof List) {
                List<?> targetObjList = (List<?>) targetObj;
                if (targetObjList.size() <= index) {
                    expression = this.diff(targetObjList.get(targetObjList.size() - 1), index);
                } else {
                    expression = this.diff(targetObjList.get(index), index);
                }
            } else {
                expression = this.diff(targetObj, index);
            }
        } else if (params.length == 2) {
            Object sourceObj = expressionEvaluator.parseExpression(params[0], annotatedElementKey, evaluationContext);
            Object targetObj = expressionEvaluator.parseExpression(params[1], annotatedElementKey, evaluationContext);
            if (sourceObj instanceof List && !(targetObj instanceof List)) {
                List<?> sourceObjList = (List<?>) sourceObj;
                if (sourceObjList.size() <= index) {
                    expression = this.diff(sourceObjList.get(sourceObjList.size() - 1), targetObj);
                } else {
                    expression = this.diff(sourceObjList.get(index), targetObj);
                }
            } else if (!(sourceObj instanceof List) && targetObj instanceof List) {
                List<?> targetObjList = (List<?>) targetObj;
                if (targetObjList.size() <= index) {
                    expression = this.diff(sourceObj, targetObjList.get(targetObjList.size() - 1));
                } else {
                    expression = this.diff(sourceObj, targetObjList.get(index));
                }
            } else if (sourceObj instanceof List && targetObj instanceof List) {
                List<?> sourceObjList = (List<?>) sourceObj;
                List<?> targetObjList = (List<?>) targetObj;
                int srcIndex = sourceObjList.size() > index ? index : sourceObjList.size() - 1;
                int targetIndex = targetObjList.size() > index ? index : targetObjList.size() - 1;
                expression = this.diff(sourceObjList.get(srcIndex), targetObjList.get(targetIndex));
            } else {
                expression = this.diff(sourceObj, targetObj);
            }
        }
        return expression;
    }

    public String[] parseDiffFunction(String expression) {
        if (expression.contains(StringUtil.COMMA) && StringUtil.strCount(expression, StringUtil.COMMA) == 1) {
            return expression.split(StringUtil.COMMA);
        }
        return new String[]{expression};
    }
}
