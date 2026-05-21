// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

/**
 * Representa la respuesta de autorización detallada devuelta por el SRI.
 *
 * <p>Contiene el estado final del comprobante, el número y fecha de autorización asignados, así
 * como el XML completo que ha sido autorizado.
 *
 * @param status estado de autorización normalizado desde la respuesta SOAP del SRI
 * @param authorizationNumber número de autorización de 37 o más dígitos asignado por el SRI
 * @param authorizationDate fecha y hora oficial de autorización del comprobante
 * @param environment ambiente (PRUEBAS/PRODUCCIÓN) reportado por el SRI
 * @param authorizedXML contenido XML autorizado devuelto por el SRI
 */
public record Authorization(
    String status,
    String authorizationNumber,
    String authorizationDate,
    String environment,
    String authorizedXML) {}
