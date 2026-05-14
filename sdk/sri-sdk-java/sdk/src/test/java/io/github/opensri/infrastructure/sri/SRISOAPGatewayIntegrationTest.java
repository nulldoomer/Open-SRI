// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.infrastructure.sri;

import static org.junit.jupiter.api.Assertions.*;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
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
import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import io.github.opensri.infrastructure.serializers.InvoiceXmlSerializerFactory;
import io.github.opensri.infrastructure.services.SRIAccessKeyGeneratorFactory;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.util.List;

import io.github.opensri.infrastructure.sri.client.AuthorizationSoapClient;
import io.github.opensri.infrastructure.sri.client.SendReceiptSoapClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test de integración real con los servidores del SRI (Ambiente de Pruebas).
 *
 * <p>ADVERTENCIA: Este test requiere conexión a internet y que los servicios del SRI estén activos.
 * Utiliza un certificado de prueba que, aunque es válido para firmar, será rechazado por el SRI
 * por no ser emitido por una entidad certificadora autorizada en Ecuador. Esto es suficiente
 * para validar que la comunicación SOAP (HTTPS, Proxies, Mappers) funciona correctamente.
 */
class SRISOAPGatewayIntegrationTest {

  private SRISOAPGateway gateway;
  private XmlSerializer<Invoice> serializer;
  private DocumentSigner signer;
  private AccessKeyGenerator  accessKeyGenerator;
  private HttpClient httpClient;

  private Invoice sampleInvoice;
  private String accessKey;
  private Environment environment;
  private IssuerProfile issuerProfile;

  @BeforeEach
  void setUp() throws Exception {
    // 1. Configurar infraestructura SOAP real (Pruebas)
    httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    SendReceiptSoapClient sendReceiptSoapClient =
            new SendReceiptSoapClient(httpClient, SRIEndpoints.SRI_PRUEBAS_RECEPCION_COMPROBANTES_WSDL);
    AuthorizationSoapClient authorizationSoapClient =
            new AuthorizationSoapClient(httpClient, SRIEndpoints.SRI_PRUEBAS_AUTORIZACION_COMPROBANTES_WSDL);

    gateway = new SRISOAPGateway(sendReceiptSoapClient, authorizationSoapClient);

    // 2. Configurar Serializador y Firmador
    serializer = InvoiceXmlSerializerFactory.create();


    InputStream stream = getClass().getResourceAsStream("/test-firma.p12");
    if (stream == null) {
      throw new IllegalStateException("No se encontró el certificado de prueba /test-firma.p12");
    }
    byte[] p12Bytes = stream.readAllBytes();
    SigningKey signingKey = CertificateLoader.load(p12Bytes, "password", "SRI-Test-Firma");
    signer = XAdEsSignerFactory.create(signingKey);

    // 3. Crear data de prueba
    environment = Environment.PRUEBAS;

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
                    Currency.USD);

    accessKey = accessKeyGenerator.generate(sampleInvoice.issueDate(),sampleInvoice.documentNumber(),sampleInvoice.taxInfo(),environment);
  }

  @Test
  @DisplayName("Debe conectar con el SRI y recibir una respuesta (aunque sea de firma inválida)")
  void should_connect_to_sri_and_receive_response() throws IOException, InterruptedException {
    // 1. Serializar
    String xml =
            serializer.serialize(sampleInvoice, accessKey, environment, issuerProfile);

    // 2. Firmar
    String signedXml = signer.signDocument(xml);
    System.out.println(signedXml);

    // 3. Enviar al SRI (Real)
    ReceiptResponse response = gateway.sendDocument(signedXml);

    // 4. Verificar
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
}

