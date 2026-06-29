// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application.impl;

import io.github.opensri.playground_service.api.dto.RunSessionRequest;
import io.github.opensri.playground_service.application.RunSessionUseCase;
import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.model.SessionLog;
import io.github.opensri.playground_service.domain.model.SessionStatus;
import io.github.opensri.playground_service.domain.port.SdkExecutor;
import io.github.opensri.playground_service.domain.port.SessionRepository;
import io.github.opensri.playground_service.infrastructure.sdk.SdkExecutionRequest;
import io.github.opensri.playground_service.infrastructure.sse.SessionEventPublisher;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class RunSessionUseCaseImpl implements RunSessionUseCase {

  private final SessionRepository sessionRepository;
  private final SdkExecutor javaExecutor;
  private final SessionEventPublisher eventPublisher;

  public RunSessionUseCaseImpl(
      SessionRepository sessionRepository,
      SdkExecutor javaExecutor,
      SessionEventPublisher eventPublisher) {
    this.sessionRepository = sessionRepository;
    this.javaExecutor = javaExecutor;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public Mono<String> run(RunSessionRequest request) {
    String sessionId = UUID.randomUUID().toString();
    Instant now = Instant.now();

    PlaygroundSession pendingSession =
        new PlaygroundSession(
            sessionId,
            request.language(),
            request.sdkVersion(),
            SessionStatus.PENDING,
            now,
            null,
            null,
            null,
            request.invoicePayload(),
            null,
            new ArrayList<>(),
            null);

    return sessionRepository
        .save(pendingSession)
        .then(
            Mono.defer(
                () -> {
                  PlaygroundSession runningSession =
                      new PlaygroundSession(
                          sessionId,
                          request.language(),
                          request.sdkVersion(),
                          SessionStatus.RUNNING,
                          now,
                          Instant.now(),
                          null,
                          null,
                          request.invoicePayload(),
                          null,
                          new ArrayList<>(),
                          null);

                  return sessionRepository
                      .save(runningSession)
                      .then(eventPublisher.emit(runningSession))
                      .then(executeSession(sessionId, request, now));
                }))
        .then(Mono.just(sessionId))
        .subscribeOn(Schedulers.boundedElastic());
  }

  private Mono<Void> executeSession(
      String sessionId, RunSessionRequest request, Instant createdAt) {
    long startTime = System.currentTimeMillis();

    SdkExecutionRequest sdkRequest =
        new SdkExecutionRequest(
            request.invoicePayload(),
            request.language(),
            request.sdkVersion(),
            request.certificatePath(),
            request.certificatePassword());

    return javaExecutor
        .execute(sdkRequest)
        .flatMap(
            result -> {
              long executionTime = System.currentTimeMillis() - startTime;

              List<SessionLog> logs = new ArrayList<>();
              if (result.logs() != null) {
                for (String logMsg : result.logs()) {
                  logs.add(new SessionLog(Instant.now(), logMsg));
                }
              }

              PlaygroundSession completedSession =
                  new PlaygroundSession(
                      sessionId,
                      request.language(),
                      request.sdkVersion(),
                      result.success() ? SessionStatus.COMPLETED : SessionStatus.FAILED,
                      createdAt,
                      Instant.now(),
                      Instant.now(),
                      executionTime,
                      request.invoicePayload(),
                      result.responsePayload(),
                      logs,
                      result.errorMessage());

              return sessionRepository
                  .save(completedSession)
                  .then(eventPublisher.emit(completedSession))
                  .then(eventPublisher.complete(sessionId));
            })
        .onErrorResume(
            error -> {
              long executionTime = System.currentTimeMillis() - startTime;

              List<SessionLog> logs =
                  List.of(new SessionLog(Instant.now(), "Error: " + error.getMessage()));

              PlaygroundSession failedSession =
                  new PlaygroundSession(
                      sessionId,
                      request.language(),
                      request.sdkVersion(),
                      SessionStatus.FAILED,
                      createdAt,
                      Instant.now(),
                      Instant.now(),
                      executionTime,
                      request.invoicePayload(),
                      null,
                      logs,
                      error.getMessage());

              return sessionRepository
                  .save(failedSession)
                  .then(eventPublisher.emit(failedSession))
                  .then(eventPublisher.complete(sessionId));
            });
  }
}
