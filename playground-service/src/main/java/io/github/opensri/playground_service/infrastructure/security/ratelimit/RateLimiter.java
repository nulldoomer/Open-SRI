// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.security.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import java.time.Duration;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

/**
 * In-memory rate limiter backed by Bucket4j. This is the single component aware of the library: it
 * keeps one token bucket per client in a Caffeine cache and translates the outcome into a
 * domain-level {@link RateLimitResult}, so callers never depend on Bucket4j types.
 *
 * <p>The cache is bounded both in time ({@code idleExpiry}, evicting idle clients) and in size
 * ({@code maxClients}, capping the number of live buckets), which prevents unbounded memory growth
 * as new client keys appear.
 *
 * <p>Buckets are thread-safe and the cache loads them atomically, so a single shared instance can
 * serve all concurrent requests. Note this limiter is <em>per-instance</em>: when running multiple
 * replicas the effective limit is {@code replicas × capacity}. A distributed limit would require a
 * shared backend (e.g. {@code bucket4j-redis}) behind this same API.
 */
@Component
public class RateLimiter {

  private final RateLimitProperties properties;
  private final Cache<String, Bucket> buckets;

  public RateLimiter(RateLimitProperties properties) {
    this.properties = properties;
    this.buckets =
        Caffeine.newBuilder()
            .expireAfterAccess(properties.idleExpiry())
            .maximumSize(properties.maxClients())
            .build();
  }

  /**
   * Attempts to consume a single token for the given client, creating its bucket on first use.
   *
   * @param clientKey stable identifier of the caller (typically the resolved client IP).
   * @return an allowed result with the remaining tokens, or a rejected result carrying the wait
   * time until the next refill.
   */
  public RateLimitResult tryConsume(String clientKey) {
    Bucket bucket = buckets.get(clientKey, key -> newBucket());
    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

    if (probe.isConsumed()) {
      return RateLimitResult.allowed(probe.getRemainingTokens());
    }
    return RateLimitResult.rejected(Duration.ofNanos(probe.getNanosToWaitForRefill()));
  }

  private Bucket newBucket() {
    Bandwidth limit =
        Bandwidth.builder()
            .capacity(properties.capacity())
            .refillGreedy(properties.capacity(), properties.refillPeriod())
            .build();

    return Bucket.builder().addLimit(limit).build();
  }
}
