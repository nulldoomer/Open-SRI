// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.invoice.steps;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;

/**
 * Defines the builder step that captures the invoice numbering information.
 *
 * <p>Once the document number has been provided, the flow advances to the XML version selection
 * step so the invoice carries the target SRI schema version as part of its own data.
 */
public interface DocumentNumberStep {

  /**
   * Stores the document numbering data for the invoice.
   *
   * @param documentNumber document code, establishment, emission point, and sequential number
   * @return next step that requires the XML document version
   */
  DocumentVersionStep documentNumber(DocumentNumber documentNumber);
}
