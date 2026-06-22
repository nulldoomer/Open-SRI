// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.withholding;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;

/**
 * Fábrica de serializadores XML para comprobantes de retención.
 *
 * <p>Permite obtener una instancia configurada de {@link WithholdingReceiptXmlSerializer} sin
 * exponer su implementación directa.
 */
public class WithholdingReceiptXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private WithholdingReceiptXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de comprobantes de retención.
   *
   * @return instancia de XmlSerializer para objetos WithholdingReceipt
   */
  public static XmlSerializer<WithholdingReceipt> create() {
    return new WithholdingReceiptXmlSerializer();
  }
}
