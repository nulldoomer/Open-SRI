// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

import io.github.opensri.playground_service.domain.model.SdkLanguage;
import io.github.opensri.playground_service.domain.model.SessionLog;
import io.github.opensri.playground_service.domain.model.SessionStatus;
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
    String requestPayload,
    String responsePayload,
    List<SessionLog> logs,
    String errorMessage) {}
