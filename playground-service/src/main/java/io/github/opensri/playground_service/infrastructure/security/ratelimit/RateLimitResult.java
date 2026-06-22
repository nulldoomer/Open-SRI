// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.security.ratelimit;

import java.time.Duration;

/**
 * Outcome of a single consumption attempt against the rate limiter, expressed in domain terms
 * rather than Bucket4j types. This is what decouples the filter from the library: if the
 * implementation changes (e.g. to a Redis-backed limiter), callers of this type are unaffected.
 *
 * <p>Instances are created through the {@link #allowed(long)} / {@link #rejected(Duration)} factory
 * methods, which guarantee a consistent state (a rejected result always reports zero remaining
 * tokens, an allowed one a zero wait).
 *
 * @param allowed whether the request may proceed.
 * @param remainingTokens tokens left in the current window (zero when rejected).
 * @param retryAfter time to wait before retrying; only meaningful when {@code !allowed}.
 */
public record RateLimitResult(boolean allowed, long remainingTokens, Duration retryAfter) {

  public static RateLimitResult allowed(long remainingTokens) {

    return new RateLimitResult(true, remainingTokens, Duration.ZERO);
  }

  public static RateLimitResult rejected(Duration retryAfter) {

    return new RateLimitResult(false, 0, retryAfter);
  }
}
