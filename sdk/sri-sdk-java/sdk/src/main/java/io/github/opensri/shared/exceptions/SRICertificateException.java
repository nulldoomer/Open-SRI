// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals certificate-related failures during SDK setup or XML signing.
 *
 * <p>This exception is thrown when the signing certificate cannot be loaded,
 * resolved by alias, or used to produce a valid digital signature.
 */
public class SRICertificateException extends RuntimeException {
  public SRICertificateException(String message) {
    super(message);
  }

  public SRICertificateException(String message, Throwable cause) {
    super(message, cause);
  }

  public SRICertificateException(Throwable cause) {
    super(cause);
  }
}
