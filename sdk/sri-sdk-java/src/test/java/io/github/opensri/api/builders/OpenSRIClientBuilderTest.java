package io.github.opensri.api.builders;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.opensri.api.builders.client.OpenSRIClientBuilder;
import io.github.opensri.api.client.OpenSRIClient;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.Ruc;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class OpenSRIClientBuilderTest {
  @Test
  void builder() throws Exception {

    // Arrange
    Ruc issuerRuc = new Ruc("1710034065001");
    DocumentVersion version = DocumentVersion.VERSION_100;

    IssuerProfile profileUnderTest = new IssuerProfile(issuerRuc, null, AccountingObligation.SI);

    InputStream stream = getClass().getResourceAsStream("/test-firma.p12");
    byte[] certBytes = stream.readAllBytes();

    OpenSRIClient openSRIClientUnderTest =
        OpenSRIClientBuilder.builder()
            .environment(Environment.PRUEBAS)
            .certificate(certBytes)
            .certificatePassword("password")
            .certificateAlias("sri-test-firma")
            .issuerProfile(profileUnderTest)
            .timeout(200)
            .build(); // ========= ACT ==============

    // Assert
    assertNotNull(openSRIClientUnderTest);
  }
}
