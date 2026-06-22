package io.github.opensri.infrastructure.serializers.creditnote;

import static org.junit.jupiter.api.Assertions.fail;

import io.github.opensri.api.builders.creditnote.CreditNoteBuilder;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.ModifiedDocument;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.creditnote.CreditNote;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
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

class CreditNoteXmlSerializerXsdValidationTest {

  private CreditNoteXmlSerializer serializer;
  private DocumentSigner documentSigner;
  private Environment environment;
  private String accessKey;
  private IssuerProfile issuerProfile;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private Client client;
  private InvoiceItem item;
  private ModifiedDocument modifiedDocument;

  @BeforeEach
  void setUp() throws IOException {
    serializer = new CreditNoteXmlSerializer();

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
    documentNumber = new DocumentNumber("04", "001", "001", "000000001");
    client = new Client(new NationalId("1710034065"), "Cliente Ejemplo");
    item =
        new InvoiceItem(
            "PROD-001",
            "AUX-001",
            "Producto de prueba",
            BigDecimal.ONE,
            new BigDecimal("100.00"),
            BigDecimal.ZERO,
            new BigDecimal("100.00"),
            List.of(),
            List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00"))));
    modifiedDocument = new ModifiedDocument("01", "001-001-000000001", IssueDate.now());
  }

  private CreditNote creditNote(DocumentVersion version) {
    return CreditNoteBuilder.builder()
        .issueDate(IssueDate.now())
        .establishmentDirection("Av. Siempre Viva 742")
        .taxInfo(taxInfo)
        .documentNumber(documentNumber)
        .documentVersion(version)
        .client(client)
        .modifiedDocument(modifiedDocument)
        .motivo("Devolución de mercadería")
        .addItems(List.of(item))
        .build();
  }

  private void validateAgainst(CreditNote note, String xsdPath) throws Exception {
    String xml = serializer.serialize(note, accessKey, environment, issuerProfile);
    String signedXml = documentSigner.signDocument(xml);

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(getClass().getResource(xsdPath));
    Validator validator = schema.newValidator();
    try {
      validator.validate(new StreamSource(new StringReader(signedXml)));
    } catch (Exception e) {
      fail("XML did not validate against " + xsdPath + ": " + e.getMessage() + "\nXML:\n" + signedXml);
    }
  }

  @Test
  void credit_note_v100_should_validate_against_xsd() throws Exception {
    validateAgainst(
        creditNote(DocumentVersion.VERSION_100), "/xml-xsd/nota_credito/NotaCredito_V1.0.0.xsd");
  }

  @Test
  void credit_note_v110_should_validate_against_xsd() throws Exception {
    validateAgainst(
        creditNote(DocumentVersion.VERSION_110), "/xml-xsd/nota_credito/NotaCredito_V1.1.0.xsd");
  }
}
