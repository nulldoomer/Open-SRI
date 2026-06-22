// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.invoice.steps;

import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;

/** Paso del constructor que permite definir la información tributaria. */
public interface TaxInfoStep {
  /**
   * Define la información tributaria base para el comprobante.
   *
   * @param taxInfo información tributaria
   * @return siguiente paso del constructor
   */
  DocumentNumberStep taxInfo(TaxInfo taxInfo);
}
