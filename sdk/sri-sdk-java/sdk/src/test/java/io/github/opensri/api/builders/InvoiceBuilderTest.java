package io.github.opensri.api.builders;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.valueobjects.ClientIdentification;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvoiceBuilderTest {

  // =================== HELPERS =============================================

  private TaxInfo dummyTaxInfo() {
    Ruc ruc = new Ruc("1004456727001");
    Issuer issuer = new Issuer("Clínica", ruc);
    return new TaxInfo(1, issuer, "Calle A 8392835");
  }

  private DocumentNumber dummyDoc() {
    return new DocumentNumber("01", "001", "001", "001032058");
  }

  private Client dummyClient() {
    ClientIdentification id = new NationalId("1004456727");
    return new Client(id, "Won XD");
  }

  private Invoice buildInvoice(InvoiceItem first, InvoiceItem... rest) {
    List<InvoiceItem> items = new java.util.ArrayList<>();
    items.add(first);
    items.addAll(List.of(rest));

    return InvoiceBuilder.builder()
        .issueDate(IssueDate.now())
        .establishmentDirection("CASA LOL")
        .taxInfo(dummyTaxInfo())
        .documentNumber(dummyDoc())
        .client(dummyClient())
        .addItems(items)
        .build();
  }

  // ============================ TESTS ======================================

  @Test
  void should_build_invoice_and_calculate_totals_correctly() {

    // ------------------------ Arrange ------------------------------------
    InvoiceItem item =
        new InvoiceItem(
            "001",
            "AUX-001",
            "Consulta médica",
            BigDecimal.valueOf(2),
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("100.00"),
            List.of(),
            List.of(new Tax("2", "2", new BigDecimal("15.00"), new BigDecimal("100.00"))));

    // ------------------------------- Act ---------------------------------
    Invoice invoice = buildInvoice(item);

    // ---------------------------- Assert ---------------------------------
    assertNotNull(invoice);
    assertNotNull(invoice.totals());
    assertTrue(invoice.additionalInfo().isEmpty());

    assertEquals(new BigDecimal("100.00"), invoice.totals().totalWithoutTaxes());

    assertEquals(new BigDecimal("15.00"), invoice.totals().totalTaxes().getFirst().value());

    assertEquals(new BigDecimal("115.00"), invoice.totals().totalValue());
  }

  @Test
  void should_group_same_tax_and_separate_different_taxes() {

    // ------------------------ Arrange ------------------------------------
    InvoiceItem item1 =
        new InvoiceItem(
            "001",
            "AUX-001",
            "Producto A",
            BigDecimal.ONE,
            new BigDecimal("100.00"),
            BigDecimal.ZERO,
            new BigDecimal("100.00"),
            List.of(),
            List.of(new Tax("2", "2", new BigDecimal("15.00"), new BigDecimal("100.00"))));

    InvoiceItem item2 =
        new InvoiceItem(
            "002",
            "AUX-002",
            "Producto B",
            BigDecimal.ONE,
            new BigDecimal("200.00"),
            BigDecimal.ZERO,
            new BigDecimal("200.00"),
            List.of(),
            List.of(new Tax("2", "2", new BigDecimal("15.00"), new BigDecimal("200.00"))));

    InvoiceItem item3 =
        new InvoiceItem(
            "003",
            "AUX-003",
            "Producto ICE",
            BigDecimal.ONE,
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("50.00"),
            List.of(),
            List.of(new Tax("3", "0", new BigDecimal("10.00"), new BigDecimal("50.00"))));

    // ------------------------------- Act ---------------------------------
    Invoice invoice = buildInvoice(item1, item2, item3);
    Totals totals = invoice.totals();

    // ---------------------------- Assert ---------------------------------

    // Subtotal sin impuestos
    assertEquals(new BigDecimal("350.00"), totals.totalWithoutTaxes());

    // Total final: 350 + 45 (IVA) + 5 (ICE) = 400
    assertEquals(new BigDecimal("400.00"), totals.totalValue());

    // Deben agruparse en exactamente 2 tipos de impuesto
    assertEquals(2, totals.totalTaxes().size());

    // IVA agrupado: base 100 + 200 = 300, valor 15 + 30 = 45
    TotalTax iva =
        totals.totalTaxes().stream()
            .filter(t -> t.code().equals("2"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("IVA no encontrado"));

    assertEquals(new BigDecimal("300.00"), iva.taxableBase());
    assertEquals(new BigDecimal("45.00"), iva.value());

    // ICE separado: base 50, valor 50 * 10 / 100 = 5
    TotalTax ice =
        totals.totalTaxes().stream()
            .filter(t -> t.code().equals("3"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("ICE no encontrado"));

    assertEquals(new BigDecimal("50.00"), ice.taxableBase());
    assertEquals(new BigDecimal("5.00"), ice.value());
  }

  @Test
  void should_build_invoice_with_optional_additional_info() {
    InvoiceItem item =
        new InvoiceItem(
            "001",
            "AUX-001",
            "Consulta médica",
            BigDecimal.ONE,
            new BigDecimal("50.00"),
            BigDecimal.ZERO,
            new BigDecimal("50.00"),
            List.of(),
            List.of(new Tax("2", "2", new BigDecimal("15.00"), new BigDecimal("50.00"))));

    Invoice invoice =
        InvoiceBuilder.builder()
            .issueDate(IssueDate.now())
            .establishmentDirection("CASA LOL")
            .taxInfo(dummyTaxInfo())
            .documentNumber(dummyDoc())
            .client(dummyClient())
            .addItems(List.of(item))
            .addInfos(List.of(new AdditionalInfo("Impuesto ISD", "15.42x")))
            .build();

    assertEquals(1, invoice.additionalInfo().size());
    assertEquals("Impuesto ISD", invoice.additionalInfo().getFirst().name());
    assertEquals("15.42x", invoice.additionalInfo().getFirst().value());
  }
}
