// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.common;

import io.github.opensri.domain.valueobjects.ClientIdentification;

/**
 * Representa al comprador registrado en un comprobante electrónico.
 *
 * <p>Agrupa la identificación fiscal del cliente y el nombre que debe constar en la factura u otro
 * documento tributario emitido por el SDK.
 *
 * @see io.github.opensri.domain.valueobjects.ClientIdentification
 */
public record Client(ClientIdentification identification, String names) {}
