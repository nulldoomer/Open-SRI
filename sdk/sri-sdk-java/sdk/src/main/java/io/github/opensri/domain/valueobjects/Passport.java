// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Represents a passport identification number.
 *
 * <p>This value object ensures that the passport number is non-null, non-blank, and formatted
 * according to general identification requirements.
 *
 * <p>Validation rules are intentionally flexible to support international formats.
 */
public record Passport(String number) implements ClientIdentification {
  public Passport {
    if (number == null) {
      throw new OpenSRIValidationException("Passport cannot be null");
    }

    if (number.isBlank()) {
      throw new OpenSRIValidationException("Passport cannot be blank");
    }

    if (number.length() < 6 || number.length() > 20) {
      throw new OpenSRIValidationException("Invalid passport length");
    }

    if (!number.chars().allMatch(Character::isLetterOrDigit)) {
      throw new OpenSRIValidationException("Passport must be alphanumeric");
    }
  }

  /**
   * Returns the SRI identification type for passport documents.
   *
   * @return {@link IdentificationType#PASAPORTE}
   */
  @Override
  public IdentificationType identificationType() {
    return IdentificationType.PASAPORTE;
  }

  /**
   * Returns the validated passport value.
   *
   * @return passport identification string
   */
  @Override
  public String value() {
    return number;
  }
}
