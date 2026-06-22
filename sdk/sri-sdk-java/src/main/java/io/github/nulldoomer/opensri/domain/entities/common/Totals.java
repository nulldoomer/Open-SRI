// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.common;

import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TotalTax;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Agrupa los valores monetarios finales y el resumen de impuestos de un comprobante.
 *
 * <p>Centraliza los importes totales calculados a partir de los detalles del documento, incluyendo
 * subtotales, descuentos, propina e impuestos acumulados por tipo y tarifa. Su objetivo es mantener
 * en una sola estructura los valores que luego se reflejan en la sección de totales del XML del
 * comprobante.
 *
 * @param totalValue valor total final del comprobante incluyendo impuestos
 * @param totalWithoutTaxes base imponible total sin impuestos ni descuentos
 * @param totalDiscount descuento total aplicado sobre el subtotal
 * @param totalTipValue valor opcional de propina
 * @param totalTaxes resumen de impuestos agrupados por tipo y tarifa
 * @see TotalTax
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
    // Resumen acumulado de impuestos agrupados por tipo y tarifa <totalConImpuestos>
    List<TotalTax> totalTaxes) {
  /**
   * Canonical constructor.
   *
   * <p>Ensures the list of taxes is non-null and immutable by making a defensive copy.
   *
   * @param totalValue valor total final del comprobante incluyendo impuestos
   * @param totalWithoutTaxes base imponible total sin impuestos ni descuentos
   * @param totalDiscount descuento total aplicado sobre el subtotal
   * @param totalTipValue valor opcional de propina
   * @param totalTaxes resumen de impuestos agrupados por tipo y tarifa
   */
  public Totals {
    totalTaxes = totalTaxes == null ? List.of() : List.copyOf(totalTaxes);
  }

  /**
   * Calcula los totales del comprobante a partir de sus detalles.
   *
   * <p>Agrupa los impuestos por combinación de código y tarifa, acumula sus bases imponibles y
   * valores, y construye el importe total final considerando descuentos y propina. Este método
   * evita que el código cliente tenga que repetir manualmente la lógica de agregación tributaria
   * para cada factura.
   *
   * @param items detalles de la factura desde los que se obtienen subtotales e impuestos
   * @return instancia con los valores monetarios agregados del comprobante
   * @throws OpenSRIValidationException si la lista de items es nula o está vacía
   */
  public static Totals from(List<InvoiceItem> items) {

    if (items == null || items.isEmpty()) {
      throw new OpenSRIValidationException("items cannot be null or empty");
    }

    BigDecimal totalWithoutTaxes = BigDecimal.ZERO;
    BigDecimal totalDiscount = BigDecimal.ZERO;
    BigDecimal totalTipValue = BigDecimal.ZERO;

    List<TotalTax> totalTaxes = new ArrayList<>();

    record TaxAccumulator(BigDecimal base, BigDecimal value) {}

    Map<String, TaxAccumulator> taxesGroups = new HashMap<>();

    for (InvoiceItem item : items) {

      totalWithoutTaxes = totalWithoutTaxes.add(item.totalPriceWithoutTax());
      totalDiscount = totalDiscount.add(item.discount());

      if (item.taxes() != null && !item.taxes().isEmpty()) {
        for (Tax tax : item.taxes()) {
          String taxKey = tax.code() + "-" + tax.rateCode();

          BigDecimal taxableBase = tax.taxableBase();
          BigDecimal value = tax.value();

          if (taxesGroups.containsKey(taxKey)) {

            TaxAccumulator current = taxesGroups.get(taxKey);

            BigDecimal newTaxableBase = current.base().add(taxableBase);
            BigDecimal newValue = current.value().add(value);

            taxesGroups.put(taxKey, new TaxAccumulator(newTaxableBase, newValue));

          } else {

            taxesGroups.put(taxKey, new TaxAccumulator(taxableBase, value));
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

      totalTaxes.add(new TotalTax(code, rateCode, acc.base(), acc.value()));
    }
    BigDecimal totalValue =
        totalWithoutTaxes.subtract(totalDiscount).add(totalTaxValue).add(totalTipValue);

    return new Totals(totalValue, totalWithoutTaxes, totalDiscount, totalTipValue, totalTaxes);
  }
}
