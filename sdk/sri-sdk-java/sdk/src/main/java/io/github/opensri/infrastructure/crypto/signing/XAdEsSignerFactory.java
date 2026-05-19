// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.crypto.signing;

import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;

public final class XAdEsSignerFactory {

  public static DocumentSigner create(SigningKey signingKey) {
    return new XAdEsSigner(signingKey);
  }

  public static DocumentSigner create(
      byte[] certificate, String certificatePassword, String certificateAlias) {
    SigningKey signingKey =
        CertificateLoader.load(certificate, certificatePassword, certificateAlias);

    return new XAdEsSigner(signingKey);
  }
}
