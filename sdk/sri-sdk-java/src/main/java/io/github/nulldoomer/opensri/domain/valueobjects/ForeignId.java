// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.nulldoomer.opensri.domain.valueobjects;

import io.github.nulldoomer.opensri.domain.enums.IdentificationType;
import io.github.nulldoomer.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Representa un número de identificación para extranjeros.
 *
 * <p>Este objeto de valor encapsula los valores de identificación utilizados para clientes no
 * nacionales en los documentos tributarios.
 *
 * <p>Las instancias se validan para asegurar que cumplen con las restricciones estructurales
 * requeridas, manteniendo la flexibilidad para los formatos de identificación extranjeros.
 *
 * @param number el número de identificación extranjera
 */
public record ForeignId(String number) implements ClientIdentification {

  /**
   * Valida el valor de identificación extranjero.
   *
   * <p>Lanza {@link OpenSRIValidationException} si el número es nulo, vacío, tiene longitud
   * inválida o contiene caracteres no alfanuméricos.
   */
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
