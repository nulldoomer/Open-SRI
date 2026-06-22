// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.serializers.invoice;

import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.invoice.Invoice;

/**
 * Fabrica de serializadores XML para facturas.
 *
 * <p>Permite obtener una instancia configurada de {@link InvoiceXmlSerializer} sin exponer su
 * implementación directa.
 */
public class InvoiceXmlSerializerFactory {

  /** Constructor privado para evitar la instanciación de la clase de utilidad. */
  private InvoiceXmlSerializerFactory() {}

  /**
   * Crea una nueva instancia del serializador XML de facturas.
   *
   * @return instancia de XmlSerializer para objetos Invoice
   */
  public static XmlSerializer<Invoice> create() {
    return new InvoiceXmlSerializer();
  }
}
