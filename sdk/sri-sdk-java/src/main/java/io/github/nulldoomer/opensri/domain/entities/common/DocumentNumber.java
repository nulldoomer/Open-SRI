// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.common;

/**
 * Representa la numeración fiscal del comprobante electrónico.
 *
 * <p>Contiene el código del tipo de documento, el establecimiento, el punto de emisión y el
 * secuencial con los que el SRI identifica de forma única cada comprobante dentro de una serie.
 *
 * @param documentCode código del tipo de documento (ej. "01" para factura)
 * @param establishment código del establecimiento (3 dígitos)
 * @param emissionPoint código del punto de emisión (3 dígitos)
 * @param sequentialNumber número secuencial del comprobante (9 dígitos)
 */
public record DocumentNumber(
    // Código del documento |Tabla Nro. 3 SRI | <codDoc>
    String documentCode,
    // Código del establecimiento
    String establishment,
    // Código del punto de emisión
    String emissionPoint,
    // Número secuencial de los documentos
    String sequentialNumber) {}
