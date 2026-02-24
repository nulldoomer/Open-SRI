package io.github.opensri.builders;

import io.github.opensri.api.builders.client.OpenSRIClientBuilder;
import io.github.opensri.api.client.OpenSRIClient;
import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.Ruc;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OpenSRIClientBuilderTest {
    @Test
    void builder(){

        // Arrange
        Ruc issuerRuc = new Ruc("1004456727001");

        IssuerProfile profileUnderTest = new IssuerProfile(
                issuerRuc,
                null,
                AccountingObligation.SI
        );

        OpenSRIClient openSRIClientUnderTest = OpenSRIClientBuilder.builder()
                .environment(Environment.PRUEBAS)
                .certificate("fake-certificate".getBytes(StandardCharsets.UTF_8))
                .certificatePassword("LOL")
                .certificateAlias("fake-fake")
                .issuerProfile(profileUnderTest)
                .timeout(200)
                .build();  // ========= ACT ==============

        // Assert
        assertNotNull(openSRIClientUnderTest);
    }
}
