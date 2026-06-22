// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.sri;

import io.github.nulldoomer.opensri.application.ports.SRIGateway;
import io.github.nulldoomer.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.nulldoomer.opensri.domain.entities.responses.ReceiptResponse;
import io.github.nulldoomer.opensri.infrastructure.sri.client.AuthorizationSoapClient;
import io.github.nulldoomer.opensri.infrastructure.sri.client.SendReceiptSoapClient;
import io.github.nulldoomer.opensri.infrastructure.sri.mappers.AuthorizationMapper;
import io.github.nulldoomer.opensri.infrastructure.sri.mappers.ReceiptMapper;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRICommunicationException;
import java.io.IOException;

/**
 * Implementación de {@link SRIGateway} que gestiona la comunicación con los servicios SOAP del SRI.
 *
 * <p>Esta clase orquesta el envío de comprobantes y la consulta de autorizaciones, delegando la
 * comunicación técnica a clientes SOAP específicos y la transformación de datos hacia el modelo de
 * dominio mediante mappers especializados.
 */
public class SRISOAPGateway implements SRIGateway {

  private final SendReceiptSoapClient sendReceiptSoapClient;
  private final AuthorizationSoapClient authorizationSoapClient;

  /**
   * Crea una instancia de la pasarela con los clientes SOAP requeridos.
   *
   * @param sendReceiptSoapClient cliente para el servicio de recepción de comprobantes
   * @param authorizationSoapClient cliente para el servicio de autorización de comprobantes
   */
  public SRISOAPGateway(
      SendReceiptSoapClient sendReceiptSoapClient,
      AuthorizationSoapClient authorizationSoapClient) {
    this.sendReceiptSoapClient = sendReceiptSoapClient;
    this.authorizationSoapClient = authorizationSoapClient;
  }

  /**
   * Envía el XML firmado al servicio de recepción y devuelve su respuesta en formato de dominio.
   *
   * @param signedXML comprobante firmado en XML
   * @return estado de recepción y mensajes informativos reportados por el SRI
   */
  @Override
  public ReceiptResponse sendDocument(String signedXML) {
    try {
      String response = sendReceiptSoapClient.sendReceipt(signedXML);
      return ReceiptMapper.toDomain(response);
    } catch (IOException e) {
      throw new OpenSRICommunicationException(
          "Error de comunicación con el servicio de recepción del SRI", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OpenSRICommunicationException("La operación de envío fue interrumpida", e);
    }
  }

  /**
   * Consulta la autorización de una clave de acceso en el servicio correspondiente del SRI.
   *
   * @param accessKey clave de acceso previamente generada para el comprobante
   * @return respuesta de autorización traducida al modelo de dominio
   */
  @Override
  public AuthorizationResponse sendAuthorization(String accessKey) {
    try {
      String response = authorizationSoapClient.authorizeDocument(accessKey);
      return AuthorizationMapper.toDomain(response);
    } catch (IOException e) {
      throw new OpenSRICommunicationException(
          "Error de comunicación con el servicio de autorización del SRI", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OpenSRICommunicationException(
          "La operación de consulta de autorización fue interrumpida", e);
    }
  }
}
