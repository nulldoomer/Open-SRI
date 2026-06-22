// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.worker.mapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.playground_service.api.dto.InvoicePayload;
import io.github.opensri.playground_service.api.dto.PlaygroundRunRequest;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceMapperTest {

  // RUC de persona natural válido (cédula 1710034065 + establecimiento 001), módulo 11.
  private static final String VALID_RUC = "1710034065001";

  private final InvoiceMapper mapper = new InvoiceMapper();

  @Test
  void mapsValidPayloadIntoInvoiceWithDerivedTotalsAndDefaultPayment() {
    Invoice invoice = mapper.toInvoice(validRequest());

    assertThat(invoice.documentVersion()).isEqualTo(DocumentVersion.VERSION_210);
    assertThat(invoice.items()).hasSize(1);
    // 2 x 10.00 = 20.00 base + 15% IVA = 23.00 total.
    assertThat(invoice.totals().totalWithoutTaxes()).isEqualByComparingTo("20.00");
    assertThat(invoice.totals().totalValue()).isEqualByComparingTo("23.00");
    // No payments provided -> a single payment covering the total is generated.
    assertThat(invoice.payments()).hasSize(1);
    assertThat(invoice.payments().get(0).total()).isEqualByComparingTo("23.00");
  }

  @Test
  void resolvesEnvironmentFromName() {
    assertThat(mapper.toEnvironment("PRUEBAS")).isEqualTo(Environment.PRUEBAS);
  }

  @Test
  void rejectsUnknownEnvironment() {
    assertThatThrownBy(() -> mapper.toEnvironment("SANDBOX"))
        .isInstanceOf(OpenSRIValidationException.class);
  }

  @Test
  void rejectsInvalidIssuerRuc() {
    PlaygroundRunRequest request = requestWithIssuerRuc("0000000000000");
    assertThatThrownBy(() -> mapper.toInvoice(request))
        .isInstanceOf(OpenSRIValidationException.class);
  }

  @Test
  void rejectsUnsupportedIdentificationType() {
    InvoicePayload payload =
        invoicePayload(new InvoicePayload.Client("DRIVER_LICENSE", "X", "Someone"));
    PlaygroundRunRequest request =
        new PlaygroundRunRequest("java", "PRUEBAS", certificate(), issuer(VALID_RUC), payload);

    assertThatThrownBy(() -> mapper.toInvoice(request))
        .isInstanceOf(OpenSRIValidationException.class);
  }

  private PlaygroundRunRequest validRequest() {
    return requestWithIssuerRuc(VALID_RUC);
  }

  private PlaygroundRunRequest requestWithIssuerRuc(String ruc) {
    InvoicePayload payload =
        invoicePayload(new InvoicePayload.Client("FINAL_CONSUMER", "9999999999999", "CONSUMIDOR"));
    return new PlaygroundRunRequest("java", "PRUEBAS", certificate(), issuer(ruc), payload);
  }

  private InvoicePayload invoicePayload(InvoicePayload.Client client) {
    InvoicePayload.Tax iva15 = new InvoicePayload.Tax(2, 4);
    InvoicePayload.Item item =
        new InvoicePayload.Item(
            "P001",
            null,
            "Producto de prueba",
            new BigDecimal("2"),
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            List.of(iva15));

    return new InvoicePayload(
        "2.1.0",
        "2026-01-15",
        "Av. Siempre Viva 123",
        "01",
        "001",
        "001",
        "000000001",
        client,
        List.of(item),
        List.of());
  }

  private PlaygroundRunRequest.Certificate certificate() {
    return new PlaygroundRunRequest.Certificate("", "password", "alias");
  }

  private PlaygroundRunRequest.Issuer issuer(String ruc) {
    return new PlaygroundRunRequest.Issuer(
        "OpenSRI Test", ruc, "Matriz 123", 1, "NO", null);
  }
}
