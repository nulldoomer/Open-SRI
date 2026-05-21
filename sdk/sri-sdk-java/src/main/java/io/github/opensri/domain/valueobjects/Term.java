// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Representa el plazo de pago de una obligación.
 *
 * <p>Define el tiempo, expresado en días, con el que cuenta el receptor para liquidar el pago del
 * comprobante electrónico.
 *
 * @param termDays número de días del plazo
 */
public record Term(int termDays) {
  /**
   * Validates that the payment term is a positive number of days.
   *
   * @throws OpenSRIValidationException if {@code termDays} is zero or negative
   */
  public Term {
    if (termDays <= 0) {
      throw new OpenSRIValidationException("termDays must be greater than zero");
    }
  }
}
