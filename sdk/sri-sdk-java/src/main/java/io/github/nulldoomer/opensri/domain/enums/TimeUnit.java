// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.enums;

/** Representa las unidades de tiempo permitidas por el SRI para plazos de pago. */
public enum TimeUnit {
  /** Días. */
  DAY("dias"),
  /** Semanas. */
  WEEK("semanas"),
  /** Meses. */
  MONTH("meses"),
  /** Años. */
  YEAR("años");

  private final String unit;

  /**
   * Obtiene la descripción de la unidad de tiempo en español.
   *
   * @return descripción de la unidad
   */
  public String getUnit() {
    return unit;
  }

  TimeUnit(String unit) {
    this.unit = unit;
  }
}
