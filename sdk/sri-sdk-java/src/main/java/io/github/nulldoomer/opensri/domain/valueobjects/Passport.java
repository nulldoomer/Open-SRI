// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.valueobjects;

import io.github.nulldoomer.opensri.domain.enums.IdentificationType;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Representa un número de identificación de pasaporte.
 *
 * <p>Este objeto de valor asegura que el número de pasaporte no sea nulo, no esté en blanco y esté
 * formateado de acuerdo con los requisitos generales de identificación.
 *
 * <p>Las reglas de validación son intencionalmente flexibles para soportar formatos
 * internacionales.
 *
 * @param number el número de pasaporte
 */
public record Passport(String number) implements ClientIdentification {
  /**
   * Validates the passport number format.
   *
   * <p>Ensures the number is non-null, non-blank, between 6 and 20 characters in length, and
   * contains only alphanumeric characters to support international passport formats.
   *
   * @throws OpenSRIValidationException if any validation rule is violated
   */
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
