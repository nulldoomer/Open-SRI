// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

import io.github.opensri.playground_service.domain.model.*;
import java.time.Instant;
import java.util.List;

public record SessionResponse(
    String id,
    SdkLanguage language,
    String sdkVersion,
    SessionStatus status,
    Instant createdAt,
    Instant startedAt,
    Instant completedAt,
    Long durationMs,
    InvoicePayload requestPayload,
    ResponsePayload responsePayload,
    List<SessionLog> logs,
    String errorMessage) {

  public SessionResponse {
    logs = logs == null ? List.of() : List.copyOf(logs);
  }

  public static SessionResponse from(PlaygroundSession session) {
    return new SessionResponse(
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
        session.errorMessage());
  }
}
