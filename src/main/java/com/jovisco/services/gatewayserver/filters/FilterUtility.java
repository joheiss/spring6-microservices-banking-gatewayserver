package com.jovisco.services.gatewayserver.filters;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "jovisco-banking-correlation-id";

    public String getCorrelationId(HttpHeaders requestHeaders) {

        if (requestHeaders.get(CORRELATION_ID) != null) {
            var requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        }
        return null;
    }

    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }

    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) { 
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }

}
