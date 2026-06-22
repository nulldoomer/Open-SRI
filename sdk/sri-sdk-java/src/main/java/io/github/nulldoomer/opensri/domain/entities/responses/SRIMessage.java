// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.responses;

/**
 * Representa un mensaje emitido por el SRI durante la recepción o autorización de un comprobante.
 *
 * <p>Conserva el identificador, el texto principal, la información adicional y el tipo devueltos
 * por los servicios SOAP para que puedan exponerse sin depender de clases generadas por WSDL.
 *
 * @param identifier identificador único del mensaje reportado por el SRI
 * @param message descripción principal del evento, advertencia o error
 * @param AdditionalInfo información técnica complementaria enviada por el SRI
 * @param type tipo de mensaje (ERROR, ADVERTENCIA, etc.) devuelto por el servicio
 */
public record SRIMessage(
    // Código o identificador reportado por el SRI para el mensaje.
    String identifier,
    // Descripción principal del evento, advertencia o error reportado.
    String message,
    // Información complementaria enviada por el SRI cuando aplica.
    String AdditionalInfo,
    // Tipo de mensaje devuelto por el servicio SOAP.
    String type) {}
