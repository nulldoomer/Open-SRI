// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.infrastructure.serializers.purchasesettlement;

import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;

/**
 * Fábrica de serializadores XML para liquidaciones de compra.
 *
 * <p>Permite obtener una instancia configurada de {@link PurchaseSettlementXmlSerializer} sin
 * exponer su implementación directa.
 */
public class PurchaseSettlementXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private PurchaseSettlementXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de liquidaciones de compra.
   *
   * @return instancia de XmlSerializer para objetos PurchaseSettlement
   */
  public static XmlSerializer<PurchaseSettlement> create() {
    return new PurchaseSettlementXmlSerializer();
  }
}
