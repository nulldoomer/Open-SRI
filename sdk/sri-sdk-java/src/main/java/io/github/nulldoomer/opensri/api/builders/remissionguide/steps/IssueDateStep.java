// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.remissionguide.steps;

import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;

/** Paso inicial del constructor de guías de remisión que define la fecha de emisión. */
public interface IssueDateStep {
  /**
   * Define la fecha de emisión del comprobante.
   *
   * @param issueDate fecha de emisión
   * @return siguiente paso del constructor
   */
  TaxInfoStep issueDate(IssueDate issueDate);
}
