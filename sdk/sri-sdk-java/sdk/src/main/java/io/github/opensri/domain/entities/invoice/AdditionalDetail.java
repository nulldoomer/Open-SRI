package io.github.opensri.domain.entities.invoice;

/**
 * Detalle adicional de un item del detalle de la factura.
 * @param name
 * @param value
 */
public record AdditionalDetail(
        // Nombre del detalle adicional
        String name,
        // Valor correspondiente
        String value
) {
}
