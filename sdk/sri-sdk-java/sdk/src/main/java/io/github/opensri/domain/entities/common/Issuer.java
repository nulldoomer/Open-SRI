// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.Ruc;

/**
 * Representa al emisor tributario del documento electrónico.
 *
 * <p>Reúne la razón social visible en el comprobante y el RUC con el que el negocio o contribuyente
 * se identifica ante el SRI.
 *
 * @see io.github.opensri.domain.valueobjects.Ruc
 */
public record Issuer(
    // Nombre del establecimiento
    String socialReason,
    // Ruc del emisor
    Ruc ruc) {}
