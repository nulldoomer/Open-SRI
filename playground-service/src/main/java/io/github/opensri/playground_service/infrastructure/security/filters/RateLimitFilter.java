// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.security.filters;

import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitProperties;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitResult;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimiter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Reactive rate-limiting filter. It owns only the HTTP concerns: identify the client, delegate the
 * decision to {@link RateLimiter}, and translate the resulting {@link RateLimitResult} into a
 * response. It has no knowledge of Bucket4j.
 *
 * <p>This filter is wired into the Spring Security chain (see {@code SecurityConfig}) right after
 * the CORS filter and before authentication, so excess traffic is rejected as early as possible.
 *
 * <p>It is intentionally <strong>not</strong> annotated with {@code @Component}: in WebFlux every
 * {@link WebFilter} bean is auto-registered as a global filter, so exposing it as a bean would run
 * it twice (once globally and once inside the security chain). Instead it is instantiated
 * explicitly in {@code SecurityConfig}.
 *
 * <p>A single instance is shared across all requests; it is stateless and therefore thread-safe.
 */
public class RateLimitFilter implements WebFilter {

  private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";
  private static final String REMAINING_HEADER = "X-Rate-Limit-Remaining";
  private static final String UNKNOWN_CLIENT = "unknown";

  private final RateLimiter rateLimiter;
  private final RateLimitProperties properties;

  public RateLimitFilter(RateLimiter rateLimiter, RateLimitProperties properties) {
    this.rateLimiter = rateLimiter;
    this.properties = properties;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if (!properties.enabled()) {
      return chain.filter(exchange);
    }

    String clientKey = resolveClientKey(exchange.getRequest());
    RateLimitResult result = rateLimiter.tryConsume(clientKey);

    if (result.allowed()) {
      exchange
          .getResponse()
          .getHeaders()
          .add(REMAINING_HEADER, Long.toString(result.remainingTokens()));
      return chain.filter(exchange);
    }

    return rejectOverLimit(exchange.getResponse(), result);
  }

  /**
   * Resolves the identity used as the rate-limit key. When the app runs behind a trusted proxy/load
   * balancer the real client IP is carried in {@code X-Forwarded-For}; otherwise the socket address
   * is used. Falls back to {@code "unknown"} if no address is available.
   *
   * <p><strong>Security note:</strong> {@code X-Forwarded-For} is client-spoofable, so it is only
   * trustworthy when a trusted proxy rewrites it. In a direct deployment (no proxy) prefer relying
   * solely on the socket address, otherwise a caller could bypass the limit by forging the header.
   */
  private String resolveClientKey(ServerHttpRequest request) {
    String forwardedFor = request.getHeaders().getFirst(FORWARDED_FOR_HEADER);
    if (forwardedFor != null && !forwardedFor.isBlank()) {
      return forwardedFor.split(",")[0].trim();
    }

    return Optional.ofNullable(request.getRemoteAddress())
        .map(InetSocketAddress::getAddress)
        .map(InetAddress::getHostAddress)
        .orElse(UNKNOWN_CLIENT);
  }

  private Mono<Void> rejectOverLimit(ServerHttpResponse response, RateLimitResult result) {
    long retryAfterSeconds = Math.max(1, result.retryAfter().toSeconds());

    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
    response.getHeaders().add(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds));
    response.getHeaders().add(REMAINING_HEADER, "0");
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    String body =
        """
                {"error":"TOO_MANY_REQUESTS","message":"Rate limit exceeded. Retry after %d seconds"}\
                """
            .formatted(retryAfterSeconds);

    DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
    return response.writeWith(Mono.just(buffer));
  }
}
