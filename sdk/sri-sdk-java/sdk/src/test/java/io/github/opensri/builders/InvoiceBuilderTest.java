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

import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvoiceBuilderTest {
    @Test
    void builder(){

        // Arrange

        Ruc rucUnderTest = new Ruc("1004456727001");
        Issuer issuerUnderTest = new Issuer(
                "Clínica",
                rucUnderTest

        );

        TaxInfo taxInfoUnderTest = new TaxInfo(
                1,
                1,
                issuerUnderTest,
                "2006201401179772773900110010010010320580103205813",
                "Calle A 8392835"
        );

        DocumentNumber documentNumberUnderTest = new DocumentNumber(
                "01",
                "001",
                "001",
                "001032058"
        );

        ClientIdentification clientIdentificationUnderTest =
                new NationalId("1004456727");

        Client  clientUnderTest = new Client(
                clientIdentificationUnderTest,
                "Won XD"
        );

        Totals totalsUnderTest = new Totals(
                new BigDecimal("115.00"), // totalValue
                new BigDecimal("100.00"), // totalTaxableValue
                new BigDecimal("15.00"),  // totalTaxValue
                BigDecimal.ZERO,          // totalExemptValue
                BigDecimal.ZERO,          // totalDiscount
                BigDecimal.ZERO,          // totalTipValue
                BigDecimal.ZERO,          // totalWithholdingValue
                List.of(
                        new TotalTax(
                                "2",
                                "2",
                                "IVA 15%",
                                new BigDecimal("15.00"),
                                new BigDecimal("100.00"),
                                new BigDecimal("15.00")
                        )
                )
        );
        InvoiceItem itemUnderTest = new InvoiceItem(
                "001",
                "AUX-001",
                "Consulta médica",
                "2",
                new BigDecimal("50.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(
                        new Tax(
                                "2",                      // código IVA
                                "2",                      // código tarifa 15%
                                new BigDecimal("15.00"),  // porcentaje
                                new BigDecimal("100.00"), // base
                                new BigDecimal("15.00")   // valor impuesto
                        )
                )
        );

        Invoice invoiceUnderTest = InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("CASA LOL")
                .taxInfo(taxInfoUnderTest)
                .documentNumber(documentNumberUnderTest)
                .client(clientUnderTest)
                .totals(totalsUnderTest)
                .addItem(itemUnderTest)
                .doneItems().build(); // ========= ACT ==============

        // Assert
        assertNotNull(invoiceUnderTest);

    }
}
