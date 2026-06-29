// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application.impl;

import io.github.opensri.playground_service.api.dto.SessionResponse;
import io.github.opensri.playground_service.application.GetSessionUseCase;
import io.github.opensri.playground_service.domain.port.SessionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetSessionUseCaseImpl implements GetSessionUseCase {

  private final SessionRepository sessionRepository;

  public GetSessionUseCaseImpl(SessionRepository sessionRepository) {
    this.sessionRepository = sessionRepository;
  }

  @Override
  public Mono<SessionResponse> get(String sessionId) {
    return sessionRepository
        .findById(sessionId)
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
                    session.errorMessage()));
  }
}
