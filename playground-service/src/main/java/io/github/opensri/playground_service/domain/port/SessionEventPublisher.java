// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.port;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SessionEventPublisher {
  Mono<Void> emit(PlaygroundSession session);

  Mono<Void> complete(String sessionId);

  Flux<PlaygroundSession> subscribe(String sessionId);
}
