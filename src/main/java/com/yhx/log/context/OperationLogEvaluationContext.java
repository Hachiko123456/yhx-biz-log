package com.yhx.log.context;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author yanghuaxu
 * @date 2022/6/8 10:00
 */
public class OperationLogEvaluationContext extends MethodBasedEvaluationContext {

    public OperationLogEvaluationContext(Object rootObject, Method method, Object[] arguments, ParameterNameDiscoverer parameterNameDiscoverer, Object ret, String errorMsg) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
        Map<String, Object> variables = OperationRecordContext.get();
        if (!CollectionUtils.isEmpty(variables)) {
            // 把当前上下文的变量放到root变量中
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                setVariable(entry.getKey(), entry.getValue());
            }
        }
        // 把当前方法的参数放到root变量中
        if (arguments != null && arguments.length > 0) {
            String[] argsName = parameterNameDiscoverer.getParameterNames(method);
            for (int i = 0; i < argsName.length; i++) {
                setVariable(argsName[i], arguments[i]);
            }
        }
        // 方法的返回值
        setVariable("_ret", ret);
        // 异常信息
        setVariable("_errorMsg", errorMsg);
    }
}
