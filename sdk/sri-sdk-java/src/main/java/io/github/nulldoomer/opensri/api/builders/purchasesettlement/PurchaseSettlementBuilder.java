// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.purchasesettlement;

import io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;

/**
 * Exposes the entry point for the step-based construction of {@link PurchaseSettlement} instances.
 *
 * <p>Starts a fluent workflow that collects the mandatory settlement data in a fixed order,
 * requires at least one item, and delegates total calculation to the internal step implementation
 * before producing the final immutable entity.
 *
 * @see Steps
 */
public final class PurchaseSettlementBuilder {

  private PurchaseSettlementBuilder() {}

  /**
   * Starts the fluent builder flow for creating a purchase settlement.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
