// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.creditnote.steps;

import io.github.opensri.domain.entities.common.taxes.TaxInfo;

/** Paso del constructor que define la información tributaria del emisor. */
public interface TaxInfoStep {
  /**
   * Define la información tributaria del emisor.
   *
   * @param taxInfo información tributaria del emisor
   * @return siguiente paso del constructor
   */
  DocumentNumberStep taxInfo(TaxInfo taxInfo);
}
