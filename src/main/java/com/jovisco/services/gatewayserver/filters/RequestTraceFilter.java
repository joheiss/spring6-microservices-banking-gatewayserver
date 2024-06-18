package com.jovisco.services.gatewayserver.filters;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    private final FilterUtility filterUtility;

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        var requestHeaders = exchange.getRequest().getHeaders();
        if (isCorreclationIdPresent(requestHeaders)) {
            log.debug(FilterUtility.CORRELATION_ID + " found in request trace filter: {}", 
                filterUtility.getCorrelationId(requestHeaders));
        } else {
            String correlationId = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationId);
            log.debug(FilterUtility.CORRELATION_ID + " generated in request trace filter: {}", 
                correlationId);
        }
        return chain.filter(exchange);
    }

    private boolean isCorreclationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null ? true : false;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

}
