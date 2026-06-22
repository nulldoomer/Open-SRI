// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.withholding.steps;

import io.github.nulldoomer.opensri.domain.entities.common.Client;

/** Paso del constructor que define el sujeto retenido. */
public interface SubjectStep {
  /**
   * Define el sujeto retenido.
   *
   * @param subject identificación fiscal y razón social del sujeto retenido
   * @return siguiente paso del constructor
   */
  FiscalPeriodStep subject(Client subject);
}
