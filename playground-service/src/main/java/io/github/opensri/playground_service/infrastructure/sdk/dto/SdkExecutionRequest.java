// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk.dto;

import io.github.opensri.playground_service.domain.model.InvoicePayload;
import io.github.opensri.playground_service.domain.model.SdkLanguage;

public record SdkExecutionRequest(
    InvoicePayload invoicePayload, SdkLanguage language, String sdkVersion) {}
