package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.TaxRate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Datos obligatorios de valores totales e impuestos totales
 * @param totalValue
 * @param totalWithoutTaxes
 * @param totalDiscount
 * @param totalTipValue
 * @param totalTaxes
 */
public record Totals(
        // Valor total final del comprobante <importeTotal>
        BigDecimal totalValue,
        // Base imponible gravada con impuestos <totalSinImpuestos>
        BigDecimal totalWithoutTaxes,
        // Descuento aplicado al subtotal antes de impuestos <totalDescuento>
        BigDecimal totalDiscount,
        // Valor de propina (Depende de las reglas del negocio)
        BigDecimal totalTipValue,
        /**
         * Colección que contiene el resumen agrupado de los impuestos generados
         * en la factura.
         *
         * Cada elemento representa el total acumulado por cada combinación única de:
         *
         * - code
         * - codeRate
         *
         * Estos valores se obtienen agrupando los impuestos previamente
         * calculados en cada detalle (item) de la factura.
         * < totalConImpuestos >
         */
        List<TotalTax> totalTaxes
) {
    public Totals{
        totalTaxes = totalTaxes == null ? List.of(): List.copyOf(totalTaxes);
    }

    public static Totals from(List<InvoiceItem> items) {

        if( items == null || items.isEmpty()){
            throw new IllegalArgumentException("items cannot be null or empty");
        }

        BigDecimal totalWithoutTaxes = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;
        BigDecimal totalTipValue = BigDecimal.ZERO;

        List<TotalTax> totalTaxes = new ArrayList<>();

        record TaxAccumulator(BigDecimal base, BigDecimal value){}

        Map<String,TaxAccumulator> taxesGroups= new HashMap<>();

        for(InvoiceItem item : items){

            totalWithoutTaxes = totalWithoutTaxes.add(item.totalPriceWithoutTax());
            totalDiscount = totalDiscount.add(item.discount());

            if(item.taxes() != null && !item.taxes().isEmpty()){
                for(Tax tax : item.taxes()){
                    String taxKey = tax.code() + "-" + tax.rateCode();

                    BigDecimal taxableBase = tax.taxableBase();
                    BigDecimal value = tax.value();

                    if(taxesGroups.containsKey(taxKey)){

                        TaxAccumulator current= taxesGroups.get(taxKey);

                        BigDecimal newTaxableBase = current.base().add(taxableBase);
                        BigDecimal newValue = current.value().add(value);

                        taxesGroups.put(taxKey, new TaxAccumulator(newTaxableBase, newValue));

                    }else{

                        taxesGroups.put(taxKey, new TaxAccumulator(taxableBase,value));
                    }
                }
            }
        }
        BigDecimal totalTaxValue = BigDecimal.ZERO;
        for (Map.Entry<String, TaxAccumulator> entry : taxesGroups.entrySet()) {

            String[] parts = entry.getKey().split("-");
            String code = parts[0];
            String rateCode = parts[1];

            TaxAccumulator acc = entry.getValue();

            totalTaxValue = totalTaxValue.add(acc.value());

            totalTaxes.add(
                    new TotalTax(
                            code,
                            rateCode,
                            TaxRate.fromCode(Integer.parseInt(code)).getValue(),
                            acc.base(),
                            acc.value()
                    )
            );
        }
        BigDecimal totalValue = totalWithoutTaxes
                .subtract(totalDiscount)
                .add(totalTaxValue)
                .add(totalTipValue);

        return new Totals(
                totalValue,
                totalWithoutTaxes,
                totalDiscount,
                totalTipValue,
                totalTaxes
        );

    }
}
