package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.Ruc;

public record Issuer(
        // Nombre del establecimiento
        String socialReason,
        // Ruc del emisor
        Ruc ruc
) {
}
