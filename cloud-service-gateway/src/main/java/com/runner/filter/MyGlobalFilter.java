package com.runner.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * one of {@link  GlobalFilter}
 *
 * @author Runner
 * @version 1.0
 * @since 2024/9/20 15:55
 */
@Component
@Slf4j
public class MyGlobalFilter implements GlobalFilter {
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("trigger MyGlobalFilter ...... ");
        return chain.filter(exchange);
    }
}
