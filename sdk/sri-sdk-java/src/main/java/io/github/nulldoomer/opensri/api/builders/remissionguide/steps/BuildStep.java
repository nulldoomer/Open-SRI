// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.remissionguide.steps;

import io.github.nulldoomer.opensri.domain.entities.remissionguide.RemissionGuide;

/** Paso final del constructor de guías de remisión que permite obtener la instancia configurada. */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link RemissionGuide}.
   *
   * @return instancia de RemissionGuide configurada
   */
  RemissionGuide build();
}
