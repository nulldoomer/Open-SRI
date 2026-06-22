package io.github.nulldoomer.opensri.infrastructure.serializers.debitnote;

import static org.junit.jupiter.api.Assertions.fail;

import io.github.nulldoomer.opensri.api.builders.debitnote.DebitNoteBuilder;
import io.github.nulldoomer.opensri.application.ports.DocumentSigner;
import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.ModifiedDocument;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.Issuer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNote;
import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNoteReason;
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

class DebitNoteXmlSerializerXsdValidationTest {

  private DebitNoteXmlSerializer serializer;
  private DocumentSigner documentSigner;
  private Environment environment;
  private String accessKey;
  private IssuerProfile issuerProfile;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private Client client;
  private ModifiedDocument modifiedDocument;

  @BeforeEach
  void setUp() throws IOException {
    serializer = new DebitNoteXmlSerializer();

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
    documentNumber = new DocumentNumber("05", "001", "001", "000000001");
    client = new Client(new NationalId("1710034065"), "Cliente Ejemplo");
    modifiedDocument = new ModifiedDocument("01", "001-001-000000001", IssueDate.now());
  }

  @Test
  void debit_note_v100_should_validate_against_xsd() throws Exception {
    DebitNote note =
        DebitNoteBuilder.builder()
            .issueDate(IssueDate.now())
            .establishmentDirection("Av. Siempre Viva 742")
            .taxInfo(taxInfo)
            .documentNumber(documentNumber)
            .documentVersion(DocumentVersion.VERSION_100)
            .client(client)
            .modifiedDocument(modifiedDocument)
            .totalSinImpuestos(new BigDecimal("100.00"))
            .addTaxes(
                List.of(new Tax("2", "4", new BigDecimal("15.00"), new BigDecimal("100.00"))))
            .addReasons(List.of(new DebitNoteReason("Interés por mora", new BigDecimal("100.00"))))
            .build();

    String xml = serializer.serialize(note, accessKey, environment, issuerProfile);
    String signedXml = documentSigner.signDocument(xml);

    SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    Schema schema = factory.newSchema(getClass().getResource("/xml-xsd/nota_debito/NotaDebito_V1.0.0.xsd"));
    Validator validator = schema.newValidator();
    try {
      validator.validate(new StreamSource(new StringReader(signedXml)));
    } catch (Exception e) {
      fail("XML did not validate against ND V1.0.0 XSD: " + e.getMessage() + "\nXML:\n" + signedXml);
    }
  }
}
