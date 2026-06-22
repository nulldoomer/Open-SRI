// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.client.steps;

/** Paso del constructor que permite definir la contraseña del certificado. */
public interface CertificatePasswordStep {
  /**
   * Define la contraseña del certificado digital.
   *
   * @param password contraseña del certificado
   * @return siguiente paso del constructor
   */
  CertificateAliasStep certificatePassword(String password);
}
