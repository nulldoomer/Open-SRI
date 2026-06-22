// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker;

import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.playground_service.api.dto.SdkEvent;
import reactor.core.publisher.Flux;

/**
 * Strategy that runs the SRI pipeline for a given SDK language and streams its progress.
 *
 * <p>The worker depends only on this abstraction, so adding a language (e.g. an HTTP sidecar for
 * C#) means adding a new implementation and registering it in {@link SdkGatewayFactory} — the
 * worker and controller stay unchanged.
 */
public interface SdkGateway {

  /**
   * The language id this gateway handles (e.g. {@code java}).
   *
   * @return the language identifier
   */
  String language();

  /**
   * Runs the pipeline and emits one {@link SdkEvent} per step until a terminal event.
   *
   * @param request the playground run request
   * @return a cold stream of pipeline events
   */
  Flux<SdkEvent> run(PlaygroundRunRequest request);
}
