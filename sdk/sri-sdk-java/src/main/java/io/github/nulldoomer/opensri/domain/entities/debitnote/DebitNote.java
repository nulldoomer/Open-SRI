// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.debitnote;

import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.Compensation;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.ElectronicDocument;
import io.github.nulldoomer.opensri.domain.entities.common.ModifiedDocument;
import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.Tax;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Representa una nota de débito electrónica dentro del dominio del SDK.
 *
 * <p>Una nota de débito incrementa el valor de un comprobante previamente emitido (referenciado
 * mediante {@link ModifiedDocument}) por intereses, multas u otros cargos. Sus impuestos se aplican
 * a nivel de documento y sus cargos se describen mediante {@link DebitNoteReason}.
 *
 * @param issueDate fecha de emisión de la nota de débito
 * @param establishmentDirection dirección del establecimiento emisor
 * @param taxInfo información tributaria base del emisor
 * @param documentNumber numeración fiscal de la nota de débito (codDoc "05")
 * @param documentVersion versión del esquema XML a utilizar
 * @param client información del comprador
 * @param modifiedDocument referencia al comprobante modificado
 * @param totalSinImpuestos base imponible total antes de impuestos
 * @param taxes impuestos aplicados a nivel de documento
 * @param valorTotal valor total del documento incluyendo impuestos
 * @param reasons motivos del cargo
 * @param compensations compensaciones aplicadas; opcional
 * @param payments formas de pago; opcional
 * @param additionalInfo información adicional personalizada; opcional
 */
public record DebitNote(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    Client client,
    ModifiedDocument modifiedDocument,
    BigDecimal totalSinImpuestos,
    List<Tax> taxes,
    BigDecimal valorTotal,
    List<DebitNoteReason> reasons,
    List<Compensation> compensations,
    List<Payment> payments,
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
   * @param client información del cliente
   * @param modifiedDocument referencia al comprobante modificado
   * @param totalSinImpuestos base imponible total
   * @param taxes impuestos del documento
   * @param valorTotal valor total
   * @param reasons motivos del cargo
   * @param compensations compensaciones
   * @param payments formas de pago
   * @param additionalInfo información adicional
   */
  public DebitNote {
    taxes = taxes == null ? List.of() : List.copyOf(taxes);
    reasons = reasons == null ? List.of() : List.copyOf(reasons);
    compensations = compensations == null ? List.of() : List.copyOf(compensations);
    payments = payments == null ? List.of() : List.copyOf(payments);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
  }
}
