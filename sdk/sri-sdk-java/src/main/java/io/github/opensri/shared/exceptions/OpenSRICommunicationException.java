// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals failures during communication with SRI services.
 *
 * <p>This exception wraps network errors, timeouts, or SOAP/HTTP specific failures.
 */
public class OpenSRICommunicationException extends SRIException {
  public OpenSRICommunicationException(String message) {
    super(message);
  }

  public OpenSRICommunicationException(String message, Throwable cause) {
    super(message, cause);
  }

  public OpenSRICommunicationException(Throwable cause) {
    super(cause);
  }
}
