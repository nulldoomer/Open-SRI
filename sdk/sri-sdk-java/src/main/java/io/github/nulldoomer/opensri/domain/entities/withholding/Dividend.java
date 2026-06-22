// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.withholding;

import java.math.BigDecimal;

/**
 * Representa la información de dividendos asociada a una retención versión 2.0.0.
 *
 * @param paymentDate fecha de pago del dividendo en formato {@code dd/MM/yyyy}
 * @param corporateIncomeTax impuesto a la renta sociedad atribuible al dividendo
 * @param fiscalYear ejercicio fiscal de las utilidades distribuidas (formato {@code yyyy})
 */
public record Dividend(String paymentDate, BigDecimal corporateIncomeTax, String fiscalYear) {}
