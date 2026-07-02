// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.infrastructure.sdk;

import io.github.nulldoomer.opensri.api.builders.client.OpenSRIClientBuilder;
import io.github.nulldoomer.opensri.api.client.OpenSRIClient;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.enums.AccountingObligation;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.valueobjects.Ruc;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;

@Component
class OpenSRIClientFactory {

  OpenSRIClient create(String issuerRuc) throws IOException {
    byte[] p12Bytes = loadCertificate();
    IssuerProfile issuerProfile =
        new IssuerProfile(new Ruc(issuerRuc), null, AccountingObligation.SI);

    return OpenSRIClientBuilder.builder()
        .environment(Environment.PRUEBAS)
        .certificate(p12Bytes)
        .certificatePassword("password")
        .certificateAlias("SRI-Test-Firma")
        .issuerProfile(issuerProfile)
        .timeout(30)
        .build();
  }

  private byte[] loadCertificate() throws IOException {
    try (InputStream stream = getClass().getResourceAsStream("/test-firma.p12")) {
      assert stream != null;
      return stream.readAllBytes();
    }
  }
}
