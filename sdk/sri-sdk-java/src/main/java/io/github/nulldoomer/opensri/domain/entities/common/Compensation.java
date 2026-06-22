// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.common;

import java.math.BigDecimal;

/**
 * Representa una compensación aplicada en una nota de crédito o de débito.
 *
 * <p>El SRI permite registrar compensaciones (por ejemplo, las del régimen de devolución) dentro
 * del comprobante. El código de compensación es siempre {@code "1"} según la tabla del SRI, por lo
 * que la entidad solo modela la tarifa y el valor.
 *
 * @param rate tarifa de la compensación aplicada
 * @param value valor monetario de la compensación
 */
public record Compensation(BigDecimal rate, BigDecimal value) {}
