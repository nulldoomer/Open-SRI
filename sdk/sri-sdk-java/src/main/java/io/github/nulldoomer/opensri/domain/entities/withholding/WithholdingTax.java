// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.withholding;

import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.math.BigDecimal;

/**
 * Representa una retención individual dentro de un comprobante de retención versión 1.0.0.
 *
 * <p>Cada retención describe el impuesto retenido, el porcentaje aplicado, el valor retenido y,
 * opcionalmente, el documento sustento sobre el que se practica la retención.
 *
 * @param code código del tipo de impuesto retenido (Tabla SRI)
 * @param withholdingCode código del porcentaje de retención
 * @param taxableBase base imponible sobre la que se calcula la retención
 * @param withholdingPercentage porcentaje de retención aplicado
 * @param withheldValue valor retenido calculado
 * @param supportDocCode código del documento sustento; opcional
 * @param supportDocNumber número del documento sustento; opcional
 * @param supportDocIssueDate fecha de emisión del documento sustento; opcional
 */
public record WithholdingTax(
    String code,
    String withholdingCode,
    BigDecimal taxableBase,
    BigDecimal withholdingPercentage,
    BigDecimal withheldValue,
    String supportDocCode,
    String supportDocNumber,
    IssueDate supportDocIssueDate) {}
