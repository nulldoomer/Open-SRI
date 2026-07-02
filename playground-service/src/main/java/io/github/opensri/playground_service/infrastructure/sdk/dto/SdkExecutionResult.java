// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk.dto;

import java.util.List;

public record SdkExecutionResult(
    String responsePayload,
    List<String> logs,
    Long executionTimeMs,
    String errorMessage,
    boolean success) {}
