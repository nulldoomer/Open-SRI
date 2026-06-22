// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers.debitnote;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.debitnote.DebitNote;

/**
 * Fábrica de serializadores XML para notas de débito.
 *
 * <p>Permite obtener una instancia configurada de {@link DebitNoteXmlSerializer} sin exponer su
 * implementación directa.
 */
public class DebitNoteXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private DebitNoteXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de notas de débito.
   *
   * @return instancia de XmlSerializer para objetos DebitNote
   */
  public static XmlSerializer<DebitNote> create() {
    return new DebitNoteXmlSerializer();
  }
}
