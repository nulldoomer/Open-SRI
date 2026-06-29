// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

import io.github.opensri.playground_service.domain.model.SdkLanguage;

public record RunSessionRequest(
    String invoicePayload,
    SdkLanguage language,
    String sdkVersion,
    String certificatePath,
    String certificatePassword) {}
