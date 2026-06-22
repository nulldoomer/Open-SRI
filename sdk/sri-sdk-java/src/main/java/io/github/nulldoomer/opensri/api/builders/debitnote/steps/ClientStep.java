// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.debitnote.steps;

import io.github.nulldoomer.opensri.domain.entities.common.Client;

/** Paso del constructor que define los datos del comprador. */
public interface ClientStep {
  /**
   * Define los datos del comprador.
   *
   * @param client identificación fiscal y nombre del comprador
   * @return siguiente paso del constructor
   */
  ModifiedDocumentStep client(Client client);
}
