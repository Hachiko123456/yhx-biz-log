package com.yhx.log.annotation;

import com.yhx.log.config.OperationLogConfigureSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author yanghuaxu
 * @date 2022/6/6 19:55
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({OperationLogConfigureSelector.class})
public @interface EnableOperationLog {

    AdviceMode mode() default AdviceMode.PROXY;

    boolean proxyTargetClass() default false;

}
