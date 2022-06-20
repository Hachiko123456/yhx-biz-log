package com.yhx.log.annotation;

import java.lang.annotation.*;

/**
 * @author yanghuaxu
 * @date 2022/6/14 18:20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
@Inherited
public @interface MarkField {

    String value() default "";

}
