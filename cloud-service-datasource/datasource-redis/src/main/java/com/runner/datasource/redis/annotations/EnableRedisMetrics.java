package com.runner.datasource.redis.annotations;

import com.runner.datasource.redis.proxy.RedisConnectionFactoryBeanPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * {@link EnableRedisMetrics} enable redis operate monitor
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/13 11:10
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RedisConnectionFactoryBeanPostProcessor.class)
public @interface EnableRedisMetrics {
}
