package io.github.opensri.domain.entities.common;

import java.math.BigDecimal;

/**
 * Representa un impuesto ya acumulado dentro de la sección de totales del comprobante.
 *
 * <p>Cada instancia resume la base imponible y el valor total de una combinación
 * específica de tipo de impuesto y código de tarifa, tal como exige el bloque
 * {@code totalConImpuestos} del XML del SRI.
 */
public record TotalTax(
        // Tabla 16: Tipo de impuesto <codigo>
        String code,
        // Tabla 17: Tarifa del IVA <codigoPorcentaje>
        String rateCode,
        // Base imponible a la que se le aplica el impuesto <baseImponible>
        BigDecimal taxableBase,
        // Valor monetario del impuesto calculado <valor>
        BigDecimal value
) {
}
