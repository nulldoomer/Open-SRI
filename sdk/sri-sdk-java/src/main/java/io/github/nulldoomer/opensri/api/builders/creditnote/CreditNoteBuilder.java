// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.creditnote;

import io.github.nulldoomer.opensri.api.builders.creditnote.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.creditnote.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;

/**
 * Exposes the entry point for the step-based construction of {@link CreditNote} instances.
 *
 * <p>Starts a fluent workflow that collects the mandatory credit-note data in a fixed order,
 * requires at least one item, and delegates total calculation to the internal step implementation
 * before producing the final immutable entity.
 *
 * @see Steps
 */
public final class CreditNoteBuilder {

  private CreditNoteBuilder() {}

  /**
   * Starts the fluent builder flow for creating a credit note.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
