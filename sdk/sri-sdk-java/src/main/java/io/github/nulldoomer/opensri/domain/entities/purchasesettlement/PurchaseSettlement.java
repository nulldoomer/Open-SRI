// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.entities.purchasesettlement;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.ElectronicDocument;
import io.github.nulldoomer.opensri.domain.entities.common.Totals;
import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.domain.enums.Currency;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.List;

/**
 * Representa una liquidación de compra electrónica dentro del dominio del SDK.
 *
 * <p>La liquidación de compra es un comprobante primario que emite el comprador cuando el proveedor
 * no puede emitir factura. Reutiliza las líneas de detalle de la factura ({@link InvoiceItem}) y el
 * cálculo de totales ({@link Totals}), e identifica a la contraparte mediante un {@link Provider}.
 *
 * @param issueDate fecha de emisión de la liquidación
 * @param establishmentDirection dirección del establecimiento emisor
 * @param taxInfo información tributaria base del emisor
 * @param documentNumber numeración fiscal de la liquidación (codDoc "03")
 * @param documentVersion versión del esquema XML a utilizar
 * @param provider información del proveedor
 * @param totals totales calculados a partir de los detalles
 * @param items líneas de detalle de los bienes o servicios adquiridos
 * @param currency moneda del documento; opcional
 * @param payments formas de pago; opcional
 * @param additionalInfo información adicional personalizada; opcional
 */
public record PurchaseSettlement(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    Provider provider,
    Totals totals,
    List<InvoiceItem> items,
    Currency currency,
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
   * @param provider información del proveedor
   * @param totals totales del documento
   * @param items ítems del documento
   * @param currency moneda
   * @param payments formas de pago
   * @param additionalInfo información adicional
   */
  public PurchaseSettlement {
    items = items == null ? List.of() : List.copyOf(items);
    payments = payments == null ? List.of() : List.copyOf(payments);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
  }
}
