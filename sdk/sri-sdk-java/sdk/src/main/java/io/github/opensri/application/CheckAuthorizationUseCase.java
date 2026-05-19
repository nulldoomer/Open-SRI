// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;
import java.io.IOException;

class CheckAuthorizationUseCase {
  private final SRIGateway sriGateway;

  CheckAuthorizationUseCase(SRIGateway sriGateway) {
    this.sriGateway = sriGateway;
  }

  AuthorizationResponse execute(String accessKey) {
    try {
      return sriGateway.sendAuthorization(accessKey);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
