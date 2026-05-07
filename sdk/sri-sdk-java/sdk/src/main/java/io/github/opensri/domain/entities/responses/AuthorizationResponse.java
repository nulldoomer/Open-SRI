// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agrupa la información de autorización devuelta por el SRI para un comprobante electrónico.
 *
 * <p>Incluye el estado final, los metadatos de autorización, el XML autorizado y los mensajes
 * complementarios reportados por el servicio SOAP de autorización.
 */
public record AuthorizationResponse(
    // Estado de autorización normalizado desde la respuesta SOAP del SRI.
    String status,
    // Número de autorización asignado por el SRI.
    String authorizationNumber,
    // Fecha y hora en que el SRI autorizó el comprobante.
    LocalDateTime authorizationDate,
    // Ambiente reportado por el SRI para la autorización.
    String environment,
    // XML autorizado que el SRI devuelve en la respuesta.
    String authorizedXML,
    // Mensajes adicionales emitidos durante la autorización.
    List<SRIMessage> messages) {}
