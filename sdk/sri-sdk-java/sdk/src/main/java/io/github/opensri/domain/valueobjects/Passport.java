package io.github.opensri.domain.valueobjects;

import java.util.Objects;
/**
 * Represents a passport identification number.
 *
 * <p>This value object ensures that the passport number
 * is non-null, non-blank, and formatted according to
 * general identification requirements.
 *
 * <p>Validation rules are intentionally flexible to
 * support international formats.
 */

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
