package io.github.opensri.domain.entities.common;

/**
 * Información fiscal reutilizable que cubre todos los datos necesarios de
 * < infoFactura >
 * @param emissionType
 * @param issuer
 * @param parentAddress
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
