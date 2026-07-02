// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application.impl;

import io.github.opensri.playground_service.api.dto.RunSessionRequest;
import io.github.opensri.playground_service.application.RunSessionUseCase;
import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.port.SessionRepository;
import io.github.opensri.playground_service.infrastructure.sse.SseSessionEventPublisher;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RunSessionUseCaseImpl implements RunSessionUseCase {

  private final SessionExecutionService executionService;
  private final SessionRepository sessionRepository;
  private final SseSessionEventPublisher eventPublisher;

  public RunSessionUseCaseImpl(
      SessionExecutionService executionService,
      SessionRepository sessionRepository,
      SseSessionEventPublisher eventPublisher) {

    this.executionService = executionService;
    this.sessionRepository = sessionRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Mono<String> run(RunSessionRequest request) {

    String sessionId = UUID.randomUUID().toString();

    PlaygroundSession pending =
        PlaygroundSession.pending(
            sessionId,
            request.language(),
            request.sdkVersion(),
            request.invoicePayload(),
            Instant.now());

    return persistAndPublish(pending)
        .map(PlaygroundSession::start)
        .flatMap(this::persistAndPublish)
        .flatMap(running -> executionService.execute(running, request))
        .flatMap(this::persistAndPublish)
        .flatMap(finalSession -> eventPublisher.complete(sessionId))
        .thenReturn(sessionId)
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<PlaygroundSession> persistAndPublish(PlaygroundSession session) {

    return sessionRepository.save(session).then(eventPublisher.emit(session)).thenReturn(session);
  }
}
