package io.github.nulldoomer.opensri.infrastructure.serializers.invoice;

import io.github.nulldoomer.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.nulldoomer.opensri.application.ports.DocumentSigner;
import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.invoice.*;
import io.github.nulldoomer.opensri.domain.enums.AccountingObligation;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.enums.PaymentMethod;
import io.github.nulldoomer.opensri.domain.entities.common.*;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.*;
import io.github.nulldoomer.opensri.domain.enums.*;
import io.github.nulldoomer.opensri.domain.valueobjects.ClientIdentification;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import io.github.nulldoomer.opensri.domain.valueobjects.NationalId;
import io.github.nulldoomer.opensri.domain.valueobjects.Ruc;
import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.nulldoomer.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceXmlSerializerXsdValidationTest {
    private InvoiceXmlSerializer serializer;
    private DocumentSigner  documentSigner;
    private Invoice invoiceBase;
    private String accessKey;
    private Environment environment;
    private IssuerProfile issuerProfile;

    @BeforeEach
    void setUp() throws IOException {
        serializer = new InvoiceXmlSerializer();

        InputStream stream = getClass().getResourceAsStream("/test-firma.p12");
        if (stream == null) {
            throw new IllegalStateException("No se encontró el certificado de prueba /test-firma.p12");
        }
        byte[] p12Bytes = stream.readAllBytes();
        SigningKey signingKey = CertificateLoader.load(p12Bytes, "password", "sri-test-firma");

        documentSigner = XAdEsSignerFactory.create(signingKey);


        environment = Environment.PRUEBAS;
        accessKey = "1234567890123456789012345678901234567890123456789";
        Ruc ruc = new Ruc("1710034065001");
        Issuer issuer = new Issuer("Empresa Test SA", ruc);
        issuerProfile = new IssuerProfile(ruc, null, AccountingObligation.SI);
        TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
        DocumentNumber documentNumber = new DocumentNumber("01", "001", "001", "000000001");
        ClientIdentification id = new NationalId("1710034065");
        Client client = new Client(id, "Cliente Ejemplo");
        AdditionalDetail addDet = new AdditionalDetail(
                "a",
                "xd"
        );
        InvoiceItem item = new InvoiceItem(
                "PROD-001", "AUX-001", "Producto de prueba",
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                List.of(addDet),
                List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00")))
        );
        ImmediatePayment payment = new ImmediatePayment(
                PaymentMethod.SIN_SISTEMA_FINANCIERO,
                BigDecimal.valueOf(115)
        );

        AdditionalInfo addInfo = new AdditionalInfo(
                "lol",
                "xd"
        );
        invoiceBase = InvoiceBuilder.builder()
                .issueDate(IssueDate.now())
                .establishmentDirection("Lol")
                .taxInfo(taxInfo)
                .documentNumber(documentNumber)
                .documentVersion(DocumentVersion.VERSION_100)
                .client(client)
                .addItems(List.of(item))
                .addInfos(List.of(addInfo))
                .addPayments(List.of(payment))
                .build();

    }

    @Test
    void xml_should_validate_against_factura_xsd() throws Exception {
        String xml = serializer.serialize(invoiceBase, accessKey, environment, issuerProfile);
        String signedXml = documentSigner.signDocument(xml);

        assertNotNull(signedXml);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(getClass().getResource("/xml-xsd/facturas/factura_V1.0.0.xsd"));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(signedXml)));
        } catch (Exception e) {
            fail("XML did not validate against XSD: " + e.getMessage() + "\nXML:\n" + signedXml);
        }
    }

    @Test
    void xml_v110_with_retenciones_should_validate_against_xsd() throws Exception {
        Retention retencion = new Retention("4", "1", new BigDecimal("2.75"), new BigDecimal("2.75"));
        Invoice invoice = InvoiceBuilder.builder()
                .issueDate(invoiceBase.issueDate())
                .establishmentDirection(invoiceBase.establishmentDirection())
                .taxInfo(invoiceBase.taxInfo())
                .documentNumber(invoiceBase.documentNumber())
                .documentVersion(DocumentVersion.VERSION_110)
                .client(invoiceBase.client())
                .addItems(invoiceBase.items())
                .addRetenciones(List.of(retencion))
                .addPayments(invoiceBase.payments())
                .build();

        String xml = serializer.serialize(invoice, accessKey, environment, issuerProfile);
        String signedXml = documentSigner.signDocument(xml);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(getClass().getResource("/xml-xsd/facturas/factura_V1.1.0.xsd"));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(signedXml)));
        } catch (Exception e) {
            fail("XML did not validate against V1.1.0 XSD: " + e.getMessage() + "\nXML:\n" + signedXml);
        }
    }

    @Test
    void xml_v200_with_transport_fields_should_validate_against_xsd() throws Exception {
        Destination destino = new Destination("Venta", null, null, null);
        RemisionGuideSubstituteInfo guia = new RemisionGuideSubstituteInfo(
                "Quito", "Guayaquil", "01/01/2026", "02/01/2026",
                "Transportes SA", "04", "1710034065001", "ABC-1234",
                List.of(destino));
        OtherThirdCategory rubro = new OtherThirdCategory("Flete", new BigDecimal("5.00"));

        Invoice invoice = InvoiceBuilder.builder()
                .issueDate(invoiceBase.issueDate())
                .establishmentDirection(invoiceBase.establishmentDirection())
                .taxInfo(invoiceBase.taxInfo())
                .documentNumber(invoiceBase.documentNumber())
                .documentVersion(DocumentVersion.VERSION_200)
                .client(invoiceBase.client())
                .addItems(invoiceBase.items())
                .addInfoSustitutivaGuiaRemision(guia)
                .addOtrosRubrosTerceros(List.of(rubro))
                .addPayments(invoiceBase.payments())
                .build();

        String xml = serializer.serialize(invoice, accessKey, environment, issuerProfile);
        String signedXml = documentSigner.signDocument(xml);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(getClass().getResource("/xml-xsd/facturas/factura_V2.0.0.xsd"));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(signedXml)));
        } catch (Exception e) {
            fail("XML did not validate against V2.0.0 XSD: " + e.getMessage() + "\nXML:\n" + signedXml);
        }
    }

    @Test
    void xml_v210_with_all_optional_sections_should_validate_against_xsd() throws Exception {
        Retention retencion = new Retention("4", "1", new BigDecimal("2.75"), new BigDecimal("2.75"));
        Destination destino = new Destination("Venta", null, null, null);
        RemisionGuideSubstituteInfo guia = new RemisionGuideSubstituteInfo(
                "Quito", "Guayaquil", "01/01/2026", "02/01/2026",
                "Transportes SA", "04", "1710034065001", "ABC-1234",
                List.of(destino));
        OtherThirdCategory rubro = new OtherThirdCategory("Flete", new BigDecimal("5.00"));

        Invoice invoice = InvoiceBuilder.builder()
                .issueDate(invoiceBase.issueDate())
                .establishmentDirection(invoiceBase.establishmentDirection())
                .taxInfo(invoiceBase.taxInfo())
                .documentNumber(invoiceBase.documentNumber())
                .documentVersion(DocumentVersion.VERSION_210)
                .client(invoiceBase.client())
                .addItems(invoiceBase.items())
                .addRetenciones(List.of(retencion))
                .addInfoSustitutivaGuiaRemision(guia)
                .addOtrosRubrosTerceros(List.of(rubro))
                .addPayments(invoiceBase.payments())
                .build();

        String xml = serializer.serialize(invoice, accessKey, environment, issuerProfile);
        String signedXml = documentSigner.signDocument(xml);

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(getClass().getResource("/xml-xsd/facturas/factura_V2.1.0.xsd"));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(signedXml)));
        } catch (Exception e) {
            fail("XML did not validate against V2.1.0 XSD: " + e.getMessage() + "\nXML:\n" + signedXml);
        }
    }
}
