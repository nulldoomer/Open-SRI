// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.port;

import io.github.opensri.playground_service.domain.model.SdkLanguage;
import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionRequest;
import io.github.opensri.playground_service.infrastructure.sdk.dto.SdkExecutionResult;
import reactor.core.publisher.Mono;

public interface SdkExecutor {
  SdkLanguage supports();

  Mono<SdkExecutionResult> execute(SdkExecutionRequest request);
}
