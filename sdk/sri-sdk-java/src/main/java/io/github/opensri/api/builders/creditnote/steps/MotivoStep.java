// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.creditnote.steps;

/** Paso del constructor que define el motivo de la nota de crédito. */
public interface MotivoStep {
  /**
   * Define el motivo de la emisión de la nota de crédito.
   *
   * @param motivo motivo del documento
   * @return siguiente paso del constructor
   */
  ItemsStep motivo(String motivo);
}
