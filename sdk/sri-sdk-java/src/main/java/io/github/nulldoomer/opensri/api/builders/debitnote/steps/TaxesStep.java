// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.debitnote.steps;

import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import java.util.List;

/** Paso del constructor que define los impuestos a nivel de documento de la nota de débito. */
public interface TaxesStep {
  /**
   * Define los impuestos aplicados a nivel de documento.
   *
   * @param taxes lista de impuestos del documento
   * @return siguiente paso del constructor
   */
  ReasonsStep addTaxes(List<Tax> taxes);
}
