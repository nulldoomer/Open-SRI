// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.remissionguide;

import java.util.List;

/**
 * Representa un destinatario dentro de una guía de remisión.
 *
 * <p>Describe a quién y a dónde se trasladan los bienes, el motivo del traslado y las líneas de
 * detalle correspondientes a ese destino.
 *
 * @param identification identificación del destinatario
 * @param socialReason razón social del destinatario
 * @param address dirección del destinatario
 * @param transferReason motivo del traslado
 * @param customsDoc documento aduanero único; opcional
 * @param destEstablishmentCode código del establecimiento destino (3 dígitos); opcional
 * @param route ruta del transporte; opcional
 * @param items líneas de detalle de los bienes trasladados a este destino
 */
public record Recipient(
    String identification,
    String socialReason,
    String address,
    String transferReason,
    String customsDoc,
    String destEstablishmentCode,
    String route,
    List<RemissionGuideItem> items) {

  /**
   * Constructor compacto para inicializar la colección inmutable de ítems.
   *
   * @param identification identificación del destinatario
   * @param socialReason razón social del destinatario
   * @param address dirección del destinatario
   * @param transferReason motivo del traslado
   * @param customsDoc documento aduanero único
   * @param destEstablishmentCode código del establecimiento destino
   * @param route ruta del transporte
   * @param items líneas de detalle
   */
  public Recipient {
    items = items == null ? List.of() : List.copyOf(items);
  }
}
