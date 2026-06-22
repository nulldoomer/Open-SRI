// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.creditnote;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;

/**
 * Fábrica de serializadores XML para notas de crédito.
 *
 * <p>Permite obtener una instancia configurada de {@link CreditNoteXmlSerializer} sin exponer su
 * implementación directa.
 */
public class CreditNoteXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private CreditNoteXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de notas de crédito.
   *
   * @return instancia de XmlSerializer para objetos CreditNote
   */
  public static XmlSerializer<CreditNote> create() {
    return new CreditNoteXmlSerializer();
  }
}
