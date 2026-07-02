// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.security;

import io.github.opensri.playground_service.infrastructure.security.filters.RateLimitFilter;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitProperties;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimiter;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(RateLimitProperties.class)
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain filterChain(
      ServerHttpSecurity httpSecurity,
      RateLimiter rateLimiter,
      RateLimitProperties rateLimitProperties) {

    RateLimitFilter rateLimitFilter = new RateLimitFilter(rateLimiter, rateLimitProperties);

    httpSecurity
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        // No user authentication: this is a public API. We keep only security headers, CORS and
        // the rate limiter, so we turn off the login mechanisms Spring Security enables by default.
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .cors(cors -> cors.configurationSource(corsConfig()))
        // Run the rate limiter right after CORS (so preflight and CORS headers are resolved
        // first), rejecting excess traffic as early as possible.
        .addFilterAfter(rateLimitFilter, SecurityWebFiltersOrder.CORS)
        // Public API with a fixed set of endpoints. There are no users, so instead of
        // authentication we expose only the known paths and deny everything else by default
        // (unknown routes return 401 rather than being silently reachable).
        .authorizeExchange(
            auth ->
                auth.pathMatchers("/actuator/health", "/actuator/prometheus")
                    .permitAll()
                    .pathMatchers("/sessions/**", "/validator/**")
                    .permitAll()
                    .anyExchange()
                    .denyAll())
        .headers(
            headers ->
                headers
                    .frameOptions(
                        frame -> frame.mode(XFrameOptionsServerHttpHeadersWriter.Mode.DENY))
                    .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")));

    return httpSecurity.build();
  }

  @Bean
  CorsConfigurationSource corsConfig() {
    var config = new CorsConfiguration();

    config.setAllowedOrigins(List.of("https://opensri.vercel.app", "http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
