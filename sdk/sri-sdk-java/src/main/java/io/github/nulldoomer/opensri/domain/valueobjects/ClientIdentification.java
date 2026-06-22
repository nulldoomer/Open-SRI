// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.valueobjects;

import io.github.nulldoomer.opensri.domain.enums.IdentificationType;

/**
 * Represents a client identification used in electronic tax documents.
 *
 * <p>This is a sealed hierarchy that models all valid identification types accepted by the tax
 * authority (SRI).
 *
 * <p>Each implementation encapsulates its own validation rules and guarantees that the
 * identification number is always valid according to its type.
 *
 * <p>The permitted implementations are:
 *
 * <ul>
 *   <li>{@link Ruc}
 *   <li>{@link NationalId}
 *   <li>{@link Passport}
 *   <li>{@link ForeignId}
 *   <li>{@link FinalConsumer}
 * </ul>
 *
 * <p>This interface ensures that invalid identification states cannot be represented in the domain
 * model.
 */
public sealed interface ClientIdentification
    permits Ruc, NationalId, Passport, ForeignId, FinalConsumer {

  /**
   * Returns the SRI identification type associated with this value object.
   *
   * @return identification type code category required by the document schema
   */
  IdentificationType identificationType();

  /**
   * Returns the serialized identification value that must appear in the document.
   *
   * @return normalized identification string represented by this value object
   */
  String value();
}
