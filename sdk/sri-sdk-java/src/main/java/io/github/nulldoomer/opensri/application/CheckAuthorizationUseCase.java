// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.application;

import io.github.nulldoomer.opensri.application.ports.SRIGateway;
import io.github.nulldoomer.opensri.domain.entities.responses.AuthorizationResponse;

/**
 * Use case responsible for querying the authorization status of an electronic document from the
 * SRI.
 */
class CheckAuthorizationUseCase {
  private final SRIGateway sriGateway;

  CheckAuthorizationUseCase(SRIGateway sriGateway) {
    this.sriGateway = sriGateway;
  }

  /**
   * Executes the authorization query via the configured gateway.
   *
   * @param accessKey the 49-digit access key of the document to check
   * @return the authorization response containing the SRI status and messages
   */
  AuthorizationResponse execute(String accessKey) {
    return sriGateway.sendAuthorization(accessKey);
  }
}
