// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.taxes.Tax;
import java.math.BigDecimal;
import java.util.List;

/**
 * Representa una línea de detalle dentro de la factura.
 *
 * <p>Contiene la información comercial y tributaria de un producto o servicio, incluyendo
 * cantidades, precios, descuentos, detalles adicionales e impuestos aplicados sobre ese ítem
 * específico.
 *
 * <p>Las colecciones asociadas al detalle se almacenan como copias inmutables para preservar la
 * consistencia del documento una vez creado.
 *
 * @param mainCode código principal del producto o servicio
 * @param auxCode código auxiliar opcional
 * @param description descripción del bien o servicio
 * @param quantity cantidad facturada
 * @param unitPrice precio unitario antes de impuestos
 * @param discount descuento total aplicado a esta línea
 * @param totalPriceWithoutTax precio total de la línea sin impuestos
 * @param additionalDetails detalles adicionales (par nombre-valor) para el ítem
 * @param taxes lista de impuestos aplicados a este producto (IVA, ICE, IRBPNR)
 */
public record InvoiceItem(
    String mainCode,
    String auxCode,
    String description,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal discount,
    BigDecimal totalPriceWithoutTax,
    List<AdditionalDetail> additionalDetails,
    /*
    Lista de impuestos a un producto, para cubrir casos en donde se cobre
    el IVA y el producto aplique el ICE
     */
    List<Tax> taxes) {

  public InvoiceItem {
    additionalDetails = additionalDetails == null ? List.of() : List.copyOf(additionalDetails);
    taxes = taxes == null ? List.of() : List.copyOf(taxes);
  }
}
