// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

import java.util.List;

/**
 * Representa la información sustitutiva de guía de remisión adjunta a la factura.
 *
 * <p>Aplica únicamente en documentos con versión 2.0.0 y 2.1.0. Permite incluir dentro de la
 * factura los datos del transporte que normalmente se registran en una guía de remisión separada,
 * cuando ambos documentos se emiten de forma simultánea.
 *
 * @param dirPartida dirección desde donde parten las mercancías
 * @param dirDestinatario dirección del destinatario final de las mercancías
 * @param fechaIniTransporte fecha de inicio del transporte en formato {@code dd/MM/yyyy}
 * @param fechaFinTransporte fecha de finalización del transporte en formato {@code dd/MM/yyyy}
 * @param razonSocialTransportista nombre o razón social del transportista
 * @param tipoIdentificacionTransportista código del tipo de identificación del transportista
 * @param rucTransportista número de identificación del transportista
 * @param placa número de placa del vehículo utilizado en el transporte
 * @param destinations lista de destinations incluidos en el traslado; debe contener al menos uno
 */
public record RemisionGuideSubstituteInfo(
    String dirPartida,
    String dirDestinatario,
    String fechaIniTransporte,
    String fechaFinTransporte,
    String razonSocialTransportista,
    String tipoIdentificacionTransportista,
    String rucTransportista,
    String placa,
    List<Destination> destinations) {

  /**
   * Validates that the list of destinations is not null and creates a defensive immutable copy.
   *
   * @throws IllegalArgumentException if {@code destinations} is null
   */
  public RemisionGuideSubstituteInfo {
    destinations = destinations == null ? List.of() : List.copyOf(destinations);
  }
}
