package io.github.nulldoomer.opensri.infrastructure.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.nulldoomer.opensri.application.ports.AccessKeyGenerator;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import io.github.nulldoomer.opensri.domain.valueobjects.Ruc;
import net.jqwik.api.*;

class SRIAccessKeyGeneratorPropertyTest {
  private final AccessKeyGenerator generator = new SRIAccessKeyGenerator();

  // =========================================================================
  // Feature: sri-sdk-completion
  // Property: Para cualquier combinación válida de inputs, la clave generada
  // tiene exactamente 49 dígitos numéricos.
  // (Condición necesaria para que CheckAuthorizationUseCase la acepte)
  // =========================================================================

  @Property(tries = 100)
  void generated_key_is_always_49_numeric_digits(
      @ForAll("anyEnvironment") Environment environment) {
    // Arrange
    IssueDate date = IssueDate.now();

    DocumentNumber docNumber = new DocumentNumber("01", "001", "001", "001032058");

    Ruc ruc = new Ruc("1710034065001");
    Issuer issuer = new Issuer("Clinica", ruc);
    TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle A 8392835");

    // Act
    String key = generator.generate(date, docNumber, taxInfo, environment);

    // Assert
    // TODO: verificar longitud 49 y que sean todos dígitos
    assertEquals(
        49,
        key.length(),
        "La clave debe tener 49 dígitos para cualquier Environment, pero tuvo: "
            + key.length()
            + " con ambiente "
            + environment);
    assertTrue(
        key.matches("\\d{49}"), "La clave debe ser solo dígitos numéricos, pero fue: " + key);
  }

  // =========================================================================
  // Feature: sri-sdk-completion
  // Property: Para cualquier Environment, el código de ambiente embebido en la
  // clave coincide con Environment.getCode()
  // =========================================================================

  @Property(tries = 100)
  void generated_key_always_embeds_correct_environment_code(
      @ForAll("anyEnvironment") Environment environment) {
    // Arrange
    IssueDate date = IssueDate.now();

    DocumentNumber docNumber = new DocumentNumber("01", "001", "001", "001032058");

    Ruc ruc = new Ruc("1710034065001");
    Issuer issuer = new Issuer("Clinica", ruc);
    TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle A 8392835");

    // Act
    String key = generator.generate(date, docNumber, taxInfo, environment);

    // Assert
    // TODO: extraer el dígito en la posición del ambiente (índice 23 si fecha=8 dígitos)
    // y verificar que coincide con environment.getCode()
    String embeddedEnvCode = String.valueOf(key.charAt(23));
    assertEquals(String.valueOf(environment.getCode()), embeddedEnvCode);
  }

  // =========================================================================
  // PROVIDERS
  // =========================================================================

  @Provide
  Arbitrary<Environment> anyEnvironment() {
    return Arbitraries.of(Environment.values());
  }
}
