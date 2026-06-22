package io.github.nulldoomer.opensri.infrastructure.crypto.signing;

import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.nulldoomer.opensri.infrastructure.crypto.certificates.model.SigningKey;
import net.jqwik.api.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class XAdEsSignerPropertyTest {

    private final XAdEsSigner signer;

    XAdEsSignerPropertyTest() throws IOException {

        InputStream stream = getClass().getResourceAsStream("/test-firma.p12");
        byte[] p12Bytes = stream.readAllBytes();
        SigningKey signingKey = CertificateLoader.load(p12Bytes, "password",
                "SRI-Test-Firma");

        signer = new XAdEsSigner(signingKey);
    }

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 7: Para cualquier XML válido firmado con XadesSigner, el resultado
    // contiene SignedProperties y la firma es verificable con la clave pública
    // =========================================================================

    @Property(tries = 20)
    void signed_xml_always_contains_signed_properties_for_any_valid_input(
            @ForAll("anyValidXml") String xmlDocument
    ) {
        // Act
        String signed = signer.signDocument(xmlDocument);

        // Assert
        // TODO: verificar que SignedProperties está presente en cualquier XML firmado
        assertTrue(signed.contains("SignedProperties"),
                "Todo XML firmado debe contener SignedProperties (P7)");
        assertTrue(signed.contains("Signature"),
                "Todo XML firmado debe contener el elemento Signature (P7)");
        assertTrue(signed.contains("X509Certificate"),
                "Todo XML firmado debe incluir el certificado en KeyInfo (P7)");
    }

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 8: Firmar el mismo XML dos veces produce documentos con la misma
    // estructura (mismos elementos firmados, misma referencia al certificado)
    // =========================================================================

    @Property(tries = 20)
    void signing_same_xml_twice_produces_structurally_equivalent_documents(
            @ForAll("anyValidXml") String xmlDocument
    ) throws Exception {
        // Act
        String firstSigned  = signer.signDocument(xmlDocument);
        String secondSigned = signer.signDocument(xmlDocument);

        String firstCertX509 = extractX509Certificate(firstSigned);
        String secondCertX509 = extractX509Certificate(secondSigned);

        // Assert

        // Los valores de SignatureValue y SigningTime pueden diferir entre firmas,
        // pero los elementos estructurales deben ser los mismos en ambos documentos

        // Verificar que ambos tienen Signature
        assertTrue(firstSigned.contains("Signature"));
        assertTrue(secondSigned.contains("Signature"));

        // Verificar que ambos tienen SignedProperties
        assertTrue(firstSigned.contains("SignedProperties"));
        assertTrue(secondSigned.contains("SignedProperties"));

        // Verificar que ambos tienen el mismo certificado embebido
        assertEquals(firstCertX509, secondCertX509);
        assertTrue(firstSigned.contains("X509Certificate"));
        assertTrue(secondSigned.contains("X509Certificate"));

    }

    // =========================================================================
    // PROVIDERS
    // =========================================================================

    @Provide
    Arbitrary<String> anyValidXml() {
        // TODO: ampliar con más variantes de XML mínimo válido
        // Por ahora cubre los casos más representativos del dominio
        return Arbitraries.of(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<factura id=\"comprobante\" version=\"1.1.0\">" +
                        "<infoTributaria><ruc>1004456727001</ruc></infoTributaria>" +
                        "</factura>",

                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<factura id=\"comprobante\" version=\"1.1.0\">" +
                        "<infoTributaria><ruc>1004499999001</ruc></infoTributaria>" +
                        "<infoFactura><importeTotal>115.00</importeTotal></infoFactura>" +
                        "</factura>",

                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<comprobante id=\"comprobante\"><dato>valor</dato></comprobante>"
        );
    }

    public static String extractX509Certificate(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document doc = factory.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList nodeList = doc.getElementsByTagNameNS(
                "http://www.w3.org/2000/09/xmldsig#",
                "X509Certificate"
        );

        if (nodeList.getLength() == 0) {
            throw new RuntimeException("No X509Certificate node found");
        }

        return nodeList.item(0).getTextContent().trim();
    }
}