package io.github.opensri.api.builders;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.opensri.api.builders.client.OpenSRIClientBuilder;
import io.github.opensri.api.client.OpenSRIClient;
import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.Ruc;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class OpenSRIClientBuilderTest {
  @Test
  void builder() {

    // Arrange
    Ruc issuerRuc = new Ruc("1004499999001");

    IssuerProfile profileUnderTest = new IssuerProfile(issuerRuc, null, AccountingObligation.SI);

    OpenSRIClient openSRIClientUnderTest =
        OpenSRIClientBuilder.builder()
            .environment(Environment.PRUEBAS)
            .certificate("fake-certificate".getBytes(StandardCharsets.UTF_8))
            .certificatePassword("LOL")
            .certificateAlias("fake-fake")
            .issuerProfile(profileUnderTest)
            .timeout(200)
            .build(); // ========= ACT ==============

    // Assert
    assertNotNull(openSRIClientUnderTest);
  }
}
