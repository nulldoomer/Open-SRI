// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application.impl;

import io.github.opensri.playground_service.api.dto.RunSessionRequest;
import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import io.github.opensri.playground_service.domain.model.SessionLog;
import io.github.opensri.playground_service.domain.port.SdkExecutor;
import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
class SessionExecutionService {
  private final SdkExecutor sdkExecutor;

  SessionExecutionService(SdkExecutor sdkExecutor) {
    this.sdkExecutor = sdkExecutor;
  }

  Mono<PlaygroundSession> execute(PlaygroundSession runningSession, RunSessionRequest request) {

    long start = System.currentTimeMillis();
    SdkExecutionRequest sdkRequest =
        new SdkExecutionRequest(request.invoicePayload(), request.language(), request.sdkVersion());

    return sdkExecutor
        .execute(sdkRequest)
        .map(result -> runningSession.complete(result, elapsed(start), toLogs(result.logs())))
        .onErrorResume(error -> Mono.just(runningSession.fail(error.getMessage(), elapsed(start))));
  }

  private long elapsed(long start) {
    return System.currentTimeMillis() - start;
  }

  private List<SessionLog> toLogs(List<String> rawLogs) {

    if (rawLogs == null) return List.of();

    return rawLogs.stream().map(message -> new SessionLog(Instant.now(), message)).toList();
  }
}
