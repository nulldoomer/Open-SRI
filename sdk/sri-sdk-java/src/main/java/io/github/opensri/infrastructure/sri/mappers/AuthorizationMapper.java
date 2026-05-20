// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.mappers;

import io.github.opensri.domain.entities.responses.Authorization;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.SRIMessage;
import io.github.opensri.infrastructure.sri.parsers.AuthorizationParser;
import io.github.opensri.infrastructure.sri.parsers.SRIMessageParser;
import io.github.opensri.infrastructure.sri.utils.XmlUtils;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Traduce la respuesta SOAP de autorización del SRI a la representación de dominio del SDK.
 *
 * <p>Extrae el número de comprobantes consultados, la autorización principal y los mensajes
 * asociados sin exponer al resto de la aplicación la estructura XML del servicio SOAP.
 */
public class AuthorizationMapper {

  /**
   * Convierte un envelope SOAP de autorización en {@link AuthorizationResponse}.
   *
   * @param soapXml respuesta SOAP cruda devuelta por el servicio de autorización
   * @return autorización con estado, XML autorizado y mensajes asociados
   * @throws RuntimeException si el XML no puede parsearse o la respuesta no contiene el nodo
   *     principal esperado
   */
  public static AuthorizationResponse toDomain(String soapXml) {
    try {
      Document document = XmlUtils.parse(soapXml);

      Element response =
          XmlUtils.firstElementByLocalName(document, "RespuestaAutorizacionComprobante");

      if (response == null) {
        throw new IllegalArgumentException(
            "SOAP response does not contain " + "RespuestaAutorizacionComprobante");
      }

      String documentsNumber = XmlUtils.textOfFirstChild(response, "numeroComprobantes");

      Element authorizations = XmlUtils.firstDirectChild(response, "autorizaciones");

      Element authorizationElement =
          authorizations != null ? XmlUtils.firstDirectChild(authorizations, "autorizacion") : null;

      Authorization authorization =
          authorizationElement != null
              ? AuthorizationParser.parseSingle(authorizationElement).orElse(null)
              : null;

      List<SRIMessage> messages =
          authorizationElement != null
              ? SRIMessageParser.parse(XmlUtils.firstDirectChild(authorizationElement, "mensajes"))
              : List.of();

      return new AuthorizationResponse(documentsNumber, authorization, messages);

    } catch (Exception e) {
      throw new RuntimeException("Cannot parse SOAP authorization response", e);
    }
  }
}
