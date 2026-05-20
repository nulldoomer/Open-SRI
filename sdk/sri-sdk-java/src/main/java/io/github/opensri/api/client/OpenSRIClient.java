// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.client;

import io.github.opensri.application.OPENSRIApplication;
import io.github.opensri.application.ports.DocumentSigner;
import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.domain.entities.common.issuer.IssuerProfile;
import io.github.opensri.domain.entities.invoice.Invoice;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import io.github.opensri.domain.entities.responses.SendInvoiceResult;
import io.github.opensri.domain.enums.Environment;
import io.github.opensri.infrastructure.crypto.signing.XAdEsSignerFactory;
import io.github.opensri.infrastructure.serializers.InvoiceXmlSerializerFactory;
import io.github.opensri.infrastructure.services.SRIAccessKeyGeneratorFactory;
import io.github.opensri.infrastructure.sri.SRIGatewayFactory;

/**
 * Facade class that serves as the main entry point for interacting with the Open-SRI SDK.
 *
 * <p>The {@code OpenSRIClient} encapsulates the complex interaction between domain logic,
 * infrastructure services, and SRI web services. It should be instantiated using the {@link
 * io.github.opensri.api.builders.client.OpenSRIClientBuilder}.
 */
public final class OpenSRIClient {

  private final Environment environment;
  private final IssuerProfile issuerProfile;
  private final OPENSRIApplication applicationFacade;

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
        new OPENSRIApplication(
            SRIAccessKeyGeneratorFactory.create(),
            InvoiceXmlSerializerFactory.create(),
            signer,
            gateway);
  }

  /**
   * Sends an electronic invoice to the SRI for reception.
   *
   * @param invoice the invoice domain model to be sent
   * @return the result of the submission, including the access key and signed XML
   */
  public SendInvoiceResult sendInvoice(Invoice invoice) {
    return applicationFacade.sendInvoice(invoice, environment, issuerProfile);
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
