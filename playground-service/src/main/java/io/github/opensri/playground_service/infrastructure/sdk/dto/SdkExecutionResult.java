// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk.dto;

import io.github.opensri.playground_service.domain.model.ResponsePayload;
import java.util.List;

public record SdkExecutionResult(
    ResponsePayload responsePayload,
    List<String> logs,
    Long executionTimeMs,
    String errorMessage,
    boolean success) {}
