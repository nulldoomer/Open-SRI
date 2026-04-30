// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.enums;

public enum Currency {
  USD("USD");

  private final String currency;

  public String getCurrency() {
    return currency;
  }

  Currency(String currency) {
    this.currency = currency;
  }
}
