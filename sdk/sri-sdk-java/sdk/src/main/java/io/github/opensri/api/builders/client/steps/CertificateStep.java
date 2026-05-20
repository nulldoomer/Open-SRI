// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.client.steps;

/**
 * Paso del builder para configurar el certificado digital de firma electrónica.
 *
 * <p>El certificado debe estar en formato P12 (o PFX) y es esencial para la validez legal de los
 * documentos enviados al SRI.
 */
public interface CertificateStep {
  /**
   * Establece el contenido del certificado digital.
   *
   * @param certificate los bytes del archivo de certificado (.p12 o .pfx)
   * @return el siguiente paso para configurar la contraseña del certificado
   */
  CertificatePasswordStep certificate(byte[] certificate);
}
