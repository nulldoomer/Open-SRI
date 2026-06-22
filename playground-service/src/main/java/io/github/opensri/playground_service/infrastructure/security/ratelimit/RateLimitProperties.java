// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.security.ratelimit;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Rate limiter settings, externalized in {@code application.yaml} under the {@code ratelimit}
 * prefix. Bound at startup via constructor binding; each field falls back to its {@link
 * DefaultValue} when absent.
 *
 * @param enabled toggles rate limiting on/off without code changes (handy for tests/local).
 * @param capacity maximum number of requests allowed per window, per client.
 * @param refillPeriod window over which {@code capacity} tokens are replenished (greedy refill).
 * @param idleExpiry how long a client's bucket survives without activity before being evicted;
 *     bounds memory over time.
 * @param maxClients maximum number of live buckets held in the cache; bounds memory by count as
 *     well as by time (a safety net against a surge of distinct client keys).
 */
@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
    @DefaultValue("true") boolean enabled,
    @DefaultValue("20") int capacity,
    @DefaultValue("1m") Duration refillPeriod,
    @DefaultValue("10m") Duration idleExpiry,
    @DefaultValue("100000") long maxClients) {}
