// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.debitnote;

import java.math.BigDecimal;

/**
 * Representa un motivo dentro de una nota de débito.
 *
 * <p>A diferencia de las líneas de detalle de una factura, una nota de débito se compone de motivos
 * simples que describen el cargo adicional y su valor.
 *
 * @param razon descripción del motivo del cargo
 * @param value valor monetario asociado al motivo
 */
public record DebitNoteReason(String razon, BigDecimal value) {}
