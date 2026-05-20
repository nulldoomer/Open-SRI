// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.invoice;

import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.enums.Currency;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.util.List;

/**
 * Representa una factura electrónica completa dentro del dominio del SDK.
 *
 * <p>Reúne la información fiscal del emisor, los datos del comprador, la numeración tributaria, los
 * detalles de productos o servicios y los valores totales necesarios para construir el comprobante
 * electrónico.
 *
 * <p>También admite información adicional opcional de nivel factura que luego puede reflejarse en
 * la sección {@code infoAdicional} del XML del SRI. La versión XML del documento se conserva como
 * parte de la entidad para que el flujo de serialización no tenga que recibirla como un parámetro
 * separado.
 *
 * @param issueDate fecha de emisión del comprobante
 * @param establishmentDirection dirección de la sucursal o establecimiento emisor
 * @param taxInfo información tributaria base del emisor (RUC, ambiente, etc.)
 * @param documentNumber numeración fiscal (establecimiento, punto de emisión, secuencial)
 * @param documentVersion versión del esquema XML a utilizar
 * @param client información del comprador
 * @param totals resumen de valores monetarios e impuestos
 * @param items lista de productos o servicios facturados
 * @param additionalInfo información adicional personalizada del documento
 * @param payments formas de pago aplicadas
 * @param currency moneda en la que se emite la factura
 */
public record Invoice(
    IssueDate issueDate,
    String establishmentDirection,
    TaxInfo taxInfo,
    DocumentNumber documentNumber,
    DocumentVersion documentVersion,
    Client client,
    Totals totals,
    List<InvoiceItem> items,
    List<AdditionalInfo> additionalInfo,
    List<Payment> payments,
    Currency currency) {

  public Invoice {
    items = items == null ? List.of() : List.copyOf(items);
    additionalInfo = additionalInfo == null ? List.of() : List.copyOf(additionalInfo);
    payments = payments == null ? List.of() : List.copyOf(payments);
  }
}
