// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.invoice;

import java.math.BigDecimal;

/**
 * Representa una retención en la fuente aplicada dentro de la factura.
 *
 * <p>Aplica únicamente en documentos emitidos con versión 1.1.0 y 2.1.0, donde el SRI permite
 * incluir retenciones directamente en el comprobante de venta.
 *
 * @param codigo código del tipo de retención (siempre "4" según Tabla SRI)
 * @param codigoPorcentaje código del porcentaje de retención (1–3 dígitos numéricos)
 * @param tarifa porcentaje de retención aplicado
 * @param valor monto retenido calculado sobre la base imponible
 */
public record Retention(
    String codigo, String codigoPorcentaje, BigDecimal tarifa, BigDecimal valor) {}
