// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.creditnote.steps;

import io.github.opensri.domain.entities.creditnote.CreditNote;

/** Paso final del constructor de notas de crédito que permite obtener la instancia configurada. */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link CreditNote}.
   *
   * @return instancia de CreditNote configurada
   */
  CreditNote build();
}
