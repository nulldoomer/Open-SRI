// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.withholding;

import java.math.BigDecimal;

/**
 * Representa un impuesto del documento sustento ({@code impuestoDocSustento}) en un comprobante de
 * retención versión 2.0.0.
 *
 * @param code código del impuesto del documento sustento (Tabla SRI)
 * @param rateCode código del porcentaje aplicado
 * @param taxableBase base imponible del impuesto
 * @param rate tarifa aplicada
 * @param value valor del impuesto
 */
public record SupportDocumentTax(
    String code, String rateCode, BigDecimal taxableBase, BigDecimal rate, BigDecimal value) {}
