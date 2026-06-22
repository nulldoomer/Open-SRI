// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.debitnote;

import io.github.nulldoomer.opensri.api.builders.debitnote.steps.IssueDateStep;
import io.github.nulldoomer.opensri.api.builders.debitnote.steps.Steps;
import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNote;

/**
 * Exposes the entry point for the step-based construction of {@link DebitNote} instances.
 *
 * <p>Starts a fluent workflow that collects the mandatory debit-note data in a fixed order,
 * requires at least one reason, and derives the document total before producing the final immutable
 * entity.
 *
 * @see Steps
 */
public final class DebitNoteBuilder {

  private DebitNoteBuilder() {}

  /**
   * Starts the fluent builder flow for creating a debit note.
   *
   * @return first step that requires the issue date
   */
  public static IssueDateStep builder() {
    return new Steps();
  }
}
