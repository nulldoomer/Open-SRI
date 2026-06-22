// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api;

import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.playground_service.api.dto.PlaygroundRunResponse;
import io.github.opensri.playground_service.api.dto.SdkEvent;
import io.github.opensri.playground_service.worker.JobBus;
import io.github.opensri.playground_service.worker.PlaygroundWorker;
import io.github.opensri.playground_service.worker.SdkGatewayFactory;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Exposes the interactive playground pipeline.
 *
 * <p>{@code POST /playground/run} accepts a job and returns immediately with a {@code jobId};
 * {@code GET /playground/status/{jobId}} streams the pipeline trace as Server-Sent Events. The
 * split keeps the request non-blocking and lets the frontend render each step live.
 */
@RestController
@RequestMapping("/playground")
public class PlaygroundController {

  private final PlaygroundWorker worker;
  private final JobBus jobBus;
  private final SdkGatewayFactory gatewayFactory;

  /**
   * Creates the controller.
   *
   * @param worker drives the pipeline for a job
   * @param jobBus transport the SSE stream subscribes to
   * @param gatewayFactory used to validate the requested language up front
   */
  public PlaygroundController(
      PlaygroundWorker worker, JobBus jobBus, SdkGatewayFactory gatewayFactory) {
    this.worker = worker;
    this.jobBus = jobBus;
    this.gatewayFactory = gatewayFactory;
  }

  /**
   * Accepts a pipeline run and starts it asynchronously.
   *
   * @param request the run request
   * @return {@code 202 Accepted} with the new job id
   */
  @PostMapping("/run")
  public Mono<ResponseEntity<PlaygroundRunResponse>> run(
      @RequestBody PlaygroundRunRequest request) {
    validate(request);

    String jobId = UUID.randomUUID().toString();
    jobBus.register(jobId);
    worker.start(jobId, request);

    return Mono.just(ResponseEntity.accepted().body(new PlaygroundRunResponse(jobId)));
  }

  /**
   * Streams the pipeline trace for a job until a terminal event.
   *
   * @param jobId the job identifier
   * @return an SSE stream of pipeline events
   */
  @GetMapping(value = "/status/{jobId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<SdkEvent>> status(@PathVariable String jobId) {
    return jobBus
        .subscribe(jobId)
        .map(event -> ServerSentEvent.builder(event).event(event.type()).build())
        .takeUntil(sse -> sse.data() != null && sse.data().isTerminal());
  }

  private void validate(PlaygroundRunRequest request) {
    if (request.invoice() == null || request.issuer() == null || request.certificate() == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "invoice, issuer and certificate are required");
    }
    if (!gatewayFactory.supports(request.lang())) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Unsupported SDK language: " + request.lang());
    }
  }
}
