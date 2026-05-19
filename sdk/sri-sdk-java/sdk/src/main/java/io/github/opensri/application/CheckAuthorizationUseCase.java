// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.application;

import io.github.opensri.application.ports.SRIGateway;
import io.github.opensri.domain.entities.responses.AuthorizationResponse;

class CheckAuthorizationUseCase {
  private final SRIGateway sriGateway;

  CheckAuthorizationUseCase(SRIGateway sriGateway) {
    this.sriGateway = sriGateway;
  }

  AuthorizationResponse execute(String accessKey) {
    return sriGateway.sendAuthorization(accessKey);
  }
}
