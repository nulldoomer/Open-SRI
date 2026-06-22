// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.debitnote.steps;

import io.github.opensri.domain.entities.common.ModifiedDocument;

/** Paso del constructor que define el comprobante modificado por la nota de débito. */
public interface ModifiedDocumentStep {
  /**
   * Define la referencia al comprobante modificado.
   *
   * @param modifiedDocument código, número y fecha del documento modificado
   * @return siguiente paso del constructor
   */
  TotalSinImpuestosStep modifiedDocument(ModifiedDocument modifiedDocument);
}
