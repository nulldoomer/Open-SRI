// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.invoice.steps;

import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;

/** Paso final del constructor de facturas que permite obtener la instancia configurada. */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link Invoice}.
   *
   * @return instancia de Invoice configurada
   */
  Invoice build();
}
