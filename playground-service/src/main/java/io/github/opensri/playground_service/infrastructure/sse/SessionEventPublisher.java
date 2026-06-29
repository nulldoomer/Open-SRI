// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sse;

import io.github.opensri.playground_service.domain.model.PlaygroundSession;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Component
public class SessionEventPublisher {

  private final ConcurrentHashMap<String, Sinks.Many<PlaygroundSession>> sinks =
      new ConcurrentHashMap<>();

  public Mono<Void> emit(PlaygroundSession session) {
    return Mono.fromRunnable(
        () -> {
          Sinks.Many<PlaygroundSession> sink =
              sinks.computeIfAbsent(
                  session.id(), k -> Sinks.many().multicast().onBackpressureBuffer());
          sink.tryEmitNext(session);
        });
  }

  public Mono<Void> complete(String sessionId) {
    return Mono.fromRunnable(
        () -> {
          Sinks.Many<PlaygroundSession> sink = sinks.get(sessionId);
          if (sink != null) {
            sink.tryEmitComplete();
            sinks.remove(sessionId);
          }
        });
  }

  public reactor.core.publisher.Flux<PlaygroundSession> subscribe(String sessionId) {
    Sinks.Many<PlaygroundSession> sink =
        sinks.computeIfAbsent(sessionId, k -> Sinks.many().multicast().onBackpressureBuffer());
    return sink.asFlux();
  }
}
