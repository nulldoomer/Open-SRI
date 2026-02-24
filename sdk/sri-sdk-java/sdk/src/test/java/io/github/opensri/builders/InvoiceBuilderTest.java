package io.github.opensri.builders;

import io.github.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.entities.taxinfo.TaxInfo;
import io.github.opensri.domain.valueobjects.ClientIdentification;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvoiceBuilderTest {
    @Test
    void should_build_invoice_and_calculate_totals_correctly() {

        // Arrange
        Ruc ruc = new Ruc("1004456727001");

        Issuer issuer = new Issuer(
                "Clínica",
                ruc
        );

        TaxInfo taxInfo = new TaxInfo(
                1,
                issuer,
                "2006201401179772773900110010010010320580103205813",
                "Calle A 8392835"
        );

        DocumentNumber documentNumber = new DocumentNumber(
                "01",
                "001",
                "001",
                "001032058"
        );

        ClientIdentification clientIdentification =
                new NationalId("1004456727");

        Client client = new Client(
                clientIdentification,
                "Won XD"
        );

        InvoiceItem item = new InvoiceItem(
                "001",
                "AUX-001",
                "Consulta médica",
                BigDecimal.valueOf(2),
                new BigDecimal("50.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(
                        new Tax(
                                "2",                      // código IVA
                                "2",                      // código tarifa 15%
                                new BigDecimal("15.00"),  // porcentaje
                                new BigDecimal("100.00")  // base
                        )
                )
        );

        // Act
        Invoice invoice = InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("CASA LOL")
                .taxInfo(taxInfo)
                .documentNumber(documentNumber)
                .client(client)
                .addItem(item)
                .doneItems()
                .build();

        // Assert
        assertNotNull(invoice);
        assertNotNull(invoice.totals());

        // subtotal sin impuestos
        assertEquals(new BigDecimal("100.00"),
                invoice.totals().totalWithoutTaxes());

        // impuesto calculado dinámicamente: 100 * 15 / 100 = 15
        assertEquals(new BigDecimal("15.00"),
                invoice.totals().totalTaxes().get(0).value());

        // total final: 100 + 15
        assertEquals(new BigDecimal("115.00"),
                invoice.totals().totalValue());
    }
}