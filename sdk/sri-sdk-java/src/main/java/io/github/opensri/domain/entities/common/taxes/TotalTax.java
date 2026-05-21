// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.taxes;

import java.math.BigDecimal;

/**
 * Representa un impuesto ya acumulado dentro de la sección de totales del comprobante.
 *
 * <p>Cada instancia resume la base imponible y el valor total de una combinación específica de tipo
 * de impuesto y código de tarifa, tal como exige el bloque {@code totalConImpuestos} del XML del
 * SRI.
 *
 * @param code código del tipo de impuesto (IVA, ICE, etc.)
 * @param rateCode código de la tarifa o porcentaje aplicado
 * @param taxableBase monto total sobre el cual se calculó el tributo
 * @param value valor monetario total del impuesto acumulado
 */
public record TotalTax(
    // Tabla 16: Tipo de impuesto <codigo>
    String code,
    // Tabla 17: Tarifa del IVA <codigoPorcentaje>
    String rateCode,
    // Base imponible a la que se le aplica el impuesto <baseImponible>
    BigDecimal taxableBase,
    // Valor monetario del impuesto calculado <valor>
    BigDecimal value) {}
