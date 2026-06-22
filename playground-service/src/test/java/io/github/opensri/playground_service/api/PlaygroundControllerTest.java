// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.opensri.playground_service.api.dto.InvoicePayload;
import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.playground_service.api.dto.PlaygroundRunResponse;
import io.github.opensri.playground_service.api.dto.SdkEvent;
import io.github.opensri.playground_service.worker.SdkGateway;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaygroundControllerTest {

  @TestConfiguration
  static class FakeGatewayConfig {
    @Bean
    SdkGateway fakeGateway() {
      return new SdkGateway() {
        @Override
        public String language() {
          return "fake";
        }

        @Override
        public Flux<SdkEvent> run(PlaygroundRunRequest request) {
          return Flux.just(
                  SdkEvent.step("build"),
                  SdkEvent.step("access_key", "1501202601..."),
                  SdkEvent.xml("raw", "<factura/>"),
                  SdkEvent.done("ok"))
              .subscribeOn(Schedulers.boundedElastic());
        }
      };
    }
  }

  @LocalServerPort private int port;

  private WebTestClient webTestClient() {
    return WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(15))
        .build();
  }

  @Test
  void runReturnsAcceptedWithJobId() {
    PlaygroundRunResponse response =
        webTestClient()
            .post()
            .uri("/playground/run")
            .bodyValue(request())
            .exchange()
            .expectStatus()
            .isAccepted()
            .expectBody(PlaygroundRunResponse.class)
            .returnResult()
            .getResponseBody();

    assertThat(response).isNotNull();
    assertThat(response.jobId()).isNotBlank();
  }

  @Test
  void statusStreamsPipelineEventsUntilDone() {
    String jobId =
        webTestClient()
            .post()
            .uri("/playground/run")
            .bodyValue(request())
            .exchange()
            .expectStatus()
            .isAccepted()
            .expectBody(PlaygroundRunResponse.class)
            .returnResult()
            .getResponseBody()
            .jobId();

    List<SdkEvent> events =
        webTestClient()
            .get()
            .uri("/playground/status/{jobId}", jobId)
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(SdkEvent.class)
            .getResponseBody()
            .collectList()
            .block(Duration.ofSeconds(10));

    assertThat(events).isNotNull();
    assertThat(events).extracting(SdkEvent::step).contains("build", "access_key");
    assertThat(events.get(events.size() - 1).type()).isEqualTo("done");
  }

  @Test
  void runRejectsUnsupportedLanguage() {
    PlaygroundRunRequest request =
        new PlaygroundRunRequest(
            "ruby", "PRUEBAS", request().certificate(), request().issuer(), request().invoice());

    webTestClient()
        .post()
        .uri("/playground/run")
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  private PlaygroundRunRequest request() {
    InvoicePayload.Item item =
        new InvoicePayload.Item(
            "P001",
            null,
            "Item",
            new BigDecimal("1"),
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            List.of(new InvoicePayload.Tax(2, 4)));

    InvoicePayload invoice =
        new InvoicePayload(
            "2.1.0",
            "2026-01-15",
            "Av. Test",
            "01",
            "001",
            "001",
            "000000001",
            new InvoicePayload.Client("FINAL_CONSUMER", "9999999999999", "CONSUMIDOR"),
            List.of(item),
            List.of());

    return new PlaygroundRunRequest(
        "fake",
        "PRUEBAS",
        new PlaygroundRunRequest.Certificate("", "password", "alias"),
        new PlaygroundRunRequest.Issuer("OpenSRI", "1234567890001", "Matriz", 1, "NO", null),
        invoice);
  }
}
