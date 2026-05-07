// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import ec.sri.autorizacion.RespuestaComprobante;
import ec.sri.recepcion.RespuestaSolicitud;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.infrastructure.sri.mappers.AuthorizationMapper;
import io.github.opensri.infrastructure.sri.mappers.ReceiptMapper;
import io.github.opensri.infrastructure.sri.proxy.AuthorizationProxy;
import io.github.opensri.infrastructure.sri.proxy.SendReceiptProxy;
import java.nio.charset.StandardCharsets;

/**
 * Implementa {@link SRIGateway} usando los web services SOAP oficiales del SRI.
 *
 * <p>Convierte el XML firmado a bytes UTF-8 para el servicio de recepción y delega la
 * transformación de las respuestas SOAP hacia el modelo de dominio mediante mappers especializados.
 */
public class SRISOAPGateway implements SRIGateway {

  private final SendReceiptProxy sendReceiptProxy;
  private final AuthorizationProxy authorizationProxy;

  /**
   * Crea un adapter SOAP con los proxies de recepción y autorización ya configurados.
   *
   * @param sendReceiptProxy cliente SOAP para {@code validarComprobante}
   * @param authorizationProxy cliente SOAP para {@code autorizacionComprobante}
   */
  public SRISOAPGateway(SendReceiptProxy sendReceiptProxy, AuthorizationProxy authorizationProxy) {
    this.sendReceiptProxy = sendReceiptProxy;
    this.authorizationProxy = authorizationProxy;
  }

  /**
   * Envía el XML firmado al servicio de recepción y devuelve su respuesta en formato de dominio.
   *
   * @param signedXML comprobante firmado en XML
   * @return estado de recepción y mensajes informativos reportados por el SRI
   */
  @Override
  public ReceiptResponse sendDocument(String signedXML) {

    byte[] bytesSignedXML = signedXML.getBytes(StandardCharsets.UTF_8);
    RespuestaSolicitud response = sendReceiptProxy.sendReceipt(bytesSignedXML);

    return ReceiptMapper.toDomain(response);
  }

  /**
   * Consulta la autorización de una clave de acceso en el servicio correspondiente del SRI.
   *
   * @param accessKey clave de acceso previamente generada para el comprobante
   * @return respuesta de autorización traducida al modelo de dominio
   */
  @Override
  public AuthorizationResponse sendAuthorization(String accessKey) {

    RespuestaComprobante response = authorizationProxy.singleAuthorize(accessKey);

    return AuthorizationMapper.toDomain(response);
  }
}
