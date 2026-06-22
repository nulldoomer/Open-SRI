// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.entities.withholding;

import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.math.BigDecimal;
import java.util.List;

/**
 * Representa un documento sustento ({@code docSustento}) en un comprobante de retención versión
 * 2.0.0.
 *
 * <p>Agrupa la referencia al comprobante sobre el que se practican las retenciones, sus totales,
 * los impuestos del documento, las retenciones aplicadas y las formas de pago.
 *
 * @param sustentoCode código de sustento (Tabla SRI)
 * @param docCode código del tipo de documento sustento
 * @param docNumber número del documento sustento (15 dígitos)
 * @param docIssueDate fecha de emisión del documento sustento
 * @param paymentLocation forma de pago local o del exterior ({@code 01} o {@code 02})
 * @param totalWithoutTaxes total sin impuestos del documento sustento
 * @param totalAmount importe total del documento sustento
 * @param taxes impuestos del documento sustento
 * @param withholdings retenciones practicadas sobre el documento sustento
 * @param payments formas de pago del documento sustento
 */
public record SupportDocument(
    String sustentoCode,
    String docCode,
    String docNumber,
    IssueDate docIssueDate,
    String paymentLocation,
    BigDecimal totalWithoutTaxes,
    BigDecimal totalAmount,
    List<SupportDocumentTax> taxes,
    List<WithholdingDetail> withholdings,
    List<Payment> payments) {

  /**
   * Constructor compacto para inicializar colecciones inmutables.
   *
   * @param sustentoCode código de sustento
   * @param docCode código del documento sustento
   * @param docNumber número del documento sustento
   * @param docIssueDate fecha de emisión del documento sustento
   * @param paymentLocation forma de pago local o del exterior
   * @param totalWithoutTaxes total sin impuestos
   * @param totalAmount importe total
   * @param taxes impuestos del documento sustento
   * @param withholdings retenciones practicadas
   * @param payments formas de pago
   */
  public SupportDocument {
    taxes = taxes == null ? List.of() : List.copyOf(taxes);
    withholdings = withholdings == null ? List.of() : List.copyOf(withholdings);
    payments = payments == null ? List.of() : List.copyOf(payments);
  }
}
