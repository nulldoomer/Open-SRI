package io.github.opensri.domain.valueobjects;

import java.util.Objects;

public record Passport(String number) implements ClientIdentification {
    public Passport {
        Objects.requireNonNull(number);

        if (number.isBlank()) {
            throw new IllegalArgumentException("Passport cannot be blank");
        }

        if (number.length() < 6 || number.length() > 20) {
            throw new IllegalArgumentException("Invalid passport length");
        }

        if (!number.chars().allMatch(Character::isLetterOrDigit)) {
            throw new IllegalArgumentException("Passport must be alphanumeric");
        }
    }
}
