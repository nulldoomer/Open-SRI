// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.Client;

/**
 * Paso del builder para configurar la información del cliente o receptor de la factura.
 *
 * <p>Define quién recibirá el documento, incluyendo su identificación, nombre y datos de contacto.
 */
public interface ClientStep {
  /**
   * Establece el cliente de la factura.
   *
   * @param client la información del receptor del documento
   * @return el siguiente paso para configurar los ítems o detalles de la factura
   */
  ItemsStep client(Client client);
}
