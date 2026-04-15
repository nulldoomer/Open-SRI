// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Base runtime exception for SDK errors related to SRI processing.
 *
 * <p>This unchecked exception provides a common root for failures that occur
 * while preparing, sending, or processing electronic tax documents.
 */
public class SRIException extends RuntimeException {
  public SRIException(String message) {
    super(message);
  }

  public SRIException(String message, Throwable cause) {
    super(message, cause);
  }

  public SRIException(Throwable cause) {
    super(cause);
  }
}
