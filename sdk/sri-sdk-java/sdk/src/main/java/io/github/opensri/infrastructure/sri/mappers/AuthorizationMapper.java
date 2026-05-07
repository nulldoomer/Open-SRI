// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.mappers;

import ec.sri.autorizacion.Autorizacion;
import ec.sri.autorizacion.RespuestaComprobante;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.SRIMessage;

/**
 * Convierte la respuesta SOAP de autorización del SRI a la representación de dominio del SDK.
 *
 * <p>Toma la primera autorización devuelta por el servicio y normaliza sus mensajes y metadatos
 * para exponerlos sin dependencia del modelo JAXB generado.
 */
public class AuthorizationMapper {
  /**
   * Traduce una {@link RespuestaComprobante} SOAP a un {@link AuthorizationResponse} de dominio.
   *
   * @param respuestaComprobante respuesta del servicio {@code autorizacionComprobante}
   * @return autorización con estado, XML autorizado y mensajes asociados
   */
  public static AuthorizationResponse toDomain(RespuestaComprobante respuestaComprobante) {

    Autorizacion auth = respuestaComprobante.getAutorizaciones().getAutorizacion().get(0);

    return new AuthorizationResponse(
        auth.getEstado().replace(" ", "_"),
        auth.getNumeroAutorizacion(),
        auth.getFechaAutorizacion().toGregorianCalendar().toZonedDateTime().toLocalDateTime(),
        auth.getAmbiente(),
        auth.getComprobante(),
        auth.getMensajes().getMensaje().stream()
            .map(
                m ->
                    new SRIMessage(
                        m.getIdentificador(),
                        m.getMensaje(),
                        m.getInformacionAdicional() != null ? m.getInformacionAdicional() : "",
                        m.getTipo()))
            .toList());
  }
}
