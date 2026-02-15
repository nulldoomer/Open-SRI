package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.ClientIdentification;

/**
 * Datos fiscales obligatorios al cliente que se factura
 * @param identification
 * @param names
 */
public record Client(
        ClientIdentification identification,
        String names
) {
}
