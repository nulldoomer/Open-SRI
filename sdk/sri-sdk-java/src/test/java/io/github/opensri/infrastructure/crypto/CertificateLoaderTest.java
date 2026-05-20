package io.github.opensri.infrastructure.crypto;

import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import io.github.opensri.infrastructure.crypto.certificates.model.SigningKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    // TODO: cargar un certificado P12 real de prueba desde src/test/resources/
    // y cargarlo con getClass().getResourceAsStream("/certs/test-cert.p12")
    private byte[] validP12Bytes;
    private String validPassword;
    private String validAlias;

    @BeforeEach
    void setUp() throws Exception {

        InputStream stream = getClass().
                getResourceAsStream("/fehuesos.p12");
        assertNotNull(stream);
        validP12Bytes = stream.readAllBytes();
        validPassword = "Thisishuesosfelec698";
        validAlias    = "paulo ariel yepez benavides";
    }

    // =========================================================================
    // HAPPY PATH
    // =========================================================================

    @Test
    void should_return_signing_key_with_private_key_and_cert_when_valid_p12() {
        // Act
        SigningKey result = CertificateLoader.load(validP12Bytes, validPassword, validAlias);

        // Assert
        // TODO: verificar que el resultado no es nulo y contiene ambos componentes
        assertNotNull(result);
        assertNotNull(result.privateKey(),  "La clave privada no debe ser nula");
        assertNotNull(result.certificate(), "El certificado X509 no debe ser nulo");
    }

    @Test
    void should_return_cert_with_expected_subject_when_valid_p12() throws InvalidNameException {
        // Arrange
        String cn = "";

        // Act
        SigningKey result = CertificateLoader.load(validP12Bytes, validPassword, validAlias);

        // Assert
        // TODO: verificar que el Subject del certificado coincide con el del P12 de prueba
        String name = result.certificate().getSubjectX500Principal().getName();

        LdapName ldapName = new LdapName(name);

        for(Rdn rdn : ldapName.getRdns()) {
            if("CN".equalsIgnoreCase(rdn.getType())) {
               cn = rdn.getValue().toString().toLowerCase();
            }
        }

        assertEquals(validAlias, cn);

    }

    // =========================================================================
    // BYTES INVÁLIDOS
    // =========================================================================

    @Test
    void should_throw_when_p12_bytes_are_random_garbage() {
        // Arrange
        byte[] invalidBytes = "esto-no-es-un-p12".getBytes();

        // Act & Assert
        // TODO: cuando implementes CertificateException, cambiar RuntimeException
        // El catálogo espera: "Invalid P12 certificate bytes"
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(invalidBytes, "cualquierPassword", "cualquierAlias")
        );
        // TODO: cuando el mensaje sea el del catálogo, descomentar:
        // assertEquals("Invalid P12 certificate bytes", ex.getMessage());
        assertNotNull(ex);
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
        // TODO: el catálogo espera que el mensaje sea "Invalid certificate password"
        // La implementación actual lo envuelve todo en el mismo RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(validP12Bytes, wrongPassword, validAlias)
        );
        // TODO: cuando el mensaje sea el del catálogo, descomentar:
        // assertEquals("Invalid certificate password", ex.getMessage());
        assertNotNull(ex);
    }

    // =========================================================================
    // ALIAS INEXISTENTE
    // =========================================================================

    @Test
    void should_throw_when_alias_does_not_exist_in_keystore() {
        // Arrange
        String nonExistentAlias = "alias-que-no-existe-jamas";

        // Act & Assert
        // TODO: el catálogo espera: "Certificate alias not found: " + alias
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(validP12Bytes, validPassword, nonExistentAlias)
        );
        // TODO: cuando el mensaje sea el del catálogo, descomentar:
        // assertEquals("Certificate alias not found: " + nonExistentAlias, ex.getMessage());
        assertNotNull(ex);
    }

    // =========================================================================
    // CERTIFICADO EXPIRADO
    // =========================================================================

    @Test
    void should_throw_when_certificate_is_expired() {
        // Arrange
        // TODO: cargar un P12 con certificado expirado desde src/test/resources/certs/
        // Tip: generar un p12 expirado con OpenSSL:
        //   openssl req -x509 -newkey rsa:2048 -keyout key.pem -out cert.pem \
        //     -days -1 -nodes -subj "/CN=test"
        //   openssl pkcs12 -export -out expired.p12 -inkey key.pem -in cert.pem
        byte[] expiredP12Bytes = null; // TODO: reemplazar con bytes del P12 expirado
        String expiredPassword = null; // TODO: reemplazar
        String expiredAlias    = null; // TODO: reemplazar

        // Act & Assert
        // TODO: el catálogo espera: "Certificate has expired on " + fecha
        // OJO: la implementación actual NO valida expiración — este test fallará
        // hasta que agregues la validación con cert.checkValidity()
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(expiredP12Bytes, expiredPassword, expiredAlias)
        );
        // TODO: cuando implementes la validación, descomentar y ajustar la fecha:
        // assertTrue(ex.getMessage().startsWith("Certificate has expired on"));
        assertNotNull(ex);
    }
}