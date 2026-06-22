// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.remissionguide.steps;

import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;

/** Paso del constructor que define la versión del esquema XML del comprobante. */
public interface DocumentVersionStep {
  /**
   * Define la versión del esquema XML del comprobante.
   *
   * @param documentVersion versión del documento
   * @return siguiente paso del constructor
   */
  TransportInfoStep documentVersion(DocumentVersion documentVersion);
}
