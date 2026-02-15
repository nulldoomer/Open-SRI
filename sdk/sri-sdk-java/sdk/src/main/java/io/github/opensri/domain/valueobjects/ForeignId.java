package io.github.opensri.domain.valueobjects;

/**
 * Represents a foreign identification number.
 *
 * <p>This value object encapsulates identification
 * values used for non-national clients in tax documents.
 *
 * <p>Instances are validated to ensure they meet
 * the required structural constraints.
 */
public record ForeignId(String number) implements ClientIdentification {

    public ForeignId {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Foreign ID cannot be blank");
        }

        if (number.length() < 3 || number.length() > 20) {
            throw new IllegalArgumentException("Invalid foreign ID length");
        }

        if (!number.chars().allMatch(Character::isLetterOrDigit)) {
            throw new IllegalArgumentException("Foreign ID must be alphanumeric");
        }
    }
}
