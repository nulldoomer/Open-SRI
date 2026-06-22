// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.shared.exceptions;

/**
 * Base runtime exception for SDK errors related to SRI processing.
 *
 * <p>This unchecked exception provides a common root for failures that occur while preparing,
 * sending, or processing electronic tax documents.
 */
public class SRIException extends RuntimeException {
  /**
   * Constructs a new SRIException with the specified detail message.
   *
   * @param message the detail message
   */
  public SRIException(String message) {
    super(message);
  }

  /**
   * Constructs a new SRIException with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of this exception
   */
  public SRIException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a new SRIException with the specified cause.
   *
   * @param cause the cause of this exception
   */
  public SRIException(Throwable cause) {
    super(cause);
  }
}
