// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.issuer.Issuer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.common.payment.ImmediatePayment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.ReceiptResponse;
import io.github.opensri.domain.enums.*;
import io.github.opensri.domain.valueobjects.*;
import io.github.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import io.github.opensri.infrastructure.serializers.InvoiceXmlSerializerFactory;
import io.github.opensri.infrastructure.services.SRIAccessKeyGeneratorFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Test de integración real con los servidores del SRI (Ambiente de Pruebas).
 *
 * <p>ADVERTENCIA: este test requiere conexión a internet, un certificado PKCS#12 real y que los
 * servicios del SRI estén activos. No corre por defecto.
 *
 * <p>Variables requeridas:
 *
 * <ul>
 *   <li>OPEN_SRI_INTEGRATION_TESTS=true
 *   <li>OPEN_SRI_CERT_PATH=/ruta/certificado.p12
 *   <li>OPEN_SRI_CERT_PASSWORD=password
 *   <li>OPEN_SRI_CERT_ALIAS=alias
 * </ul>
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "OPEN_SRI_INTEGRATION_TESTS", matches = "true")
class SRISOAPGatewayIntegrationTest {

  private SRIGateway gateway;
  private XmlSerializer<Invoice> serializer;
  private DocumentSigner signer;
  private AccessKeyGenerator  accessKeyGenerator;

  private Invoice sampleInvoice;
  private String accessKey;
  private Environment environment;
  private IssuerProfile issuerProfile;

  @BeforeEach
  void setUp() throws Exception {
    String certificatePath = requiredEnv("OPEN_SRI_CERT_PATH");
    String certificatePassword = requiredEnv("OPEN_SRI_CERT_PASSWORD");
    String certificateAlias = requiredEnv("OPEN_SRI_CERT_ALIAS");

    environment = Environment.PRUEBAS;
    gateway = SRIGatewayFactory.create(environment, 30);
    serializer = InvoiceXmlSerializerFactory.create();
    signer =
        XAdEsSignerFactory.create(
            Files.readAllBytes(Path.of(certificatePath)), certificatePassword, certificateAlias);

    Ruc ruc = new Ruc("1791248678001");
    Issuer issuer = new Issuer("EMPRESA DE PRUEBA", ruc);
    issuerProfile = new IssuerProfile(ruc, null, AccountingObligation.SI);
    TaxInfo taxInfo = new TaxInfo(1, issuer, "Calle Principal 123");
    DocumentNumber docNum = new DocumentNumber("01", "001", "001", "000000001");
    Client client = new Client(new NationalId("1004456727"), "CLIENTE DE PRUEBA");

    accessKeyGenerator = SRIAccessKeyGeneratorFactory.create();

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
    ImmediatePayment payment =
            new ImmediatePayment(PaymentMethod.SIN_SISTEMA_FINANCIERO, totals.totalValue());

    sampleInvoice =
            new Invoice(
                    IssueDate.now(),
                    "Sucursal 1",
                    taxInfo,
                    docNum,
                    DocumentVersion.VERSION_100,
                    client,
                    totals,
                    List.of(item),
                    List.of(),
                    List.of(payment),
                    Currency.USD,
                    List.of(),
                    null,
                    List.of());

    accessKey =
        accessKeyGenerator.generate(
            sampleInvoice.issueDate(),
            sampleInvoice.documentNumber(),
            sampleInvoice.taxInfo(),
            environment);
  }

  @Test
  @DisplayName("Debe conectar con el SRI y recibir una respuesta (aunque sea de firma inválida)")
  void should_connect_to_sri_and_receive_response() throws IOException, InterruptedException {
    String xml =
            serializer.serialize(sampleInvoice, accessKey, environment, issuerProfile);

    String signedXml = signer.signDocument(xml);

    ReceiptResponse response = gateway.sendDocument(signedXml);

    assertNotNull(response);
    System.out.println("Integración SRI - Estado Recepción: " + response.status());
    System.out.println(response);

    // Nota: Como el certificado es de prueba, esperamos "DEVUELTA" o un error de firma.
    // Lo importante es que NO lance una excepción de conexión o SOAP.
    assertTrue(
            List.of("RECIBIDA", "DEVUELTA").contains(response.status()),
            "El SRI debería responder con un estado conocido");
  }

  @Test
  @DisplayName("Debe conectar con el servicio de autorización y consultar una clave")
  void should_connect_to_authorization_service() throws IOException, InterruptedException {
    // Intentamos autorizar la clave de prueba (obviamente no estará autorizada)
    AuthorizationResponse response = gateway.sendAuthorization("1305202601179124867800110010010000000014437512010");

    assertNotNull(response);
    System.out.println("Integración SRI - Estado Autorización: " + response.authorization());
    System.out.println(response);

    // Se espera "NO_AUTORIZADO" o "ERROR" porque la clave no existe en sus registros reales.
    assertNotNull(response.messages());
  }

  private static String requiredEnv(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing required environment variable: " + name);
    }
    return value;
  }
}
