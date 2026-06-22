// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.api.client;

import io.github.nulldoomer.opensri.api.builders.client.OpenSRIClientBuilder;
import io.github.nulldoomer.opensri.application.OPENSRIApplication;
import io.github.nulldoomer.opensri.application.ports.DocumentSigner;
import io.github.nulldoomer.opensri.application.ports.SRIGateway;
import io.github.nulldoomer.opensri.application.ports.XmlSerializer;
import io.github.nulldoomer.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.nulldoomer.opensri.domain.entities.creditnote.CreditNote;
import io.github.nulldoomer.opensri.domain.entities.debitnote.DebitNote;
import io.github.nulldoomer.opensri.domain.entities.invoice.Invoice;
import io.github.nulldoomer.opensri.domain.entities.purchasesettlement.PurchaseSettlement;
import io.github.nulldoomer.opensri.domain.entities.remissionguide.RemissionGuide;
import io.github.nulldoomer.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.nulldoomer.opensri.domain.entities.responses.SendDocumentResult;
import io.github.nulldoomer.opensri.domain.entities.withholding.WithholdingReceipt;
import io.github.nulldoomer.opensri.domain.enums.Environment;
import io.github.nulldoomer.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.creditnote.CreditNoteXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.debitnote.DebitNoteXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.invoice.InvoiceXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.purchasesettlement.PurchaseSettlementXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.remissionguide.RemissionGuideXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.serializers.withholding.WithholdingReceiptXmlSerializerFactory;
import io.github.nulldoomer.opensri.infrastructure.services.SRIAccessKeyGeneratorFactory;
import io.github.nulldoomer.opensri.infrastructure.sri.SRIGatewayFactory;

/**
 * Facade class that serves as the main entry point for interacting with the Open-SRI SDK.
 *
 * <p>The {@code OpenSRIClient} encapsulates the complex interaction between domain logic,
 * infrastructure services, and SRI web services. It should be instantiated using the {@link
 * OpenSRIClientBuilder}.
 */
public final class OpenSRIClient {

  private final Environment environment;
  private final IssuerProfile issuerProfile;
  private final OPENSRIApplication applicationFacade;
  private final XmlSerializer<Invoice> invoiceSerializer = InvoiceXmlSerializerFactory.create();
  private final XmlSerializer<CreditNote> creditNoteSerializer =
      CreditNoteXmlSerializerFactory.create();
  private final XmlSerializer<DebitNote> debitNoteSerializer =
      DebitNoteXmlSerializerFactory.create();
  private final XmlSerializer<PurchaseSettlement> purchaseSettlementSerializer =
      PurchaseSettlementXmlSerializerFactory.create();
  private final XmlSerializer<RemissionGuide> remissionGuideSerializer =
      RemissionGuideXmlSerializerFactory.create();
  private final XmlSerializer<WithholdingReceipt> withholdingReceiptSerializer =
      WithholdingReceiptXmlSerializerFactory.create();

  /**
   * Initializes the client with the required security and environment settings.
   *
   * @param environment the SRI environment (DEV or PROD)
   * @param certificate the raw bytes of the signing certificate
   * @param certificatePassword the password for the certificate
   * @param certificateAlias the alias of the private key within the certificate
   * @param issuerProfile the tax profile of the document issuer
   * @param timeoutSeconds the communication timeout in seconds
   */
  public OpenSRIClient(
      Environment environment,
      byte[] certificate,
      String certificatePassword,
      String certificateAlias,
      IssuerProfile issuerProfile,
      int timeoutSeconds) {

    this.environment = environment;
    this.issuerProfile = issuerProfile;

    DocumentSigner signer =
        XAdEsSignerFactory.create(certificate, certificatePassword, certificateAlias);

    SRIGateway gateway = SRIGatewayFactory.create(environment, timeoutSeconds);

    this.applicationFacade =
        new OPENSRIApplication(SRIAccessKeyGeneratorFactory.create(), signer, gateway);
  }

  /**
   * Sends an electronic invoice to the SRI for reception.
   *
   * @param invoice the invoice domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendInvoice(Invoice invoice) {
    return applicationFacade.send(invoice, invoiceSerializer, environment, issuerProfile);
  }

  /**
   * Sends an electronic credit note to the SRI for reception.
   *
   * @param creditNote the credit-note domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendCreditNote(CreditNote creditNote) {
    return applicationFacade.send(creditNote, creditNoteSerializer, environment, issuerProfile);
  }

  /**
   * Sends an electronic debit note to the SRI for reception.
   *
   * @param debitNote the debit-note domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendDebitNote(DebitNote debitNote) {
    return applicationFacade.send(debitNote, debitNoteSerializer, environment, issuerProfile);
  }

  /**
   * Sends an electronic purchase settlement to the SRI for reception.
   *
   * @param purchaseSettlement the purchase-settlement domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendPurchaseSettlement(PurchaseSettlement purchaseSettlement) {
    return applicationFacade.send(
        purchaseSettlement, purchaseSettlementSerializer, environment, issuerProfile);
  }

  /**
   * Sends an electronic remission guide to the SRI for reception.
   *
   * @param remissionGuide the remission-guide domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendRemissionGuide(RemissionGuide remissionGuide) {
    return applicationFacade.send(
        remissionGuide, remissionGuideSerializer, environment, issuerProfile);
  }

  /**
   * Sends an electronic withholding receipt to the SRI for reception.
   *
   * @param withholdingReceipt the withholding-receipt domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendDocumentResult sendWithholdingReceipt(WithholdingReceipt withholdingReceipt) {
    return applicationFacade.send(
        withholdingReceipt, withholdingReceiptSerializer, environment, issuerProfile);
  }

  /**
   * Queries the SRI for the authorization status of a previously sent document.
   *
   * @param accessKey the 49-digit access key of the document
   * @return the authorization response from the SRI
   */
  public AuthorizationResponse checkAuthorization(String accessKey) {
    return applicationFacade.checkAuthorization(accessKey);
  }
}
