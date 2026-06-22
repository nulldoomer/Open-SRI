// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.withholding;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.ElectronicDocument;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.util.List;

/**
 * Representa un comprobante de retención electrónico dentro del dominio del SDK.
 *
 * <p>Un comprobante de retención documenta las retenciones en la fuente practicadas a un sujeto
 * retenido (reutiliza {@link Client} para su identificación) en un período fiscal determinado.
 *
 * <p>La entidad soporta ambas versiones del esquema: en la versión 1.0.0 las retenciones se listan
 * de forma plana en {@code withholdings}; en la versión 2.0.0 se agrupan por documento sustento en
 * {@code supportDocuments} y se exigen los campos {@code relatedParty} (parteRel) y, opcionalmente,
 * {@code subjectType} (tipoSujetoRetenido).
 *
 * @param issueDate fecha de emisión del comprobante
 * @param establishmentDirection dirección del establecimiento emisor; opcional
 * @param taxInfo información tributaria base del emisor
 * @param documentNumber numeración fiscal del comprobante (codDoc "07")
 * @param documentVersion versión del esquema XML a utilizar
 * @param subject sujeto retenido
 * @param fiscalPeriod período fiscal en formato {@code MM/yyyy}
 * @param subjectType tipo de sujeto retenido (tipoSujetoRetenido); solo versión 2.0.0, opcional
 * @param relatedParty indica si es parte relacionada (parteRel); requerido en versión 2.0.0
 * @param withholdings retenciones practicadas; aplica en versión 1.0.0
 * @param supportDocuments documentos sustento con sus retenciones; aplica en versión 2.0.0
 * @param additionalInfo información adicional personalizada; opcional
 */
public record WithholdingReceipt(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    Client subject,
    String fiscalPeriod,
    String subjectType,
    String relatedParty,
    List<WithholdingTax> withholdings,
    List<SupportDocument> supportDocuments,
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
   * @param subject sujeto retenido
   * @param fiscalPeriod período fiscal
   * @param subjectType tipo de sujeto retenido
   * @param relatedParty indicador de parte relacionada
   * @param withholdings retenciones practicadas (versión 1.0.0)
   * @param supportDocuments documentos sustento (versión 2.0.0)
   * @param additionalInfo información adicional
   */
  public WithholdingReceipt {
    withholdings = withholdings == null ? List.of() : List.copyOf(withholdings);
    supportDocuments = supportDocuments == null ? List.of() : List.copyOf(supportDocuments);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
  }
}
