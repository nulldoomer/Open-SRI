// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.invoice;

import io.github.nulldoomer.opensri.api.builders.invoice.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.invoice.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;

/**
 * Exposes the entry point for the step-based construction of {@link Invoice} instances.
 *
 * <p>This builder starts a fluent workflow that collects the mandatory invoice data in a fixed
 * order, including the target XML document version, requires at least one item, and delegates total
 * calculation to the internal step implementation before producing the final immutable entity.
 *
 * @see Steps
 */
public final class InvoiceBuilder {

  private InvoiceBuilder() {}

  /**
   * Starts the fluent builder flow for creating an invoice.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
