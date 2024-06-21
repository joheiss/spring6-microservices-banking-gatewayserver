package com.jovisco.services.gatewayserver;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator routeConfig(RouteLocatorBuilder routeLocatorBuilder) {

		// Resilience4j order: retry, circuitbreaker, ratelimiter, timelimiter, bulkhead
		return routeLocatorBuilder.routes()
				.route(p -> p
						.path("/banking/api/v1/accounts/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig
										.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
								.circuitBreaker(config -> config
										.setName("accountsCircuitBreaker")
										.setFallbackUri("forward:/contact-support"))
								.requestRateLimiter(config -> config
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(userKeyResolver())))
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
										.setFallbackUri("forward:/contact-support"))
								.requestRateLimiter(config -> config
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(userKeyResolver())))
						.metadata(RESPONSE_TIMEOUT_ATTR, 1500)
						.metadata(CONNECT_TIMEOUT_ATTR, 3000)
						.uri("lb://ACCOUNTS"))
				.route(p -> p
						.path("/banking/api/v1/loans/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								.retry(retryConfig -> retryConfig
										.setRetries(3)
										.setMethods(HttpMethod.GET)
										.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)))
								// .requestRateLimiter(config -> config
								// 		.setRateLimiter(redisRateLimiter())
								// 		.setKeyResolver(userKeyResolver())))
						.uri("lb://LOANS"))
				.route(p -> p
						.path("/banking/api/v1/cards/**")
						.filters(f -> f
								.stripPrefix(1)
								.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
								// .retry(retryConfig -> retryConfig
								// .setRetries(3)
								// .setMethods(HttpMethod.GET)
								// .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true))
								.requestRateLimiter(config -> config
										.setRateLimiter(redisRateLimiter())
										.setKeyResolver(userKeyResolver())))
						.uri("lb://CARDS"))
				.build();
	}

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(3)).build())
				.build());
	}

	@Bean
	public RedisRateLimiter redisRateLimiter() {
		final int REPLENISH_RATE = 1;
		final int BURST_CAPACITY = 1;
		final int REQUESTED_TOKENS = 1;
		return new RedisRateLimiter(REPLENISH_RATE, BURST_CAPACITY, REQUESTED_TOKENS);
	}

	@Bean
	KeyResolver userKeyResolver() {
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user"))
				.defaultIfEmpty("anonymous");
	}
}
