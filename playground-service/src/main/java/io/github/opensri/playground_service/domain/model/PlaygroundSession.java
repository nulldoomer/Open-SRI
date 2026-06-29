// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

import java.time.Instant;
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
    String requestPayload,
    String responsePayload,
    List<SessionLog> logs,
    String errorMessage) {

  public PlaygroundSession {
    logs = logs == null ? List.of() : List.copyOf(logs);
  }
}
