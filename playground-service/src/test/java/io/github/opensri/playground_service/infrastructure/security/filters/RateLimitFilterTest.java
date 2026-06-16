package io.github.opensri.playground_service.infrastructure.security.filters;

import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitProperties;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitResult;
import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RateLimitFilterTest {

    @Mock
    RateLimiter rateLimiter;

    @Mock
    WebFilterChain filterChain;

    private RateLimitFilter filter;


    @BeforeEach
    void setUp(){

        // ------------------ Arrange -----------------------------------------
        RateLimitProperties properties = new RateLimitProperties(
                true,
                20,
                Duration.ofMinutes(1),
                Duration.ofMinutes(10),
                1000L
        );

        filter = new RateLimitFilter(rateLimiter, properties);

        // It returns Mono.empty() when the request passes
        when(filterChain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    void adds_remaining_header_when_allowed(){

        // ------------------ Act -----------------------------------------
        when(rateLimiter.tryConsume(any())).thenReturn(RateLimitResult.allowed(19));

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/run").build()
        );

        filter.filter(exchange, filterChain).block();

        // ------------------ Assert -----------------------------------------
        assertThat(exchange.getResponse().getHeaders().getFirst("X-Rate-Limit-Remaining"))
                .isEqualTo("19");

        verify(filterChain).filter(exchange);
    }

    @Test
    void usesX_forwarded_for_header_as_clientKey(){

        // ------------------ Act -----------------------------------------
        when(rateLimiter.tryConsume("203.0.113.5")).thenReturn(RateLimitResult.allowed(10));

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/api/run")
                        .header("X-Forwarded-For", "203.0.113.5, 10.0.0.1")
                        .build()
        );

        filter.filter(exchange, filterChain).block();

        // ------------------ Assert -----------------------------------------
        verify(rateLimiter).tryConsume("203.0.113.5");
    }
}
