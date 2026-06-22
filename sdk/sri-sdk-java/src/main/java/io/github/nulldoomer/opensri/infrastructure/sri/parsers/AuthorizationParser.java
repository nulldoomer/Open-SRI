// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.sri.parsers;

import io.github.nulldoomer.opensri.domain.entities.responses.Authorization;
import io.github.nulldoomer.opensri.infrastructure.sri.utils.XmlUtils;
import java.util.Optional;
import org.w3c.dom.Element;

/**
 * Reconstruye la autorización principal contenida en la respuesta SOAP del SRI.
 *
 * <p>Lee el bloque {@code autorizaciones/autorizacion} y lo transforma al record de dominio {@link
 * Authorization}, preservando solo la información relevante para el SDK.
 */
public final class AuthorizationParser {

  private AuthorizationParser() {}

  /**
   * Obtiene la primera autorización presente en el nodo XML recibido.
   *
   * @param authorizationsElement nodo contenedor que agrupa autorizaciones del SRI
   * @return autorización principal si el XML contiene el nodo esperado
   */
  public static Optional<Authorization> parseSingle(Element authorizationsElement) {
    Element authorizationElement = XmlUtils.firstDirectChild(authorizationsElement, "autorizacion");

    if (authorizationElement == null) {
      return Optional.empty();
    }

    Authorization authorization =
        new Authorization(
            XmlUtils.textOfFirstChild(authorizationElement, "estado"),
            XmlUtils.textOfFirstChild(authorizationElement, "numeroAutorizacion"),
            XmlUtils.textOfFirstChild(authorizationElement, "fechaAutorizacion"),
            XmlUtils.textOfFirstChild(authorizationElement, "ambiente"),
            XmlUtils.textOfFirstChild(authorizationElement, "comprobante"));

    return Optional.of(authorization);
  }
}
