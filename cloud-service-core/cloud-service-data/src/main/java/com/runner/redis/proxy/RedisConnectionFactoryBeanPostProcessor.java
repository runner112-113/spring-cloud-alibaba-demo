package com.runner.redis.proxy;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * {@link RedisConnectionFactoryBeanPostProcessor} wrapper {@link RedisConnectionFactory} and metrics
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/13 10:43
 */
public class RedisConnectionFactoryBeanPostProcessor implements BeanPostProcessor {

    final MeterRegistry meterRegistry;
    final RedisProperties redisProperties;

    public RedisConnectionFactoryBeanPostProcessor(MeterRegistry meterRegistry, RedisProperties redisProperties) {
        this.meterRegistry = meterRegistry;
        this.redisProperties = redisProperties;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof RedisConnectionFactory) {
            ProxyFactory proxyFactory = new ProxyFactory(bean);
            proxyFactory.addAdvice((MethodInterceptor) invocation -> {
                Method method = invocation.getMethod();
                method.setAccessible(true);
                String methodName = method.getName();
                Object result = invocation.proceed();
                if ("getConnection".equals(methodName)) {
                    // wrapper result
                    result = newProxyRedisConnection((RedisConnection)result);
                }
                return result;
            });

            return proxyFactory.getProxy();
        }
        return bean;
    }

    private Object newProxyRedisConnection(RedisConnection rawRedisConnection) {
        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), ClassUtils.getAllInterfaces(rawRedisConnection), (proxy, method, args) -> {
            if ("set".equals(method.getName())) {
                Counter.builder("redis.set")
                        .tag("redis.instance", String.join(":", redisProperties.getHost(), String.valueOf(redisProperties.getPort())))
                        .register(meterRegistry).increment();
            }

            Timer.Sample sample = Timer.start(meterRegistry);

            Object result = method.invoke(rawRedisConnection, args);

            sample.stop(Timer.builder("redis.set.cost")
                    .tag("redis.instance", String.join(":", redisProperties.getHost(), String.valueOf(redisProperties.getPort())))
                    .tag("op.command", "set")
                    .register(meterRegistry));

            return result;
        });
    }


    static class RedisMetricsInterceptor implements MethodInterceptor {

        private MeterRegistry meterRegistry;
        private RedisProperties redisProperties;

        public RedisMetricsInterceptor(MeterRegistry meterRegistry, RedisProperties redisProperties) {
            this.meterRegistry = meterRegistry;
            this.redisProperties = redisProperties;
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {

            if ("set".equals(invocation.getMethod().getName())) {
                Counter.builder("redis.set")
                        .tag("redis.instance", String.join(":", redisProperties.getHost(), String.valueOf(redisProperties.getPort())))
                        .register(meterRegistry).increment();
            }

            Timer.Sample sample = Timer.start(meterRegistry);

            Object proceed = invocation.proceed();

/*            Timer timer = Timer.builder("redis.set.cost")
                    .tag("redis.instance", String.join(":", redisProperties.getHost(), String.valueOf(redisProperties.getPort())))
                    .tag("op.command", "set")
                    .register(meterRegistry);*/

            sample.stop(Timer.builder("redis.set.cost")
                    .tag("redis.instance", String.join(":", redisProperties.getHost(), String.valueOf(redisProperties.getPort())))
                    .tag("op.command", "set")
                    .register(meterRegistry));

            return proceed;
        }
    }
}
