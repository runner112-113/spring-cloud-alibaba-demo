package com.runner.interfaces;

import java.lang.annotation.*;

/**
 * TODO
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/19 11:03
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAgentIntercept {
}
