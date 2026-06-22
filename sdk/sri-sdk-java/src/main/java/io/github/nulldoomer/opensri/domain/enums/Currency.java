// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.enums;

/**
 * Representa las monedas soportadas por el SRI en los comprobantes electrónicos.
 *
 * <p>Actualmente el SRI opera principalmente con el Dólar Estadounidense (USD).
 */
public enum Currency {
  /** Dólar estadounidense. */
  USD("USD");

  private final String currency;

  /**
   * Obtiene el código de la moneda.
   *
   * @return código ISO de la moneda
   */
  public String getCurrency() {
    return currency;
  }

  /**
   * Constructor de la moneda.
   *
   * @param currency código de la moneda
   */
  Currency(String currency) {
    this.currency = currency;
  }
}
