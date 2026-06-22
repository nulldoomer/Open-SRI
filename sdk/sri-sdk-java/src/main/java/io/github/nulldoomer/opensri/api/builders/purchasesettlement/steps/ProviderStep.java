// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps;

import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.Provider;

/** Paso del constructor que define los datos del proveedor. */
public interface ProviderStep {
  /**
   * Define los datos del proveedor.
   *
   * @param provider identificación fiscal, razón social y dirección del proveedor
   * @return siguiente paso del constructor
   */
  ItemsStep provider(Provider provider);
}
