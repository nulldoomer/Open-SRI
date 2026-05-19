// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application.ports;

import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;

/**
 * Define la comunicación del SDK con los servicios SOAP del SRI.
 *
 * <p>Este puerto abstrae el envío del XML firmado y la consulta de autorización sin exponer al
 * resto de la aplicación los DTO generados desde el WSDL ni los detalles del cliente JAX-WS.
 */
public interface SRIGateway {

  /**
   * Envía un comprobante firmado al servicio de recepción del SRI.
   *
   * @param signedXML XML firmado listo para ser validado por el SRI
   * @return resultado de recepción transformado al modelo de dominio
   */
  ReceiptResponse sendDocument(String signedXML);

  /**
   * Consulta la autorización de un comprobante previamente recibido por el SRI.
   *
   * @param accessKey clave de acceso de 49 dígitos del comprobante
   * @return respuesta de autorización transformada al modelo de dominio
   */
  AuthorizationResponse sendAuthorization(String accessKey);
}
