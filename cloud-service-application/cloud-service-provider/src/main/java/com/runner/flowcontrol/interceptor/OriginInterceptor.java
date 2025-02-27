package com.runner.flowcontrol.interceptor;

import com.alibaba.csp.sentinel.context.Context;
import com.alibaba.csp.sentinel.context.ContextUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AnnotationProcessor find all http method info, if invoke method is a Sentinel resource,find or create Context
 * and set origin
 *
 * @author Runner
 * @version 1.0
 * @since 2025/2/27 16:52
 */
@Component
public class OriginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String origin = request.getHeader("origin");

        Context enter = ContextUtil.enter(getContextNameByOrigin(origin), origin);
        return true;
    }

    private String getContextNameByOrigin(String origin) {
        return null;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ContextUtil.exit();
    }
}
