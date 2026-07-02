// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application.impl;

import io.github.opensri.playground_service.api.dto.SessionResponse;
import io.github.opensri.playground_service.application.GetSessionUseCase;
import io.github.opensri.playground_service.domain.port.SessionRepository;
import io.github.opensri.playground_service.infrastructure.sse.SseSessionEventPublisher;
import io.github.opensri.playground_service.shared.exceptions.SessionNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetSessionUseCaseImpl implements GetSessionUseCase {

  private final SessionRepository sessionRepository;
  private final SseSessionEventPublisher eventPublisher;

  public GetSessionUseCaseImpl(
      SessionRepository sessionRepository, SseSessionEventPublisher eventPublisher) {
    this.sessionRepository = sessionRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Mono<SessionResponse> get(String sessionId) {
    return sessionRepository.findById(sessionId).map(SessionResponse::from);
  }

  @Override
  public Flux<SessionResponse> streamEvents(String id) {
    return get(id)
        .switchIfEmpty(Mono.error(new SessionNotFoundException(id)))
        .flatMapMany(
            initial ->
                Flux.concat(
                    Mono.just(initial), eventPublisher.subscribe(id).map(SessionResponse::from)));
  }
}
