// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.debitnote.steps;

import io.github.opensri.domain.entities.debitnote.DebitNote;

/** Paso final del constructor de notas de débito que permite obtener la instancia configurada. */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link DebitNote}.
   *
   * @return instancia de DebitNote configurada
   */
  DebitNote build();
}
