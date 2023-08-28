package com.yhx.log.evaluator;

import com.yhx.log.context.OperationLogEvaluationContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yanghuaxu
 * @date 2022/6/8 9:53
 */
public class OperationLogExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(1 << 6);

    private final Map<AnnotatedElementKey, Method> targetMethodCache = new ConcurrentHashMap<>(1 << 6);

    public Object parseExpression(String conditionExpression, AnnotatedElementKey methodKey, EvaluationContext evaluationContext) {
        return getExpression(this.expressionCache, methodKey, conditionExpression).getValue(evaluationContext, Object.class);
    }

    /**
     * 创建SPEL上下文执行容器
     * @param method      当前被拦截的方法
     * @param args        方法对应的参数
     * @param targetClass 方法对应的类
     * @param result      方法返回结果
     * @param errorMsg    方法执行出错后的错误信息
     * @param beanFactory bean容器
     * @return org.springframework.expression.EvaluationContext
     **/
    public EvaluationContext createEvaluationContext(Method method, Object[] args, Class<?> targetClass, Object result, String errorMsg, BeanFactory beanFactory) {
        Method targetMethod = getTargetMethod(targetClass, method);
        OperationLogEvaluationContext context = new OperationLogEvaluationContext(null, targetMethod, args, getParameterNameDiscoverer(), result, errorMsg);
        if (beanFactory != null) {
            context.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        return context;
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = this.targetMethodCache.get(methodKey);
        if (targetMethod == null) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            this.targetMethodCache.put(methodKey, targetMethod);
        }
        return targetMethod;
    }
}
