// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

public enum TimeUnit {
  DAY("dias"),
  WEEK("semanas"),
  MONTH("meses"),
  YEAR("años");

  private final String unit;

  public String getUnit() {
    return unit;
  }

  TimeUnit(String unit) {
    this.unit = unit;
  }
}
