// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.remissionguide;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.RemissionGuide;

/**
 * Fábrica de serializadores XML para guías de remisión.
 *
 * <p>Permite obtener una instancia configurada de {@link RemissionGuideXmlSerializer} sin exponer
 * su implementación directa.
 */
public class RemissionGuideXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private RemissionGuideXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de guías de remisión.
   *
   * @return instancia de XmlSerializer para objetos RemissionGuide
   */
  public static XmlSerializer<RemissionGuide> create() {
    return new RemissionGuideXmlSerializer();
  }
}
