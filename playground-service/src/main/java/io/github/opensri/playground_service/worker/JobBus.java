// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker;

import io.github.opensri.playground_service.api.dto.SdkEvent;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

/**
 * In-memory transport that decouples job producers (the worker) from SSE consumers (the
 * controller).
 *
 * <p>Each job owns a replaying sink so a status subscriber that connects right after {@code
 * /playground/run} returns still receives every event from the start, including the terminal one.
 * This is intentionally the single seam that a later phase swaps for Redis Pub/Sub without touching
 * the controller or the adapter.
 */
@Component
public class JobBus {

  /** How long a completed job's buffer stays available for late subscribers. */
  private static final Duration RETENTION = Duration.ofMinutes(5);

  private final Map<String, Sinks.Many<SdkEvent>> sinks = new ConcurrentHashMap<>();

  /**
   * Registers a new job and its event buffer. Must be called before the worker starts so no early
   * events are lost.
   *
   * @param jobId the job identifier
   */
  public void register(String jobId) {
    sinks.put(jobId, Sinks.many().replay().all());
  }

  /**
   * Publishes an event to a job's stream.
   *
   * @param jobId the job identifier
   * @param event the event to emit
   */
  public void publish(String jobId, SdkEvent event) {
    Sinks.Many<SdkEvent> sink = sinks.get(jobId);
    if (sink != null) {
      sink.tryEmitNext(event);
    }
  }

  /**
   * Completes a job's stream and schedules its buffer for eviction after a retention window.
   *
   * @param jobId the job identifier
   */
  public void complete(String jobId) {
    Sinks.Many<SdkEvent> sink = sinks.get(jobId);
    if (sink == null) {
      return;
    }
    sink.tryEmitComplete();
    Schedulers.parallel()
        .schedule(
            () -> sinks.remove(jobId),
            RETENTION.toMillis(),
            java.util.concurrent.TimeUnit.MILLISECONDS);
  }

  /**
   * Subscribes to a job's event stream. Unknown jobs yield an empty, completed stream.
   *
   * @param jobId the job identifier
   * @return the stream of events for the job
   */
  public Flux<SdkEvent> subscribe(String jobId) {
    Sinks.Many<SdkEvent> sink = sinks.get(jobId);
    return sink == null ? Flux.empty() : sink.asFlux();
  }
}
