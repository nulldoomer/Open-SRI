// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.debitnote.steps;

import java.math.BigDecimal;

/** Paso del constructor que define la base imponible total antes de impuestos. */
public interface TotalSinImpuestosStep {
  /**
   * Define la base imponible total antes de impuestos.
   *
   * @param totalSinImpuestos base imponible total
   * @return siguiente paso del constructor
   */
  TaxesStep totalSinImpuestos(BigDecimal totalSinImpuestos);
}
