// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.withholding;

import java.math.BigDecimal;

/**
 * Representa la compra de cajas de banano asociada a una retención versión 2.0.0.
 *
 * @param boxCount número de cajas de banano
 * @param boxPrice precio por caja de banano
 */
public record BananaBoxPurchase(Integer boxCount, BigDecimal boxPrice) {}
