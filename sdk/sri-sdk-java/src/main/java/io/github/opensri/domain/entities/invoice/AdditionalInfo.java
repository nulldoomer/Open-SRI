// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

/**
 * Representa un campo adicional de nivel comprobante.
 *
 * <p>Permite adjuntar información complementaria a la factura completa, como referencias
 * descriptivas o datos informativos que el SRI admite dentro del bloque {@code infoAdicional}.
 */
public record AdditionalInfo(String name, String value) {}
