// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

/** Paso del constructor que permite definir la dirección del establecimiento. */
public interface EstablishmentDirectionStep {
  /**
   * Define la dirección física del establecimiento emisor.
   *
   * @param establishmentDirection dirección del establecimiento
   * @return siguiente paso del constructor
   */
  TaxInfoStep establishmentDirection(String establishmentDirection);
}
