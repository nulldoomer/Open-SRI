// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

/**
 * Final result of a successful pipeline run, carried by the {@code done} event.
 *
 * @param accessKey the generated 49-digit access key
 * @param receiptStatus SRI reception status (e.g. {@code RECIBIDA})
 * @param authorizationStatus SRI authorization status (e.g. {@code AUTORIZADO}), or {@code null}
 * @param authorizationNumber SRI authorization number, or {@code null}
 * @param authorizationDate SRI authorization date, or {@code null}
 * @param signedXml the signed XML that was sent to the SRI
 * @param authorizedXml the authorized XML returned by the SRI, or {@code null}
 */
public record PipelineResult(
    String accessKey,
    String receiptStatus,
    String authorizationStatus,
    String authorizationNumber,
    String authorizationDate,
    String signedXml,
    String authorizedXml) {}
