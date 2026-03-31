// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.api.client;

import io.github.opensri.domain.entities.common.IssuerProfile;
import io.github.opensri.domain.enums.Environment;

public final class OpenSRIClient {

  private final Environment environment;
  private final byte[] certificate;
  private final String certificatePassword;
  private final String certificateAlias;
  private final IssuerProfile issuerProfile;
  private final int timeoutSeconds;

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
  }
}
