// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.mappers;

import ec.sri.recepcion.RespuestaSolicitud;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.entities.responses.SRIMessage;
import java.util.List;

/**
 * Traduce la respuesta SOAP de recepción del SRI al modelo de dominio del SDK.
 *
 * <p>Extrae el estado general y aplana los mensajes de los comprobantes devueltos por el servicio
 * para que el resto de la aplicación no dependa de la estructura JAXB generada desde el WSDL.
 */
public class ReceiptMapper {

  /**
   * Convierte una {@link RespuestaSolicitud} SOAP en un {@link ReceiptResponse} de dominio.
   *
   * @param respuestaSolicitud respuesta recibida desde {@code validarComprobante}
   * @return estado de recepción y mensajes relevantes del SRI
   */
  public static ReceiptResponse toDomain(RespuestaSolicitud respuestaSolicitud) {
    return new ReceiptResponse(
        respuestaSolicitud.getEstado(), mapMessages(respuestaSolicitud.getComprobantes()));
  }

  private static List<SRIMessage> mapMessages(RespuestaSolicitud.Comprobantes comprobantes) {
    if (comprobantes == null) {
      return List.of();
    }

    return comprobantes.getComprobante().stream()
        .flatMap(c -> c.getMensajes().getMensaje().stream())
        .map(
            m ->
                new SRIMessage(
                    m.getIdentificador(), m.getMensaje(), m.getInformacionAdicional(), m.getTipo()))
        .toList();
  }
}
