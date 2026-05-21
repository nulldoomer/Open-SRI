// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals failure in the technical infrastructure or internal configuration.
 *
 * <p>This includes file reading errors, XML processing failures, or environment setup issues.
 */
public class OpenSRIInfrastructureException extends SRIException {
  /**
   * Constructs a new OpenSRIInfrastructureException with the specified message.
   *
   * @param message detail message describing the infrastructure error
   */
  public OpenSRIInfrastructureException(String message) {
    super(message);
  }

  /**
   * Constructs a new OpenSRIInfrastructureException with the specified message and cause.
   *
   * @param message detail message describing the infrastructure error
   * @param cause underlying cause of the error
   */
  public OpenSRIInfrastructureException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new OpenSRIInfrastructureException with the specified cause.
   *
   * @param cause underlying cause of the infrastructure error
   */
  public OpenSRIInfrastructureException(Throwable cause) {
    super(cause);
  }
}
