package io.github.opensri.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.entities.responses.Authorization;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.entities.responses.SendDocumentResult;
import io.github.opensri.domain.entities.responses.SRIMessage;
import io.github.opensri.domain.enums.AccountingObligation;
import io.github.opensri.domain.enums.Currency;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.domain.enums.PaymentMethod;
import io.github.opensri.domain.valueobjects.IssueDate;
import io.github.opensri.domain.valueobjects.NationalId;
import io.github.opensri.domain.valueobjects.Ruc;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class OPENSRIApplicationEndToEndTest {

  @Test
  void sendInvoice_should_execute_complete_application_flow() {
    FakeAccessKeyGenerator accessKeyGenerator = new FakeAccessKeyGenerator();
    FakeXmlSerializer serializer = new FakeXmlSerializer();
    FakeDocumentSigner signer = new FakeDocumentSigner();
    FakeSriGateway gateway = new FakeSriGateway();

    OPENSRIApplication application =
        new OPENSRIApplication(accessKeyGenerator, signer, gateway);

    Invoice invoice = sampleInvoice();
    IssuerProfile issuerProfile =
        new IssuerProfile(new Ruc("1791248678001"), null, AccountingObligation.SI);

    SendDocumentResult result =
        application.send(invoice, serializer, Environment.PRUEBAS, issuerProfile);

    assertEquals("ACCESS-KEY-TEST", result.accessKey());
    assertEquals("<signed><invoice accessKey=\"ACCESS-KEY-TEST\"/></signed>", result.signedXml());
    assertEquals("RECIBIDA", result.response().status());

    assertEquals(invoice.issueDate(), accessKeyGenerator.issueDateSeen);
    assertEquals(invoice.documentNumber(), accessKeyGenerator.documentNumberSeen);
    assertEquals(invoice.taxInfo(), accessKeyGenerator.taxInfoSeen);
    assertEquals(Environment.PRUEBAS, accessKeyGenerator.environmentSeen);
    assertSame(invoice, serializer.invoiceSeen);
    assertSame(issuerProfile, serializer.issuerProfileSeen);
    assertEquals(Environment.PRUEBAS, serializer.environmentSeen);
    assertEquals("ACCESS-KEY-TEST", serializer.accessKeySeen);
    assertEquals("<invoice accessKey=\"ACCESS-KEY-TEST\"/>", signer.unsignedXmlSeen);
    assertEquals(result.signedXml(), gateway.signedXmlSeen);
  }

  @Test
  void checkAuthorization_should_delegate_to_gateway() {
    OPENSRIApplication application =
        new OPENSRIApplication(
            new FakeAccessKeyGenerator(), new FakeDocumentSigner(), new FakeSriGateway());

    AuthorizationResponse response = application.checkAuthorization("ACCESS-KEY-TEST");

    assertNotNull(response);
    assertEquals("1", response.documentsNumber());
    assertEquals("AUTORIZADO", response.authorization().status());
    assertTrue(response.messages().isEmpty());
  }

  private static Invoice sampleInvoice() {
    Ruc ruc = new Ruc("1791248678001");
    Issuer issuer = new Issuer("EMPRESA DE PRUEBA", ruc);
    TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
    DocumentNumber documentNumber = new DocumentNumber("01", "001", "001", "000000001");
    Client client = new Client(new NationalId("1004456727"), "CLIENTE DE PRUEBA");

    InvoiceItem item =
        new InvoiceItem(
            "P001",
            "A001",
            "SERVICIO DE PRUEBA",
            BigDecimal.ONE,
            new BigDecimal("10.00"),
            BigDecimal.ZERO,
            new BigDecimal("10.00"),
            List.of(),
            List.of(new Tax("2", "0", new BigDecimal("0.00"), new BigDecimal("10.00"))));

    Totals totals = Totals.from(List.of(item));

    return new Invoice(
        IssueDate.now(),
        "Sucursal 1",
        taxInfo,
        documentNumber,
        DocumentVersion.VERSION_100,
        client,
        totals,
        List.of(item),
        List.of(),
        List.of(new ImmediatePayment(PaymentMethod.SIN_SISTEMA_FINANCIERO, totals.totalValue())),
        Currency.USD,
        List.of(),
        null,
        List.of());
  }

  private static final class FakeAccessKeyGenerator implements AccessKeyGenerator {
    private IssueDate issueDateSeen;
    private DocumentNumber documentNumberSeen;
    private TaxInfo taxInfoSeen;
    private Environment environmentSeen;

    @Override
    public String generate(
        IssueDate date,
        DocumentNumber documentNumber,
        TaxInfo taxInfo,
        Environment environment) {
      this.issueDateSeen = date;
      this.documentNumberSeen = documentNumber;
      this.taxInfoSeen = taxInfo;
      this.environmentSeen = environment;
      return "ACCESS-KEY-TEST";
    }
  }

  private static final class FakeXmlSerializer implements XmlSerializer<Invoice> {
    private Invoice invoiceSeen;
    private String accessKeySeen;
    private Environment environmentSeen;
    private IssuerProfile issuerProfileSeen;

    @Override
    public String serialize(
        Invoice document, String accessKey, Environment environment, IssuerProfile issuerProfile) {
      this.invoiceSeen = document;
      this.accessKeySeen = accessKey;
      this.environmentSeen = environment;
      this.issuerProfileSeen = issuerProfile;
      return "<invoice accessKey=\"" + accessKey + "\"/>";
    }
  }

  private static final class FakeDocumentSigner implements DocumentSigner {
    private String unsignedXmlSeen;

    @Override
    public String signDocument(String xmlDocument) {
      this.unsignedXmlSeen = xmlDocument;
      return "<signed>" + xmlDocument + "</signed>";
    }
  }

  private static final class FakeSriGateway implements SRIGateway {
    private String signedXmlSeen;

    @Override
    public ReceiptResponse sendDocument(String signedXML) {
      this.signedXmlSeen = signedXML;
      return new ReceiptResponse(
          "RECIBIDA", List.of(new SRIMessage("0", "Comprobante recibido", null, "INFO")));
    }

    @Override
    public AuthorizationResponse sendAuthorization(String accessKey) {
      return new AuthorizationResponse(
          "1",
          new Authorization(
              "AUTORIZADO", accessKey, "2026-05-19T00:00:00", "PRUEBAS", "<xml/>"),
          List.of());
    }
  }
}
