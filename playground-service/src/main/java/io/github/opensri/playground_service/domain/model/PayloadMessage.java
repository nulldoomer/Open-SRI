// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.model;

public record PayloadMessage(
    String identifier, String message, String additionalInformation, String type) {}
