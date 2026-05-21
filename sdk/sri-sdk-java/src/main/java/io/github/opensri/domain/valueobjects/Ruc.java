// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Representa un número de identificación del Registro Único de Contribuyentes (RUC).
 *
 * <p>Este objeto de valor valida la estructura y el formato de acuerdo con las reglas del SRI.
 *
 * <p>Las instancias son inmutables y se garantiza que contienen un número de RUC válido, incluyendo
 * las restricciones de provincia, dígito verificador y código de establecimiento.
 *
 * @param number el número de RUC (13 dígitos)
 */
public record Ruc(String number) implements ClientIdentification {

  /**
   * Validates the RUC number format and structure according to SRI rules.
   *
   * <p>Verifies that the number is exactly 13 digits, that the province code is between 01 and 24,
   * that the third digit is valid (not 7 or 8), that the verifier digit matches the algorithm for
   * the corresponding entity type (natural person, public entity, or private company), and that the
   * establishment code is not {@code 000}.
   *
   * @throws OpenSRIValidationException if any validation rule is violated
   */
  public Ruc {
    if (number == null) {
      throw new OpenSRIValidationException("RUC cannot be null");
    }

    if (!number.matches("\\d{13}")) {
      throw new OpenSRIValidationException("Invalid RUC length, it must be 13 digits");
    }

    validateProvince(number);
    validateThirdDigit(number);
    validateVerifierDigit(number);
    validateEstablishmentCode(number);
  }

  private void validateProvince(String number) {
    int provinceDigits = Integer.parseInt(number.subSequence(0, 2).toString());
    if (provinceDigits < 1 || provinceDigits > 24) {
      throw new OpenSRIValidationException("Invalid province code in RUC");
    }
  }

  private void validateThirdDigit(String number) {
    int thirdDigit = Character.getNumericValue(number.charAt(2));

    if (thirdDigit == 7 || thirdDigit == 8) {
      throw new OpenSRIValidationException("Invalid third digit in RUC");
    }
  }

  private void validateVerifierDigit(String number) {

    int thirdDigit = Character.getNumericValue(number.charAt(2));

    if (thirdDigit <= 5) {
      validateNaturalPersonVerifier(number);
    } else if (thirdDigit == 6) {
      validatePublicEntityVerifier(number);
    } else if (thirdDigit == 9) {
      validatePrivateCompanyVerifier(number);
    }
  }

  private void validateNaturalPersonVerifier(String number) {

    int[] coefficients = {2, 1, 2, 1, 2, 1, 2, 1, 2};
    int sum = 0;

    for (int i = 0; i < 9; i++) {
      int digit = Character.getNumericValue(number.charAt(i));
      int result = digit * coefficients[i];

      if (result >= 10) {
        result -= 9;
      }

      sum += result;
    }

    int verifier = Character.getNumericValue(number.charAt(9));

    int nexTen = ((sum + 9) / 10) * 10;
    int calculated = nexTen - sum;

    if (calculated == 10) {
      calculated = 0;
    }
    if (calculated != verifier) {
      throw new OpenSRIValidationException("Invalid RUC verifier digit (natural person)");
    }
  }

  private void validatePublicEntityVerifier(String number) {
    int[] coefficients = {3, 2, 7, 6, 5, 4, 3, 2};
    int sum = 0;

    for (int i = 0; i < 8; i++) {
      int digit = Character.getNumericValue(number.charAt(i));

      sum += digit * coefficients[i];
    }

    int remainder = sum % 11;
    int calculated = 11 - remainder;

    if (calculated == 11) {
      calculated = 0;
    }

    int verifier = Character.getNumericValue(number.charAt(9));

    if (calculated != verifier) {
      throw new OpenSRIValidationException("Invalid RUC verifier digit (private company)");
    }
  }

  private void validatePrivateCompanyVerifier(String number) {

    int[] coefficients = {4, 3, 2, 7, 6, 5, 4, 3, 2};
    int sum = 0;
    for (int i = 0; i < 9; i++) {
      int digit = Character.getNumericValue(number.charAt(i));

      sum += digit * coefficients[i];
    }

    int remainder = sum % 11;
    int calculated = 11 - remainder;

    if (calculated == 11) {
      calculated = 0;
    } else if (calculated == 10) {
      throw new OpenSRIValidationException("Invalid RUC verifier digit (private company)");
    }

    int verifier = Character.getNumericValue(number.charAt(9));

    if (calculated != verifier) {
      throw new OpenSRIValidationException("Invalid RUC verifier digit (private company)");
    }
  }

  private void validateEstablishmentCode(String number) {
    String establishmentCode = String.valueOf(number.subSequence(10, 13));
    if (establishmentCode.equals("000")) {
      throw new OpenSRIValidationException("Invalid establishment code in RUC");
    }
  }

  /**
   * Returns the SRI identification type for taxpayer registry numbers.
   *
   * @return {@link IdentificationType#RUC}
   */
  @Override
  public IdentificationType identificationType() {
    return IdentificationType.RUC;
  }

  /**
   * Returns the validated RUC number.
   *
   * @return taxpayer identification number
   */
  @Override
  public String value() {
    return number;
  }
}
