// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.purchasesettlement.steps;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.Totals;
import io.github.nulldoomer.opensri.domain.entities.common.payment.Payment;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.Provider;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;
import io.github.nulldoomer.opensri.domain.enums.Currency;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of a {@link PurchaseSettlement} through compile-time steps.
 *
 * <p>Collects the mandatory issuer, provider and detail data required to assemble a purchase
 * settlement, and derives {@link Totals} automatically from the registered items during the final
 * build step, keeping the resulting domain entity immutable.
 *
 * @see PurchaseSettlement
 */
public final class Steps
    implements IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        DocumentVersionStep,
        ProviderStep,
        ItemsStep {

  /** Creates a new Steps builder instance. */
  public Steps() {}

  private IssueDate issueDate;
  private String establishmentDirection;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private DocumentVersion documentVersion;
  private Provider provider;
  private final List<InvoiceItem> items = new ArrayList<>();
  private Currency currency;
  private final List<Payment> payments = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

  /**
   * Stores the issue date of the purchase settlement being built.
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
   * Stores the establishment address printed in the purchase settlement.
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
   * @param taxInfo issuer tax data required by the purchase settlement
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the purchase settlement.
   *
   * @param documentNumber document code, establishment, emission point, and sequential number
   * @return next step that requires the XML document version
   */
  @Override
  public DocumentVersionStep documentNumber(DocumentNumber documentNumber) {
    this.documentNumber = Objects.requireNonNull(documentNumber, "DocumentNumber is required");
    return this;
  }

  /**
   * Stores the XML document version associated with the purchase settlement.
   *
   * @param documentVersion target SRI XML version for the final document
   * @return next step that requires the provider information
   */
  @Override
  public ProviderStep documentVersion(DocumentVersion documentVersion) {
    this.documentVersion = Objects.requireNonNull(documentVersion, "DocumentVersion is required");
    return this;
  }

  /**
   * Stores the provider data associated with the purchase settlement.
   *
   * @param provider provider tax identification, name, and address
   * @return step that accepts items and optional sections
   */
  @Override
  public ItemsStep provider(Provider provider) {
    this.provider = Objects.requireNonNull(provider, "Provider is required");
    return this;
  }

  /**
   * Adds a collection of line items to the purchase settlement being built.
   *
   * @param items settlement items to include in the final document
   * @return same step so more sections or build can follow
   */
  @Override
  public ItemsStep addItems(List<InvoiceItem> items) {
    addAllRequired(items, this.items, "Items are required", "InvoiceItem cannot be null");
    return this;
  }

  /**
   * Sets the currency in which the settlement amounts are expressed.
   *
   * @param currency currency to associate with the document
   * @return same step so more configuration can follow
   */
  @Override
  public ItemsStep addCurrency(Currency currency) {
    this.currency = Objects.requireNonNull(currency);
    return this;
  }

  /**
   * Adds the payment methods of the purchase settlement.
   *
   * @param payments payment entries to register
   * @return same step so more configuration can follow
   */
  @Override
  public ItemsStep addPayments(List<Payment> payments) {
    addAllRequired(payments, this.payments, "Payments list is required", "Payment cannot be null");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the purchase settlement.
   *
   * @param infos additional fields to append
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

  /**
   * Creates an immutable {@link PurchaseSettlement} and calculates its totals from the added items.
   *
   * @return settlement populated with mandatory data, derived totals, items, and optional sections
   * @throws IllegalStateException if no items have been added
   */
  @Override
  public PurchaseSettlement build() {

    if (items.isEmpty()) {
      throw new IllegalStateException("Purchase settlement must contain at least one item");
    }

    Totals totals = Totals.from(items);

    return new PurchaseSettlement(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        documentVersion,
        provider,
        totals,
        List.copyOf(items),
        currency,
        List.copyOf(payments),
        List.copyOf(additionalInfos));
  }

  private static <T> void addAllRequired(
      List<T> source, List<T> target, String nullListMessage, String nullItemMessage) {
    Objects.requireNonNull(source, nullListMessage);

    for (T item : source) {
      target.add(Objects.requireNonNull(item, nullItemMessage));
    }
  }
}
