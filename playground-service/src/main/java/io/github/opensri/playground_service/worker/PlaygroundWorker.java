// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker;

import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.playground_service.api.dto.SdkEvent;
import org.springframework.stereotype.Component;

/**
 * Drives a single playground job: runs the selected SDK gateway and relays its events to the {@link
 * JobBus}.
 *
 * <p>Each step event is forwarded as it arrives; any failure is turned into a terminal {@code
 * error} event so the SSE subscriber always sees a clean termination instead of a stream error. The
 * job's buffer is completed in every outcome.
 */
@Component
public class PlaygroundWorker {

  private final SdkGatewayFactory gatewayFactory;
  private final JobBus jobBus;

  /**
   * Creates the worker.
   *
   * @param gatewayFactory resolves the gateway for the requested language
   * @param jobBus transport that fans events out to SSE subscribers
   */
  public PlaygroundWorker(SdkGatewayFactory gatewayFactory, JobBus jobBus) {
    this.gatewayFactory = gatewayFactory;
    this.jobBus = jobBus;
  }

  /**
   * Starts the pipeline for a job asynchronously.
   *
   * <p>Returns immediately; the gateway runs on its own scheduler and pushes events to the bus.
   *
   * @param jobId the job identifier (its bus entry must already be registered)
   * @param request the playground run request
   */
  public void start(String jobId, PlaygroundRunRequest request) {
    SdkGateway gateway = gatewayFactory.get(request.lang());

    gateway
        .run(request)
        .subscribe(
            event -> jobBus.publish(jobId, event),
            error -> {
              jobBus.publish(jobId, SdkEvent.error(reason(error)));
              jobBus.complete(jobId);
            },
            () -> jobBus.complete(jobId));
  }

  private static String reason(Throwable error) {
    return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
  }
}
