// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.debitnote.steps;

/** Paso del constructor que define la dirección del establecimiento emisor. */
public interface EstablishmentDirectionStep {
  /**
   * Define la dirección del establecimiento emisor.
   *
   * @param establishmentDirection dirección del establecimiento
   * @return siguiente paso del constructor
   */
  TaxInfoStep establishmentDirection(String establishmentDirection);
}
