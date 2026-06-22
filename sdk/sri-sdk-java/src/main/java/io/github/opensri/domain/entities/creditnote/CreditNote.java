// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.creditnote;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.Compensation;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.ElectronicDocument;
import io.github.opensri.domain.entities.common.ModifiedDocument;
import io.github.opensri.domain.entities.common.Totals;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.Currency;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.util.List;

/**
 * Representa una nota de crédito electrónica dentro del dominio del SDK.
 *
 * <p>Una nota de crédito modifica un comprobante previamente emitido (referenciado mediante {@link
 * ModifiedDocument}) para reducir su valor por devoluciones, descuentos o anulaciones. Reutiliza
 * las mismas líneas de detalle que la factura ({@link InvoiceItem}) y el cálculo de totales ({@link
 * Totals}), ya que su estructura comercial es equivalente.
 *
 * @param issueDate fecha de emisión de la nota de crédito
 * @param establishmentDirection dirección del establecimiento emisor
 * @param taxInfo información tributaria base del emisor
 * @param documentNumber numeración fiscal de la nota de crédito (codDoc "04")
 * @param documentVersion versión del esquema XML a utilizar
 * @param client información del comprador
 * @param modifiedDocument referencia al comprobante modificado
 * @param motivo motivo de la emisión de la nota de crédito
 * @param totals totales calculados a partir de los detalles
 * @param items líneas de detalle de los bienes o servicios afectados
 * @param currency moneda del documento; opcional
 * @param compensations compensaciones aplicadas; opcional
 * @param additionalInfo información adicional personalizada; opcional
 */
public record CreditNote(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    Client client,
    ModifiedDocument modifiedDocument,
    String motivo,
    Totals totals,
    List<InvoiceItem> items,
    Currency currency,
    List<Compensation> compensations,
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
   * @param motivo motivo de la nota de crédito
   * @param totals totales del documento
   * @param items ítems del documento
   * @param currency moneda
   * @param compensations compensaciones
   * @param additionalInfo información adicional
   */
  public CreditNote {
    items = items == null ? List.of() : List.copyOf(items);
    compensations = compensations == null ? List.of() : List.copyOf(compensations);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
  }
}
