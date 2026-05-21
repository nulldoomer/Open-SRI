// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

import io.github.opensri.api.client.OpenSRIClient;

/** Paso final del constructor del cliente que permite obtener la instancia configurada. */
public interface BuildStep {
  /**
   * Construye y devuelve una nueva instancia de {@link OpenSRIClient}.
   *
   * @return instancia de OpenSRIClient configurada
   */
  OpenSRIClient build();
}
