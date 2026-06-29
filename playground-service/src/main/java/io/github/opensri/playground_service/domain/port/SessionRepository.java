// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.domain.port;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import reactor.core.publisher.Mono;

public interface SessionRepository {
  Mono<Void> save(PlaygroundSession session);

  Mono<PlaygroundSession> findById(String sessionId);

  Mono<Void> delete(String sessionId);
}
