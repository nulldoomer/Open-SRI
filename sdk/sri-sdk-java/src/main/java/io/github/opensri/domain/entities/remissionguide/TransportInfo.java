// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.remissionguide;

/**
 * Agrupa los datos del transportista y del transporte de una guía de remisión.
 *
 * <p>Reúne en una sola estructura la dirección de partida, la identificación del transportista, el
 * período del transporte y la placa del vehículo, simplificando la construcción de la guía.
 *
 * @param departureAddress dirección de partida de los bienes
 * @param carrierName razón social del transportista
 * @param carrierIdType tipo de identificación del transportista
 * @param carrierId identificación (RUC) del transportista
 * @param transportStartDate fecha de inicio del transporte en formato {@code dd/MM/yyyy}
 * @param transportEndDate fecha de fin del transporte en formato {@code dd/MM/yyyy}
 * @param plate placa del vehículo de transporte
 */
public record TransportInfo(
    String departureAddress,
    String carrierName,
    String carrierIdType,
    String carrierId,
    String transportStartDate,
    String transportEndDate,
    String plate) {}
