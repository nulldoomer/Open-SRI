// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.invoice;

/**
 * Representa un atributo adicional asociado a un detalle de factura.
 *
 * <p>Se utiliza para incorporar información complementaria de un producto o servicio dentro del
 * bloque de detalles, sin alterar la estructura fiscal principal del ítem.
 *
 * @param name nombre o etiqueta descriptiva del detalle adicional
 * @param value valor o contenido asociado al detalle
 */
public record AdditionalDetail(
    // Nombre del detalle adicional
    String name,
    // Valor correspondiente
    String value) {}
