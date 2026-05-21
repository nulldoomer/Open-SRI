package io.github.opensri.infrastructure.crypto.certificates;

import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.InputStream;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.*;

class CertificateLoaderTest {

    // =========================================================================
    // FIXTURES
    // =========================================================================

    private byte[] validP12Bytes;
    private String validPassword;
    private String validAlias;

    @BeforeEach
    void setUp() throws Exception {

        InputStream stream = getClass().
                getResourceAsStream("/test-firma.p12");
        assertNotNull(stream, "No se encontró el archivo /test-firma.p12");
        validP12Bytes = stream.readAllBytes();
        validPassword = "password";
        validAlias    = "sri-test-firma";
    }

    // =========================================================================
    // HAPPY PATH
    // =========================================================================

    @Test
    void should_return_signing_key_with_private_key_and_cert_when_valid_p12() {
        // Act
        SigningKey result = CertificateLoader.load(validP12Bytes, validPassword, validAlias);

        // Assert
        assertNotNull(result);
        assertNotNull(result.privateKey(),  "La clave privada no debe ser nula");
        assertNotNull(result.certificate(), "El certificado X509 no debe ser nula");
    }

    @Test
    void should_return_cert_with_expected_subject_when_valid_p12() throws InvalidNameException {
        // Arrange
        String cn = "";

        // Act
        SigningKey result = CertificateLoader.load(validP12Bytes, validPassword, validAlias);

        // Assert
        String name = result.certificate().getSubjectX500Principal().getName();

        LdapName ldapName = new LdapName(name);

        for(Rdn rdn : ldapName.getRdns()) {
            if("CN".equalsIgnoreCase(rdn.getType())) {
               cn = rdn.getValue().toString().toLowerCase();
            }
        }

        assertEquals("sri test firma", cn);

    }

    // =========================================================================
    // BYTES INVÁLIDOS
    // =========================================================================

    @Test
    void should_throw_when_p12_bytes_are_random_garbage() {
        // Arrange
        byte[] invalidBytes = "esto-no-es-un-p12".getBytes();

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(invalidBytes, "cualquierPassword", "cualquierAlias")
        );
    }

    @Test
    void should_throw_when_p12_bytes_are_empty() {
        // Arrange
        byte[] emptyBytes = new byte[0];

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(emptyBytes, validPassword, validAlias)
        );
    }

    // =========================================================================
    // CONTRASEÑA INCORRECTA
    // =========================================================================

    @Test
    void should_throw_when_password_is_wrong() {
        // Arrange
        String wrongPassword = "password-incorrecto-xd";

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(validP12Bytes, wrongPassword, validAlias)
        );
    }

    // =========================================================================
    // ALIAS INEXISTENTE
    // =========================================================================

    @Test
    void should_throw_when_alias_does_not_exist_in_keystore() {
        // Arrange
        String nonExistentAlias = "alias-que-no-existe-jamas";

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(validP12Bytes, validPassword, nonExistentAlias)
        );
    }

    // =========================================================================
    // CERTIFICADO EXPIRADO
    // =========================================================================

    @Test
    @Disabled("Certificado expirado no disponible")
    void should_throw_when_certificate_is_expired() {
        // ...
    }
}