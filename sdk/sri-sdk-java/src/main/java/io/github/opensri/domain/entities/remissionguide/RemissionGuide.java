// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.remissionguide;

import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.ElectronicDocument;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.util.List;

/**
 * Representa una guía de remisión electrónica dentro del dominio del SDK.
 *
 * <p>La guía de remisión sustenta el traslado físico de bienes. Contiene la información del
 * transportista, el período del transporte y uno o más destinatarios ({@link Recipient}) con sus
 * respectivos bienes trasladados.
 *
 * <p>Las fechas de inicio y fin del transporte se modelan como cadenas en formato {@code
 * dd/MM/yyyy} porque, a diferencia de la fecha de emisión, pueden corresponder a fechas futuras.
 *
 * @param issueDate fecha de emisión de la guía de remisión
 * @param establishmentDirection dirección del establecimiento emisor; opcional
 * @param taxInfo información tributaria base del emisor
 * @param documentNumber numeración fiscal de la guía (codDoc "06")
 * @param documentVersion versión del esquema XML a utilizar
 * @param departureAddress dirección de partida de los bienes
 * @param carrierName razón social del transportista
 * @param carrierIdType tipo de identificación del transportista
 * @param carrierId identificación (RUC) del transportista
 * @param transportStartDate fecha de inicio del transporte en formato {@code dd/MM/yyyy}
 * @param transportEndDate fecha de fin del transporte en formato {@code dd/MM/yyyy}
 * @param plate placa del vehículo de transporte
 * @param recipients destinatarios de los bienes trasladados
 * @param additionalInfo información adicional personalizada; opcional
 */
public record RemissionGuide(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    String departureAddress,
    String carrierName,
    String carrierIdType,
    String carrierId,
    String transportStartDate,
    String transportEndDate,
    String plate,
    List<Recipient> recipients,
    List<AdditionalInfo> additionalInfo)
    implements ElectronicDocument {

  /**
   * Constructor compacto para inicializar colecciones inmutables.
   *
   * @param issueDate fecha de emisión
   * @param establishmentDirection dirección del establecimiento
   * @param taxInfo información tributaria
   * @param documentNumber número de documento
   * @param documentVersion versión del documento
   * @param departureAddress dirección de partida
   * @param carrierName razón social del transportista
   * @param carrierIdType tipo de identificación del transportista
   * @param carrierId identificación del transportista
   * @param transportStartDate fecha de inicio del transporte
   * @param transportEndDate fecha de fin del transporte
   * @param plate placa del vehículo
   * @param recipients destinatarios
   * @param additionalInfo información adicional
   */
  public RemissionGuide {
    recipients = recipients == null ? List.of() : List.copyOf(recipients);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
  }
}
