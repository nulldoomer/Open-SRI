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

public final class OPENSRIApplication {

  private final SendInvoiceUseCase sendInvoiceUseCase;
  private final CheckAuthorizationUseCase authorizationUseCase;

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

  public SendInvoiceResult sendInvoice(
      Invoice invoice, Environment environment, IssuerProfile profile) {

    return sendInvoiceUseCase.execute(invoice, environment, profile);
  }

  public AuthorizationResponse checkAuthorization(String accessKey) {

    return authorizationUseCase.execute(accessKey);
  }
}
