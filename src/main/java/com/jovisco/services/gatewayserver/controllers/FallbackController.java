package com.jovisco.services.gatewayserver.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

  @GetMapping("/contact-support")
  public Mono<String> contactSupport() {
    return Mono.just("An error occurred. Please try later again or contact the support team.");
  }
}
