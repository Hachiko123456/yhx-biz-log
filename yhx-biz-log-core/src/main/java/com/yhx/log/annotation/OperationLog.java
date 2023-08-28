package com.yhx.log.annotation;

import java.lang.annotation.*;

/**
 * @author yanghuaxu
 * @date 2022/6/6 19:48
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface OperationLog {

    /**
     * 模块
     */
    String module() default "";

    /**
     * 业务编号
     */
    String bizNo() default "";

    /**
     * 链路追踪id
     */
    String traceId() default "";

    /**
     * 操作者
     */
    String operator() default "";

    /**
     * 日志的详细信息
     */
    String detail() default "";

    /**
     * 日志的详细信息对应参数
     */
    String[] detailArgs() default "";

    /**
     * 方法执行成功后的日志模板
     */
    String success() default "";

    /**
     * 方法执行失败后的日志模板
     */
    String fail() default "";

    /**
     * 满足该条件才会执行
     */
    String condition() default "";

    /**
     * 是否批量操作
     */
    boolean isBatch() default false;

}
