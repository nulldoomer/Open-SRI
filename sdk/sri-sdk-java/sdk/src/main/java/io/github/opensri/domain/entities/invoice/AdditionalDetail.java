package io.github.opensri.domain.entities.invoice;

/**
 * Representa un atributo adicional asociado a un detalle de factura.
 *
 * <p>Se utiliza para incorporar información complementaria de un producto
 * o servicio dentro del bloque de detalles, sin alterar la estructura fiscal
 * principal del ítem.
 */
public record AdditionalDetail(
        // Nombre del detalle adicional
        String name,
        // Valor correspondiente
        String value
) {
}
