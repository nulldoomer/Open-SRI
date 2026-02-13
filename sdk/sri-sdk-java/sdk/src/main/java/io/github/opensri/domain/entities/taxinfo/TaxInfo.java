package io.github.opensri.domain.entities.taxinfo;

import io.github.opensri.domain.entities.common.Issuer;

public record TaxInfo(
        // Código del tipo de ambiente del Web Service del SRI
        Integer environment,
        // Código del tipo de emisión | Tabla Nro. 2 | <tipoEmision>
        Integer emissionType,
        // Información del remitente del documento electrónico
        Issuer issuer,
        // Clave de acceso generada automáticamente por el SDK
        String accessKey,
        // Código del documento |Tabla Nro. 3 SRI | <codDoc>
        String documentCode,
        // Código del establecimiento
        String establishment,
        // Código del punto de emisión
        String emissionPoint,
        // Número secuencial de los documentos
        String sequentialNumber,
        // Dirección de la Matriz del negocio
        String parentAddress
) {
}
