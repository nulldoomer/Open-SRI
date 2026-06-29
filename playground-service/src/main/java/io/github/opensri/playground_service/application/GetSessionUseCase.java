// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.application;

import io.github.opensri.playground_service.api.dto.SessionResponse;
import reactor.core.publisher.Mono;

public interface GetSessionUseCase {
  Mono<SessionResponse> get(String sessionId);
}
