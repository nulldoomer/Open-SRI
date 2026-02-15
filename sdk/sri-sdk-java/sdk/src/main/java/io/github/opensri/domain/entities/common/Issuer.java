package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.Ruc;

/**
 * Datos fiscales obligatorios del emisor
 * @param socialReason
 * @param ruc
 */
public record Issuer(
        // Nombre del establecimiento
        String socialReason,
        // Ruc del emisor
        Ruc ruc
) {
}
