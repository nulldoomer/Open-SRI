// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri.parsers;

import io.github.opensri.domain.entities.responses.SRIMessage;
import io.github.opensri.infrastructure.sri.utils.XmlUtils;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * Extrae mensajes del SRI desde nodos XML de respuesta SOAP.
 *
 * <p>Este parser encapsula la lectura de la estructura repetible {@code mensajes/mensaje} usada
 * tanto en recepción como en autorización y la convierte a {@link SRIMessage}.
 */
public final class SRIMessageParser {

  private SRIMessageParser() {}

  /**
   * Convierte el nodo {@code mensajes} del SRI en la colección de mensajes de dominio.
   *
   * @param mensajesElement nodo contenedor de elementos {@code mensaje}; puede ser nulo
   * @return lista de mensajes reportados por el SRI o una lista vacía si no existen
   */
  public static List<SRIMessage> parse(Element mensajesElement) {
    if (mensajesElement == null) {
      return List.of();
    }

    List<SRIMessage> messages = new ArrayList<>();

    for (Element mensaje : XmlUtils.children(mensajesElement, "mensaje")) {

      messages.add(
          new SRIMessage(
              XmlUtils.textOfFirstChild(mensaje, "identificador"),
              XmlUtils.textOfFirstChild(mensaje, "mensaje"),
              XmlUtils.textOfFirstChild(mensaje, "informacionAdicional"),
              XmlUtils.textOfFirstChild(mensaje, "tipo")));
    }
    return messages;
  }
}
