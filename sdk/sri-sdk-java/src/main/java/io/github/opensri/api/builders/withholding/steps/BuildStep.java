// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.withholding.steps;

import io.github.opensri.domain.entities.withholding.WithholdingReceipt;

/**
 * Paso final del constructor de comprobantes de retención que permite obtener la instancia
 * configurada.
 */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link WithholdingReceipt}.
   *
   * @return instancia de WithholdingReceipt configurada
   */
  WithholdingReceipt build();
}
