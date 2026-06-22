// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.creditnote.steps;

import io.github.opensri.domain.valueobjects.IssueDate;

/** Paso inicial del constructor de notas de crédito que permite definir la fecha de emisión. */
public interface IssueDateStep {
  /**
   * Define la fecha de emisión del comprobante.
   *
   * @param issueDate fecha de emisión
   * @return siguiente paso del constructor
   */
  EstablishmentDirectionStep issueDate(IssueDate issueDate);
}
