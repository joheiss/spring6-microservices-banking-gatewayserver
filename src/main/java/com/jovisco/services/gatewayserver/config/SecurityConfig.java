package com.jovisco.services.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {

    serverHttpSecurity.authorizeExchange(exchange -> exchange
        .pathMatchers(HttpMethod.GET).permitAll()
        .pathMatchers("/banking/api/v1/accounts/**").authenticated()
        .pathMatchers("/banking/api/v1/loans**").authenticated()
        .pathMatchers("/banking/api/v1/cards**").authenticated())
        .oauth2ResourceServer(oAuth -> oAuth.jwt(Customizer.withDefaults()));

    serverHttpSecurity.csrf(csrf -> csrf.disable());

    return serverHttpSecurity.build();
  }
}
