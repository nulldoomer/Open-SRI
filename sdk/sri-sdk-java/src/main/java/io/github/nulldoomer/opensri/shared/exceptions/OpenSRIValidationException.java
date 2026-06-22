// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.shared.exceptions;

/**
 * Signals that the input data provided to the SDK is invalid.
 *
 * <p>This exception is thrown during domain validation (e.g., invalid RUC, missing required
 * fields).
 */
public class OpenSRIValidationException extends SRIException {
  /**
   * Constructs a new OpenSRIValidationException with the specified message.
   *
   * @param message detail message describing the validation error
   */
  public OpenSRIValidationException(String message) {
    super(message);
  }

  /**
   * Constructs a new OpenSRIValidationException with the specified message and cause.
   *
   * @param message detail message describing the validation error
   * @param cause underlying cause of the validation error
   */
  public OpenSRIValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new OpenSRIValidationException with the specified cause.
   *
   * @param cause underlying cause of the validation error
   */
  public OpenSRIValidationException(Throwable cause) {
    super(cause);
  }
}
