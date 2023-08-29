package com.yhx.log.annotation;

import com.yhx.log.config.CSBOperationLogConfigureSelector;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author yanghuaxu
 * @date 2023/8/28 20:24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({CSBOperationLogConfigureSelector.class})
public @interface EnableCSBOperationLog {

    AdviceMode mode() default AdviceMode.PROXY;

    boolean proxyTargetClass() default false;

}
