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

public final class OpenSRIClient {

  private final Environment environment;
  private final IssuerProfile issuerProfile;
  private final OPENSRIApplication applicationFacade;

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

  public SendInvoiceResult sendInvoice(Invoice invoice) {
    return applicationFacade.sendInvoice(invoice, environment, issuerProfile);
  }

  public AuthorizationResponse checkAuthorization(String accessKey) {
    return applicationFacade.checkAuthorization(accessKey);
  }
}
