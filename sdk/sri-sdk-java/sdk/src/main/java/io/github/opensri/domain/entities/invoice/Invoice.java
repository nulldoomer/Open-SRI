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
