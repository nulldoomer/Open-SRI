// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.builders.remissionguide.steps;

import io.github.nulldoomer.opensri.domain.entities.common.DocumentNumber;
import io.github.nulldoomer.opensri.domain.entities.common.taxes.TaxInfo;
import io.github.nulldoomer.opensri.domain.entities.invoice.AdditionalInfo;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.Recipient;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.RemissionGuide;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.TransportInfo;
import io.github.nulldoomer.opensri.domain.enums.DocumentVersion;
import io.github.nulldoomer.opensri.domain.valueobjects.IssueDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the fluent construction of a {@link RemissionGuide} through compile-time steps.
 *
 * <p>Collects the mandatory issuer, transport and recipient data required to assemble a remission
 * guide, requires at least one recipient, and keeps the resulting domain entity immutable.
 *
 * @see RemissionGuide
 */
public final class Steps
    implements IssueDateStep,
        TaxInfoStep,
        DocumentNumberStep,
        DocumentVersionStep,
        TransportInfoStep,
        RecipientsStep {

  /** Creates a new Steps builder instance. */
  public Steps() {}

  private IssueDate issueDate;
  private TaxInfo taxInfo;
  private DocumentNumber documentNumber;
  private DocumentVersion documentVersion;
  private TransportInfo transportInfo;
  private String establishmentDirection;
  private final List<Recipient> recipients = new ArrayList<>();
  private final List<AdditionalInfo> additionalInfos = new ArrayList<>();

  /**
   * Stores the issue date of the remission guide being built.
   *
   * @param issueDate validated issue date of the document
   * @return next step that requires reusable tax information
   */
  @Override
  public TaxInfoStep issueDate(IssueDate issueDate) {
    this.issueDate = Objects.requireNonNull(issueDate, "IssueDate is required");
    return this;
  }

  /**
   * Stores the reusable tax information of the issuer.
   *
   * @param taxInfo issuer tax data required by the remission guide
   * @return next step that requires the document number
   */
  @Override
  public DocumentNumberStep taxInfo(TaxInfo taxInfo) {
    this.taxInfo = Objects.requireNonNull(taxInfo, "TaxInfo is required");
    return this;
  }

  /**
   * Stores the document numbering data for the remission guide.
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
   * Stores the XML document version associated with the remission guide.
   *
   * @param documentVersion target SRI XML version for the final document
   * @return next step that requires the transport information
   */
  @Override
  public TransportInfoStep documentVersion(DocumentVersion documentVersion) {
    this.documentVersion = Objects.requireNonNull(documentVersion, "DocumentVersion is required");
    return this;
  }

  /**
   * Stores the carrier and transport data of the remission guide.
   *
   * @param transportInfo departure address, carrier, transport period, and plate
   * @return step that accepts recipients and optional sections
   */
  @Override
  public RecipientsStep transportInfo(TransportInfo transportInfo) {
    this.transportInfo = Objects.requireNonNull(transportInfo, "TransportInfo is required");
    return this;
  }

  /**
   * Adds the recipients of the goods being transported.
   *
   * @param recipients recipient entries to register
   * @return same step so more sections or build can follow
   */
  @Override
  public RecipientsStep addRecipients(List<Recipient> recipients) {
    addAllRequired(
        recipients, this.recipients, "Recipients are required", "Recipient cannot be null");
    return this;
  }

  /**
   * Sets the establishment address of the issuer.
   *
   * @param establishmentDirection address of the issuing establishment
   * @return same step so more configuration can follow
   */
  @Override
  public RecipientsStep establishmentDirection(String establishmentDirection) {
    this.establishmentDirection =
        Objects.requireNonNull(establishmentDirection, "Establishment direction is required");
    return this;
  }

  /**
   * Adds a collection of optional additional information fields to the remission guide.
   *
   * @param infos additional fields to append
   * @return same step so more fields or build can follow
   */
  @Override
  public RecipientsStep addInfos(List<AdditionalInfo> infos) {
    addAllRequired(
        infos,
        this.additionalInfos,
        "Additional information list is required",
        "AdditionalInfo cannot be null");
    return this;
  }

  /**
   * Creates an immutable {@link RemissionGuide} from the registered data.
   *
   * @return remission guide populated with mandatory data, recipients, and optional sections
   * @throws IllegalStateException if no recipients have been added
   */
  @Override
  public RemissionGuide build() {

    if (recipients.isEmpty()) {
      throw new IllegalStateException("Remission guide must contain at least one recipient");
    }

    return new RemissionGuide(
        issueDate,
        establishmentDirection,
        taxInfo,
        documentNumber,
        documentVersion,
        transportInfo.departureAddress(),
        transportInfo.carrierName(),
        transportInfo.carrierIdType(),
        transportInfo.carrierId(),
        transportInfo.transportStartDate(),
        transportInfo.transportEndDate(),
        transportInfo.plate(),
        List.copyOf(recipients),
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
