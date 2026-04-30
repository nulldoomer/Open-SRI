package io.github.opensri.infrastructure.serializers;

import io.github.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.enums.PaymentMethod;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import net.jqwik.api.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceXmlSerializerPropertyTest {
    private final InvoiceXmlSerializer serializer = new InvoiceXmlSerializer();

    // Inputs fijos reutilizables en todos los properties
    private final String accessKey = "1234567890123456789012345678901234567890123456789";
    private final DocumentVersion version = DocumentVersion.VERSION_100;
    private final IssuerProfile issuerProfile = new IssuerProfile(
            new Ruc("1004456727001"), null, AccountingObligation.SI
    );

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 1: Para cualquier Invoice válida, el XML producido es parseable
    // y contiene infoTributaria, infoFactura y detalles
    // =========================================================================

    @Property(tries = 100)
    void xml_contains_required_elements_for_any_valid_invoice(
            @ForAll("anyValidInvoice") Invoice invoice,
            @ForAll("anyEnvironment") Environment environment
    ) {
        // Act
        String xml = serializer.serialize(invoice, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertNotNull(xml);
        assertTrue(xml.contains("infoTributaria"),
                "XML debe contener infoTributaria");
        assertTrue(xml.contains("infoFactura"),
                "XML debe contener infoFactura");
        assertTrue(xml.contains("detalles"),
                "XML debe contener detalles");
    }

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 2: Para cualquier Invoice válida, serializar → parsear → serializar
    // produce XML equivalente al original
    // =========================================================================

    @Property(tries = 50)
    void round_trip_produces_equivalent_xml_for_any_valid_invoice(
            @ForAll("anyValidInvoice") Invoice invoice,
            @ForAll("anyEnvironment") Environment environment
    ) {
        // Act
        String firstXml  = serializer.serialize(invoice, accessKey, environment,
                version, issuerProfile);
        // TODO: parsear firstXml de vuelta a FacturaXML (usando JAXB Unmarshaller)
        // y serializar de nuevo para obtener secondXml
        // String secondXml = serializer.serialize(parsed, accessKey, environment, issuerProfile);

        // Assert
        // TODO: verificar que firstXml y secondXml son equivalentes
        // (mismos elementos, mismos valores — puedes comparar strings o usar XMLUnit)
        assertNotNull(firstXml, "El primer XML no debe ser nulo");
        // TODO: descomentar cuando implementes el round-trip:
        // assertEquals(firstXml, secondXml, "El round-trip debe producir XML equivalente");
    }

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 3: Para cualquier Invoice con campo obligatorio nulo,
    // serialize() lanza XmlSerializationException
    // =========================================================================

    @Property(tries = 100)
    void throws_exception_when_invoice_has_null_mandatory_field(
            @ForAll("anyEnvironment") Environment environment
    ) {
        // Arrange
        // TODO: construir una Invoice con al menos un campo obligatorio nulo

        // Act & Assert
        // TODO: cuando implementes XmlSerializationException, cambiar RuntimeException por ella
        assertThrows(RuntimeException.class, () ->
                serializer.serialize(null, accessKey, environment,
                        version, issuerProfile)
        );
    }

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 4: Para cualquier Invoice con ítems con impuestos, totalConImpuestos
    // tiene exactamente un totalImpuesto por cada (codigo, codigoPorcentaje) único
    // =========================================================================

    @Property(tries = 100)
    void total_con_impuestos_has_one_entry_per_unique_tax_code_pair(
            @ForAll("anyValidInvoice") Invoice invoice,
            @ForAll("anyEnvironment") Environment environment
    ) {
        // Act
        String xml = serializer.serialize(invoice, accessKey, environment,
                version, issuerProfile);

        // Assert
        // TODO: contar cuántas combinaciones únicas (codigo, codigoPorcentaje) hay en
        // invoice.totals().totalTaxes() y verificar que el XML tiene el mismo número
        // de bloques <totalImpuesto>
        long uniqueTaxPairs = invoice.totals().totalTaxes().size();
        long xmlTotalImpuestoCount = xml.split("<totalImpuesto>", -1).length - 1;

        assertEquals(uniqueTaxPairs, xmlTotalImpuestoCount,
                "El número de <totalImpuesto> en el XML debe coincidir con las " +
                        "combinaciones únicas de (codigo, codigoPorcentaje) en los totales");
    }

    // =========================================================================
    // PROVIDERS
    // =========================================================================

    @Provide
    Arbitrary<Invoice> anyValidInvoice() {
        // TODO: construir un Arbitrary<Invoice> combinando generadores de cada campo
        // Tip: usar Combinators.combine() con Arbitraries para:
        //   - IssueDate.now() (fijo, no varía)
        //   - documentNumber con establishment, emissionPoint, sequentialNumber
        //   - TaxInfo con un Issuer y Ruc fijos (Ruc válido es difícil de generar aleatoriamente)
        //   - Client con NationalId fijo
        //   - Al menos 1 InvoiceItem con Tax(code, rateCode, rate, base) conocidos
        // Por ahora retorna un único ejemplo válido hasta que implementes el generador:
        return Arbitraries.just(buildSampleInvoice());
    }

    @Provide
    Arbitrary<Environment> anyEnvironment() {
        return Arbitraries.of(Environment.values());
    }

    // =========================================================================
    // HELPER — invoice de ejemplo para bootstrap del generador
    // =========================================================================

    private Invoice buildSampleInvoice() {
        Ruc ruc = new Ruc("1004456727001");
        Issuer issuer = new Issuer("Empresa Test SA", ruc);
        TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
        DocumentNumber doc = new DocumentNumber("01", "001", "001", "000000001");
        Client client = new Client(new NationalId("1004456727"), "Cliente Ejemplo");

        InvoiceItem item = new InvoiceItem(
                "P001", "A001", "Servicio",
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00")))
        );

        ImmediatePayment payment = new ImmediatePayment(
                PaymentMethod.SIN_SISTEMA_FINANCIERO,
                BigDecimal.valueOf(115)
        );

        return InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("AH")
                .taxInfo(taxInfo)
                .documentNumber(doc)
                .client(client)
                .addItems(List.of(item))
                .addPayments(List.of(payment))
                .build();
    }

}
