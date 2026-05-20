// SPDX-License-Identifier: Apache-2.0
// Copyright (c) 2026 Nulldoomer

package io.github.opensri.domain.valueobjects;

import io.github.opensri.domain.enums.IdentificationType;
import io.github.opensri.shared.exceptions.OpenSRIValidationException;

/**
 * Represents a national identification number.
 *
 * <p>This value object validates the identification number according to the official national
 * checksum algorithm.
 *
 * <p>An instance of this class always contains a valid and properly formatted national ID.
 */
public record NationalId(String number) implements ClientIdentification {
  public NationalId {
    if (number == null) {
      throw new OpenSRIValidationException("National ID cannot be null");
    }

    if (number.length() != 10) {
      throw new OpenSRIValidationException("National ID must have 10 digits");
    }

    if (!number.chars().allMatch(Character::isDigit)) {
      throw new OpenSRIValidationException("National ID must contain only digits");
    }

    validateProvince(number);
    validateThirdDigit(number);
    validateVerifierDigit(number);
  }

  private void validateProvince(String number) {

    int province = Integer.parseInt(number.substring(0, 2));

    if (province < 1 || province > 24) {
      throw new OpenSRIValidationException("Invalid province code");
    }
  }

  private void validateThirdDigit(String number) {

    int thirdDigit = Character.getNumericValue(number.charAt(2));

    if (thirdDigit < 0 || thirdDigit > 5) {
      throw new OpenSRIValidationException("Invalid third digit for national ID");
    }
  }

  private void validateVerifierDigit(String number) {

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

    int nextTen = ((sum + 9) / 10) * 10;
    int calculated = nextTen - sum;

    if (calculated == 10) {
      calculated = 0;
    }

    if (calculated != verifier) {
      throw new OpenSRIValidationException("Invalid national ID verifier digit");
    }
  }

  /**
   * Returns the SRI identification type for national identity documents.
   *
   * @return {@link IdentificationType#CEDULA}
   */
  @Override
  public IdentificationType identificationType() {
    return IdentificationType.CEDULA;
  }

  /**
   * Returns the validated national identification value.
   *
   * @return national ID number
   */
  @Override
  public String value() {
    return number;
  }
}
