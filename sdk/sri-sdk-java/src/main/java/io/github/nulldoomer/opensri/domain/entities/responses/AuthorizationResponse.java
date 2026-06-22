// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.responses;

import java.util.List;

/**
 * Agrupa la información de autorización devuelta por el SRI para un comprobante electrónico.
 *
 * <p>Incluye el estado final, los metadatos de autorización, el XML autorizado y los mensajes
 * complementarios reportados por el servicio SOAP de autorización.
 *
 * @param documentsNumber cantidad de comprobantes incluidos en la respuesta de autorización
 * @param authorization datos principales del nodo de autorización procesado por el SRI
 * @param messages mensajes adicionales emitidos por el servicio de autorización
 */
public record AuthorizationResponse(

    // Cantidad de comprobantes incluidos en la respuesta de autorización.
    String documentsNumber,

    // Datos principales del nodo de autorización procesado por el SRI.
    Authorization authorization,

    // Mensajes adicionales emitidos por el servicio de autorización.
    List<SRIMessage> messages) {}
