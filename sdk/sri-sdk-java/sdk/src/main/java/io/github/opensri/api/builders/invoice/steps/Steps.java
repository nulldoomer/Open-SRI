// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.invoice.steps;

import io.github.opensri.domain.entities.common.*;
import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.invoice.InvoiceItem;
import io.github.opensri.domain.enums.Currency;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of an {@link Invoice} through compile-time steps.
 *
 * <p>This builder implementation collects the mandatory tax data, buyer information, line items,
 * and optional additional information required to assemble an invoice entity. It accepts
 * collections directly for variable-size sections and calculates {@link Totals} automatically from
 * the registered items during the final build step.
 *
 * <p>It backs {@link io.github.opensri.api.builders.invoice.InvoiceBuilder} and centralizes the
 * mutable state needed to enforce the step-builder flow while keeping the resulting domain entity
 * immutable.
 *
 * @see Invoice
 * @see Totals
 */
public final class Steps
    implements IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        ClientStep,
        ItemsStep,
        PaymentStep {

  private IssueDate issueDate;
  private String establishmentDirection;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private Client client;
  private final List<InvoiceItem> items = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();
  private final List<Payment> payments = new ArrayList<>();
  private Currency currency;

  /**
   * Stores the issue date of the invoice being built.
   *
   * @param issueDate validated issue date of the document
   * @return next step that requires the establishment address
   */
  @Override
  public EstablishmentDirectionStep issueDate(IssueDate issueDate) {
    this.issueDate = Objects.requireNonNull(issueDate, "IssueDate is required");
    return this;
  }

  /**
   * Stores the establishment address printed in the invoice.
   *
   * @param establishmentDirection address of the issuing establishment
   * @return next step that requires reusable tax information
   */
  @Override
  public TaxInfoStep establishmentDirection(String establishmentDirection) {
    this.establishmentDirection =
        Objects.requireNonNull(establishmentDirection, "Establishment direction is required");
    return this;
  }

  /**
   * Stores the reusable tax information of the issuer.
   *
   * @param taxInfo issuer tax data required by the invoice
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the invoice.
   *
   * @param documentNumber document code, establishment, emission point, and sequential number
   * @return next step that requires the buyer information
   */
  @Override
  public ClientStep documentNumber(DocumentNumber documentNumber) {
    this.documentNumber = Objects.requireNonNull(documentNumber, "DocumentNumber is required");
    return this;
  }

  /**
   * Stores the buyer data associated with the invoice.
   *
   * @param client buyer tax identification and display name
   * @return step that accepts invoice items and optional additional information
   */
  @Override
  public ItemsStep client(Client client) {
    this.client = Objects.requireNonNull(client, "Client is required");
    return this;
  }

  /**
   * Adds a collection of line items to the invoice being built.
   *
   * <p>This method can be called multiple times to append additional batches of items. At least one
   * item must be registered before the invoice can be built.
   *
   * @param items invoice items to include in the final document
   * @return same step so more item batches or additional information can be appended
   */
  @Override
  public ItemsStep addItems(List<InvoiceItem> items) {
    addAllRequired(items, this.items, "Invoice items are required", "InvoiceItem cannot be null");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the invoice.
   *
   * <p>This method can be skipped entirely when the invoice does not require an {@code
   * infoAdicional} section in the resulting XML.
   *
   * @param infos additional invoice fields to append
   * @return same step so more fields or build can follow
   */
  @Override
  public ItemsStep addInfos(List<AdditionalInfo> infos) {
    addAllRequired(
        infos,
        this.additionalInfos,
        "Additional information list is required",
        "AdditionalInfo cannot be null");
    return this;
  }

  @Override
  public PaymentStep addPayments(List<Payment> payments) {
    addAllRequired(payments, this.payments, "Payments are required", "Payments cannot be null");
    return this;
  }

  @Override
  public ItemsStep addCurrency(Currency currency) {
    this.currency = Objects.requireNonNull(currency);
    return this;
  }

  /**
   * Creates an immutable {@link Invoice} and calculates its totals from the added items.
   *
   * <p>The build step derives {@link Totals} automatically, preserving the builder contract that
   * invoice totals are consistent with the registered detail lines.
   *
   * @return invoice entity populated with mandatory data, derived totals, items, and optional
   *     additional information
   * @throws IllegalStateException if no items have been added
   */
  @Override
  public Invoice build() {

    if (items.isEmpty()) {
      throw new IllegalStateException("Invoice must contain at least one item");
    }

    Totals totals = Totals.from(items);

    validatePayments(totals);

    return new Invoice(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        client,
        totals,
        List.copyOf(items),
        List.copyOf(additionalInfos),
        List.copyOf(payments),
        currency);
  }

  private static <T> void addAllRequired(
      List<T> source, List<T> target, String nullListMessage, String nullItemMessage) {
    Objects.requireNonNull(source, nullListMessage);

    for (T item : source) {
      target.add(Objects.requireNonNull(item, nullItemMessage));
    }
  }

  private void validatePayments(Totals totals) {

    if (payments.isEmpty()) {
      throw new IllegalStateException("Invoice must contain at least one payment");
    }

    BigDecimal total =
        payments.stream().map(Payment::total).reduce(BigDecimal.ZERO, BigDecimal::add);

    if (total.compareTo(totals.totalValue()) != 0) {
      throw new IllegalStateException("The total payment doesn't match the total value");
    }
  }
}
