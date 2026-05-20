// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

public record Authorization(
    // Estado de autorización normalizado desde la respuesta SOAP del SRI.
    String status,
    // Número de autorización asignado por el SRI.
    String authorizationNumber,
    // Fecha y hora en que el SRI autorizó el comprobante.
    String authorizationDate,
    // Ambiente reportado por el SRI para la autorización.
    String environment,
    // XML autorizado que el SRI devuelve en la respuesta.
    String authorizedXML) {}
