package io.github.opensri.infrastructure.crypto;

import io.github.opensri.infrastructure.crypto.certificates.CertificateLoader;
import net.jqwik.api.*;

import static org.junit.jupiter.api.Assertions.*;

class CertificateLoaderPropertyTest {

    // =========================================================================
    // Feature: sri-sdk-completion
    // Property 6: Para cualquier byte[] que no sea P12 válido,
    // CertificateLoader.load() lanza CertificateException
    // =========================================================================

    @Property(tries = 100)
    void throws_exception_for_any_non_p12_bytes(
            @ForAll("invalidP12Bytes") byte[] bytes
    ) {
        // Arrange — bytes aleatorios casi nunca forman un P12 válido
        String anyPassword = "password";
        String anyAlias    = "alias";

        // Act & Assert
        // TODO: cuando implementes CertificateException, cambiar RuntimeException
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(bytes, anyPassword, anyAlias)
        );
    }

    @Property(tries = 50)
    void throws_exception_for_any_wrong_password_on_fixed_valid_p12(
            @ForAll("wrongPasswords") String wrongPassword
    ) {
        // Arrange
        // TODO: cargar el P12 válido de test resources igual que en CertificateLoaderTest
        byte[] validP12Bytes = null; // TODO: reemplazar con bytes del P12 de prueba
        String validAlias    = null; // TODO: reemplazar

        // Act & Assert
        // TODO: verificar que CUALQUIER contraseña incorrecta lanza excepción
        assertThrows(RuntimeException.class, () ->
                CertificateLoader.load(validP12Bytes, wrongPassword, validAlias)
        );
    }

    // =========================================================================
    // PROVIDERS
    // =========================================================================

    @Provide
    Arbitrary<byte[]> invalidP12Bytes() {
        // Los bytes aleatorios nunca son P12 válidos — no hace falta filtrar
        return Arbitraries.bytes()
                .array(byte[].class)
                .ofMinSize(1)
                .ofMaxSize(200);
    }

    @Provide
    Arbitrary<String> wrongPasswords() {
        // TODO: asegurarse de que la contraseña real del P12 de prueba no aparezca aquí
        // Tip: usar .filter(p -> !p.equals(VALID_PASSWORD))
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(1)
                .ofMaxLength(30)
                .filter(p -> !p.equals("TODO-password-real-del-p12"));
    }
}