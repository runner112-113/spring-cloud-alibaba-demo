package com.runner.processor;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/10/30 17:51
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface MyAnnotation {


    String value();
}
