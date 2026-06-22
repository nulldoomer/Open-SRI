// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.remissionguide;

import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalDetail;
import java.math.BigDecimal;
import java.util.List;

/**
 * Representa una línea de detalle de un destinatario dentro de una guía de remisión.
 *
 * <p>A diferencia del detalle de una factura, el de la guía de remisión solo describe el bien
 * trasladado y su cantidad, sin precios ni impuestos.
 *
 * @param mainCode código interno del bien; opcional
 * @param auxCode código adicional del bien; opcional
 * @param description descripción del bien trasladado
 * @param quantity cantidad trasladada
 * @param additionalDetails detalles adicionales (par nombre-valor); opcional
 */
public record RemissionGuideItem(
    String mainCode,
    String auxCode,
    String description,
    BigDecimal quantity,
    List<AdditionalDetail> additionalDetails) {

  /**
   * Constructor compacto para inicializar la colección inmutable de detalles adicionales.
   *
   * @param mainCode código interno
   * @param auxCode código adicional
   * @param description descripción
   * @param quantity cantidad
   * @param additionalDetails detalles adicionales
   */
  public RemissionGuideItem {
    additionalDetails = additionalDetails == null ? List.of() : List.copyOf(additionalDetails);
  }
}
