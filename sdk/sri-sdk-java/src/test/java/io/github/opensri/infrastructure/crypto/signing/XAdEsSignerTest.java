package io.github.opensri.infrastructure.crypto.signing;

import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import io.github.opensri.shared.exceptions.SRICertificateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class XAdEsSignerTest {

    // =========================================================================
    // FIXTURES
    // =========================================================================

    private XAdEsSigner signer;

    // XML mínimo válido que el SRI esperaría firmar
    private static final String VALID_XML =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<factura id=\"comprobante\" version=\"1.0.0\">" +
                    "<infoTributaria>" +
                    "<ambiente>1</ambiente>" +
                    "<tipoEmision>1</tipoEmision>" +
                    "<razonSocial>Clinica</razonSocial>" +
                    "<ruc>1797727739001</ruc>" +
                    "<claveAcceso>2006201401179772773900110010010010320580103205813</claveAcceso>" +
                    "<codDoc>01</codDoc>" +
                    "<estab>001</estab>" +
                    "<ptoEmi>001</ptoEmi>" +
                    "<secuencial>001032058</secuencial>" +
                    "<dirMatriz>Calle A 8392835</dirMatriz>" +
                    "</infoTributaria>" +
                    "<infoFactura>" +
                    "<fechaEmision>20/06/2014</fechaEmision>" +
                    "<dirEstablecimiento>Calle A 8392835</dirEstablecimiento>" +
                    "<contribuyenteEspecial></contribuyenteEspecial>" +
                    "<obligadoContabilidad>SI</obligadoContabilidad>" +
                    "<tipoIdentificacionComprador>05</tipoIdentificacionComprador>" +
                    "<razonSocialComprador>GARCIA  RIVADENEIRA MANUEL  GONZALO</razonSocialComprador>" +
                    "<identificacionComprador>1701971408</identificacionComprador>" +
                    "<totalSinImpuestos>60.00</totalSinImpuestos>" +
                    "<totalDescuento>0.00</totalDescuento>" +
                    "<totalConImpuestos>" +
                    "<totalImpuesto>" +
                    "<codigo>2</codigo>" +
                    "<codigoPorcentaje>0</codigoPorcentaje>" +
                    "<baseImponible>60.00000</baseImponible>" +
                    "<tarifa>0.00</tarifa>" +
                    "<valor>0.00</valor>" +
                    "</totalImpuesto>" +
                    "</totalConImpuestos>" +
                    "<propina>0.00</propina>" +
                    "<importeTotal>60.00</importeTotal>" +
                    "<moneda>DOLAR</moneda>" +
                    "</infoFactura>" +
                    "<detalles>" +
                    "<detalle>" +
                    "<codigoPrincipal>EC002</codigoPrincipal>" +
                    "<descripcion>ECOGRAMA ABDOMEN SUPERIOR</descripcion>" +
                    "<cantidad>1.00</cantidad>" +
                    "<precioUnitario>60.00000</precioUnitario>" +
                    "<descuento>0.00</descuento>" +
                    "<precioTotalSinImpuesto>60.00000</precioTotalSinImpuesto>" +
                    "<impuestos>" +
                    "<impuesto>" +
                    "<codigo>2</codigo>" +
                    "<codigoPorcentaje>0</codigoPorcentaje>" +
                    "<tarifa>0</tarifa>" +
                    "<baseImponible>60.00000</baseImponible>" +
                    "<valor>0.00</valor>" +
                    "</impuesto>" +
                    "</impuestos>" +
                    "</detalle>" +
                    "</detalles>" +
                    "<infoAdicional>" +
                    "<campoAdicional nombre=\"email\">gromanec@hotmail.com</campoAdicional>" +
                    "<campoAdicional nombre=\"direccion\">Isla Seymour 144 y Gaspar de Villarroel</campoAdicional>" +
                    "<campoAdicional nombre=\"telefono\">0987840639</campoAdicional>" +
                    "</infoAdicional>" +
                    "</factura>";
    @BeforeEach
    void setUp() throws Exception {

        // Arrange
        InputStream stream = getClass().getResourceAsStream("/test-firma.p12");
        byte[] p12Bytes = stream.readAllBytes();
        SigningKey signingKey = CertificateLoader.load(p12Bytes, "password",
                "SRI-Test-Firma");

        signer = new XAdEsSigner(signingKey);

    }

    // =========================================================================
    // HAPPY PATH — XML firmado producido
    // =========================================================================

    @Test
    void should_return_non_empty_string_when_valid_xml_is_signed() {
        // Act
        String signed = signer.signDocument(VALID_XML);

        // Assert
        assertNotNull(signed);
        assertFalse(signed.isBlank());
    }

    @Test
    void should_contain_signature_element_when_xml_is_signed() {
        // Act
        String signed = signer.signDocument(VALID_XML);

        // Assert
        assertTrue(signed.contains("Signature"),
                "El XML firmado debe contener el elemento Signature");
    }

    @Test
    void should_contain_signed_properties_element_when_xml_is_signed() {
        // Act
        String signed = signer.signDocument(VALID_XML);
        System.out.println(signed);

        // Assert
        // TODO: verificar que el XML contiene SignedProperties — bloque obligatorio
        // en XAdES-BES con la referencia al certificado firmante
        assertTrue(signed.contains("SignedProperties"),
                "El XML firmado debe contener SignedProperties (requerido por XAdES-BES)");
    }

    @Test
    void should_contain_x509_certificate_in_signature_when_xml_is_signed() {
        // Act
        String signed = signer.signDocument(VALID_XML);

        // Assert
        // TODO: verificar que el bloque KeyInfo contiene el certificado X509
        // que el SRI usa para verificar la firma
        assertTrue(signed.contains("X509Certificate"),
                "La firma debe incluir el certificado X509 en KeyInfo");
    }

    @Test
    void should_preserve_original_root_element_when_xml_is_signed() {
        // Act
        String signed = signer.signDocument(VALID_XML);

        // Assert
        // TODO: verificar que el elemento raíz original sigue presente tras la firma
        // XAdES Enveloped inserta la firma DENTRO del raíz, no reemplaza el documento
        assertTrue(signed.contains("<factura"),
                "El elemento raíz original debe preservarse en el XML firmado");
    }

    // =========================================================================
    // MANEJO DE ERRORES — inputs inválidos
    // =========================================================================

    @Test
    void should_throw_SRICertificateException_when_xml_is_null() {
        // Act & Assert
        SRICertificateException ex = assertThrows(SRICertificateException.class, () ->
                signer.signDocument(null)
        );
        assertNotNull(ex);
        assertEquals("XML document cannot be null or empty", ex.getMessage());
    }

    @Test
    void should_throw_SRICertificateException_when_xml_is_empty_string() {
        // Act & Assert
        SRICertificateException ex = assertThrows(SRICertificateException.class, () ->
                signer.signDocument("")
        );
        assertNotNull(ex);
    }

    @Test
    void should_throw_SRICertificateException_when_xml_is_malformed() {
        // Arrange
        String malformedXml = "<factura><sin-cierre>";

        // Act & Assert
        // TODO: verificar que un XML mal formado lanza SRICertificateException
        // porque el parseXml() interno fallará antes de llegar a la firma
        assertThrows(SRICertificateException.class, () ->
                signer.signDocument(malformedXml)
        );
    }

    @Test
    void should_preserve_cause_when_SRICertificateException_is_thrown() {
        // Arrange
        String malformedXml = "<raiz><sin-cierre>";

        // Act
        SRICertificateException ex = assertThrows(SRICertificateException.class, () ->
                signer.signDocument(malformedXml)
        );

        // Assert
        assertNotNull(ex.getCause(),
                "SRICertificateException debe preservar la excepción original como cause");
    }

    // =========================================================================
    // HARDENING — XML con entidades externas debe ser rechazado
    // =========================================================================

    @Test
    void should_throw_when_xml_contains_external_entity_reference() {
        // Arrange
        String xxeXml =
                "<?xml version=\"1.0\"?>" +
                        "<!DOCTYPE foo [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
                        "<factura>&xxe;</factura>";

        // Act & Assert
        assertThrows(SRICertificateException.class, () ->
                signer.signDocument(xxeXml)
        );
    }
}