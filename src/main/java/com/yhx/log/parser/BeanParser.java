package com.yhx.log.parser;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Bean方法解析器
 * @author yanghuaxu
 * @date 2022/6/8 14:50
 */
public class BeanParser {

    private BeanFactory beanFactory;

    public BeanParser(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Object getFunctionReturnValue(String beanName, String methodName, Object[] args) {
        Assert.hasLength(beanName, "beanName不能为空");
        Object bean = beanFactory.getBean(beanName);
        Class<?> targetClass = bean.getClass();
        Method method = ReflectionUtils.findMethod(targetClass, methodName, getArgsClass(args));
        Assert.notNull(method, String.format("%s不存在方法名为%s的方法", bean.getClass().getName(), methodName));
        return ReflectionUtils.invokeMethod(method, bean, args);
    }

    private Class[] getArgsClass(Object[] args) {
        if (args == null || args.length == 0) {
            return new Class[0];
        }
        Class[] argsClass = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClass[i] = args[i].getClass();
        }
        return argsClass;
    }
}
