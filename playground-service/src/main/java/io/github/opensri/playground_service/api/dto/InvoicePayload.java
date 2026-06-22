// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.playground_service.api.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * JSON contract for the invoice the playground runs through the SDK.
 *
 * <p>The frontend was previously mocked, so this record defines the canonical request shape. It
 * mirrors the inputs the SDK's {@code InvoiceBuilder} requires; the mapping into the SDK domain
 * happens in {@code InvoiceMapper}. Monetary line totals and the tax summary are derived from the
 * items, so callers only provide quantities, unit prices and discounts.
 *
 * @param documentVersion SRI XML schema version (e.g. {@code 2.1.0})
 * @param issueDate issue date in {@code yyyy-MM-dd} format
 * @param establishmentDirection address of the issuing establishment
 * @param documentCode SRI document type code (e.g. {@code 01} for invoice)
 * @param establishment establishment code (3 digits)
 * @param emissionPoint emission point code (3 digits)
 * @param sequentialNumber sequential number (9 digits)
 * @param client buyer information
 * @param items invoice line items (at least one required)
 * @param payments payment entries; when empty a single cash payment covering the total is generated
 */
public record InvoicePayload(
    String documentVersion,
    String issueDate,
    String establishmentDirection,
    String documentCode,
    String establishment,
    String emissionPoint,
    String sequentialNumber,
    Client client,
    List<Item> items,
    List<Payment> payments) {

  /**
   * Buyer information.
   *
   * @param identificationType one of {@code RUC}, {@code CEDULA}, {@code FINAL_CONSUMER}
   * @param identification identification number (ignored for {@code FINAL_CONSUMER})
   * @param names buyer name or business name
   */
  public record Client(String identificationType, String identification, String names) {}

  /**
   * Invoice line item.
   *
   * @param mainCode primary product/service code
   * @param auxCode optional auxiliary code
   * @param description item description
   * @param quantity billed quantity
   * @param unitPrice unit price before taxes
   * @param discount discount applied to the line (use {@code 0} when none)
   * @param taxes taxes applied to the line
   */
  public record Item(
      String mainCode,
      String auxCode,
      String description,
      BigDecimal quantity,
      BigDecimal unitPrice,
      BigDecimal discount,
      List<Tax> taxes) {}

  /**
   * Tax applied to a line item, expressed via SRI catalog codes.
   *
   * @param taxType SRI tax type code (table 19; e.g. {@code 2} for IVA)
   * @param rateCode SRI tax rate code (table 17; e.g. {@code 4} for 15%)
   */
  public record Tax(int taxType, int rateCode) {}

  /**
   * Payment entry covering part of the invoice total.
   *
   * @param paymentMethod {@code PaymentMethod} enum name (e.g. {@code SIN_SISTEMA_FINANCIERO})
   * @param total amount covered by this payment
   */
  public record Payment(String paymentMethod, BigDecimal total) {}
}
