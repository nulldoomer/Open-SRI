package io.github.opensri.playground_service.infrastructure.security.ratelimit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp(){
        //--------- Arrange ------------------

        RateLimitProperties properties = new RateLimitProperties(
                true,
                3,
                Duration.ofMinutes(1),
                Duration.ofMinutes(10),
                1000L
        );

        rateLimiter = new RateLimiter(properties);
    }

    @Test
    void allows_requests_within_capacity(){

        // -------------- Act ---------------------------
        RateLimitResult result = rateLimiter.tryConsume("client-1");

        // -------------- Assert ---------------------------
        assertThat(result.allowed()).isTrue();
        assertThat(result.remainingTokens()).isEqualTo(2);
    }

    @Test
    void rejects_request_after_capacity_exhausted(){

        // -------------- Act ---------------------------
        rateLimiter.tryConsume("client-1");
        rateLimiter.tryConsume("client-1");
        rateLimiter.tryConsume("client-1");

        RateLimitResult result = rateLimiter.tryConsume("client-1");

        // -------------- Assert ---------------------------
        assertThat(result.allowed()).isFalse();
        assertThat(result.remainingTokens()).isZero();
        assertThat(result.retryAfter()).isPositive();
    }

    @Test
    void tracks_clients_independently() {
        // -------------- Act -------------------------
        // "client-A" agota sus tokens
        rateLimiter.tryConsume("client-A");
        rateLimiter.tryConsume("client-A");
        rateLimiter.tryConsume("client-A");

        // "client-B" tiene su propio bucket, no se ve afectado
        RateLimitResult result = rateLimiter.tryConsume("client-B");

        // -------------- Assert -------------------------
        assertThat(result.allowed()).isTrue();
    }

}
