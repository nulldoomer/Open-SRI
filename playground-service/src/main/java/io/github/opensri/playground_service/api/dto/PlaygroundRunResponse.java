// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

/**
 * Response for {@code POST /playground/run}.
 *
 * <p>The run is accepted asynchronously; the caller uses {@code jobId} to subscribe to the pipeline
 * trace stream at {@code GET /playground/status/{jobId}}.
 *
 * @param jobId identifier of the started job
 */
public record PlaygroundRunResponse(String jobId) {}
