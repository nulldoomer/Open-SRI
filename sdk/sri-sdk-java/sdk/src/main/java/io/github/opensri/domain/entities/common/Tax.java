package io.github.opensri.domain.entities.common;

import java.math.BigDecimal;

/**
 * Datos obligatorios de impuestos con base a las tablas de la documentación
 * del SRI
 * @param code
 * @param rateCode
 * @param rate
 * @param taxableBase
 * @param value
 */
public record Tax(
        // Tabla 16: Tipo de impuesto
        String code,
        // Tabla 17: Tarifa del IVA
        String rateCode,
        // Porcentaje aplicado del impuesto
        BigDecimal rate,
        // Base imponible a la que se le aplica el impuesto
        BigDecimal taxableBase,
        // Valor monetario del impuesto calculado
        BigDecimal value
) {
}
