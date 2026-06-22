// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.withholding.steps;

import io.github.nulldoomer.opensri.domain.entities.common.Client;
import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.withholding.SupportDocument;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingTax;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of a {@link WithholdingReceipt} through compile-time steps.
 *
 * <p>Collects the mandatory issuer, withheld-subject, fiscal-period and withholding data required
 * to assemble a withholding receipt, requires at least one withholding, and keeps the resulting
 * domain entity immutable.
 *
 * @see WithholdingReceipt
 */
public final class Steps
    implements IssueDateStep,
        EstablishmentDirectionStep,
        TaxInfoStep,
        DocumentNumberStep,
        DocumentVersionStep,
        SubjectStep,
        FiscalPeriodStep,
        WithholdingsStep {

  /** Creates a new Steps builder instance. */
  public Steps() {}

  private IssueDate issueDate;
  private String establishmentDirection;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private DocumentVersion documentVersion;
  private Client subject;
  private String fiscalPeriod;
  private String subjectType;
  private String relatedParty;
  private final List<WithholdingTax> withholdings = new ArrayList<>();
  private final List<SupportDocument> supportDocuments = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

  /**
   * Stores the issue date of the withholding receipt being built.
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
   * Stores the establishment address printed in the withholding receipt.
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
   * @param taxInfo issuer tax data required by the withholding receipt
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the withholding receipt.
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
   * Stores the XML document version associated with the withholding receipt.
   *
   * @param documentVersion target SRI XML version for the final document
   * @return next step that requires the withheld subject
   */
  @Override
  public SubjectStep documentVersion(DocumentVersion documentVersion) {
    this.documentVersion = Objects.requireNonNull(documentVersion, "DocumentVersion is required");
    return this;
  }

  /**
   * Stores the withheld subject of the receipt.
   *
   * @param subject withheld subject tax identification and display name
   * @return next step that requires the fiscal period
   */
  @Override
  public FiscalPeriodStep subject(Client subject) {
    this.subject = Objects.requireNonNull(subject, "Subject is required");
    return this;
  }

  /**
   * Stores the fiscal period of the withholdings.
   *
   * @param fiscalPeriod fiscal period in {@code MM/yyyy} format
   * @return step that accepts withholdings and optional sections
   */
  @Override
  public WithholdingsStep fiscalPeriod(String fiscalPeriod) {
    this.fiscalPeriod = Objects.requireNonNull(fiscalPeriod, "fiscalPeriod is required");
    return this;
  }

  /**
   * Adds the withholdings practiced on the subject.
   *
   * @param withholdings withholding entries to register
   * @return same step so more sections or build can follow
   */
  @Override
  public WithholdingsStep addWithholdings(List<WithholdingTax> withholdings) {
    addAllRequired(
        withholdings,
        this.withholdings,
        "Withholdings are required",
        "WithholdingTax cannot be null");
    return this;
  }

  /**
   * Adds the support documents with their withholdings (version 2.0.0).
   *
   * @param supportDocuments support-document entries to register
   * @return same step so more sections or build can follow
   */
  @Override
  public WithholdingsStep addSupportDocuments(List<SupportDocument> supportDocuments) {
    addAllRequired(
        supportDocuments,
        this.supportDocuments,
        "Support documents are required",
        "SupportDocument cannot be null");
    return this;
  }

  /**
   * Sets whether the withheld subject is a related party (version 2.0.0).
   *
   * @param relatedParty {@code SI} or {@code NO}
   * @return same step so more configuration can follow
   */
  @Override
  public WithholdingsStep relatedParty(String relatedParty) {
    this.relatedParty = Objects.requireNonNull(relatedParty, "relatedParty is required");
    return this;
  }

  /**
   * Sets the withheld-subject type (version 2.0.0).
   *
   * @param subjectType withheld-subject type code
   * @return same step so more configuration can follow
   */
  @Override
  public WithholdingsStep subjectType(String subjectType) {
    this.subjectType = Objects.requireNonNull(subjectType, "subjectType is required");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the withholding receipt.
   *
   * @param infos additional fields to append
   * @return same step so more fields or build can follow
   */
  @Override
  public WithholdingsStep addInfos(List<AdditionalInfo> infos) {
    addAllRequired(
        infos,
        this.additionalInfos,
        "Additional information list is required",
        "AdditionalInfo cannot be null");
    return this;
  }

  /**
   * Creates an immutable {@link WithholdingReceipt} from the registered data.
   *
   * @return withholding receipt populated with mandatory data, withholdings, and optional sections
   * @throws IllegalStateException if no withholdings have been added
   */
  @Override
  public WithholdingReceipt build() {

    if (withholdings.isEmpty() && supportDocuments.isEmpty()) {
      throw new IllegalStateException(
          "Withholding receipt must contain at least one withholding or support document");
    }

    return new WithholdingReceipt(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        documentVersion,
        subject,
        fiscalPeriod,
        subjectType,
        relatedParty,
        List.copyOf(withholdings),
        List.copyOf(supportDocuments),
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
