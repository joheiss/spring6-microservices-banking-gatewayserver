package com.jovisco.services.gatewayserver;

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
				.path("/api/v1/banking/accounts/**")
				.filters(f ->f
					.rewritePath(
						"/api/v1/banking/accounts/(?<segment>.*)", 
						"/api/v1/banking/accounts/${segment}"))
				.uri("lb.ACCOUNTS"))		
			.route(p -> p
				.path("/api/v1/banking/loans/**")
				.filters(f ->f
					.rewritePath(
						"/api/v1/banking/loans/(?<segment>.*)", 
						"/api/v1/banking/loans/${segment}"))
				.uri("lb.LOANS"))		
			.route(p -> p
				.path("/api/v1/banking/cards/**")
				.filters(f ->f
					.rewritePath(
						"/api/v1/banking/cards/(?<segment>.*)", 
						"/api/v1/banking/cards/${segment}"))
				.uri("lb.CARDS"))		
			.build();
	}
}
