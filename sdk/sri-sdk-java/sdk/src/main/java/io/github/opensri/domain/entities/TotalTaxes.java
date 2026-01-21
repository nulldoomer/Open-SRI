package io.github.opensri.domain.entities;

import java.math.BigDecimal;

public record TotalTaxes(

        // Tabla 16: Tipo de impuesto
        String code,
        // Tabla 17: Tarifa del IVA
        String rateCode,
        String rateDescription,
        // Porcentaje aplicado del impuesto
        BigDecimal rate,
        // Base imponible a la que se le aplica el impuesto
        BigDecimal taxableBase,
        // Valor monetario del impuesto calculado
        BigDecimal value
) {
}
