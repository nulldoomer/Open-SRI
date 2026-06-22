// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.withholding;

import io.github.nulldoomer.opensri.api.builders.withholding.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.withholding.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;

/**
 * Exposes the entry point for the step-based construction of {@link WithholdingReceipt} instances.
 *
 * <p>Starts a fluent workflow that collects the mandatory withholding-receipt data in a fixed order
 * and requires at least one withholding before producing the final immutable entity.
 *
 * @see Steps
 */
public final class WithholdingReceiptBuilder {

  private WithholdingReceiptBuilder() {}

  /**
   * Starts the fluent builder flow for creating a withholding receipt.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
