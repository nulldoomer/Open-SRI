// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.invoice.steps;

import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;

/**
 * Defines the builder step that selects the XML version associated with the invoice.
 *
 * <p>This step makes the serialization version explicit at invoice construction time so the
 * resulting document can carry the target SRI schema version through the rest of the flow.
 */
public interface DocumentVersionStep {

  /**
   * Stores the XML version that should be associated with the invoice.
   *
   * @param documentVersion target SRI XML version for this invoice
   * @return next step that requires the buyer information
   */
  ClientStep documentVersion(DocumentVersion documentVersion);
}
