package io.github.opensri.infrastructure.serializers;

import io.github.opensri.api.builders.invoice.InvoiceBuilder;
import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalDetail;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.*;
import io.github.opensri.domain.valueobjects.ClientIdentification;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Paths;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceXmlSerializerXsdValidationTest {
    private InvoiceXmlSerializer serializer;
    private Invoice invoiceBase;
    private String accessKey;
    private Environment environment;
    private DocumentVersion version;
    private IssuerProfile issuerProfile;

    @BeforeEach
    void setUp() {
        serializer = new InvoiceXmlSerializer();
        environment = Environment.PRUEBAS;
        version = DocumentVersion.VERSION_100;
        accessKey = "1234567890123456789012345678901234567890123456789";
        Ruc ruc = new Ruc("1004456727001");
        Issuer issuer = new Issuer("Empresa Test SA", ruc);
        issuerProfile = new IssuerProfile(ruc, null, AccountingObligation.SI);
        TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
        DocumentNumber documentNumber = new DocumentNumber("01", "001", "001", "000000001");
        ClientIdentification id = new NationalId("1004456727");
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
                .client(client)
                .addItems(List.of(item))
                .addInfos(List.of(addInfo))
                .addPayments(List.of(payment))
                .build();
    }

    @Test
    void xml_should_validate_against_factura_xsd() throws Exception {
        String xml = serializer.serialize(invoiceBase, accessKey, environment, version, issuerProfile);
        assertNotNull(xml);
        // Path to the XSD file
        String xsdPath = Paths.get("", "src", "main",  "resources", "xsd", "factura_V1.0.0.xsd").toString();
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new File(xsdPath));
        Validator validator = schema.newValidator();
        try {
            validator.validate(new StreamSource(new StringReader(xml)));
        } catch (Exception e) {
            fail("XML did not validate against XSD: " + e.getMessage() + "\nXML:\n" + xml);
        }
    }
}
