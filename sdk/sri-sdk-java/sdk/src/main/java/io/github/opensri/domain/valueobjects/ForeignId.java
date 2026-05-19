// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Represents a foreign identification number.
 *
 * <p>This value object encapsulates identification values used for non-national clients in tax
 * documents.
 *
 * <p>Instances are validated to ensure they meet the required structural constraints while
 * remaining flexible enough for foreign identification formats.
 */
public record ForeignId(String number) implements ClientIdentification {

  public ForeignId {
    if (number == null || number.isBlank()) {
      throw new OpenSRIValidationException("Foreign ID cannot be blank");
    }

    if (number.length() < 3 || number.length() > 20) {
      throw new OpenSRIValidationException("Invalid foreign ID length");
    }

    if (!number.chars().allMatch(Character::isLetterOrDigit)) {
      throw new OpenSRIValidationException("Foreign ID must be alphanumeric");
    }
  }

  /**
   * Returns the SRI identification type used for foreign identifications.
   *
   * @return {@link IdentificationType#IDENTIFICATION_DEL_EXTERIOR}
   */
  @Override
  public IdentificationType identificationType() {
    return IdentificationType.IDENTIFICATION_DEL_EXTERIOR;
  }

  /**
   * Returns the validated foreign identification value.
   *
   * @return normalized foreign identification string
   */
  @Override
  public String value() {
    return number;
  }
}
