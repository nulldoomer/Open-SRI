package io.github.opensri.domain.entities.common;

/**
 * Agrupa la información tributaria reutilizable del emisor.
 *
 * <p>Contiene los datos fiscales base que suelen repetirse entre documentos,
 * como el tipo de emisión, el emisor y la dirección matriz. Esta información
 * sirve como insumo para generar claves de acceso y estructuras XML del SRI.
 *
 * @see Issuer
 */
public record TaxInfo(
        // Código del tipo de emisión | Tabla Nro. 2 | < tipoEmision >
        Integer emissionType,
        // Información del remitente del documento electrónico
        Issuer issuer,
        // Dirección de la Matriz del negocio
        String parentAddress
) {
}
