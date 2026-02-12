package io.github.opensri.domain.entities.common;

import java.math.BigDecimal;
import java.util.List;

public record Totals(
        // Valor total final del comprobante
        BigDecimal totalValue,
        // Base imponible gravada con impuestos
        BigDecimal totalTaxableValue,
        // Valor total de impuestos
        BigDecimal totalTaxValue,
        // Valor exento de impuestos
        BigDecimal totalExemptValue,
        // Descuento aplicado al subtotal antes de impuestos
        BigDecimal totalDiscount,
        // Valor de propina (Depende de las reglas del negocio)
        BigDecimal totalTipValue,
        // Valor total retenido (IVA + RENTA)
        BigDecimal totalWithholdingValue,
        List<TotalTax> totalTaxes
) {
    public Totals{
        totalTaxes = totalTaxes == null ? List.of(): List.copyOf(totalTaxes);
    }
}
