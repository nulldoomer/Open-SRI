// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.creditnote.steps;

import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.Compensation;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.ModifiedDocument;
import io.github.nulldoomer.opensri.domain.entities.common.Totals;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.InvoiceItem;
import io.github.nulldoomer.opensri.domain.enums.Currency;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of a {@link CreditNote} through compile-time steps.
 *
 * <p>Collects the mandatory issuer, buyer, modified-document and detail data required to assemble a
 * credit note, and derives {@link Totals} automatically from the registered items during the final
 * build step, keeping the resulting domain entity immutable.
 *
 * @see CreditNote
 */
public final class Steps
    implements IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        DocumentVersionStep,
        ClientStep,
        ModifiedDocumentStep,
        MotivoStep,
        ItemsStep {

  /** Creates a new Steps builder instance. */
  public Steps() {}

  private IssueDate issueDate;
  private String establishmentDirection;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private DocumentVersion documentVersion;
  private Client client;
  private ModifiedDocument modifiedDocument;
  private String motivo;
  private final List<InvoiceItem> items = new ArrayList<>();
  private Currency currency;
  private final List<Compensation> compensations = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

  /**
   * Stores the issue date of the credit note being built.
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
   * Stores the establishment address printed in the credit note.
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
   * @param taxInfo issuer tax data required by the credit note
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the credit note.
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
   * Stores the XML document version associated with the credit note.
   *
   * @param documentVersion target SRI XML version for the final document
   * @return next step that requires the buyer information
   */
  @Override
  public ClientStep documentVersion(DocumentVersion documentVersion) {
    this.documentVersion = Objects.requireNonNull(documentVersion, "DocumentVersion is required");
    return this;
  }

  /**
   * Stores the buyer data associated with the credit note.
   *
   * @param client buyer tax identification and display name
   * @return next step that requires the modified document
   */
  @Override
  public ModifiedDocumentStep client(Client client) {
    this.client = Objects.requireNonNull(client, "Client is required");
    return this;
  }

  /**
   * Stores the reference to the document modified by the credit note.
   *
   * @param modifiedDocument modified document code, number, and issue date
   * @return next step that requires the credit-note reason
   */
  @Override
  public MotivoStep modifiedDocument(ModifiedDocument modifiedDocument) {
    this.modifiedDocument =
        Objects.requireNonNull(modifiedDocument, "ModifiedDocument is required");
    return this;
  }

  /**
   * Stores the reason for the credit note.
   *
   * @param motivo reason for issuing the document
   * @return step that accepts items and optional sections
   */
  @Override
  public ItemsStep motivo(String motivo) {
    this.motivo = Objects.requireNonNull(motivo, "Motivo is required");
    return this;
  }

  /**
   * Adds a collection of line items to the credit note being built.
   *
   * @param items credit-note items to include in the final document
   * @return same step so more sections or build can follow
   */
  @Override
  public ItemsStep addItems(List<InvoiceItem> items) {
    addAllRequired(items, this.items, "Items are required", "InvoiceItem cannot be null");
    return this;
  }

  /**
   * Sets the currency in which the credit-note amounts are expressed.
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
   * Adds a collection of compensations to the credit note being built.
   *
   * @param compensations compensation entries to register
   * @return same step so more configuration can follow
   */
  @Override
  public ItemsStep addCompensations(List<Compensation> compensations) {
    addAllRequired(
        compensations,
        this.compensations,
        "Compensations list is required",
        "Compensation cannot be null");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the credit note.
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
   * Creates an immutable {@link CreditNote} and calculates its totals from the added items.
   *
   * @return credit note populated with mandatory data, derived totals, items, and optional sections
   * @throws IllegalStateException if no items have been added
   */
  @Override
  public CreditNote build() {

    if (items.isEmpty()) {
      throw new IllegalStateException("Credit note must contain at least one item");
    }

    Totals totals = Totals.from(items);

    return new CreditNote(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        documentVersion,
        client,
        modifiedDocument,
        motivo,
        totals,
        List.copyOf(items),
        currency,
        List.copyOf(compensations),
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
