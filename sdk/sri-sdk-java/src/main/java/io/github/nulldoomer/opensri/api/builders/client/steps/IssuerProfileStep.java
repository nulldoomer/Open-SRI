// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.client.steps;

import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;

/** Paso del constructor que permite definir el perfil del emisor. */
public interface IssuerProfileStep {
  /**
   * Define el perfil tributario del emisor.
   *
   * @param issuerProfile perfil del emisor
   * @return siguiente paso del constructor
   */
  TimeoutStep issuerProfile(IssuerProfile issuerProfile);
}
