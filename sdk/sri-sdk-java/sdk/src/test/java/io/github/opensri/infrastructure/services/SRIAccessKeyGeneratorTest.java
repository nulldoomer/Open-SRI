package io.github.opensri.infrastructure.services;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Issuer;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.Ruc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SRIAccessKeyGeneratorTest {

    // =========================================================================
    // FIXTURES
    // =========================================================================

    private AccessKeyGenerator generator;

    private IssueDate issueDate;
    private DocumentNumber documentNumber;
    private TaxInfo taxInfo;

    @BeforeEach
    void setUp() {
        generator = new SRIAccessKeyGenerator();

        issueDate = IssueDate.now();

        documentNumber = new DocumentNumber(
                "01",
                "001",
                "001",
                "001032058"
        );

        Ruc ruc = new Ruc("1004456727001");
        Issuer issuer = new Issuer("Clinica", ruc);
        taxInfo = new TaxInfo(1, issuer, "Calle A 8392835");
    }

    // =========================================================================
    // HAPPY PATH
    // =========================================================================

    @Test
    void should_generate_key_of_exactly_49_chars_when_valid_inputs() {
        // Arrange
        setUp();

        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRUEBAS);

        // Assert
        assertEquals(49, key.length());
    }

    @Test
    void should_generate_all_numeric_key_when_valid_inputs() {
        // Arrange
        setUp();

        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRUEBAS);

        // Assert
        assertTrue(key.matches("\\d+"), "La clave debe contener solo dígitos, pero fue: " + key);
    }

    @Test
    void should_embed_ruc_in_key_when_valid_inputs() {
        // Arrange
        String expectedRuc = taxInfo.issuer().ruc().number();

        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRUEBAS);

        // Assert
        // Estructura: fecha(8) + codDoc(2) + ruc(13) + ...
        // El RUC debería estar en los chars [10, 23)
        assertTrue(key.contains(expectedRuc));
        assertEquals(expectedRuc, key.substring(10,23));
    }

    // =========================================================================
    // AMBIENTE
    // =========================================================================

    @Test
    void should_embed_pruebas_code_in_key_when_environment_is_pruebas() {
        // Arrange
        // Environment.PRUEBAS tiene código 1

        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRUEBAS);

        // Assert
        // Estructura: fecha(8) + codDoc(2) + ruc(13) = posición 23
        String environmentCode = String.valueOf(key.charAt(23));
        assertEquals("1", environmentCode);
    }

    @Test
    void should_embed_produccion_code_in_key_when_environment_is_produccion() {
        // Arrange
        // Environment.PRODUCCION tiene código 2

        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRODUCCION);

        // Assert
        String environmentCode = String.valueOf(key.charAt(23));
        assertEquals("2", environmentCode);
    }

    // =========================================================================
    // DÍGITO VERIFICADOR
    // =========================================================================

    @Test
    void should_generate_key_with_valid_modulo11_verifier_digit() {
        // Act
        String key = generator.generate(issueDate, documentNumber, taxInfo, Environment.PRUEBAS);

        // Arrange — extraer la clave sin el último dígito
        String keyWithoutVerifier = key.substring(0, 48);
        char verifierChar = key.charAt(48);

        // Assert
        // Recalcular el módulo 11 sobre keyWithoutVerifier y comparar
        // con el último dígito de la clave generada.
        int expectedVerifier = calculateModulo11(keyWithoutVerifier);
        assertEquals(String.valueOf(expectedVerifier), String.valueOf(verifierChar));
    }


    // =========================================================================
    // HELPER
    // =========================================================================

    private int calculateModulo11(String key) {
        int sum = 0;
        int factor = 7;

        String[] digits = key.split("");

        for( String digit : digits){
            sum += Integer.parseInt(digit) * factor;

            factor = factor -1;

            if(factor == 1)
                factor = 7;
        }

        int verifierDigit = sum % 11;

        verifierDigit = 11- verifierDigit;

        if(verifierDigit == 11)
            verifierDigit = 0;

        if(verifierDigit == 10)
            verifierDigit = 1;

        return verifierDigit;
    }
}
