package com.jovisco.services.gatewayserver;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
					)
				.uri("lb://ACCOUNTS"))		
			.route(p -> p
				.path("/banking/api/v1/loans/**")
				.filters(f ->f
					.stripPrefix(1)
					.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
				)
				.uri("lb://LOANS"))		
			.route(p -> p
				.path("/banking/api/v1/cards/**")
				.filters(f ->f
					.stripPrefix(1)
					.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
				)
				.uri("lb://CARDS"))		
			.build();
	}
}
