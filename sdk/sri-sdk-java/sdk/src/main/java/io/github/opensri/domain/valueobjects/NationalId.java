package io.github.opensri.domain.valueobjects;

import java.util.Objects;

public record NationalId(String number) {
    public NationalId {
        Objects.requireNonNull(number,"National ID cannot be null");

        if (number.length() != 10) {
            throw new IllegalArgumentException("National ID must have 10 digits");
        }

        if (!number.chars().allMatch(Character::isDigit)) {
            throw new IllegalArgumentException("National ID must contain only digits");
        }

        validateProvince(number);
        validateThirdDigit(number);
        validateVerifierDigit(number);
    }

    private void validateProvince(String number) {

        int province = Integer.parseInt(number.substring(0, 2));

        if (province < 1 || province > 24) {
            throw new IllegalArgumentException("Invalid province code");
        }
    }

    private void validateThirdDigit(String number) {

        int thirdDigit = Character.getNumericValue(number.charAt(2));

        if (thirdDigit < 0 || thirdDigit > 5) {
            throw new IllegalArgumentException("Invalid third digit for national ID");
        }
    }

    private void validateVerifierDigit(String number) {

        int[] coefficients = {2,1,2,1,2,1,2,1,2};
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
            throw new IllegalArgumentException("Invalid national ID verifier digit");
        }
    }
}
