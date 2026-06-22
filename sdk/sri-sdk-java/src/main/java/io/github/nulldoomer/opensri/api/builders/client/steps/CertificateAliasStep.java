// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.client.steps;

/** Paso del constructor que permite definir el alias del certificado. */
public interface CertificateAliasStep {
  /**
   * Define el alias del certificado digital a utilizar.
   *
   * @param alias alias del certificado
   * @return siguiente paso del constructor
   */
  IssuerProfileStep certificateAlias(String alias);
}
