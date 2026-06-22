// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.withholding.steps;

/** Paso del constructor que define el período fiscal de las retenciones. */
public interface FiscalPeriodStep {
  /**
   * Define el período fiscal de las retenciones.
   *
   * @param fiscalPeriod período fiscal en formato {@code MM/yyyy}
   * @return siguiente paso del constructor
   */
  WithholdingsStep fiscalPeriod(String fiscalPeriod);
}
