package com.jovisco.services.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {

    serverHttpSecurity.authorizeExchange(exchange -> exchange
        .pathMatchers(HttpMethod.GET).permitAll()
        .pathMatchers("/banking/api/v1/accounts/**").hasRole("accounts-editor")
        .pathMatchers("/banking/api/v1/loans**").hasRole("loans-editor")
        .pathMatchers("/banking/api/v1/cards**").hasRole("cards-editor"))
        .oauth2ResourceServer(
            oAuth -> oAuth.jwt(spec -> spec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));

    serverHttpSecurity.csrf(csrf -> csrf.disable());

    return serverHttpSecurity.build();
  }

  private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
    return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
  }
}
