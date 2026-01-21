package io.github.opensri.domain.entities.invoice;

public record AdditionalDetail(
        // Nombre del detalle adicional
        String name,
        // Valor correspondiente
        String value
) {
}
