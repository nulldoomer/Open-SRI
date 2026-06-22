package io.github.nulldoomer.opensri.infrastructure.serializers.withholding;

import static org.junit.jupiter.api.Assertions.fail;

import io.github.nulldoomer.opensri.api.builders.withholding.WithholdingReceiptBuilder;
import io.github.nulldoomer.opensri.application.ports.DocumentSigner;
import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.withholding.SupportDocument;
import io.github.nulldoomer.opensri.domain.entities.withholding.SupportDocumentTax;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingDetail;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingTax;
import io.github.nulldoomer.opensri.domain.enums.PaymentMethod;
import io.github.nulldoomer.opensri.domain.enums.AccountingObligation;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import io.github.nulldoomer.opensri.domain.valueobjects.NationalId;
import io.github.nulldoomer.opensri.domain.valueobjects.Ruc;
import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.nulldoomer.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WithholdingReceiptXmlSerializerXsdValidationTest {

  private WithholdingReceiptXmlSerializer serializer;
  private DocumentSigner documentSigner;
  private Environment environment;
  private String accessKey;
  private IssuerProfile issuerProfile;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private Client subject;

  @BeforeEach
  void setUp() throws IOException {
    serializer = new WithholdingReceiptXmlSerializer();

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
    taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
    documentNumber = new DocumentNumber("07", "001", "001", "000000001");
    subject = new Client(new NationalId("1710034065"), "Sujeto Retenido Ejemplo");
  }

  @Test
  void withholding_receipt_v100_should_validate_against_xsd() throws Exception {
    WithholdingTax withholding =
        new WithholdingTax(
            "1",
            "303",
            new BigDecimal("100.00"),
            new BigDecimal("10.00"),
            new BigDecimal("10.00"),
            "01",
            "001001000000001",
            IssueDate.now());

    WithholdingReceipt receipt =
        WithholdingReceiptBuilder.builder()
            .issueDate(IssueDate.now())
            .establishmentDirection("Av. Siempre Viva 742")
            .taxInfo(taxInfo)
            .documentNumber(documentNumber)
            .documentVersion(DocumentVersion.VERSION_100)
            .subject(subject)
            .fiscalPeriod("01/2026")
            .addWithholdings(List.of(withholding))
            .build();

    String xml = serializer.serialize(receipt, accessKey, environment, issuerProfile);
    String signedXml = documentSigner.signDocument(xml);

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema =
        factory.newSchema(
            getClass().getResource("/xml-xsd/comprobante_retencion/ComprobanteRetencion_V1.0.0.xsd"));
    Validator validator = schema.newValidator();
    try {
      validator.validate(new StreamSource(new StringReader(signedXml)));
    } catch (Exception e) {
      fail(
          "XML did not validate against Retención V1.0.0 XSD: "
              + e.getMessage()
              + "\nXML:\n"
              + signedXml);
    }
  }

  @Test
  void withholding_receipt_v200_should_validate_against_xsd() throws Exception {
    WithholdingDetail detail =
        new WithholdingDetail(
            "1",
            "303",
            new BigDecimal("100.00"),
            new BigDecimal("10.00"),
            new BigDecimal("10.00"),
            null,
            null);
    SupportDocumentTax docTax =
        new SupportDocumentTax(
            "2",
            "4",
            new BigDecimal("100.00"),
            new BigDecimal("15.00"),
            new BigDecimal("15.00"));
    Payment payment =
        new ImmediatePayment(PaymentMethod.SIN_SISTEMA_FINANCIERO, new BigDecimal("115.00"));
    SupportDocument support =
        new SupportDocument(
            "01",
            "01",
            "001001000000001",
            IssueDate.now(),
            "01",
            new BigDecimal("100.00"),
            new BigDecimal("115.00"),
            List.of(docTax),
            List.of(detail),
            List.of(payment));

    WithholdingReceipt receipt =
        WithholdingReceiptBuilder.builder()
            .issueDate(IssueDate.now())
            .establishmentDirection("Av. Siempre Viva 742")
            .taxInfo(taxInfo)
            .documentNumber(documentNumber)
            .documentVersion(DocumentVersion.VERSION_200)
            .subject(subject)
            .fiscalPeriod("01/2026")
            .relatedParty("NO")
            .addSupportDocuments(List.of(support))
            .build();

    String xml = serializer.serialize(receipt, accessKey, environment, issuerProfile);
    String signedXml = documentSigner.signDocument(xml);

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema =
        factory.newSchema(
            getClass().getResource("/xml-xsd/comprobante_retencion/ComprobanteRetencion_V2.0.0.xsd"));
    Validator validator = schema.newValidator();
    try {
      validator.validate(new StreamSource(new StringReader(signedXml)));
    } catch (Exception e) {
      fail(
          "XML did not validate against Retención V2.0.0 XSD: "
              + e.getMessage()
              + "\nXML:\n"
              + signedXml);
    }
  }
}
