// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals that the input data provided to the SDK is invalid.
 *
 * <p>This exception is thrown during domain validation (e.g., invalid RUC, missing required
 * fields).
 */
public class OpenSRIValidationException extends SRIException {
  public OpenSRIValidationException(String message) {
    super(message);
  }

  public OpenSRIValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public OpenSRIValidationException(Throwable cause) {
    super(cause);
  }
}
