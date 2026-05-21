// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals failures while transforming a document model into XML.
 *
 * <p>This exception is intended for marshalling errors, invalid serialization input, or
 * schema-related issues detected during XML generation.
 */
public class XmlSerializationException extends OpenSRIInfrastructureException {
  /**
   * Constructs a XmlSerializationException with the specified message.
   *
   * @param message detail message describing the serialization error
   */
  public XmlSerializationException(String message) {
    super(message);
  }

  /**
   * Constructs a XmlSerializationException with the specified message and cause.
   *
   * @param message detail message describing the serialization error
   * @param cause underlying cause
   */
  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Constructs a XmlSerializationException with the specified cause.
   *
   * @param cause underlying cause
   */
  public XmlSerializationException(Throwable cause) {
    super(cause);
  }
}
