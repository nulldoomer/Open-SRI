// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionResult;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record PlaygroundSession(
    String id,
    SdkLanguage language,
    String sdkVersion,
    SessionStatus status,
    Instant createdAt,
    Instant startedAt,
    Instant completedAt,
    Long durationsMs,
    InvoicePayload requestPayload,
    String responsePayload,
    List<SessionLog> logs,
    String errorMessage) {

  public PlaygroundSession {
    logs = logs == null ? List.of() : List.copyOf(logs);
  }

  // --------------------------- STATE MACHINE ----------------------------------
  public static PlaygroundSession pending(
      String id,
      SdkLanguage language,
      String sdkVersion,
      InvoicePayload invoicePayload,
      Instant now) {

    return new PlaygroundSession(
        id,
        language,
        sdkVersion,
        SessionStatus.PENDING,
        now,
        null,
        null,
        null,
        invoicePayload,
        null,
        new ArrayList<>(),
        null);
  }

  public PlaygroundSession start() {

    return new PlaygroundSession(
        id,
        language,
        sdkVersion,
        SessionStatus.RUNNING,
        createdAt,
        Instant.now(),
        null,
        null,
        requestPayload,
        null,
        new ArrayList<>(),
        null);
  }

  public PlaygroundSession complete(
      SdkExecutionResult result, long executionTimeMs, List<SessionLog> logs) {

    SessionStatus status = result.success() ? SessionStatus.COMPLETED : SessionStatus.FAILED;

    return new PlaygroundSession(
        id,
        language,
        sdkVersion,
        status,
        createdAt,
        startedAt,
        Instant.now(),
        executionTimeMs,
        requestPayload,
        result.responsePayload(),
        logs,
        result.errorMessage());
  }

  public PlaygroundSession fail(String errorMessage, long executionTimeMs) {

    List<SessionLog> logs = List.of(new SessionLog(Instant.now(), "Error: " + errorMessage));

    return new PlaygroundSession(
        id,
        language,
        sdkVersion,
        SessionStatus.FAILED,
        createdAt,
        startedAt,
        Instant.now(),
        executionTimeMs,
        requestPayload,
        null,
        logs,
        errorMessage);
  }
}
