// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.crypto.signing;

import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;

/** Factory for creating DocumentSigner instances using XAdES. */
public final class XAdEsSignerFactory {
  /** Private constructor to prevent instantiation. */
  private XAdEsSignerFactory() {}

  /**
   * Creates a DocumentSigner from an existing SigningKey.
   *
   * @param signingKey the signing key to use
   * @return a DocumentSigner that performs XAdES signing
   */
  public static DocumentSigner create(SigningKey signingKey) {
    return new XAdEsSigner(signingKey);
  }

  /**
   * Loads a signing key from the provided certificate and creates a DocumentSigner.
   *
   * @param certificate the certificate bytes (PKCS12)
   * @param certificatePassword the certificate password
   * @param certificateAlias alias of the key inside the keystore
   * @return a DocumentSigner created from the loaded signing key
   */
  public static DocumentSigner create(
      byte[] certificate, String certificatePassword, String certificateAlias) {
    SigningKey signingKey =
        CertificateLoader.load(certificate, certificatePassword, certificateAlias);

    return new XAdEsSigner(signingKey);
  }
}
