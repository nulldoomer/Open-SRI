package io.github.opensri.domain.entities.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Datos obligatorios de impuestos con base a las tablas de la documentación
 * del SRI
 * @param code
 * @param rateCode
 * @param rate
 * @param taxableBase
 */
public record Tax(
        // Tabla 16: Tipo de impuesto
        String code,
        // Tabla 17: Tarifa del IVA
        String rateCode,
        // Porcentaje aplicado del impuesto
        BigDecimal rate,
        // Base imponible a la que se le aplica el impuesto
        BigDecimal taxableBase
) {

    private static final int SCALE = 2;

    // Valor monetario del impuesto calculado
    public BigDecimal value(){
        return taxableBase
                .multiply(rate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
