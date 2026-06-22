// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.client.steps;

import io.github.nulldoomer.opensri.domain.enums.Environment;

/**
 * Paso del builder para configurar el entorno del SRI.
 *
 * <p>Permite definir si las transacciones se enviarán al entorno de pruebas (TEST) o de producción
 * (PRODUCTION) del SRI.
 */
public interface EnvironmentStep {
  /**
   * Establece el entorno del SRI.
   *
   * @param environment el entorno de destino (pruebas o producción)
   * @return el siguiente paso para configurar el certificado
   */
  CertificateStep environment(Environment environment);
}
