// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.client;

import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.enums.Environment;

public final class OpenSRIClient {

  // TODO: Use this class as the composition root of the SDK.
  //  Instantiate the concrete infrastructure implementations here
  //  (access key generator, XML serializers, signer, and SRI gateway)
  //  and inject them into the corresponding use cases through their constructors.

  private final Environment environment;
  private final byte[] certificate;
  private final String certificatePassword;
  private final String certificateAlias;
  private final IssuerProfile issuerProfile;
  private final int timeoutSeconds;

  // TODO: Promote the use cases to fields once certificate loading,
  //  document signing, and SRI web service integration are finished.
  //  Example: private final SendInvoiceUseCase sendInvoiceUseCase;
  //  Later, additional document-specific use cases can be added here as well
  //  (sendRemision, sendCreditNote, etc.).

  public OpenSRIClient(
      Environment environment,
      byte[] certificate,
      String certificatePassword,
      String certificateAlias,
      IssuerProfile issuerProfile,
      int timeoutSeconds) {

    this.environment = environment;
    this.certificate = certificate;
    this.certificatePassword = certificatePassword;
    this.certificateAlias = certificateAlias;
    this.issuerProfile = issuerProfile;
    this.timeoutSeconds = timeoutSeconds;

    // TODO: Build the concrete dependencies here using the client configuration:
    //  environment, certificate bytes, certificate password, certificate alias,
    //  issuer profile, and timeout.
    //  Then wire those dependencies into the use cases so the public API methods
    //  only delegate to application-layer execute() calls.
  }

  // TODO: Expose a public sendInvoice(Invoice invoice) method that delegates to
  //  SendInvoiceUseCase.execute(...), passing the invoice plus the stable client
  //  configuration required by the use case (environment and issuer profile).
}
