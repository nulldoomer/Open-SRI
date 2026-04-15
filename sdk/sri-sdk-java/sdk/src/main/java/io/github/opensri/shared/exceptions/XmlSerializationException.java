// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.shared.exceptions;

/**
 * Signals failures while transforming a document model into XML.
 *
 * <p>This exception is intended for marshalling errors, invalid serialization
 * input, or schema-related issues detected during XML generation.
 */
public class XmlSerializationException extends RuntimeException {
  public XmlSerializationException(String message) {
    super(message);
  }

  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlSerializationException(Throwable cause) {
    super(cause);
  }
}
