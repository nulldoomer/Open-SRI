// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.AccessKeyGenerator;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.application.ports.XmlSerializer;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.SendInvoiceResult;
import io.github.opensri.domain.enums.Environment;

/**
 * Facade class that orchestrates the application layer of the Open-SRI SDK.
 *
 * <p>This class acts as a coordination point for various use cases, delegating the execution of
 * specific business processes to specialized use case handlers.
 */
public final class OPENSRIApplication {

  private final SendInvoiceUseCase sendInvoiceUseCase;
  private final CheckAuthorizationUseCase authorizationUseCase;

  /**
   * Initializes the application facade with the necessary ports.
   *
   * @param accessKeyGenerator port for generating SRI access keys
   * @param invoiceXmlSerializer port for serializing invoices to XML
   * @param documentSigner port for digitally signing XML documents
   * @param sriGateway port for communicating with SRI web services
   */
  public OPENSRIApplication(
      AccessKeyGenerator accessKeyGenerator,
      XmlSerializer<Invoice> invoiceXmlSerializer,
      DocumentSigner documentSigner,
      SRIGateway sriGateway) {

    this.sendInvoiceUseCase =
        new SendInvoiceUseCase(
            accessKeyGenerator, invoiceXmlSerializer, documentSigner, sriGateway);
    this.authorizationUseCase = new CheckAuthorizationUseCase(sriGateway);
  }

  /**
   * Executes the process of preparing, signing, and sending an invoice to the SRI.
   *
   * @param invoice the invoice to be processed
   * @param environment the target SRI environment
   * @param profile the issuer's tax profile
   * @return the result of the invoice submission
   */
  public SendInvoiceResult sendInvoice(
      Invoice invoice, Environment environment, IssuerProfile profile) {

    return sendInvoiceUseCase.execute(invoice, environment, profile);
  }

  /**
   * Executes the process of checking the authorization status of a document.
   *
   * @param accessKey the 49-digit access key of the document
   * @return the authorization response from the SRI
   */
  public AuthorizationResponse checkAuthorization(String accessKey) {

    return authorizationUseCase.execute(accessKey);
  }
}
