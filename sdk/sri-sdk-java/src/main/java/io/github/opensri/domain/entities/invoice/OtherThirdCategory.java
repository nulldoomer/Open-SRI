// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

import java.math.BigDecimal;

/**
 * Representa un rubro de terceros incluido en la factura.
 *
 * <p>Aplica en documentos con versión 2.0.0 y 2.1.0, donde el SRI permite reflejar cobros que el
 * emisor realiza por cuenta de terceros dentro del mismo comprobante electrónico.
 *
 * @param concepto descripción del concepto del rubro de tercero (1–300 caracteres)
 * @param total monto total del rubro cobrado por cuenta del tercero
 */
public record OtherThirdCategory(String concepto, BigDecimal total) {}
