// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.shared.exceptions;

/**
 * Signals failures during communication with SRI services.
 *
 * <p>This exception wraps network errors, timeouts, or SOAP/HTTP specific failures.
 */
public class OpenSRICommunicationException extends SRIException {
  /**
   * Constructs a new OpenSRICommunicationException with the specified message.
   *
   * @param message detail message explaining the communication failure
   */
  public OpenSRICommunicationException(String message) {
    super(message);
  }

  /**
   * Constructs a new OpenSRICommunicationException with the specified message and cause.
   *
   * @param message detail message explaining the communication failure
   * @param cause underlying cause of the failure
   */
  public OpenSRICommunicationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new OpenSRICommunicationException with the specified cause.
   *
   * @param cause underlying cause of the communication failure
   */
  public OpenSRICommunicationException(Throwable cause) {
    super(cause);
  }
}
