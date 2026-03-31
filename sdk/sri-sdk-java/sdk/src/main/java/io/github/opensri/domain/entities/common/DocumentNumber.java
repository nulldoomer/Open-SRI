package io.github.opensri.domain.entities.common;

/**
 * Representa la numeración fiscal del comprobante electrónico.
 *
 * <p>Contiene el código del tipo de documento, el establecimiento,
 * el punto de emisión y el secuencial con los que el SRI identifica
 * de forma única cada comprobante dentro de una serie.
 */
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
