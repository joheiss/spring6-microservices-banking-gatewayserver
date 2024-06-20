package com.jovisco.services.gatewayserver;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.RouteMetadataUtils;
import org.springframework.context.annotation.Bean;

import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {

		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/banking/api/v1/accounts/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config
										.setName("accountsCircuitBreaker")
										.setFallbackUri("forward:/contact-support")))
						.metadata(RESPONSE_TIMEOUT_ATTR, 1000)
						.metadata(CONNECT_TIMEOUT_ATTR, 2000)
						.uri("lb://ACCOUNTS"))
				.route(p -> p
						.path("/banking/api/v1/customers/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.circuitBreaker(config -> config
										.setName("customersCircuitBreaker")
										.setFallbackUri("forward:/contact-support")))
						.metadata(RESPONSE_TIMEOUT_ATTR, 1500)
						.metadata(CONNECT_TIMEOUT_ATTR, 3000)
						.uri("lb://ACCOUNTS"))
				.route(p -> p
						.path("/banking/api/v1/loans/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://LOANS"))
				.route(p -> p
						.path("/banking/api/v1/cards/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.uri("lb://CARDS"))
				.build();
	}
}
