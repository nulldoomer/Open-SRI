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
import io.github.opensri.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceXmlSerializerTest {

    // =========================================================================
    // FIXTURES
    // =========================================================================

    private InvoiceXmlSerializer serializer;
    private Invoice invoiceBase;
    private Invoice invoiceTwoItems;
    private Invoice invoiceTwoTaxes;
    private String accessKey;
    private Environment environment;
    private DocumentVersion version;
    private IssuerProfile issuerProfile;

    @BeforeEach
    void setUp() {
        serializer = new InvoiceXmlSerializer();
        environment = Environment.PRUEBAS;
        version = DocumentVersion.VERSION_100;
        accessKey = "1234567890123456789012345678901234567890123456789"; // 49 dígitos

        Ruc ruc = new Ruc("1004456727001");
        Issuer issuer = new Issuer("Empresa Test SA", ruc);
        issuerProfile = new IssuerProfile(ruc, null, AccountingObligation.SI);

        TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");

        DocumentNumber documentNumber = new DocumentNumber("01", "001", "001", "000000001");

        ClientIdentification id = new NationalId("1004456727");
        Client client = new Client(id, "Cliente Ejemplo");

        InvoiceItem item = new InvoiceItem(
                "PROD-001", "AUX-001", "Producto de prueba",
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00")))
        );

        InvoiceItem item2 = new InvoiceItem(
                "PROD-002", "AUX-002", "Producto de prueba 2",
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00")))
        );

        InvoiceItem item3 = new InvoiceItem(
                "PROD-002", "AUX-002", "Producto de prueba 2",
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(),
                List.of(new Tax("3", "3011", new BigDecimal("0.17"), new BigDecimal("100.00")))
        );

        List<InvoiceItem> twoItemsList = List.of(item, item2);

        Totals totals = Totals.from(List.of(item));

        ImmediatePayment payment = new ImmediatePayment(
                io.github.opensri.domain.enums.PaymentMethod.SIN_SISTEMA_FINANCIERO,
                totals.totalValue()
        );

        invoiceBase = new Invoice(
                IssueDate.now(),
                "Av. Siempre Viva 742",
                taxInfo,
                documentNumber,
                client,
                totals,
                List.of(item),
                List.of(),
                List.of(payment),
                io.github.opensri.domain.enums.Currency.USD
        );

        Totals totalsTwoItems = Totals.from(List.of(item, item2));
        ImmediatePayment paymentTwoItems = new ImmediatePayment(io.github.opensri.domain.enums.PaymentMethod.SIN_SISTEMA_FINANCIERO, totalsTwoItems.totalValue());

        invoiceTwoItems = InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("Av. Siempre Viva 742")
                .taxInfo(taxInfo)
                .documentNumber(documentNumber)
                .client(client)
                .addItems(List.of(item, item2))
                .addCurrency(io.github.opensri.domain.enums.Currency.USD)
                .addPayments(List.of(paymentTwoItems))
                .build();

        Totals totalsTwoTaxes = Totals.from(List.of(item, item3));
        ImmediatePayment paymentTwoTaxes = new ImmediatePayment(io.github.opensri.domain.enums.PaymentMethod.SIN_SISTEMA_FINANCIERO, totalsTwoTaxes.totalValue());

        invoiceTwoTaxes = InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("Av. Siempre Viva 742")
                .taxInfo(taxInfo)
                .documentNumber(documentNumber)
                .client(client)
                .addItems(List.of(item, item3))
                .addCurrency(io.github.opensri.domain.enums.Currency.USD)
                .addPayments(List.of(paymentTwoTaxes))
                .build();
    }

    // =========================================================================
    // HAPPY PATH — XML producido
    // =========================================================================

    @Test
    void should_return_non_empty_xml_when_invoice_is_valid() {
        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertNotNull(xml);
        assertFalse(xml.isBlank());
    }

    @Test
    void should_contain_infoTributaria_when_invoice_is_valid() {
        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertTrue(xml.contains("infoTributaria"),
                "El XML debe contener el bloque infoTributaria, pero fue:\n" + xml);
    }

    @Test
    void should_contain_infoFactura_when_invoice_is_valid() {
        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertTrue(xml.contains("infoFactura"),
                "El XML debe contener el bloque infoFactura, pero fue:\n" + xml);
    }

    @Test
    void should_contain_detalles_when_invoice_is_valid() {
        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertTrue(xml.contains("detalles"),
                "El XML debe contener el bloque detalles, pero fue:\n" + xml);
    }

    @Test
    void should_embed_access_key_in_xml_when_provided() {
        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertTrue(xml.contains(accessKey),
                "La clave de acceso debe estar presente en el XML");
    }

    @Test
    void should_embed_environment_code_in_xml_when_provided() {
        // Arrange
        String expectedCode = String.valueOf(environment.getCode()); // "1" para PRUEBAS

        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);
        System.out.println(xml);

        // Assert
        assertTrue(xml.contains(expectedCode),
                "El código de ambiente debe estar presente en el XML");
    }

    // =========================================================================
    // ESTRUCTURA DE DETALLES
    // =========================================================================

    @Test
    void should_contain_one_detalle_when_invoice_has_one_item() {
        // Arrange — invoiceBase ya tiene un item

        // Act
        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert
        // Tip: xml.split("<detalle>", -1).length - 1 == 1
        int count = xml.split("<detalle>", -1).length - 1;
        assertEquals(1, count,
                "Debe haber exactamente un <detalle> para una factura con un ítem");
    }

    @Test
    void should_contain_two_detalles_when_invoice_has_two_items() {
        // Arrange

        String xml = serializer.serialize(invoiceBase, accessKey, environment,
                version, issuerProfile);

        // Assert

        int count = xml.split("<detalle>", -1).length - 1;

        assertEquals(2, count,
                "Debe haber exactamente 2 <detalle>");
    }

    // =========================================================================
    // AGRUPACIÓN DE IMPUESTOS — totalConImpuestos
    // =========================================================================

    @Test
    void should_group_same_tax_codes_in_totalConImpuestos() {
        // Arrange
        // Esperado: en totalConImpuestos debe aparecer exactamente UN bloque para ese código
        String  xml = serializer.serialize(invoiceTwoItems, accessKey, environment,
                version, issuerProfile);

        // Assert

        int count = xml.split("<totalImpuesto>", -1).length - 1;

        assertEquals(1, count,
                "Se espera un bloque de agrupacion de impuestos");
    }

    @Test
    void should_separate_different_tax_codes_in_totalConImpuestos() {
        // Arrange

        String  xml = serializer.serialize(invoiceTwoItems, accessKey, environment,
                version, issuerProfile);

        // Assert
        int count = xml.split("<totalImpuesto>", -1).length - 1;
        assertEquals(2,count,
                "Deben existir 2 bloques de impuestos");
    }

    // =========================================================================
    // MANEJO DE ERRORES
    // =========================================================================

    @Test
    void should_throw_when_invoice_is_null() {
        // Act & Assert
        // NOTA DISEÑO: según el catálogo de mensajes el mensaje esperado sería
        // "XML marshalling failed" si el error viene de JAXB, pero la implementación
        // actual lanza RuntimeException. Cuando implementes XmlSerializationException
        // actualiza este assert al tipo correcto.
        assertThrows(RuntimeException.class, () ->
                serializer.serialize(null, accessKey, environment,
                        version, issuerProfile)
        );
    }

    @Test
    void should_produce_well_formed_xml_declaration_when_serializing() {
        // Act
        String  xml = serializer.serialize(invoiceTwoItems, accessKey, environment,
                version, issuerProfile);

        // Assert
        assertTrue(xml.startsWith("<?xml") || xml.contains("<factura"),
                "El XML debe comenzar con declaración XML o elemento raíz <factura>");
    }
}
