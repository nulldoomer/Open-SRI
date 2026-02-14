package io.github.opensri.domain.valueobjects;

public record ForeignId(String number) {

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
