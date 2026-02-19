package io.github.opensri.domain.entities.common;

public record DocumentNumber (
        // Código del documento |Tabla Nro. 3 SRI | <codDoc>
        String documentCode,
        // Código del establecimiento
        String establishment,
        // Código del punto de emisión
        String emissionPoint,
        // Número secuencial de los documentos
        String sequentialNumber
){
}
