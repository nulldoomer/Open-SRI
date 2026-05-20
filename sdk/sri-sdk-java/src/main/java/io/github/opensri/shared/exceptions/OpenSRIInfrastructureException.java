// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals failure in the technical infrastructure or internal configuration.
 *
 * <p>This includes file reading errors, XML processing failures, or environment setup issues.
 */
public class OpenSRIInfrastructureException extends SRIException {
  public OpenSRIInfrastructureException(String message) {
    super(message);
  }

  public OpenSRIInfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }

  public OpenSRIInfrastructureException(Throwable cause) {
    super(cause);
  }
}
