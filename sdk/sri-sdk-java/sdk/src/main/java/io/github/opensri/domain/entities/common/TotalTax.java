package io.github.opensri.domain.entities.common;

import java.math.BigDecimal;

/**
 * Datos de los impuestos a los valores totales, basado en las tablas de la
 * documentación del SRI
 * @param code
 * @param rateCode
 * @param rate
 * @param taxableBase
 * @param value
 */
public record TotalTax(
        // Tabla 16: Tipo de impuesto <codigo>
        String code,
        // Tabla 17: Tarifa del IVA <codigoPorcentaje>
        String rateCode,
        // Porcentaje aplicado del impuesto <tarifa>
        BigDecimal rate,
        // Base imponible a la que se le aplica el impuesto <baseImponible>
        BigDecimal taxableBase,
        // Valor monetario del impuesto calculado <valor>
        BigDecimal value
) {
}
