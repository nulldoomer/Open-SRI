package io.github.opensri.playground_service.infrastructure.security.filters;

import io.github.opensri.playground_service.infrastructure.security.ratelimit.RateLimitProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimitIntegrationTest {


    // Build manually the WebTestClient
    @LocalServerPort
    private int port;

    private WebTestClient webTestClient() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    // Override the application config to get the minimum capacity for the test
    @TestConfiguration
    static class TestConfig{
        @Bean
        @Primary
        RateLimitProperties rateLimitProperties(){
            return new RateLimitProperties(
                    true,
                    3,
                    Duration.ofMinutes(1),
                    Duration.ofMinutes(10),
                    1000L
            );
        }
    }

    @Test
    void exhausting_limit_returns_429(){
        String clientIp = "10.0.0.1";

        for(int i = 0; i < 3; i++){
            webTestClient().get().uri("/actuator/health")
                    .header("X-Forwarded-For", clientIp)
                    .exchange()
                    .expectStatus().isOk();
        }

        webTestClient().get().uri("/actuator/health")
                .header("X-Forwarded-For", clientIp)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.TOO_MANY_REQUESTS)
                .expectHeader().exists("Retry-After");
    }
}
