// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.responses;

import java.util.List;

/**
 * Resume el resultado de recepción de un comprobante en el SRI.
 *
 * <p>Expone el estado general de validación del XML enviado y la colección de mensajes emitidos por
 * el servicio de recepción después de procesar el comprobante.
 *
 * @param status estado general devuelto por el servicio de recepción del SRI
 * @param messages mensajes asociados al procesamiento del comprobante recibido
 */
public record ReceiptResponse(
    // Estado general devuelto por el servicio de recepción del SRI.
    String status,
    // Mensajes asociados al procesamiento del comprobante recibido.
    List<SRIMessage> messages) {}
