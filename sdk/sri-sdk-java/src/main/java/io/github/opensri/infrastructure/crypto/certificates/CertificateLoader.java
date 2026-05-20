// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.crypto.certificates;

import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.opensri.shared.exceptions.SRICertificateException;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Loads signing material from a PKCS#12 certificate container.
 *
 * <p>This infrastructure helper extracts the private key and X.509 certificate required to sign XML
 * documents. It converts the raw {@code .p12} bytes into a {@link SigningKey} value that can be
 * consumed by the signing layer.
 *
 * @see SigningKey
 */
public class CertificateLoader {

  /**
   * Loads the signing key pair identified by the provided alias.
   *
   * @param p12Data raw PKCS#12 certificate bytes
   * @param password password used to open the keystore and private key entry
   * @param alias alias of the certificate entry to retrieve
   * @return signing key containing the private key and X.509 certificate
   * @throws SRICertificateException if the keystore cannot be opened or the alias is missing
   */
  public static SigningKey load(byte[] p12Data, String password, String alias) {

    try {
      // Load the KeyStore type PKCS12
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(new ByteArrayInputStream(p12Data), password.toCharArray());

      // Retrieve the private key
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());

      // Retrieve the X509 certificate
      X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

      if (privateKey == null || cert == null) {
        throw new SRICertificateException(
            "No se encontró la llave o el certificado con el alias " + alias);
      }
      return new SigningKey(privateKey, cert);
    } catch (Exception e) {
      throw new SRICertificateException("Error cargando el certificado .p12", e);
    }
  }
}
