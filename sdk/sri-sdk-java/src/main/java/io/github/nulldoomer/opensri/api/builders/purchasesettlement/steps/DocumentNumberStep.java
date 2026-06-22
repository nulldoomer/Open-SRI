// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;

/** Paso del constructor que define la numeración fiscal del comprobante. */
public interface DocumentNumberStep {
  /**
   * Define la numeración fiscal del comprobante.
   *
   * @param documentNumber código de documento, establecimiento, punto de emisión y secuencial
   * @return siguiente paso del constructor
   */
  DocumentVersionStep documentNumber(DocumentNumber documentNumber);
}
