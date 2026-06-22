// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.sri.mappers;

import io.github.nulldoomer.opensri.domain.entities.responses.ReceiptResponse;
import io.github.nulldoomer.opensri.domain.entities.responses.SRIMessage;
import io.github.nulldoomer.opensri.infrastructure.sri.parsers.SRIMessageParser;
import io.github.nulldoomer.opensri.infrastructure.sri.utils.XmlUtils;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Traduce la respuesta SOAP de recepción del SRI al modelo de dominio del SDK.
 *
 * <p>Extrae el estado general y los mensajes de los comprobantes devueltos por el servicio para que
 * el resto de la aplicación no dependa de la estructura SOAP ni de DTOs generados desde WSDL.
 */
public class ReceiptMapper {

  /** Private constructor to prevent instantiation of this utility class. */
  private ReceiptMapper() {
    throw new AssertionError("Utility class");
  }

  /**
   * Convierte un envelope SOAP de recepción en {@link ReceiptResponse}.
   *
   * @param soapXml respuesta SOAP cruda devuelta por el servicio {@code validarComprobante}
   * @return estado de recepción y mensajes asociados al comprobante procesado
   * @throws RuntimeException si el XML no puede parsearse o no contiene la estructura esperada
   */
  public static ReceiptResponse toDomain(String soapXml) {
    try {
      Document document = XmlUtils.parse(soapXml);

      Element receiptElement =
          XmlUtils.firstElementByLocalName(document, "RespuestaRecepcionComprobante");

      if (receiptElement == null) {
        throw new IllegalArgumentException(
            "SOAP response does not contain RespuestaRecepcionComprobante");
      }

      String status = XmlUtils.textOfFirstChild(receiptElement, "estado");
      Element mensajesElement = XmlUtils.firstElementByLocalName(document, "mensajes");
      List<SRIMessage> messages = SRIMessageParser.parse(mensajesElement);
      return new ReceiptResponse(status, messages);

    } catch (Exception e) {
      throw new RuntimeException("Cannot parse SOAP receipt response", e);
    }
  }
}
