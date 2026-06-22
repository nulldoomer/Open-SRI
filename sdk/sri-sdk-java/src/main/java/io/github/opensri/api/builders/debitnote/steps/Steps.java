// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.builders.debitnote.steps;

import io.github.opensri.domain.entities.common.Client;
import io.github.opensri.domain.entities.common.Compensation;
import io.github.opensri.domain.entities.common.DocumentNumber;
import io.github.opensri.domain.entities.common.ModifiedDocument;
import io.github.opensri.domain.entities.common.payment.Payment;
import io.github.opensri.domain.entities.common.taxes.Tax;
import io.github.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.opensri.domain.entities.debitnote.DebitNote;
import io.github.opensri.domain.entities.debitnote.DebitNoteReason;
import io.github.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.opensri.domain.enums.DocumentVersion;
import io.github.opensri.domain.valueobjects.IssueDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of a {@link DebitNote} through compile-time steps.
 *
 * <p>Collects the mandatory issuer, buyer, modified-document, tax and reason data required to
 * assemble a debit note, and derives {@code valorTotal} from the declared base amount and taxes
 * during the final build step, keeping the resulting domain entity immutable.
 *
 * @see DebitNote
 */
public final class Steps
    implements IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        DocumentVersionStep,
        ClientStep,
        ModifiedDocumentStep,
        TotalSinImpuestosStep,
        TaxesStep,
        ReasonsStep {

  /** Creates a new Steps builder instance. */
  public Steps() {}

  private IssueDate issueDate;
  private String establishmentDirection;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private DocumentVersion documentVersion;
  private Client client;
  private ModifiedDocument modifiedDocument;
  private BigDecimal totalSinImpuestos;
  private final List<Tax> taxes = new ArrayList<>();
  private final List<DebitNoteReason> reasons = new ArrayList<>();
  private final List<Compensation> compensations = new ArrayList<>();
  private final List<Payment> payments = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

  /**
   * Stores the issue date of the debit note being built.
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
   * Stores the establishment address printed in the debit note.
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
   * @param taxInfo issuer tax data required by the debit note
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the debit note.
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
   * Stores the XML document version associated with the debit note.
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
   * Stores the buyer data associated with the debit note.
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
   * Stores the reference to the document modified by the debit note.
   *
   * @param modifiedDocument modified document code, number, and issue date
   * @return next step that requires the base amount
   */
  @Override
  public TotalSinImpuestosStep modifiedDocument(ModifiedDocument modifiedDocument) {
    this.modifiedDocument =
        Objects.requireNonNull(modifiedDocument, "ModifiedDocument is required");
    return this;
  }

  /**
   * Stores the taxable base amount of the debit note.
   *
   * @param totalSinImpuestos taxable base before taxes
   * @return next step that requires the document taxes
   */
  @Override
  public TaxesStep totalSinImpuestos(BigDecimal totalSinImpuestos) {
    this.totalSinImpuestos =
        Objects.requireNonNull(totalSinImpuestos, "totalSinImpuestos is required");
    return this;
  }

  /**
   * Stores the document-level taxes of the debit note.
   *
   * @param taxes document taxes to register
   * @return next step that requires the reasons
   */
  @Override
  public ReasonsStep addTaxes(List<Tax> taxes) {
    addAllRequired(taxes, this.taxes, "Taxes are required", "Tax cannot be null");
    return this;
  }

  /**
   * Adds the reasons that justify the debit note.
   *
   * @param reasons reason entries to register
   * @return same step so more sections or build can follow
   */
  @Override
  public ReasonsStep addReasons(List<DebitNoteReason> reasons) {
    addAllRequired(reasons, this.reasons, "Reasons are required", "DebitNoteReason cannot be null");
    return this;
  }

  /**
   * Adds a collection of compensations to the debit note being built.
   *
   * @param compensations compensation entries to register
   * @return same step so more configuration can follow
   */
  @Override
  public ReasonsStep addCompensations(List<Compensation> compensations) {
    addAllRequired(
        compensations,
        this.compensations,
        "Compensations list is required",
        "Compensation cannot be null");
    return this;
  }

  /**
   * Adds the payment methods of the debit note.
   *
   * @param payments payment entries to register
   * @return same step so more configuration can follow
   */
  @Override
  public ReasonsStep addPayments(List<Payment> payments) {
    addAllRequired(payments, this.payments, "Payments list is required", "Payment cannot be null");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the debit note.
   *
   * @param infos additional fields to append
   * @return same step so more fields or build can follow
   */
  @Override
  public ReasonsStep addInfos(List<AdditionalInfo> infos) {
    addAllRequired(
        infos,
        this.additionalInfos,
        "Additional information list is required",
        "AdditionalInfo cannot be null");
    return this;
  }

  /**
   * Creates an immutable {@link DebitNote} and derives {@code valorTotal} from the base amount and
   * registered taxes.
   *
   * @return debit note populated with mandatory data, derived total, reasons, and optional sections
   * @throws IllegalStateException if no reasons have been added
   */
  @Override
  public DebitNote build() {

    if (reasons.isEmpty()) {
      throw new IllegalStateException("Debit note must contain at least one reason");
    }

    BigDecimal totalTaxValue =
        taxes.stream().map(Tax::value).reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal valorTotal = totalSinImpuestos.add(totalTaxValue);

    return new DebitNote(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        documentVersion,
        client,
        modifiedDocument,
        totalSinImpuestos,
        List.copyOf(taxes),
        valorTotal,
        List.copyOf(reasons),
        List.copyOf(compensations),
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
