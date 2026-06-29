// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api;

import io.github.opensri.playground_service.api.dto.RunSessionRequest;
import io.github.opensri.playground_service.api.dto.SessionResponse;
import io.github.opensri.playground_service.application.GetSessionUseCase;
import io.github.opensri.playground_service.application.RunSessionUseCase;
import io.github.opensri.playground_service.infrastructure.sse.SessionEventPublisher;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Exposes the interactive playground pipeline.
 *
 * <p>{@code POST /sessions} accepts a request and returns immediately with a {@code sessionId};
 * {@code GET /sessions/{id}/events} streams the session trace as Server-Sent Events. The split
 * keeps the request non-blocking and lets the frontend render each step live.
 */
@RestController
@RequestMapping("/sessions")
public class PlaygroundController {

  private final RunSessionUseCase runSessionUseCase;
  private final GetSessionUseCase getSessionUseCase;
  private final SessionEventPublisher eventPublisher;

  public PlaygroundController(
      RunSessionUseCase runSessionUseCase,
      GetSessionUseCase getSessionUseCase,
      SessionEventPublisher eventPublisher) {
    this.runSessionUseCase = runSessionUseCase;
    this.getSessionUseCase = getSessionUseCase;
    this.eventPublisher = eventPublisher;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<Map<String, String>>> runSession(
      @RequestBody RunSessionRequest request) {
    return runSessionUseCase
        .run(request)
        .map(
            sessionId ->
                ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("sessionId", sessionId)));
  }

  @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<SessionResponse>> streamSessionEvents(@PathVariable String id) {
    return getSessionUseCase
        .get(id)
        .switchIfEmpty(
            Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found")))
        .flatMapMany(
            initialSession -> {
              Flux<ServerSentEvent<SessionResponse>> initial =
                  Mono.just(ServerSentEvent.builder(initialSession).build()).flux();

              Flux<ServerSentEvent<SessionResponse>> updates =
                  eventPublisher
                      .subscribe(id)
                      .map(
                          session ->
                              new SessionResponse(
                                  session.id(),
                                  session.language(),
                                  session.sdkVersion(),
                                  session.status(),
                                  session.createdAt(),
                                  session.startedAt(),
                                  session.completedAt(),
                                  session.durationsMs(),
                                  session.requestPayload(),
                                  session.responsePayload(),
                                  session.logs(),
                                  session.errorMessage()))
                      .map(response -> ServerSentEvent.builder(response).build());

              return initial.concatWith(updates);
            });
  }

  @GetMapping("/{id}")
  public Mono<ResponseEntity<SessionResponse>> getSession(@PathVariable String id) {
    return getSessionUseCase
        .get(id)
        .map(ResponseEntity::ok)
        .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
  }
}
