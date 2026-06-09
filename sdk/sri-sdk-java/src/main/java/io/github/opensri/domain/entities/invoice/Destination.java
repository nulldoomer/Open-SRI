// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

/**
 * Representa un destino dentro de la guía sustitutiva de remisión.
 *
 * <p>Cada destino describe a dónde se trasladan los bienes junto con los datos aduaneros y de ruta
 * correspondientes al tramo específico del transporte.
 *
 * @param motivoTraslado motivo por el que se realiza el traslado de las mercancías
 * @param docAduaneroUnico número del documento aduanero único si aplica, o {@code null}
 * @param codEstabDestino código de 3 dígitos del establecimiento destino, o {@code null}
 * @param ruta descripción de la ruta del transporte, o {@code null}
 */
public record Destination(
    String motivoTraslado, String docAduaneroUnico, String codEstabDestino, String ruta) {}
