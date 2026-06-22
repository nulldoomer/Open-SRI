// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.purchasesettlement.steps;

import io.github.opensri.domain.entities.purchasesettlement.PurchaseSettlement;

/**
 * Paso final del constructor de liquidaciones de compra que permite obtener la instancia
 * configurada.
 */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link PurchaseSettlement}.
   *
   * @return instancia de PurchaseSettlement configurada
   */
  PurchaseSettlement build();
}
