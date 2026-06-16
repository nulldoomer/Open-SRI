// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration;

// No user authentication in this service, so we skip the default in-memory user (and the random
// password it logs at startup). Security is limited to headers, CORS and the rate limiter.
@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class PlaygroundServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(PlaygroundServiceApplication.class, args);
  }
}
