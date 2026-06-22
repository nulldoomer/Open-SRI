// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.shared.exceptions;

/**
 * Signals certificate-related failures during SDK setup or XML signing.
 *
 * <p>This exception is thrown when the signing certificate cannot be loaded, resolved by alias, or
 * used to produce a valid digital signature.
 */
public class SRICertificateException extends SRIException {
  /**
   * Constructs a SRICertificateException with the specified message.
   *
   * @param message detail message describing the certificate error
   */
  public SRICertificateException(String message) {
    super(message);
  }

  /**
   * Constructs a SRICertificateException with the specified message and cause.
   *
   * @param message detail message describing the certificate error
   * @param cause underlying cause
   */
  public SRICertificateException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a SRICertificateException with the specified cause.
   *
   * @param cause underlying cause
   */
  public SRICertificateException(Throwable cause) {
    super(cause);
  }
}
