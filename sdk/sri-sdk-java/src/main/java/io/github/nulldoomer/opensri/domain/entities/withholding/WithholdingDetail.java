// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.withholding;

import java.math.BigDecimal;

/**
 * Representa una retención individual ({@code retencion}) dentro de un documento sustento en un
 * comprobante de retención versión 2.0.0.
 *
 * <p>Opcionalmente puede incluir información de dividendos o de compra de cajas de banano.
 *
 * @param code código del tipo de retención (Tabla SRI)
 * @param withholdingCode código del porcentaje de retención
 * @param taxableBase base imponible de la retención
 * @param percentage porcentaje de retención aplicado
 * @param withheldValue valor retenido
 * @param dividend información de dividendos; opcional
 * @param bananaBox información de compra de cajas de banano; opcional
 */
public record WithholdingDetail(
    String code,
    String withholdingCode,
    BigDecimal taxableBase,
    BigDecimal percentage,
    BigDecimal withheldValue,
    Dividend dividend,
    BananaBoxPurchase bananaBox) {}
