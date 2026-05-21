// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common.issuer;

import io.github.opensri.domain.valueobjects.Ruc;

/**
 * Representa al emisor tributario del documento electrónico.
 *
 * <p>Reúne la razón social visible en el comprobante y el RUC con el que el negocio o contribuyente
 * se identifica ante el SRI.
 *
 * @param socialReason nombre o razón social del establecimiento emisor
 * @param ruc Registro Único de Contribuyentes (RUC) del emisor
 * @see io.github.opensri.domain.valueobjects.Ruc
 */
public record Issuer(
    // Nombre del establecimiento
    String socialReason,
    // Ruc del emisor
    Ruc ruc) {}
